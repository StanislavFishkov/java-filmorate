package ru.yandex.practicum.filmorate.repository.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import({JdbcUserRepository.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DisplayName("JdbcUserRepository integration tests")
class JdbcUserRepositoryTest {
    private static final long TEST_USER_ID = 1L;
    private static final long COUNT_OF_ELEMENTS = 2L;

    private final JdbcUserRepository jdbcUserRepository;

    static User getTestUser() {
        return User.builder()
                .id(TEST_USER_ID)
                .email("email@email.com")
                .login("user")
                .name("test")
                .birthday(LocalDate.parse("2000-03-22"))
                .build();
    }

    static User getTestNewUser() {
        return User.builder()
                .email("emailNew@email.com")
                .login("userNew")
                .name("testNew")
                .birthday(LocalDate.parse("1988-10-09"))
                .build();
    }

    @Test
    @DisplayName("checkUserExists() works properly on existing and lacking elements.")
    void checkUserExists() {
        assertThat(jdbcUserRepository.checkUserExists(TEST_USER_ID)).isTrue();
        assertThat(jdbcUserRepository.checkUserExists(COUNT_OF_ELEMENTS + 1)).isFalse();
    }

    @Test
    @DisplayName("get() returns correct user.")
    void get() {
        User user = jdbcUserRepository.get(TEST_USER_ID);

        assertThat(user)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(getTestUser());
    }

    @Test
    @DisplayName("getAll() returns correct number of elements.")
    void getAll() {
        assertThat(jdbcUserRepository.getAll())
                .hasSize((int) COUNT_OF_ELEMENTS);
    }

    @Test
    @DisplayName("create() creates equivalent user on all fields except id.")
    void create() {
        User createdUser = jdbcUserRepository.create(getTestNewUser());

        assertThat(jdbcUserRepository.get(createdUser.getId()))
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(getTestNewUser());
    }

    @Test
    @DisplayName("update() updates user properly in database.")
    void update() {
        User updateUser = getTestNewUser().toBuilder()
                .id(TEST_USER_ID)
                .build();

        assertThat(jdbcUserRepository.get(updateUser.getId()))
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isNotEqualTo(updateUser);

        jdbcUserRepository.update(updateUser);

        assertThat(jdbcUserRepository.get(updateUser.getId()))
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(getTestNewUser());
    }

    @Test
    @DisplayName("addFriend() adds friends to correct user.")
    void addFriend() {
        jdbcUserRepository.addFriend(TEST_USER_ID, COUNT_OF_ELEMENTS);

        assertThat(jdbcUserRepository.getFriends(TEST_USER_ID))
                .singleElement()
                .isEqualToComparingOnlyGivenFields(User.builder().id(COUNT_OF_ELEMENTS), "id");

        assertThat(jdbcUserRepository.getFriends(COUNT_OF_ELEMENTS)).hasSize(0);
    }

    @Test
    @DisplayName("getFriends() returns actual friend list.")
    void getFriends() {
        jdbcUserRepository.addFriend(TEST_USER_ID, COUNT_OF_ELEMENTS);

        assertThat(jdbcUserRepository.getFriends(TEST_USER_ID))
                .singleElement()
                .isEqualToComparingOnlyGivenFields(User.builder().id(COUNT_OF_ELEMENTS), "id");
    }

    @Test
    @DisplayName("getMutualFriends() returns only mutual friends of two users.")
    void getMutualFriends() {
        User friend = jdbcUserRepository.create(getTestNewUser());

        jdbcUserRepository.addFriend(TEST_USER_ID, friend.getId());
        jdbcUserRepository.addFriend(COUNT_OF_ELEMENTS, friend.getId());

        assertThat(jdbcUserRepository.getMutualFriends(TEST_USER_ID, COUNT_OF_ELEMENTS))
                .singleElement()
                .isEqualTo(friend);
    }

    @Test
    @DisplayName("removeFriend() actually removes the friend in database.")
    void removeFriend() {
        jdbcUserRepository.addFriend(TEST_USER_ID, COUNT_OF_ELEMENTS);
        assertThat(jdbcUserRepository.getFriends(TEST_USER_ID)).hasSize(1);
        jdbcUserRepository.removeFriend(TEST_USER_ID, COUNT_OF_ELEMENTS);
        assertThat(jdbcUserRepository.getFriends(TEST_USER_ID)).hasSize(0);
    }

    @Test
    @DisplayName("delete() actually deletes the user in database.")
    void delete() {
        assertThat(jdbcUserRepository.checkUserExists(TEST_USER_ID)).isTrue();
        jdbcUserRepository.delete(TEST_USER_ID);
        assertThat(jdbcUserRepository.checkUserExists(TEST_USER_ID)).isFalse();
    }
}