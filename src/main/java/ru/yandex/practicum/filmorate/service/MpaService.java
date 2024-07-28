package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.mpa.MpaRepository;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaRepository mpaRepository;

    public Collection<Mpa> getAll() {
        log.trace("List of all mpa ratings is requested");
        return mpaRepository.getAll();
    }

    public Mpa get(int mpaId) {
        if (!mpaRepository.checkMpaExists(mpaId)) {
            throw new NotFoundException("Mpa rating can't be found on getting by id: " + mpaId);
        }

        log.trace("Mpa rating is requested by id: {}", mpaId);
        return mpaRepository.get(mpaId);
    }
}