package ru.yandex.practicum.filmorate.repository.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

@Repository
@Primary
@RequiredArgsConstructor
public class JdbcGenreRepository implements GenreRepository {
    private final NamedParameterJdbcOperations jdbc;

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("genre_id"))
                .name(resultSet.getString("name"))
                .build();
    }

    @Override
    public boolean checkGenreExists(Integer genreId) {
        String sqlQuery = "SELECT COUNT(*) FROM \"genre\" WHERE \"genre_id\" = :genre_id;";

        return 1 == jdbc.queryForObject(sqlQuery, new MapSqlParameterSource("genre_id", genreId), Integer.class);
    }

    @Override
    public boolean checkGenresExist(List<Integer> genreIds) {
        int genresIdsSize = genreIds.size();
        if (genresIdsSize == 0) return true;

        String sqlQuery = "SELECT COUNT(*) FROM \"genre\" WHERE \"genre_id\" IN (:genre_ids);";

        return genresIdsSize ==
                jdbc.queryForObject(sqlQuery, new MapSqlParameterSource("genre_ids", genreIds), Integer.class);
    }

    @Override
    public Genre get(Integer genreId) {
        String sqlQuery = "SELECT * FROM \"genre\" WHERE \"genre_id\" = :genre_id;";

        return jdbc.queryForObject(sqlQuery, new MapSqlParameterSource("genre_id", genreId), this::mapRowToGenre);
    }

    @Override
    public Collection<Genre> getAll() {
        String sqlQuery = "SELECT * FROM \"genre\" ORDER BY \"genre_id\";";

        return jdbc.query(sqlQuery, this::mapRowToGenre);
    }
}
