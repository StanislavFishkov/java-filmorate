package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        checkFilmConstraints(film, false);

        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Film is created: {}", film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        if (newFilm.getId() == null) {
            throw new ValidationException("Film id can't be null on update: " + newFilm, log);
        }

        if (films.containsKey(newFilm.getId())) {
            checkFilmConstraints(newFilm, true);

            films.put(newFilm.getId(), newFilm);
            log.info("Film is updated: {}", newFilm);
            return newFilm;
        }
        throw new ValidationException("Film can't be found by id: " + newFilm, log);
    }

    private void checkFilmConstraints(Film film, boolean isUpdate) {
         if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Film name can't be empty: " + film, log);
        }

        if (film.getDescription() == null || film.getDescription().length() > 200) {
            throw new ValidationException("Film description can't be longer than 200: " + film, log);
        }

        if (film.getReleaseDate() == null || !film.getReleaseDate().isAfter(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Film release date can't be earlier than 1895-12-28: " + film, log);
        }

        if (film.getDuration() == null || film.getDuration() <= 0) {
            throw new ValidationException("Film duration can't be zero or less: " + film, log);
        }
    }

    private int getNextId() {
        return films.keySet()
                .stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0) + 1;
    }
}
