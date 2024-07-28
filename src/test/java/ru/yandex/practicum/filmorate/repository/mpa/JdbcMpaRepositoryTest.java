package ru.yandex.practicum.filmorate.repository.mpa;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Mpa;

import static org.assertj.core.api.Assertions.*;

@JdbcTest
@Import({JdbcMpaRepository.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DisplayName("JdbcMpaRepository integration tests")
class JdbcMpaRepositoryTest {
    private static final int TEST_MPA_ID = 1;
    private static final int COUNT_OF_ELEMENTS = 5;

    private final JdbcMpaRepository jdbcMpaRepository;

    static Mpa getTestMpa() {
        return Mpa.builder()
                .id(TEST_MPA_ID)
                .name("G")
                .build();
    }

    @Test
    @DisplayName("checkMpaExists() works properly on existing and lacking elements.")
    void checkMpaExists() {
        assertThat(jdbcMpaRepository.checkMpaExists(TEST_MPA_ID)).isTrue();
        assertThat(jdbcMpaRepository.checkMpaExists(COUNT_OF_ELEMENTS + 1)).isFalse();
    }

    @Test
    @DisplayName("get() returns correct mpa.")
    void get() {
        Mpa mpa = jdbcMpaRepository.get(TEST_MPA_ID);

        assertThat(mpa)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(getTestMpa());
    }

    @Test
    @DisplayName("getAll() returns correct number of elements.")
    void getAll() {
        assertThat(jdbcMpaRepository.getAll())
                .hasSize(COUNT_OF_ELEMENTS);
    }
}