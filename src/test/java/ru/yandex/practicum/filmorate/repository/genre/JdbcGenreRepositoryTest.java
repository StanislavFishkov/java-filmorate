package ru.yandex.practicum.filmorate.repository.genre;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@JdbcTest
@Import({JdbcGenreRepository.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DisplayName("JdbcGenreRepository integration tests")
class JdbcGenreRepositoryTest {
    private static final int TEST_GENRE_ID = 1;
    private static final int COUNT_OF_ELEMENTS = 6;

    private final JdbcGenreRepository jdbcGenreRepository;

    static Genre getTestGenre() {
        return Genre.builder()
                .id(TEST_GENRE_ID)
                .name("Комедия")
                .build();
    }

    @Test
    @DisplayName("checkGenreExists() works properly on existing and lacking elements.")
    void checkGenreExists() {
        assertThat(jdbcGenreRepository.checkGenreExists(TEST_GENRE_ID)).isTrue();
        assertThat(jdbcGenreRepository.checkGenreExists(COUNT_OF_ELEMENTS + 1)).isFalse();
    }

    @Test
    @DisplayName("checkGenresExist() works properly on an empty list, existing and lacking elements in the list.")
    void checkGenresExist() {
        assertThat(jdbcGenreRepository.checkGenresExist(List.of())).isTrue();
        assertThat(jdbcGenreRepository.checkGenresExist(List.of(TEST_GENRE_ID, COUNT_OF_ELEMENTS))).isTrue();
        assertThat(jdbcGenreRepository.checkGenresExist(List.of(TEST_GENRE_ID, COUNT_OF_ELEMENTS + 1))).isFalse();
    }

    @Test
    @DisplayName("get() returns correct genre.")
    void get() {
        Genre genre = jdbcGenreRepository.get(TEST_GENRE_ID);

        assertThat(genre)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(getTestGenre());
    }

    @Test
    @DisplayName("getAll() returns correct number of elements.")
    void getAll() {
        assertThat(jdbcGenreRepository.getAll())
                .hasSize(COUNT_OF_ELEMENTS);
    }
}