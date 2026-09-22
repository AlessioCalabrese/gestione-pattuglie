-- ============================================================
-- Schema DB - Gestione Pattuglie e Obiettivi
-- Istituto di Vigilanza - Versione Microsoft SQL Server
-- ============================================================

-- Creazione del database (in SQL Server IF NOT EXISTS si gestisce con una verifica preventiva)
IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'gestione_pattuglie')
BEGIN
    CREATE DATABASE gestione_pattuglie;
END
GO

USE gestione_pattuglie;
GO

-- ------------------------------------------------------------
-- Tabella UTENTE
-- ------------------------------------------------------------
CREATE TABLE utente (
                        id              BIGINT IDENTITY(1,1) PRIMARY KEY, -- Convertito AUTO_INCREMENT in IDENTITY
                        username        VARCHAR(50)  NOT NULL UNIQUE,
                        password        VARCHAR(255) NOT NULL,
                        nome            VARCHAR(100) NOT NULL,
                        cognome         VARCHAR(100) NOT NULL,
                        ruolo           VARCHAR(20)  NOT NULL DEFAULT 'PATTUGLIA', -- Convertito ENUM in VARCHAR + CHECK
                        nfc_tag_id      VARCHAR(100) UNIQUE,
                        abilitato       BIT NOT NULL DEFAULT 1, -- Convertito BOOLEAN in BIT
                        data_creazione  DATETIME NOT NULL DEFAULT GETDATE(), -- Convertito CURRENT_TIMESTAMP in GETDATE()

                        CONSTRAINT chk_utente_ruolo CHECK (ruolo IN ('ADMIN', 'PATTUGLIA'))
);

-- ------------------------------------------------------------
-- Tabella GRUPPO_PATTUGLIE (accorpamento configurato dall'amministratore: le pattuglie di uno stesso
-- gruppo vedono e possono flaggare anche gli obiettivi delle altre pattuglie del gruppo)
-- ------------------------------------------------------------
CREATE TABLE gruppo_pattuglie (
                                  id              BIGINT IDENTITY(1,1) PRIMARY KEY,
                                  nome            VARCHAR(150) NOT NULL,
                                  data_creazione  DATETIME NOT NULL DEFAULT GETDATE()
);

-- ------------------------------------------------------------
-- Tabella PATTUGLIA
-- ------------------------------------------------------------
CREATE TABLE pattuglia (
                           id                      BIGINT IDENTITY(1,1) PRIMARY KEY,
                           nome                    VARCHAR(100) NOT NULL,
                           descrizione             VARCHAR(255),
                           tipo_carburante         VARCHAR(20) NOT NULL DEFAULT 'BENZINA', -- Convertito ENUM in VARCHAR + CHECK
                           attiva                  BIT NOT NULL DEFAULT 1,
                           gruppo_id               BIGINT, -- NULL = nessun accorpamento con altre pattuglie
                           data_creazione          DATETIME NOT NULL DEFAULT GETDATE(),

                           CONSTRAINT chk_pattuglia_carburante CHECK (tipo_carburante IN ('BENZINA', 'GASOLIO')),
                           FOREIGN KEY (gruppo_id) REFERENCES gruppo_pattuglie(id) ON DELETE SET NULL
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
                           id                      BIGINT IDENTITY(1,1) PRIMARY KEY,
                           pattuglia_id            BIGINT NOT NULL,
                           nome                    VARCHAR(150) NOT NULL,
                           via                     VARCHAR(150) NOT NULL,
                           comune                  VARCHAR(100) NOT NULL,
                           latitudine              DECIMAL(10, 7) NOT NULL,
                           longitudine             DECIMAL(10, 7) NOT NULL,
                           priorita                BIT NOT NULL DEFAULT 0,
                           tipo_obiettivo          VARCHAR(20) NOT NULL DEFAULT 'ISPEZIONE', -- Convertito ENUM in VARCHAR + CHECK
                           telefono_riferimento    VARCHAR(20), -- cellulare in formato internazionale, sole cifre (es. 393331234567)
                           ordine_visita           INT,
                           attivo                  BIT NOT NULL DEFAULT 1,
                           data_creazione          DATETIME NOT NULL DEFAULT GETDATE(),
                           FOREIGN KEY (pattuglia_id) REFERENCES pattuglia(id) ON DELETE CASCADE,
                           CONSTRAINT chk_obiettivo_tipo CHECK (tipo_obiettivo IN ('DATIX','ISPEZIONE', 'BIGLIETTAZIONE'))
);

-- Fasce orarie di servizio: un obiettivo può averne più di una nello stesso giorno (es. mattina e sera)
-- e fasce diverse in giorni diversi. Vuota = mai visibile alle pattuglie (va configurata esplicitamente).
CREATE TABLE obiettivo_fascia_oraria (
                                         obiettivo_id            BIGINT NOT NULL,
                                         giorno                  VARCHAR(15) NOT NULL, -- Convertito ENUM in VARCHAR + CHECK
                                         ora_inizio              TIME, -- NULL = nessun vincolo dall'inizio della giornata
                                         ora_fine                TIME, -- NULL = nessun vincolo fino alla fine della giornata
                                         ripetizioni_richieste   INT NOT NULL DEFAULT 1, -- quante volte va flaggato in questa fascia
                                         FOREIGN KEY (obiettivo_id) REFERENCES obiettivo(id) ON DELETE CASCADE,

                                         CONSTRAINT chk_obiettivo_fascia_giorno CHECK (giorno IN ('LUNEDI','MARTEDI','MERCOLEDI','GIOVEDI','VENERDI','SABATO','DOMENICA'))
);

CREATE INDEX idx_obiettivo_fascia_oraria_obiettivo ON obiettivo_fascia_oraria(obiettivo_id);

-- ------------------------------------------------------------
-- Tabella CONFIGURAZIONE_TURNI (riga singola: orari di default dei turni Mattina/Pomeriggio/Notte,
-- usati come proposta rapida per le fasce orarie degli obiettivi, modificabili dall'amministratore)
-- ------------------------------------------------------------
CREATE TABLE configurazione_turni (
                                       id                  BIGINT PRIMARY KEY,
                                       mattina_inizio      TIME NOT NULL DEFAULT '06:00',
                                       mattina_fine        TIME NOT NULL DEFAULT '14:00',
                                       pomeriggio_inizio   TIME NOT NULL DEFAULT '14:00',
                                       pomeriggio_fine     TIME NOT NULL DEFAULT '22:00',
                                       notte_inizio        TIME NOT NULL DEFAULT '22:00',
                                       notte_fine          TIME NOT NULL DEFAULT '06:00' -- del giorno successivo a notte_inizio
);

INSERT INTO configurazione_turni (id) VALUES (1);

-- ------------------------------------------------------------
-- Tabella OBIETTIVO_FLAG  (append-only)
-- ------------------------------------------------------------
CREATE TABLE obiettivo_flag (
                                id                  BIGINT IDENTITY(1,1) PRIMARY KEY,
                                obiettivo_id        BIGINT NOT NULL,
                                utente_id           BIGINT NOT NULL,
                                pattuglia_id        BIGINT NOT NULL,
                                data_ora            DATETIME NOT NULL DEFAULT GETDATE(),
                                latitudine_reg      DECIMAL(10, 7) NOT NULL,
                                longitudine_reg     DECIMAL(10, 7) NOT NULL,
                                precisione_metri    DECIMAL(6, 2),
                                note                VARCHAR(255),
                                FOREIGN KEY (obiettivo_id) REFERENCES obiettivo(id) ON DELETE CASCADE,
                                FOREIGN KEY (utente_id) REFERENCES utente(id),
                                FOREIGN KEY (pattuglia_id) REFERENCES pattuglia(id)
);
GO

CREATE INDEX idx_obiettivo_flag_obiettivo ON obiettivo_flag(obiettivo_id);
CREATE INDEX idx_obiettivo_flag_data ON obiettivo_flag(data_ora);
GO

-- ------------------------------------------------------------
-- Tabella LOG_SISTEMA
-- ------------------------------------------------------------
CREATE TABLE log_sistema (
                             id              BIGINT IDENTITY(1,1) PRIMARY KEY,
                             utente_id       BIGINT,
                             tipo_evento     VARCHAR(50) NOT NULL,
                             descrizione     VARCHAR(500),
                             data_ora        DATETIME NOT NULL DEFAULT GETDATE(),
                             indirizzo_ip    VARCHAR(45),
                             FOREIGN KEY (utente_id) REFERENCES utente(id) ON DELETE SET NULL
);
GO

CREATE INDEX idx_log_sistema_data ON log_sistema(data_ora);
CREATE INDEX idx_log_sistema_tipo ON log_sistema(tipo_evento);
GO

-- ------------------------------------------------------------
-- Utente amministratore di default
-- ------------------------------------------------------------
INSERT INTO utente (username, password, nome, cognome, ruolo)
VALUES ('admin', '$2b$12$YElgNkNDdLJBxRmHNeQG5.jOZe/dGP0VBjpKBaA2O16Vs5AMDYjpe', 'Admin', 'Sistema', 'ADMIN'),
(N'a.calabrese', N'$2a$10$M62o//CJYgJfNWDAQJ/Z/u/XD.BlVK7GkmBLA3N6LuK98vGvqXrP2', N'Alessio', N'Calabrese', N'PATTUGLIA', N'Ale123', 1, '2026-09-21 16:16:11.723');
GO
