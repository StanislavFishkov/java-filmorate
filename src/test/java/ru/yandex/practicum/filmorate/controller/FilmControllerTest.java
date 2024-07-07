package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    FilmController filmController;
    Film verifiedFilm;

    @BeforeEach
    void setUp() {
        filmController = new FilmController(new FilmService(new InMemoryFilmStorage()));
        verifiedFilm = Film.builder()
                .id(1)
                .name("Name")
                .description("Description")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(100)
                .build();
    }

    @Test
    void createFilm() {
        filmController.create(verifiedFilm);
        assertEquals(1, filmController.getAll().size());
        assertEquals(verifiedFilm.getName(), filmController.getAll().stream().findFirst().get().getName());
    }

    @Test
    void updateFilm() {
        filmController.create(verifiedFilm);
        filmController.update(verifiedFilm.toBuilder().name("Different").build());

        assertEquals(1, filmController.getAll().size());
        assertNotEquals(verifiedFilm.getName(), filmController.getAll().stream().findFirst().get().getName());
    }

    @Test
    void nameNotEmpty() {
        Film film = verifiedFilm.toBuilder().name(null).build();
        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void descriptionMaxLength200() {
        Film film = verifiedFilm.toBuilder()
                .description("DescriptionDescriptionDescriptionDescriptionDescriptionDescriptionDescription" +
                        "DescriptionDescriptionDescriptionDescriptionDescriptionDescriptionDescription" +
                        "DescriptionDescriptionDescriptionDescriptionDes")
                .build();
        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void releaseDateNotBefore() {
        Film film = verifiedFilm.toBuilder().releaseDate(LocalDate.of(1895, 12, 27)).build();
        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void durationOverZero() {
        Film film = verifiedFilm.toBuilder().duration(0).build();
        assertThrows(ValidationException.class, () -> filmController.create(film));
    }
}