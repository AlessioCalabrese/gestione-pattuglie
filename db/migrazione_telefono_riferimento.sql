-- Migrazione per database già esistenti: telefono di riferimento dell'obiettivo (avviso WhatsApp dopo il flag).
-- Le installazioni nuove hanno già la colonna in schema.sql.
USE gestione_pattuglie;
GO

IF COL_LENGTH('obiettivo', 'telefono_riferimento') IS NULL
BEGIN
    ALTER TABLE obiettivo ADD telefono_riferimento VARCHAR(20) NULL;
END
GO
