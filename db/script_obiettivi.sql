-- ========================================================
-- SCRIPT DI INSERIMENTO OBIETTIVI (BATCH COMMIT OGNI 900)
-- DB: gestione_pattuglie.dbo.obiettivo
-- ========================================================

SET NOCOUNT ON;

BEGIN TRANSACTION;
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo)
VALUES(1, N'BAR ROMA DI CIOTTA E RIZZO SNC', N'VIA CAVOUR 153/155', N'CAMPOBELLO DI LICATA', 37.2584989, 13.9169410, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(1, N'CIULLA CINZIA', N'VIA DELLA VITTORIA N.135', N'MONTALLEGRO', 37.3887147, 13.3521541, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(1, N'ZHANG XUEJIAN', N'VIALE ALDO MORO N. 111', N'FAVARA', 37.3064142, 13.6522111, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(1, N'ZHANG WEN', N'VIA FARELLO 163', N'RIBERA', 37.4995556, 13.2733675, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(1, N'ERBORISTERIA DEL CORSO', N'Via Guglielmo Marconi, 102', N'RIBERA', 37.5036771, 13.2751188, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(1, N'GRECO ANTONINO', N'VIALE DEI GIARDINI N.7', N'PORTO EMPEDOCLE', 37.2959232, 13.4926826, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(1, N'HUANG ZHAO HUI', N'VIALE GARIBALDI N.130', N'RIBERA', 37.5003752, 13.2598710, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(1, N'ISOLA FISH S.R.L.', N'PIAZZA CONSIGLIO N.6', N'SCIACCA', 37.5051742, 13.0799709, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(1, N'LA BELLA GIROLAMO', N'VIA MUGHETTI N.10', N'PORTO EMPEDOCLE', 37.2918655, 13.4929668, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(1, N'LA FAZENDA DI CALLEA GIUSEPPE & C.', N'VIA BERLINGUER N.18', N'RIBERA', 37.4902202, 13.2687779, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(1, N'LAVANDERIA GRANO S.N.C.', N'Corso Vittorio Veneto, 254', N'FAVARA', 37.3148661, 13.6622175, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(1, N'NICOSIA FRANCA', N'VIA RICCIONE N. 3', N'AGRIGENTO', 37.2655555, 13.5806656, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(1, N'NUARA LEGNAMI SRL', N'Strada statale Sud Occidentale Sicula', N'PORTO EMPEDOCLE', 37.2993052, 13.4945207, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(1, N'PANIFICIO DEL LIDO S.N.C.', N'VIA GIOVANNI XXIII N. 8', N'AGRIGENTO', 37.5097560, 13.0722725, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(1, N'PANIFICIO DI GERIANDO MARIA', N'VIA MARCONI N. 207', N'SICULIANA', 37.3346876, 13.4203668, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(1, N'PUMA GIOVANNI', N'VIALE DEI GIARDINI N.14', N'PORTO EMPEDOCLE', 37.2959232, 13.4926826, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(1, N'REGALI D''ANNA', N'VIA AGRIGENTO N. 59/A', N'FAVARA', 37.3201521, 13.6581811, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(1, N'CARNABUCI MASSIMO', N'VIA ORCHIDEE N.23', N'PORTO EMPEDOCLE', 37.2959604, 13.4951982, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'AIERBIT SRL', N'VIA PIANODARDINE, 27/G', N'AVELLINO', 40.9262121, 14.8282810, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'RIPA GIACOMO ANTONIO', N'VIA CAPOZZI, 52', N'AVELLINO', 40.9212115, 14.7882694, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'OTTICA G. PERCOPO SAS', N'VIA G. MATTEOTTI 6', N'AVELLINO', 41.0371640, 15.2531326, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'LINEAUTO BE.MA. S.N.C.', N'C.DA VALLE MECCA 34/35', N'AVELLINO', 40.9131787, 14.7713080, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'CENTRO ENOLOGICO IRPINO DI SESSA F.', N'VIA CIRCUMVALLAZIONE', N'AVELLINO', 40.9161793, 14.8012267, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'NOVASISTEMI SRL', N'Contrada ARCHI 55-PARCO GILIA 56', N'AVELLINO', 40.9227705, 14.7980329, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'ALBAKIARA SRL', N'PIAZZALE GAMBALE,10', N'AVELLINO', 40.9239580, 14.7872537, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'DON EMILIO CARBONE', N'C.SO UMBERTO I', N'AVELLINO', 40.9143789, 14.7977696, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'IRPINIA LUCE S.R.L.', N'VIA MANFRA', N'AVELLINO', 40.9163419, 14.7678558, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'ARCH DI ROBERTO D''AGOSTINO', N'CORSO UMBERTO I,79/81', N'AVELLINO', 40.9148880, 14.8014112, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'PANIFICIO DEI F.LLI SAVELLI S.', N'VIA DE GASPERI 144', N'AVELLINO', 41.0701978, 15.0627764, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'DE VITO DELIA', N'C.DA CHIAIRE,23/P', N'AVELLINO', 40.9362465, 14.8811648, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'CONDOMINIO PARCO MATARAZZO', N'VIA SCANDONE', N'AVELLINO', 40.9230062, 14.7928440, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'DE MAIO MARIO FULVIO', N'C.DA ARCHI,8/E', N'AVELLINO', 40.9302757, 14.8049788, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'ORTOPEDIA DI MARTINO DI GUERRIERO E', N'VIA L. AMABILE, 38', N'AVELLINO', 40.9160128, 14.7946588, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'ADILETTA ALFONSO', N'CORSO UMBERTO 1°, 149-151', N'AVELLINO', 40.8303760, 15.0691036, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'TAVERNA L''ORCAGNA DI RUSSOMANTO A.', N'VIA TORRE 3/C', N'MANOCALZATI', 40.9383357, 14.8497120, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'LA CASA DI ARTURO DI SICILIANO P. &', N'VIA DEGLI IMBIMBO, 42/44', N'AVELLINO', 40.9198574, 14.7878811, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'SACOL S.R.L.', N'Contrada Amoretta', N'AVELLINO', 40.9273262, 14.7986834, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'BOLINO ROSA', N'CORSO EUROPA,123', N'AVELLINO', 40.9129521, 14.7894001, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'CAFFETTERIA SRLS', N'PIAZZA DEL POPOLO,16', N'AVELLINO', 40.9142666, 14.7945835, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'ELLETI SRL', N'VIA D. CAPUANO 36/46', N'AVELLINO', 40.9167861, 14.7669076, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'VINOLIO DI RUGGIERO FULVIO', N'C.SO UMBERTO I°, 79/81', N'AVELLINO', 40.9143789, 14.7977696, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'PER.ADA SRL', N'VIA CHIESA CONSERVATORIO 16/18', N'AVELLINO', 40.9151652, 14.7927517, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'RICCI E CAPRICCI DI CUTILLO MASSIMO', N'CORSO UMBERTO I 138', N'AVELLINO', 40.9143789, 14.7977696, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'FAAC WASH SRL', N'VIA MAZAS 9', N'AVELLINO', 40.9146156, 14.7906752, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'SALERNO ENERGIA VENDITE SPA', N'VIA DE CONCILIIS,40/44', N'AVELLINO', 40.9094073, 14.7955722, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'SALERNO ENERGIA VENDITE SPA', N'VIA CAPOZZI,17/19', N'AVELLINO', 40.9212115, 14.7882694, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'UNI-FORM SRL', N'COLLINA LIGUORINI', N'AVELLINO', 40.9078997, 14.7988093, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'MAZZA MARIA', N'VIA Gugliemo MARCONI, 11', N'AVELLINO', 40.9139406, 14.7804524, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'I BURATTINI DI BARONE MARIA F.', N'VIA PIAVE ,27', N'AVELLINO', 40.9188085, 14.7865135, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'ALVINO CATELLO & FIGLI SRL', N'VIA CASALE,28', N'AVELLINO', 40.9155542, 14.7929116, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'CESA ANTONIO', N'VIA LUIGI AMABILE,18/18A', N'AVELLINO', 40.9160128, 14.7946588, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'DITTA PARADISO UGO', N'VIA MICHELE CAPOZZI,15/17', N'AVELLINO', 40.9212115, 14.7882694, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'BAR TWINGO DI DE PETRIS R.& ANTONIO', N'VIA ANNARUMMA,94', N'AVELLINO', 40.8925876, 14.7159494, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'OFFICE LINE DI C. GENOVESE & C. SAS', N'VIA PIAVE, 89', N'AVELLINO', 40.9188085, 14.7865135, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'A. CORTESE & C. S.N.C.', N'VIA RAMPA S.MODESTINO', N'AVELLINO', 40.9166216, 14.7960021, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'NEW AGE SNC DI TODESCA C. & CAPONE', N'VIA PALTUCCI, 2/E', N'AVELLINO', 40.9149099, 14.7772674, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'ISISS P.A. DE LUCA', N'Via Francesco Scandone, 66', N'AVELLINO', 40.9231986, 14.7942803, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'CARTOLIBR. S.TOMMASO DI RULLO G.', N'VIA S.TOMMASO,31/M', N'AVELLINO', 40.9024401, 14.7954511, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'CIOFFI NICOLA', N'IRPINIA ASSISTANCE', N'AVELLINO', 40.9105649, 14.7956822, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'L''AGRICOLA S.A.S. DI VECCHIONE F.', N'VIA PAOLO IV CARAFA, 3', N'AVELLINO', 40.9112511, 14.7964487, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'ANTONIO GENGARO S.A.S.', N'VIA G.NAPPI,10', N'AVELLINO', 40.9149375, 14.7947307, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'CENTRO OTTICO S. REPPUCCI', N'VIA G.NAPPI,41', N'AVELLINO', 40.9149375, 14.7947307, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'IRPINIATOUR S.R.L.', N'PIAZZA LIBERTA'', 54 - 57', N'AVELLINO', 40.9143897, 14.7925992, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'AGENZIA U.P.A.M. DI GIAQUINTO M.', N'VIA LUIGI AMABILE,29', N'AVELLINO', 40.9160128, 14.7946588, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'DE VITA LUIGI', N'VIA DUE PRINCIPATI,32/C', N'AVELLINO', 40.9003458, 14.7982322, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'DITTA CAPOLUPO SALVATORE', N'VIA DUE PRINCIPATI,35', N'AVELLINO', 40.9003458, 14.7982322, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'MELONE CARMINE & C. SNC', N'VIA E. CAPOZZI 40', N'AVELLINO', 40.9212115, 14.7882694, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'MEDICALS DISTRIBUZIONI S.R.L.', N'VIA CANNAVIELLO 17', N'AVELLINO', 40.9194920, 14.7906226, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'PELLECCHIA CRISTINA', N'VIA FRANCESCO TEDESCO 239', N'AVELLINO', 40.9248238, 14.8212368, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'FERRAROMATICA VENDING DI FERRARO M.', N'VIA CARDUCCI', N'AVELLINO', 40.9893550, 14.9914015, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'KLIMT SAS DI ANTONIA CARBONE', N'VIA CRISTOFORO COLOMBO, 31/A', N'AVELLINO', 40.9149580, 14.7823915, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'SICUREZZA TRASPORTI AUTOLINEE', N'CAMPETTO SANTA RITA', N'AVELLINO', 40.9171516, 14.7954695, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'TITO LOGISTICS SRL', N'Via L. de Conciliis, 33', N'AVELLINO', 40.9147996, 14.7827798, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'TECNOLOGICA SPA', N'Via Raffaele Aversa, 69', N'AVELLINO', 40.9157599, 14.7698068, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'TECNOLOGICA SPA', N'VIA PALATUCCI 20/A', N'AVELLINO', 40.8206985, 15.2298770, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'TABACCHI RIV. N. 24 DI BARONE MILEN', N'Via Francesco Tedesco, 64', N'AVELLINO', 40.9150547, 14.8054828, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'NASTIMM SRL', N'VIA PENNINI 84', N'AVELLINO', 40.9256417, 14.7778928, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'CENTRO POLISPEC. FUTURA DIAGNOSTICA', N'C.SO VITTORIO EMANUELE,190', N'AVELLINO', 41.1470770, 15.0846415, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'IRPINIA RECUPERI SRL', N'VIA TUFAROLE 72', N'ATRIPALDA', 40.9042677, 14.8072912, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'S.A.T.E.I. DEI F.LLI BALDI SNC', N'CORSO UMBERTO 1, 274', N'AVELLINO', 40.9526065, 14.6036660, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'SARNO MARIA SILVANA & C. S.A.S.', N'Via Vincenzo Cannaviello, 71', N'AVELLINO', 40.9194920, 14.7906226, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'VOGLIA DI PIZZA DI RICCARDI ANIEL', N'VIA DUE PRINCIPATI, 242', N'AVELLINO', 40.9110161, 14.7932669, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'DE MATTEIS MARCO', N'C.DA AMORETTA', N'AVELLINO', 40.9262292, 14.7982522, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'BAR PICONE CIRO', N'VIA CANNAVIELLO, 38', N'AVELLINO', 40.9194920, 14.7906226, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'ZOLLO ENRICO', N'VIA Francesco TEDESCO', N'AVELLINO', 40.9248238, 14.8212368, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'COSMOPOL', N'Contrada Santorelli', N'AVELLINO', 40.9267361, 14.8193110, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'PESSINA GESTIONI SRL', N'Contrada Amoretta', N'AVELLINO', 40.9273262, 14.7986834, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'DITTA DE VITO GIOVANNI', N'VIA F. TEDESCO, 401', N'AVELLINO', 40.8136459, 15.2239201, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'DEACOP SRLS', N'PIAZZA MUNICIPIO 8', N'MERCOGLIANO', 40.9200393, 14.7365111, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'FONTANAROSA PIETRA', N'CORSO EUROPA,225', N'AVELLINO', 40.9129521, 14.7894001, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'ALIMENTARI E FORMAGGI DOC', N'VIA C. COLOMBO 26/30', N'AVELLINO', 41.0220601, 15.1117692, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'LOT BROTHERS FOOD SRLS', N'VIA ALFONSO CARPENTIERI 1', N'AVELLINO', 40.9250371, 14.7917764, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'GELAT. CAFFETT. "ARTURO" DI GAMBINO', N'Corso Vittorio Emanuele 365', N'AVELLINO', 40.9853688, 14.8501050, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'AURUBIS ITALIA SRL', N'Via Pianodardine, 95F', N'AVELLINO', 40.9262121, 14.8282810, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'ABBIGLIAMENTO "HIM" DI TESTA LUCIO', N'VIA COLOMBO, 19', N'AVELLINO', 41.0225019, 15.1114365, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'FINNO ROSALIA', N'Contrada ARCHI, 8', N'AVELLINO', 40.9268174, 14.8090687, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'OTTICA GB', N'P.ZZA DELLA LIBERTÀ 20', N'AVELLINO', 40.9143897, 14.7925992, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'GENOVESE ANDREINA', N'Contrada ARCHI', N'AVELLINO', 40.9268174, 14.8090687, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'MEDICAL TECH SRL', N'P.ZZA COCCHIA 22', N'AVELLINO', 40.8950766, 14.8284425, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'PORCELLI ANNA', N'Via Alfonso Carpentieri', N'AVELLINO', 40.9250371, 14.7917764, 0, 1, NULL, 1);
INSERT INTO gestione_pattuglie.dbo.obiettivo
(pattuglia_id, nome, via, comune, latitudine, longitudine, priorita, ripetizioni_giornaliere, ordine_visita, attivo, data_creazione, telefono_riferimento)
VALUES(2, N'VINCENT SRL', N'Corso Vittorio Emanuele Secondo', N'AVELLINO', 40.9140641, 14.7885944, 0, 1, NULL, 1);
      
COMMIT TRANSACTION;