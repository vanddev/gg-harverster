
-- ===============================
--        MAIN ENTITIES
-- ===============================

CREATE TABLE platform (
    id INTEGER PRIMARY KEY,
    name TEXT NOT NULL,
    abbreviation TEXT,
    alternative_name TEXT,
    logo TEXT
);

CREATE TABLE franchise (
    id INTEGER PRIMARY KEY,
    name TEXT NOT NULL
);

CREATE TABLE theme (
    id INTEGER PRIMARY KEY,
    name TEXT NOT NULL,
    slug TEXT
);

CREATE TABLE genre (
    id INTEGER PRIMARY KEY,
    name TEXT NOT NULL,
    slug TEXT
);

CREATE TABLE gamestatus (
    id INTEGER PRIMARY KEY,
    status TEXT NOT NULL
);

CREATE TABLE gamemode (
    id INTEGER PRIMARY KEY,
    name TEXT NOT NULL
);

CREATE TABLE gametype (
    id INTEGER PRIMARY KEY,
    type TEXT NOT NULL
);

CREATE TABLE keyword (
    id INTEGER PRIMARY KEY,
    name TEXT NOT NULL
);

CREATE TABLE player_perspective (
    id INTEGER PRIMARY KEY,
    name TEXT NOT NULL
);

CREATE TABLE age_rating (
    id INTEGER PRIMARY KEY,
    organization TEXT,
    rating TEXT,
    content_descriptions TEXT
);

CREATE TABLE game (
    id INTEGER PRIMARY KEY,
    name TEXT NOT NULL,
    gametype_id INTEGER,
    gamestatus_id INTEGER,
    igdb_url TEXT,
    cover TEXT,
    first_release_date INTEGER,
    players_rating REAL,
    players_rating_count INTEGER,
    critics_rating REAL,
    critics_rating_count INTEGER,
    rating REAL,
    rating_count INTEGER,
    rating_ration REAL,
    FOREIGN KEY (gametype_id) REFERENCES gametype(id),
    FOREIGN KEY (gamestatus_id) REFERENCES gamestatus(id)
);

CREATE TABLE release (
    id INTEGER PRIMARY KEY,
    status TEXT,
    datetime INTEGER,
    region TEXT,
    platform_id INTEGER NOT NULL,
    game_id INTEGER NOT NULL,
    FOREIGN KEY (game_id) REFERENCES game(id),
    FOREIGN KEY (platform_id) REFERENCES platform(id)
);
-- ===============================
--       ASSOCIATION TABLES
-- ===============================

CREATE TABLE game_franchise (
    game_id INTEGER NOT NULL,
    franchise_id INTEGER NOT NULL,
    PRIMARY KEY (game_id, franchise_id),
    FOREIGN KEY (game_id) REFERENCES game(id),
    FOREIGN KEY (franchise_id) REFERENCES franchise(id)
);

CREATE TABLE game_platform (
    game_id INTEGER NOT NULL,
    platform_id INTEGER NOT NULL,
    PRIMARY KEY (game_id, platform_id),
    FOREIGN KEY (game_id) REFERENCES game(id),
    FOREIGN KEY (platform_id) REFERENCES platform(id)
);

CREATE TABLE game_genre (
    game_id INTEGER NOT NULL,
    genre_id INTEGER NOT NULL,
    PRIMARY KEY (game_id, genre_id),
    FOREIGN KEY (game_id) REFERENCES game(id),
    FOREIGN KEY (genre_id) REFERENCES genre(id)
);

CREATE TABLE game_theme (
    game_id INTEGER NOT NULL,
    theme_id INTEGER NOT NULL,
    PRIMARY KEY (game_id, theme_id),
    FOREIGN KEY (game_id) REFERENCES game(id),
    FOREIGN KEY (theme_id) REFERENCES theme(id)
);

CREATE TABLE game_gamemode (
    game_id INTEGER NOT NULL,
    gamemode_id INTEGER NOT NULL,
    PRIMARY KEY (game_id, gamemode_id),
    FOREIGN KEY (game_id) REFERENCES game(id),
    FOREIGN KEY (gamemode_id) REFERENCES gamemode(id)
);

CREATE TABLE game_player_perspective (
    game_id INTEGER NOT NULL,
    player_perspective_id INTEGER NOT NULL,
    PRIMARY KEY (game_id, player_perspective_id),
    FOREIGN KEY (game_id) REFERENCES game(id),
    FOREIGN KEY (player_perspective_id) REFERENCES player_perspective(id)
);

CREATE TABLE game_age_rating (
    game_id INTEGER NOT NULL,
    age_rating_id INTEGER NOT NULL,
    PRIMARY KEY (game_id, age_rating_id),
    FOREIGN KEY (game_id) REFERENCES game(id),
    FOREIGN KEY (age_rating_id) REFERENCES age_rating(id)
);

CREATE TABLE game_keyword (
    game_id INTEGER NOT NULL,
    keyword_id INTEGER NOT NULL,
    PRIMARY KEY (game_id, keyword_id),
    FOREIGN KEY (game_id) REFERENCES game(id),
    FOREIGN KEY (keyword_id) REFERENCES keyword(id)
);

-- ===============================
--          INDEXES
-- ===============================

-- Release performance
CREATE INDEX idx_release_game_id ON release(game_id);
CREATE INDEX idx_release_platform_id ON release(platform_id);
CREATE INDEX idx_release_datetime ON release(datetime);

-- Game performance
CREATE INDEX idx_game_first_release_date ON game(first_release_date);
CREATE INDEX idx_game_gametype_id ON game(gametype_id);
CREATE INDEX idx_game_gamestatus_id ON game(gamestatus_id);

-- Association tables indexes
CREATE INDEX idx_game_franchise_game ON game_franchise(game_id);
CREATE INDEX idx_game_franchise_franchise ON game_franchise(franchise_id);

CREATE INDEX idx_game_platform_game ON game_platform(game_id);
CREATE INDEX idx_game_platform_platform ON game_platform(platform_id);

CREATE INDEX idx_game_genre_game ON game_genre(game_id);
CREATE INDEX idx_game_genre_genre ON game_genre(genre_id);

CREATE INDEX idx_game_theme_game ON game_theme(game_id);
CREATE INDEX idx_game_theme_theme ON game_theme(theme_id);

CREATE INDEX idx_game_gamemode_game ON game_gamemode(game_id);
CREATE INDEX idx_game_gamemode_gamemode ON game_gamemode(gamemode_id);

CREATE INDEX idx_game_pp_game ON game_player_perspective(game_id);
CREATE INDEX idx_game_pp_pp ON game_player_perspective(player_perspective_id);

CREATE INDEX idx_game_age_rating_game ON game_age_rating(game_id);
CREATE INDEX idx_game_age_rating_rating ON game_age_rating(age_rating_id);

CREATE INDEX idx_game_keyword_game ON game_keyword(game_id);
CREATE INDEX idx_game_keyword_keyword ON game_keyword(keyword_id);