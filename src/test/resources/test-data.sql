MERGE INTO "mpa_rating"
("mpa_rating_id", "name")
VALUES
(1, 'G'),
(2, 'PG'),
(3, 'PG-13'),
(4, 'R'),
(5, 'NC-17');

MERGE INTO "genre"
("genre_id", "name")
VALUES
(1, 'Комедия'),
(2, 'Драма'),
(3, 'Мультфильм'),
(4, 'Триллер'),
(5, 'Документальный'),
(6, 'Боевик');

INSERT INTO "user"
("email", "login", "name", "birthday")
VALUES
('email@email.com', 'user', 'test', '2000-03-22'),
('ivanov@gmail.com', 'Ivanov', 'Ivan', '1984-12-26');

INSERT INTO "film"
("name", "description", "release_date", "duration", "mpa_rating_id")
VALUES
('test', 'test', '1960-03-21', 100, 1),
('film2', 'greatest film', '2024-07-27', 500, 5),
('film3', 'worst film', '2000-01-01', 333, 3);

INSERT INTO "film_genre"
("film_id", "genre_id")
VALUES
(cast(1 as bigint), 1),
(cast(1as bigint), 2);

INSERT INTO "film_genre"
("film_id", "genre_id")
VALUES
(cast(3 as bigint), 6);