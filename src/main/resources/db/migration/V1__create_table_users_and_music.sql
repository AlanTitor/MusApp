CREATE TABLE users(
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL,
    password VARCHAR(200) NOT NULL,
    role VARCHAR(10) NOT NULL,
    date_created DATE DEFAULT (curdate()) NOT NULL,
    PRIMARY KEY(id)
);

CREATE TABLE musics(
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    genre VARCHAR(50) NOT NULL,
    file_name VARCHAR(50) NOT NULL,
    path TEXT NOT NULL,
    file_size DECIMAL(10,1) NOT NULL,
    mime_type VARCHAR(20) NOT NULL,
    date_created DATE DEFAULT (curdate()) NOT NULL,
    author_id BIGINT NOT NULL,
    PRIMARY KEY(id),
    FOREIGN KEY(author_id) REFERENCES users(id)
);