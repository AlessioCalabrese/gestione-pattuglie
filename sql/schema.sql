-- ============================================================
-- Schema DB - Gestione Pattuglie e Obiettivi
-- Istituto di Vigilanza
-- ============================================================

CREATE DATABASE IF NOT EXISTS gestione_pattuglie
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE gestione_pattuglie;

-- ------------------------------------------------------------
-- Tabella UTENTE
-- ------------------------------------------------------------
CREATE TABLE utente (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    username        VARCHAR(50)  NOT NULL UNIQUE,
    password        VARCHAR(255) NOT NULL,
    nome            VARCHAR(100) NOT NULL,
    cognome         VARCHAR(100) NOT NULL,
    ruolo           ENUM('ADMIN', 'PATTUGLIA') NOT NULL DEFAULT 'PATTUGLIA',
    nfc_tag_id      VARCHAR(100) UNIQUE,        -- identificativo tag NFC, alternativo alla password
    abilitato       BOOLEAN NOT NULL DEFAULT TRUE,
    data_creazione  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ------------------------------------------------------------
-- Tabella PATTUGLIA
-- ------------------------------------------------------------
CREATE TABLE pattuglia (
    id                      BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome                    VARCHAR(100) NOT NULL,
    descrizione             VARCHAR(255),
    veicolo_targa           VARCHAR(20),
    tipo_carburante         ENUM('BENZINA', 'GASOLIO') NOT NULL DEFAULT 'BENZINA',
    consumo_medio_l_100km   DECIMAL(5, 2) DEFAULT 10.00,  -- consumo medio veicolo, usato per stimare il costo carburante
    attiva                  BOOLEAN NOT NULL DEFAULT TRUE,
    data_creazione          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Relazione N:N tra utenti abilitati e pattuglie che possono selezionare
CREATE TABLE utente_pattuglia (
    utente_id       BIGINT NOT NULL,
    pattuglia_id    BIGINT NOT NULL,
    PRIMARY KEY (utente_id, pattuglia_id),
    FOREIGN KEY (utente_id) REFERENCES utente(id) ON DELETE CASCADE,
    FOREIGN KEY (pattuglia_id) REFERENCES pattuglia(id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- Tabella OBIETTIVO
-- ------------------------------------------------------------
CREATE TABLE obiettivo (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    pattuglia_id    BIGINT NOT NULL,
    nome            VARCHAR(150) NOT NULL,
    indirizzo       VARCHAR(255) NOT NULL,
    latitudine      DECIMAL(10, 7) NOT NULL,
    longitudine     DECIMAL(10, 7) NOT NULL,
    ordine_visita   INT,                         -- ordine calcolato dall'ottimizzatore di rotta
    attivo          BOOLEAN NOT NULL DEFAULT TRUE,
    data_creazione  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (pattuglia_id) REFERENCES pattuglia(id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- Tabella OBIETTIVO_FLAG  (append-only: uno storico per ogni passaggio)
-- ------------------------------------------------------------
CREATE TABLE obiettivo_flag (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    obiettivo_id        BIGINT NOT NULL,
    utente_id           BIGINT NOT NULL,
    pattuglia_id        BIGINT NOT NULL,
    data_ora            DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    latitudine_reg      DECIMAL(10, 7) NOT NULL,   -- coordinate al momento del flag
    longitudine_reg     DECIMAL(10, 7) NOT NULL,
    precisione_metri    DECIMAL(6, 2),             -- accuracy del GPS, opzionale ma utile
    note                VARCHAR(255),
    FOREIGN KEY (obiettivo_id) REFERENCES obiettivo(id) ON DELETE CASCADE,
    FOREIGN KEY (utente_id) REFERENCES utente(id),
    FOREIGN KEY (pattuglia_id) REFERENCES pattuglia(id)
);

CREATE INDEX idx_obiettivo_flag_obiettivo ON obiettivo_flag(obiettivo_id);
CREATE INDEX idx_obiettivo_flag_data ON obiettivo_flag(data_ora);

-- ------------------------------------------------------------
-- Tabella LOG_SISTEMA
-- ------------------------------------------------------------
CREATE TABLE log_sistema (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    utente_id       BIGINT,
    tipo_evento     VARCHAR(50) NOT NULL,   -- es. LOGIN, LOGOUT, FLAG_OBIETTIVO, MODIFICA_ADMIN
    descrizione     VARCHAR(500),
    data_ora        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    indirizzo_ip    VARCHAR(45),
    FOREIGN KEY (utente_id) REFERENCES utente(id) ON DELETE SET NULL
);

CREATE INDEX idx_log_sistema_data ON log_sistema(data_ora);
CREATE INDEX idx_log_sistema_tipo ON log_sistema(tipo_evento);

-- ------------------------------------------------------------
-- Utente amministratore di default (password da cambiare al primo accesso)
-- ------------------------------------------------------------
INSERT INTO utente (username, password, nome, cognome, ruolo)
VALUES ('admin', '$2a$10$CHANGE_ME_BCRYPT_HASH', 'Admin', 'Sistema', 'ADMIN');
