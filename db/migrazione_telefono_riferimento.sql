-- Migrazione per database già esistenti: telefono di riferimento dell'obiettivo (avviso WhatsApp dopo il flag).
-- Le installazioni nuove hanno già la colonna in schema.sql.
USE gestione_pattuglie;
GO

IF COL_LENGTH('obiettivo', 'telefono_riferimento') IS NULL
BEGIN
    ALTER TABLE obiettivo ADD telefono_riferimento VARCHAR(20) NULL;
END
GO


INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-AG-PRO', N'UM AGRIGENTO', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-AV-001', N'AVELLINO - Aquila 1', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-AV-002', N'AVELLINO - Aquila 2', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-AV-003', N'AVELLINO - Aquila 3', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-AV-004', N'AVELLINO - Aquila 4', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-AV-005', N'AVELLINO - Aquila 5', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-AV-006', N'AVELLINO - Aquila 6', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-AV-007', N'AVELLINO - Aquila 7', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-AV-008', N'AVELLINO - Aquila 8', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-AV-009', N'AVELLINO - Aquila 9', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-AV-010', N'AVELLINO - Aquila 10', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-AV-011', N'AVELLINO - Aquila 11', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-AV-012', N'AVELLINO - Aquila 12', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-AV-014', N'AVELLINO - Aquila 14', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-AV-015', N'AVELLINO - AQUILA 15', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-AV-AAA', N'FUORI ZONA', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-AV-NET', N'NETWORK', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-BR-CEG', N'UM CEGLIE', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-BR-SAN', N'UM SAN PANCRAZIO', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-BR-TOR', N'UM TORCHIAROLO', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-BR-UBC', N'BRINDISI CENTRO', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-BR-VER', N'UM SAN PIETRO VERNOTICO', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-ER-CR1', N'PATTUGLIA CREMONA 01', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-ER-FUO', N'FUORI ZONA', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-ER-LO1', N'PATTUGLIA LODI 01', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-ER-LO2', N'PATTUGLIA LODI 02', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-ER-LO3', N'PATTUGLIA LODI 03', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-ER-MI1', N'PATTUGLIA MILANO 01', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-ER-MI2', N'PATTUGLIA MILANO 02', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-ER-PI1', N'PATTUGLIA PIACENZA 01', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-ER-PV1', N'PATTUGLIA PAVIA 01', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-ER-PV2', N'PATTUGLIA PAVIA 02', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-FG-A16', N'AQUILA 16 FG', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-LE-CAR', N'UM CAROVIGNO', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-LE-CAV', N'UM CAVALLINO', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-LE-CUR', N'UM MAGLIE NORD', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-LE-FE1', N'UM CASARANO', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-LE-FE2', N'UM EX FEN02', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-LE-FE3', N'UM IONICA', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-LE-GAL', N'UM GALLIPOLI', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-LE-LEC', N'UM LECCE CENTRO', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-LE-LEN', N'UM LECCE NORD', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-LE-LES', N'UM LECCE SUD', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-LE-MAG', N'UM MAGLIE SUD', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-LE-NAR', N'UM NARDO''', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-LE-OST', N'UM OSTUNI', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-LE-TRE', N'UM TREPUZZI', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-LE-UGE', N'UM UGENTO', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-NA-018', N'AQUILA 19', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-NA-020', N'AQUILA 20 BIS', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-NA-021', N'NAPOLI - Aquila 21', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-NA-023', N'NAPOLI - Aquila 23', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-NA-024', N'NAPOLI - Aquila 24', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-NA-025', N'NAPOLI - Aquila 25', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-NA-052', N'NAPOLI - AQUILA 52', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-NA-053', N'NAPOLI - AQUILA 53', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-NA-068', N'NAPOLI-AQUILA 68', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-NA-072', N'NAPOLI-AQUILA 72', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-NA-A20', N'NAPOLI-AQUILA 20', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-NA-A49', N'AQUILA-49 NAPOLI', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-NA-A50', N'NAPOLI - AQUILA 50', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-NA-A51', N'AQUILA 51', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-NA-A56', N'NAPOLI-AQUILA 56', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-NA-A63', N'NAPOLI- AQUILA 63', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-NA-A65', N'NAPOLI - AQUILA 65', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-NA-N54', N'NAPOLI - AQUILA 54', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-NA-N55', N'NAPOLI - AQUILA 55', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-TA-AVE', N'UM AVETRANA-MANDURIA', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-TA-CAS', N'UM CASTELLANETA', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-TA-P1', N'PATTUGLIA 1 - LEPORANO', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-TA-P2', N'PATTUGLIA 2 - GROTTAGLIE', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-TA-P3', N'PATTUGLIA 3 - TARANTO', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-TA-TA2', N'TARANTO 2', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-TA-TAR', N'UM TARANTO', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL SPA-TA-TLT', N'UM TARANTO-LAMA-TALSANO', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL VIGILANZA SRL-CS-PC1', N'RADIOMOB. COSENZA', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL VIGILANZA SRL-CS-PPS', N'RADIOMOB. TIRRENO', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL VIGILANZA SRL-CS-RS1', N'RADIOMOB. ROSSANO', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL VIGILANZA SRL-CZ-BBB', N'AQUILA CATANZARO', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL VIGILANZA SRL-CZ-CCC', N'AQUILA BASSO JONIO CATANZARO', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL VIGILANZA SRL-CZ-FFF', N'AQUILA LAMEZIA', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL VIGILANZA SRL-CZ-FUO', N'FUORI ZONA', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL VIGILANZA SRL-PZ-111', N'ZONA 1', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL VIGILANZA SRL-PZ-2', N'ZONA 2', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL VIGILANZA SRL-PZ-3', N'ZONA 3', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL VIGILANZA SRL-PZ-LAU', N'LAURIA', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL VIGILANZA SRL-PZ-MAR', N'MARATEA', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL VIGILANZA SRL-PZ-PZN', N'PATTUGLIA VULTURE/MELFESE', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL VIGILANZA SRL-PZ-SEN', N'SENISE', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'COSMOPOL VIGILANZA SRL-PZ-TIT', N'TITO', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'POLIZIOTTO NOTTURNO SRL-PN-102', N'PATTUGLIA 102', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'POLIZIOTTO NOTTURNO SRL-PN-103', N'PATTUGLIA 103', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'POLIZIOTTO NOTTURNO SRL-PN-104', N'PATTUGLIA 104', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'POLIZIOTTO NOTTURNO SRL-PN-105', N'PATTUGLIA 105', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'POLIZIOTTO NOTTURNO SRL-PN-106', N'PATTUGLIA 106', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'POLIZIOTTO NOTTURNO SRL-PN-107', N'PATTUGLIA 107', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'POLIZIOTTO NOTTURNO SRL-PN-108', N'PATTUGLIA 108', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'POLIZIOTTO NOTTURNO SRL-PN-109', N'PATTUGLIA 109', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'POLIZIOTTO NOTTURNO SRL-PN-V21', N'VELA 21', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'POLIZIOTTO NOTTURNO SRL-PN-V24', N'VELA 24', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'SECURPOL SPA-BT-UMA', N'UM ALTAMURA-GRAVINA', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'SECURPOL SPA-PS-B1R', N'BARLETTA 1', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'SECURPOL SPA-PS-B2R', N'BARLETTA 2', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'SECURPOL SPA-PS-BA1', N'BARI 1', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'SECURPOL SPA-PS-BA2', N'BARI 2', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'SECURPOL SPA-PS-BA3', N'BARI 3', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'SECURPOL SPA-PS-BA4', N'BARI 4', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'SECURPOL SPA-PS-BA5', N'BARI 5', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'SECURPOL SPA-PS-BAD', N'BARI DIURNA', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'SECURPOL SPA-PS-BD2', N'BARI DIURNA 2', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'SECURPOL SPA-PS-CAS', N'CASTELLANA', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'SECURPOL SPA-PS-FER', N'SAN FERDINANDO', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'SECURPOL SPA-PS-MAR', N'ZONA MARGHERITA', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'SECURPOL SPA-PS-MOL', N'MOLFETTA', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'SECURPOL SPA-PS-TRA', N'TRANI', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'SECURPOL SPA-SG-AN1', N'ANCONA 1', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'SECURPOL SPA-SG-FN3', N'FABRIANO 1 NOTTURNA', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'SECURPOL SPA-SN-ZOC', N'PATTUGLIA-ZONA CAPRI', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'TURRIS SRL-TU-TZ2', N'PATTUGLIA Z2', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'TURRIS SRL-TU-Z1C', N'ZONA SETTORE 1/C CENTRO', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'TURRIS SRL-TU-ZOI', N'PATTUGLIA ZONA ISCHIA', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'TURRIS SRL-TU-ZS2', N'ZONA SETTORE 2/A MONTE', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'TURRIS SRL-TU-ZS3', N'ZONA SETTORE 2/B CENTRO/MONTE', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'TURRIS SRL-TU-ZSA', N'ZONA SETTORE 1 ALTA/CENTRO', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'TURRIS SRL-TU-ZSB', N'PATTUGLIA Z1', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'TURRIS SRL-TU-ZSC', N'ZONA SETTORE 2/C CENTRO/MONTE', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'TURRIS SRL-TU-ZSM', N'ZONA SETTORE 2/MONTE T/A', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'URBE VIGILANZA SPA-PA-A10', N'AQUILA 10', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'URBE VIGILANZA SPA-PA-A11', N'AQUILA 11-33 DIURNA', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'URBE VIGILANZA SPA-PA-A12', N'AQUILA 12', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'URBE VIGILANZA SPA-PA-A13', N'AQUILA 13', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'URBE VIGILANZA SPA-PA-A14', N'AQUILA 14', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'URBE VIGILANZA SPA-PA-A15', N'AQUILA 15', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'URBE VIGILANZA SPA-PA-A16', N'AQUILA 16 - 35 DIURNA', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'URBE VIGILANZA SPA-PA-A17', N'AQUILA 17', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'URBE VIGILANZA SPA-PA-A18', N'AQUILA 18', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'URBE VIGILANZA SPA-PA-A19', N'AQUILA 19', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'URBE VIGILANZA SPA-PA-A20', N'AQUILA 20', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'URBE VIGILANZA SPA-PA-A21', N'AQUILA 21', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'URBE VIGILANZA SPA-PA-A22', N'AQUILA 22', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'URBE VIGILANZA SPA-PA-A23', N'AQUILA 23', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'URBE VIGILANZA SPA-PA-A24', N'AQUILA 24', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'URBE VIGILANZA SPA-PA-A26', N'AQUILA 26', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'URBE VIGILANZA SPA-PA-A27', N'AQUILA 27', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'URBE VIGILANZA SPA-PA-A28', N'AQUILA 28', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'URBE VIGILANZA SPA-PA-AQ1', N'AQUILA 1-34 DIURNA', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'URBE VIGILANZA SPA-PA-AQ2', N'AQUILA 2', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'URBE VIGILANZA SPA-PA-AQ3', N'AQUILA 3', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'URBE VIGILANZA SPA-PA-AQ4', N'AQUILA 4', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'URBE VIGILANZA SPA-PA-AQ5', N'AQUILA 5-32 DIURNA', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'URBE VIGILANZA SPA-PA-AQ6', N'AQUILA 6-36 DIURNA', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'URBE VIGILANZA SPA-PA-AQ7', N'AQUILA 7', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'URBE VIGILANZA SPA-PA-AQ8', N'AQUILA 8', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'URBE VIGILANZA SPA-PA-AQ9', N'AQUILA 9 - 37 DIURNA', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'URBE VIGILANZA SPA-PA-NET', N'NETWORK', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'URBE VIGILANZA SPA-PA-VT1', N'VITERBO 1', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'URBE VIGILANZA SPA-PA-VT2', N'VITERBO 2', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'URBE VIGILANZA SPA-PA-VT3', N'VITERBO 3', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'URBE VIGILANZA SPA-PA-VT4', N'VITERBO 4', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'URBE VIGILANZA SPA-PA-VT5', N'VITERBO 5', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'URBE VIGILANZA SPA-PA-VT6', N'VITERBO 6', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'URBE VIGILANZA SPA-PA-VT7', N'VITERBO 7', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'URBE VIGILANZA SPA-PA-VT8', N'VITERBO 8', N'BENZINA', 1, '2026-09-21 13:15:56.917');
INSERT INTO gestione_pattuglie.dbo.pattuglia
(nome, descrizione, tipo_carburante, attiva, data_creazione)
VALUES(N'URBE VIGILANZA SPA-R4-S', N'AQUILA 06 SACROFANO/CAMPAGNANO', N'BENZINA', 1, '2026-09-21 13:15:56.917');