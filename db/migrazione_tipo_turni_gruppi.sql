-- Migrazione per database già esistenti:
--   1) tipo obiettivo (Datix / Ispezione / Bigliettazione)
--   2) turni predefiniti Mattina/Pomeriggio/Notte, configurabili dall'amministratore
--   3) accorpamento di pattuglie configurato dall'amministratore
USE gestione_pattuglie;
GO

-- 1) Tipo obiettivo -------------------------------------------------------
IF COL_LENGTH('obiettivo', 'tipo_obiettivo') IS NULL
BEGIN
    ALTER TABLE obiettivo ADD tipo_obiettivo VARCHAR(20) NOT NULL DEFAULT 'ISPEZIONE'
        CONSTRAINT chk_obiettivo_tipo CHECK (tipo_obiettivo IN ('DATIX','ISPEZIONE', 'BIGLIETTAZIONE'));
END
GO

-- 2) Turni predefiniti ------------------------------------------------------
IF OBJECT_ID('configurazione_turni', 'U') IS NULL
BEGIN
    CREATE TABLE configurazione_turni (
        id                  BIGINT PRIMARY KEY,
        mattina_inizio      TIME NOT NULL DEFAULT '06:00',
        mattina_fine        TIME NOT NULL DEFAULT '14:00',
        pomeriggio_inizio   TIME NOT NULL DEFAULT '14:00',
        pomeriggio_fine     TIME NOT NULL DEFAULT '22:00',
        notte_inizio        TIME NOT NULL DEFAULT '22:00',
        notte_fine          TIME NOT NULL DEFAULT '06:00'
    );
    INSERT INTO configurazione_turni (id) VALUES (1);
END
GO

-- 3) Accorpamento pattuglie -------------------------------------------------
IF OBJECT_ID('gruppo_pattuglie', 'U') IS NULL
BEGIN
    CREATE TABLE gruppo_pattuglie (
        id              BIGINT IDENTITY(1,1) PRIMARY KEY,
        nome            VARCHAR(150) NOT NULL,
        data_creazione  DATETIME NOT NULL DEFAULT GETDATE()
    );
END
GO

IF COL_LENGTH('pattuglia', 'gruppo_id') IS NULL
BEGIN
    ALTER TABLE pattuglia ADD gruppo_id BIGINT NULL
        CONSTRAINT fk_pattuglia_gruppo REFERENCES gruppo_pattuglie(id) ON DELETE SET NULL;
END
GO
