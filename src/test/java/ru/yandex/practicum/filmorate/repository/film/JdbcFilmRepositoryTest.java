package ru.yandex.practicum.filmorate.repository.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import(JdbcFilmRepository.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DisplayName("JdbcFilmRepository integration tests")
class JdbcFilmRepositoryTest {
    private static final long TEST_FILM_ID = 1L;
    private static final long COUNT_OF_ELEMENTS = 3L;
    private static final long TEST_USER_ID = 1L;

    private final JdbcFilmRepository jdbcFilmRepository;

    static Film getTestFilm() {
        Film film = Film.builder()
                .id(TEST_FILM_ID)
                .name("test")
                .description("test")
                .releaseDate(LocalDate.parse("1960-03-21"))
                .duration(100)
                .mpa(new Mpa(1, "G"))
                .build();

        film.getGenres().add(new Genre(1, "Комедия"));
        film.getGenres().add(new Genre(2, "Драма"));

        return film;
    }

    static Film getTestNewFilm() {
        Film newFilm = Film.builder()
                .name("testNew")
                .description("testNew")
                .releaseDate(LocalDate.parse("2000-03-22"))
                .duration(999)
                .mpa(new Mpa(2, "PG"))
                .build();

        newFilm.getGenres().add(new Genre(3, "Мультфильм"));
        newFilm.getGenres().add(new Genre(4, "Триллер"));

        return newFilm;
    }

    @Test
    @DisplayName("checkFilmExists() works properly on existing and lacking elements.")
    void checkFilmExists() {
        assertThat(jdbcFilmRepository.checkFilmExists(TEST_FILM_ID)).isTrue();
        assertThat(jdbcFilmRepository.checkFilmExists(COUNT_OF_ELEMENTS + 1)).isFalse();
    }

    @Test
    @DisplayName("get() returns correct film.")
    void get() {
        Film film = jdbcFilmRepository.get(TEST_FILM_ID);

        assertThat(film)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(getTestFilm());
    }

    @Test
    @DisplayName("getAll() returns correct number of elements.")
    void getAll() {
        assertThat(jdbcFilmRepository.getAll())
                .hasSize((int) COUNT_OF_ELEMENTS);
    }

    @Test
    @DisplayName("create() creates equivalent film on all fields except id.")
    void create() {
        Film createdFilm = jdbcFilmRepository.create(getTestNewFilm());

        assertThat(jdbcFilmRepository.get(createdFilm.getId()))
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(getTestNewFilm());
    }

    @Test
    @DisplayName("update() updates film properly in database.")
    void update() {
        Film updateFilm = getTestNewFilm();
        updateFilm.setId(TEST_FILM_ID);

        assertThat(jdbcFilmRepository.get(updateFilm.getId()))
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isNotEqualTo(updateFilm);

        jdbcFilmRepository.update(updateFilm);

        assertThat(jdbcFilmRepository.get(updateFilm.getId()))
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(getTestNewFilm());
    }

    @Test
    @DisplayName("addLike() adds likes to correct user.")
    void addLike() {
        jdbcFilmRepository.addLike(TEST_FILM_ID, TEST_USER_ID);

        assertThat(jdbcFilmRepository.getMostPopular(1))
                .singleElement()
                .isEqualToComparingOnlyGivenFields(Film.builder().id(TEST_FILM_ID), "id");

    }

    @Test
    @DisplayName("getMostPopular() returns most popular (liked) film in database.")
    void getMostPopular() {
        jdbcFilmRepository.addLike(COUNT_OF_ELEMENTS, TEST_USER_ID);
        jdbcFilmRepository.addLike(COUNT_OF_ELEMENTS, TEST_USER_ID + 1);

        jdbcFilmRepository.addLike(TEST_FILM_ID, TEST_USER_ID);

        assertThat(jdbcFilmRepository.getMostPopular(2))
                .containsExactly(jdbcFilmRepository.get(COUNT_OF_ELEMENTS), jdbcFilmRepository.get(TEST_FILM_ID));
    }

    @Test
    @DisplayName("removeLike() actually removes the like in database.")
    void removeLike() {
        jdbcFilmRepository.addLike(TEST_FILM_ID, TEST_USER_ID);
        jdbcFilmRepository.addLike(TEST_FILM_ID, TEST_USER_ID + 1);

        jdbcFilmRepository.addLike(COUNT_OF_ELEMENTS, TEST_USER_ID);

        assertThat(jdbcFilmRepository.getMostPopular(1))
                .singleElement()
                .isEqualToComparingOnlyGivenFields(Film.builder().id(TEST_FILM_ID), "id");

        jdbcFilmRepository.removeLike(TEST_FILM_ID, TEST_USER_ID);
        jdbcFilmRepository.removeLike(TEST_FILM_ID, TEST_USER_ID + 1);

        assertThat(jdbcFilmRepository.getMostPopular(1))
                .singleElement()
                .isEqualToComparingOnlyGivenFields(Film.builder().id(COUNT_OF_ELEMENTS), "id");
    }

    @Test
    @DisplayName("delete() actually deletes the film in database.")
    void delete() {
        assertThat(jdbcFilmRepository.checkFilmExists(TEST_FILM_ID)).isTrue();
        jdbcFilmRepository.delete(TEST_FILM_ID);
        assertThat(jdbcFilmRepository.checkFilmExists(TEST_FILM_ID)).isFalse();
    }
}