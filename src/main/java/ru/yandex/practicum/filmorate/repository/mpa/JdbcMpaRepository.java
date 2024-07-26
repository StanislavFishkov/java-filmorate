package ru.yandex.practicum.filmorate.repository.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Repository
@Primary
@RequiredArgsConstructor
public class JdbcMpaRepository implements MpaRepository {
    private final NamedParameterJdbcOperations jdbc;

    private Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(resultSet.getInt("mpa_rating_id"))
                .name(resultSet.getString("name"))
                .build();
    }

    @Override
    public boolean checkMpaExists(Integer mpaId) {
        String sqlQuery = "SELECT COUNT(*) FROM \"mpa_rating\" WHERE \"mpa_rating_id\" = :mpa_rating_id;";

        return 1 == jdbc.queryForObject(sqlQuery, new MapSqlParameterSource("mpa_rating_id", mpaId), Integer.class);
    }

    @Override
    public Mpa get(Integer mpaId) {
        String sqlQuery = "SELECT * FROM \"mpa_rating\" WHERE \"mpa_rating_id\" = :mpa_rating_id;";

        return jdbc.queryForObject(sqlQuery, new MapSqlParameterSource("mpa_rating_id", mpaId), this::mapRowToMpa);
    }

    @Override
    public Collection<Mpa> getAll() {
        String sqlQuery = "SELECT * FROM \"mpa_rating\" ORDER BY \"mpa_rating_id\";";

        return jdbc.query(sqlQuery, this::mapRowToMpa);
    }
}