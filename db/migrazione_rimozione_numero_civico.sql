-- Migrazione per database già esistenti: rimozione del numero civico dagli obiettivi.
-- ATTENZIONE: elimina definitivamente la colonna obiettivo.numero_civico e i suoi valori.
-- Latitudine e longitudine già salvate non cambiano. Le installazioni nuove non hanno la colonna (vedi schema.sql).
-- Eseguire PRIMA di avviare la nuova versione dell'applicazione (la colonna era NOT NULL: senza
-- questa migrazione i nuovi obiettivi non si potrebbero salvare).
USE gestione_pattuglie;
GO

IF COL_LENGTH('obiettivo', 'numero_civico') IS NOT NULL
BEGIN
    ALTER TABLE obiettivo DROP COLUMN numero_civico;
END
GO
