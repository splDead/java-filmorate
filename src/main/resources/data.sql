-- Заполнение справочника рейтингов MPA
INSERT INTO mpa_ratings (name, description) VALUES
('G', 'У фильма нет возрастных ограничений'),
('PG', 'Детям рекомендуется смотреть фильм с родителями'),
('PG-13', 'Детям до 13 лет просмотр не желателен'),
('R', 'Лицам до 17 лет просматривать фильм можно только в присутствии взрослого'),
('NC-17', 'Лицам до 18 лет просмотр запрещен')
ON CONFLICT (name) DO NOTHING;

-- Заполнение справочника жанров
INSERT INTO genres (name) VALUES
('Комедия'),
('Драма'),
('Мультфильм'),
('Триллер'),
('Документальный'),
('Боевик')
ON CONFLICT (name) DO NOTHING;

-- Заполнение справочника статусов дружбы
INSERT INTO friendship_statuses (name) VALUES
('UNCONFIRMED'),
('CONFIRMED')
ON CONFLICT (name) DO NOTHING;
