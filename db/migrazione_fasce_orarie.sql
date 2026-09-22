-- Migrazione per database già esistenti: fasce orarie multiple per obiettivo (più fasce nello stesso
-- giorno, fasce diverse in giorni diversi), al posto dell'unica fascia oraria + giorni attivi + ripetizioni
-- giornaliere validi per tutti i giorni.
--
-- Per ogni obiettivo, ogni giorno già attivo diventa una fascia su quel giorno, con l'orario e le
-- ripetizioni già configurati sull'obiettivo (nessun dato di pianificazione viene perso). Da qui in poi,
-- orario e ripetizioni si impostano per singola fascia (vedi la pagina admin di gestione obiettivi).
USE gestione_pattuglie;
GO

IF OBJECT_ID('obiettivo_fascia_oraria', 'U') IS NULL
BEGIN
    CREATE TABLE obiettivo_fascia_oraria (
        obiettivo_id            BIGINT NOT NULL,
        giorno                  VARCHAR(15) NOT NULL,
        ora_inizio              TIME,
        ora_fine                TIME,
        ripetizioni_richieste   INT NOT NULL DEFAULT 1,
        FOREIGN KEY (obiettivo_id) REFERENCES obiettivo(id) ON DELETE CASCADE,
        CONSTRAINT chk_obiettivo_fascia_giorno CHECK (giorno IN ('LUNEDI','MARTEDI','MERCOLEDI','GIOVEDI','VENERDI','SABATO','DOMENICA'))
    );
    CREATE INDEX idx_obiettivo_fascia_oraria_obiettivo ON obiettivo_fascia_oraria(obiettivo_id);

    IF OBJECT_ID('obiettivo_giorno_attivo', 'U') IS NOT NULL AND COL_LENGTH('obiettivo', 'ripetizioni_giornaliere') IS NOT NULL
    BEGIN
        INSERT INTO obiettivo_fascia_oraria (obiettivo_id, giorno, ora_inizio, ora_fine, ripetizioni_richieste)
        SELECT g.obiettivo_id, g.giorno, o.ora_inizio, o.ora_fine, o.ripetizioni_giornaliere
        FROM obiettivo_giorno_attivo g
        JOIN obiettivo o ON o.id = g.obiettivo_id;
    END
END
GO

IF COL_LENGTH('obiettivo', 'ora_inizio') IS NOT NULL
    ALTER TABLE obiettivo DROP COLUMN ora_inizio;
IF COL_LENGTH('obiettivo', 'ora_fine') IS NOT NULL
    ALTER TABLE obiettivo DROP COLUMN ora_fine;
IF COL_LENGTH('obiettivo', 'ripetizioni_giornaliere') IS NOT NULL
    ALTER TABLE obiettivo DROP COLUMN ripetizioni_giornaliere;
GO

IF OBJECT_ID('obiettivo_giorno_attivo', 'U') IS NOT NULL
    DROP TABLE obiettivo_giorno_attivo;
GO
