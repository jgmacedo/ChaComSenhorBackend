CREATE TABLE bible_verses
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    book      VARCHAR(255),
    chapter   INTEGER,
    verse     INTEGER,
    text      VARCHAR(1000),
    reference VARCHAR(100),
    CONSTRAINT pk_bible_verses PRIMARY KEY (id)
);