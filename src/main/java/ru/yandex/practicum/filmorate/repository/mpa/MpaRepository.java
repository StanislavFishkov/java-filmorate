package ru.yandex.practicum.filmorate.repository.mpa;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

public interface MpaRepository {
    boolean checkMpaExists(Integer mpaId);

    Mpa get(Integer mpaId);

    Collection<Mpa> getAll();
}