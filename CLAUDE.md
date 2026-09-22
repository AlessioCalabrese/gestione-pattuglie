# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Gestione pattuglie e obiettivi per un istituto di vigilanza (COSMOPOL): un backend Spring Boot (Java 17) e
un frontend Angular 17, compilati insieme in un unico jar eseguibile. Le pattuglie fanno il giro dei loro
obiettivi (edifici/negozi da controllare) secondo una pianificazione a fasce orarie, registrano il
passaggio ("flag") con la posizione GPS, e possono ottimizzare il percorso del giro. Gli amministratori
gestiscono utenti, pattuglie, obiettivi, turni predefiniti e accorpamenti tra pattuglie da un pannello
dedicato.

Tutto il codice (nomi di classi, metodi, variabili, commenti) è in italiano; segui questa convenzione.

## Comandi

```bash
# Backend: compila ANCHE il frontend Angular e lo include nel jar (frontend-maven-plugin)
mvn clean package
mvn clean package -Pprod              # profilo di produzione (vedi application-prod.properties)
mvn clean package -DskipFrontendBuild # salta la build Angular (serve npm/Node già installati altrove)
mvn spring-boot:run                   # avvio rapido in sviluppo (profilo "dev" di default)
mvn test                              # nessun test presente al momento nel modulo backend

# Frontend, da frontend/
npm install
ng serve --proxy-config proxy.conf.json   # dev server su :4200, proxy /api -> backend su :8080
ng build                                   # scrive in ../src/main/resources/static (vedi angular.json)
```

Non esiste una suite di test (né `src/test/java` né `*.spec.ts` nel frontend).

## Architettura

**Build integrata.** `pom.xml` usa `frontend-maven-plugin` per installare Node/npm ed eseguire `ng build`
durante `mvn clean package` (fase `generate-resources`), poi `maven-resources-plugin` copia l'output Angular
(`frontend/src/main/resources/static`, scritto direttamente lì da `angular.json`) dentro
`target/classes/static`. Il jar risultante è un'unica app Spring Boot che serve sia le API (`/api/**`) sia
il frontend statico. `SpaWebConfig` fa da fallback: qualunque richiesta che non è un'API e non corrisponde
a un file statico restituisce `index.html`, per le rotte Angular lato client.

**Profili Spring vs profili Maven.** `application.properties` contiene solo impostazioni comuni; datasource,
CORS e segreto JWT sono nei file di profilo `application-dev.properties` / `application-prod.properties`.
Quale dei due è impresso nel jar lo decide il profilo Maven (`mvn clean package` → dev, `-Pprod` → prod),
tramite resource filtering del placeholder `@spring.profiles.active@` (delimitatori `@...@`, non `${...}`,
per non confliggere con i placeholder Spring `${VAR:default}`). A runtime, `SPRING_PROFILES_ACTIVE` ha
sempre precedenza sul valore impresso in build. In dev, `spring.jpa.hibernate.ddl-auto=validate`: lo schema
non viene mai creato/alterato da Hibernate, va sempre applicato a mano da `db/schema.sql`.

**Database.** SQL Server. `db/schema.sql` è lo schema completo per un'installazione nuova. Le modifiche
successive allo schema vivono come script `db/migrazione_*.sql` separati e idempotenti (con controlli
`IF COL_LENGTH(...) IS NULL` / `IF OBJECT_ID(...) IS NULL`), da eseguire manualmente in ordine cronologico
su un database esistente — non c'è uno strumento di migrazione automatico (Flyway/Liquibase). Quando cambi
un'entità JPA, aggiorna sia `schema.sql` sia aggiungi il relativo script di migrazione.

**Sicurezza.** JWT stateless (`JwtUtil`, `JwtAuthFilter`), con login sia per username/password sia per tag
NFC (anche digitato a mano, vedi `CodiceNfc`/`AuthService.loginNfc`). `SecurityConfig` protegge `/api/admin/**`
con `ROLE_ADMIN`; `/api/pattuglie/**` e `/api/obiettivi/**` con `ROLE_ADMIN` o `ROLE_PATTUGLIA`. Il logout
revoca il token in una blacklist in-memory (`JwtUtil`) — da sostituire con Redis o una tabella su DB in un
deployment multi-istanza. `AuthenticatedUser` legge utente/ruolo correnti dal `SecurityContext`.

**Modello obiettivi/fasce orarie.** Un `Obiettivo` appartiene a una `Pattuglia` e ha una lista di
`FasciaOraria` (embeddable, tabella `obiettivo_fascia_oraria`): ogni fascia ha un `GiornoSettimana`, ora di
inizio/fine opzionali e un numero di ripetizioni richieste. Un giorno può avere più fasce (es. mattina e
sera) e una fascia è "notturna" quando l'ora di fine precede quella di inizio (es. 22:00–06:00): in quel
caso attraversa la mezzanotte e la sua coda ricade nel giorno della settimana successivo
(`FasciaOraria.isNotturna()`, `GiornoSettimana.successivo()`). `Obiettivo.isInServizio(data, ora)` e
`isRilevantePer(data)` centralizzano questa logica (comprese le fasce di ieri notte che sconfinano in
oggi mattina) — non duplicarla altrove. `ObiettivoService.toDtoConStatoFlag` calcola lo stato di ogni
fascia di oggi contando i flag nell'esatto intervallo `LocalDateTime` della fascia (non solo sull'orario),
proprio per gestire correttamente le fasce a cavallo di due giornate di calendario. `ConfigurazioneTurni`
(riga singola in DB) definisce gli orari di default di Mattina/Pomeriggio/Notte, usati come precompilazione
rapida nel form admin, editabili dall'amministratore con la stessa logica di validazione anti-sovrapposizione.

**Accorpamento pattuglie.** Una `Pattuglia` può appartenere a un `GruppoPattuglie` (al più uno). Quando
accorpata, `GruppoPattuglieService` fa sì che la pattuglia veda e possa flaggare anche gli obiettivi delle
altre pattuglie del gruppo (`ObiettivoService.findByPattuglia`/`ottimizzaRotta`/`flagObiettivo` passano
sempre dall'insieme di id "rilevanti" restituito da `idPattugliePerAccorpamento`, mai dal solo pattugliaId
selezionato).

**DTO, mai entità.** I controller non restituiscono mai entità JPA direttamente; ogni entità ha un DTO
dedicato costruito a mano nel service (`toDto...`/`toObiettivoDto`). `pom.xml` include MapStruct/Lombok ma
non risultano ancora usati nel codice esistente — i mapper visti finora sono scritti a mano.

**Frontend.** Angular 17 standalone components (nessun `NgModule`), routing con `authGuard`/`adminGuard`
(`app.routes.ts`). Un service per dominio sotto `core/services/` (`pattuglia.service.ts` per l'operatività
pattuglia, `admin.service.ts` per l'amministrazione — quest'ultimo importa e ri-esporta alcuni tipi dal
primo per evitare duplicazioni, es. `GiornoSettimana`, `TipoObiettivo`, `FasciaOraria`). `auth.interceptor.ts`
allega il JWT solo alle richieste verso `/api/` (mai a servizi esterni come la geolocalizzazione IP lato
browser). Le pagine admin (`features/admin/gestione-pattuglie`) sono form-heavy e ricche di stato locale;
i pannelli meno usati (turni predefiniti, accorpamento pattuglie, aggiornamento coordinate) sono
collassabili per non appesantire la vista principale.

**Integrazioni esterne.** Geocodifica indirizzi e stima posizione da IP via Nominatim/ipwho.is
(`GeocodingService`, `GeolocalizzazioneIpService`, con ripiego lato browser in `posizione.service.ts` se il
server vede solo un IP privato). Prezzo carburante da un dataset MIMIT (`FuelPriceService`). Percorso
ottimizzato aperto in Google Maps via URL "click to chat"-style (`NavigatoreService`, max 10 tappe per
link, quindi diviso in più tratte). Avviso WhatsApp all'obiettivo dopo il check via link `wa.me`
(`WhatsappMessageService`, template configurabile in `application.properties`).
