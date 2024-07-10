package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Film create(Film film);

    Film get(Long id);

    Collection<Film> getAll();

    Film update(Film film);

    void delete(Long id);
}