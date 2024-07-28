package ru.yandex.practicum.filmorate.repository.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmRepository implements FilmRepository {
    private final Map<Long, Film> films = new HashMap<>();
    private Long idCounter = 0L;

    @Override
    public boolean checkFilmExists(Long filmId) {
        return films.containsKey(filmId);
    }

    @Override
    public Film create(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film get(Long id) {
        return films.get(id);
    }

    @Override
    public Collection<Film> getAll() {
        return films.values();
    }

    @Override
    public Film update(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void delete(Long id) {
        films.remove(id);
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        films.get(filmId).getLikes().add(userId);
    }

    @Override
    public Collection<Film> getMostPopular(long count) {
        return films.values().stream()
                .sorted(new Comparator<Film>() {
                    @Override
                    public int compare(Film o1, Film o2) {
                        return o1.getLikes().size() - o2.getLikes().size();
                    }
                }.reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        films.get(filmId).getLikes().remove(userId);
    }

    private long getNextId() {
        return ++idCounter;
    }
}
