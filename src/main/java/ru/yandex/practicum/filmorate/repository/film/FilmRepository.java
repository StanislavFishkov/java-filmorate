package ru.yandex.practicum.filmorate.repository.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmRepository {
    Film create(Film film);

    boolean checkFilmExists(Long filmId);

    Film get(Long filmId);

    Collection<Film> getAll();

    Film update(Film film);

    void delete(Long filmId);

    void addLike(Long filmId, Long userId);

    Collection<Film> getMostPopular(long count);

    void removeLike(Long filmId, Long userId);
}