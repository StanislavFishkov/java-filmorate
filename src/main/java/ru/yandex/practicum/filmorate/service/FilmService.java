package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;

    public final Film create(Film film) {
        checkFilmConstraints(film);

        Film createdFilm = filmStorage.create(film);
        log.info("Film is created: {}", createdFilm);
        return createdFilm;
    }

    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film update(Film newFilm) {
        if (newFilm.getId() == null) {
            throw new ValidationException("Film id can't be null on update: " + newFilm);
        }

        if (filmStorage.get(newFilm.getId()) != null) {
            checkFilmConstraints(newFilm);

            Film updatedFilm = filmStorage.update(newFilm);
            log.info("Film is updated: {}", updatedFilm);
            return updatedFilm;
        }
        throw new NotFoundException("Film can't be found by id: " + newFilm);
    }

    private void checkFilmConstraints(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Film name can't be empty: " + film);
        }

        if (film.getDescription() == null || film.getDescription().length() > 200) {
            throw new ValidationException("Film description can't be longer than 200: " + film);
        }

        if (film.getReleaseDate() == null || !film.getReleaseDate().isAfter(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Film release date can't be earlier than 1895-12-28: " + film);
        }

        if (film.getDuration() == null || film.getDuration() <= 0) {
            throw new ValidationException("Film duration can't be zero or less: " + film);
        }
    }
}