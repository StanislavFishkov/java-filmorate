package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.genre.GenreRepository;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreRepository genreRepository;

    public Collection<Genre> getAll() {
        log.trace("List of all genres is requested");
        return genreRepository.getAll();
    }

    public Genre get(int genreId) {
        if (!genreRepository.checkGenreExists(genreId)) {
            throw new NotFoundException("Genre can't be found on getting by id: " + genreId);
        }

        log.trace("Genre is requested by id: {}", genreId);
        return genreRepository.get(genreId);
    }
}