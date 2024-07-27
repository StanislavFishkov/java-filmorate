package ru.yandex.practicum.filmorate.repository.film;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Repository
@Primary
@RequiredArgsConstructor
public class JdbcFilmRepository implements FilmRepository {
    private final NamedParameterJdbcOperations jdbc;

    private static Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getLong("film_id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(LocalDate.parse(resultSet.getString("release_date")))
                .duration(resultSet.getInt("duration"))
                .mpa(new Mpa(resultSet.getInt("mpa_rating_id"), resultSet.getString("mpa_rating_name")))
                .build();
    }

    private static class MapOrderedResultSetToFilms implements ResultSetExtractor<Collection<Film>> {
        @Override
        public Collection<Film> extractData(ResultSet rs) throws SQLException, DataAccessException {
            Collection<Film> films = new LinkedList<>();

            Film film = null;
            while (rs.next()) {
                if (film == null) {
                    film = mapRowToFilm(rs, rs.getRow());
                } else if (!film.getId().equals(rs.getLong("film_id"))) {
                    films.add(film);
                    film = mapRowToFilm(rs, rs.getRow());
                }
                Integer genreId = rs.getInt("genre_id");
                if (!rs.wasNull())
                    film.getGenres().add(
                            new Genre(genreId, rs.getString("genre_name"))
                    );
            }
            if (film != null) {
                films.add(film);
            }

            return films;
        }
    }

    private void setFilmGenres(Film film) {
        String sqlQuery = "DELETE FROM \"film_genre\" WHERE \"film_id\" = :film_id;";
        jdbc.update(sqlQuery, new MapSqlParameterSource("film_id", film.getId()));

        sqlQuery = "INSERT INTO \"film_genre\" (\"film_id\", \"genre_id\") VALUES (:film_id, :genre_id);";
        SqlParameterSource[] batchParams = SqlParameterSourceUtils.createBatch(
                film.getGenres().stream().map(genre -> {
                    Map<String, Object> param = new HashMap<>();
                    param.put("film_id", film.getId());
                    param.put("genre_id", genre.getId());
                    return param;
                }).toList()
        );
        jdbc.batchUpdate(sqlQuery, batchParams);
    }

    @Override
    public Film create(Film film) {
        String sqlQuery = "INSERT INTO \"film\" " +
                "(\"name\", \"description\", \"release_date\", \"duration\", \"mpa_rating_id\") " +
                "VALUES (:name, :description, :release_date, :duration, :mpa_rating_id);";

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(sqlQuery, new MapSqlParameterSource(film.toMap()), keyHolder);
        film.setId(keyHolder.getKeyAs(Long.class));

        setFilmGenres(film);

        return film;
    }

    @Override
    public boolean checkFilmExists(Long filmId) {
        String sqlQuery = "SELECT COUNT(*) FROM \"film\" WHERE \"film_id\" = :film_id;";

        return 1 == jdbc.queryForObject(sqlQuery, new MapSqlParameterSource("film_id", filmId), Integer.class);
    }

    @Override
    public Film get(Long filmId) {
        String sqlQuery = "SELECT f.*, mpa.\"name\" AS mpa_rating_name, genre.\"genre_id\", genre.\"name\" AS genre_name " +
                "FROM \"film\" AS f LEFT JOIN \"mpa_rating\" AS mpa ON f.\"mpa_rating_id\" = mpa.\"mpa_rating_id\" " +
                "LEFT JOIN \"film_genre\" AS fg ON f.\"film_id\" = fg.\"film_id\" " +
                "LEFT JOIN \"genre\" AS genre ON fg.\"genre_id\" = genre.\"genre_id\" " +
                "WHERE f.\"film_id\" = :film_id;";

        return jdbc.query(sqlQuery, new MapSqlParameterSource("film_id", filmId), new ResultSetExtractor<Film>() {
            @Override
            public Film extractData(ResultSet rs) throws SQLException, DataAccessException {
                Film film = null;
                while (rs.next()) {
                    if (film == null) {
                        film = mapRowToFilm(rs, rs.getRow());
                    }
                    Integer genreId = rs.getInt("genre_id");
                    if (!rs.wasNull())
                        film.getGenres().add(
                                new Genre(genreId, rs.getString("genre_name"))
                        );
                }
                return film;
            }
        });
    }

    @Override
    public Collection<Film> getAll() {
        String sqlQuery = "SELECT f.*, mpa.\"name\" AS mpa_rating_name, genre.\"genre_id\", genre.\"name\" AS genre_name " +
                "FROM \"film\" AS f LEFT JOIN \"mpa_rating\" AS mpa ON f.\"mpa_rating_id\" = mpa.\"mpa_rating_id\" " +
                "LEFT JOIN \"film_genre\" AS fg ON f.\"film_id\" = fg.\"film_id\" " +
                "LEFT JOIN \"genre\" AS genre ON fg.\"genre_id\" = genre.\"genre_id\" " +
                "ORDER BY f.\"film_id\";";

        return jdbc.query(sqlQuery, new MapOrderedResultSetToFilms());
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "UPDATE \"film\" SET \"name\" = :name, \"description\" = :name, " +
                "\"release_date\" = :release_date, \"duration\" = :duration, \"mpa_rating_id\" = :mpa_rating_id " +
                "WHERE \"film_id\" = :film_id;";

        jdbc.update(sqlQuery, new MapSqlParameterSource(film.toMap()));

        setFilmGenres(film);

        return film;
    }

    @Override
    public void delete(Long filmId) {
        String sqlQuery = "DELETE FROM \"film\" WHERE \"film_id\" = :film_id;";
        jdbc.update(sqlQuery, new MapSqlParameterSource("film_id", filmId));
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        String sqlQuery = "MERGE INTO \"user_film_like\" (\"user_id\", \"film_id\") VALUES (:user_id, :film_id);";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user_id", userId);
        params.addValue("film_id", filmId);

        jdbc.update(sqlQuery, params);
    }

    @Override
    public Collection<Film> getMostPopular(long count) {
        String sqlQuery = "SELECT f.*, mpa.\"name\" AS mpa_rating_name, genre.\"genre_id\", " +
                "genre.\"name\" AS genre_name FROM " +
                "(SELECT \"film_id\", COUNT(*) AS likesCount FROM \"user_film_like\" " +
                "GROUP BY \"film_id\" ORDER BY likesCount DESC LIMIT :count) AS p " +
                "JOIN \"film\" AS f ON p.\"film_id\" = f.\"film_id\" " +
                "LEFT JOIN \"mpa_rating\" AS mpa ON f.\"mpa_rating_id\" = mpa.\"mpa_rating_id\" " +
                "LEFT JOIN \"film_genre\" AS fg ON f.\"film_id\" = fg.\"film_id\" " +
                "LEFT JOIN \"genre\" AS genre ON fg.\"genre_id\" = genre.\"genre_id\" " +
                "ORDER BY p.likesCount DESC, f.\"film_id\";";

        return jdbc.query(sqlQuery, new MapSqlParameterSource("count", count),
                new MapOrderedResultSetToFilms());
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        String sqlQuery = "DELETE FROM \"user_film_like\" WHERE \"user_id\" = :user_id AND \"film_id\" = :film_id;";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user_id", userId);
        params.addValue("film_id", filmId);

        jdbc.update(sqlQuery, params);
    }
}
