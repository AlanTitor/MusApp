ALTER TABLE musics
ADD COLUMN date_changed TIMESTAMP DEFAULT (now()) AFTER date_created,
ADD COLUMN extension VARCHAR(10) NOT NULL AFTER path

