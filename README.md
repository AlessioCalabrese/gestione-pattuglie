# Gestione Pattuglie e Obiettivi

Applicazione per un istituto di vigilanza (COSMOPOL) per pianificare i giri delle pattuglie sui loro
obiettivi (edifici/negozi da controllare), registrare il passaggio ("flag") con posizione GPS,
ottimizzare il percorso e gestire utenti, pattuglie e pianificazioni da un pannello di amministrazione.

Backend Spring Boot (Java 17) e frontend Angular 17, compilati insieme in un unico jar eseguibile.

## Requisiti

- Java 17 (JDK)
- Maven 3.9+
- Node.js e npm — se non già installati, `mvn clean package` li scarica in automatico per la build del
  frontend (vedi `frontend-maven-plugin` in `pom.xml`); per lavorare sul frontend con `ng serve` servono
  installati a mano
- Microsoft SQL Server raggiungibile (locale o remoto)

## Avvio in sviluppo

1. **Database:** crea/aggiorna lo schema eseguendo `db/schema.sql` su un'istanza SQL Server (per
   un'installazione nuova). Se il database esiste già da prima di una modifica recente, esegui anche gli
   script `db/migrazione_*.sql` non ancora applicati, **in ordine cronologico** (sono idempotenti: si
   possono rieseguire senza danni).
2. **Backend:**
   ```bash
   mvn spring-boot:run
   ```
   Parte con il profilo Spring `dev` (`application-dev.properties`), che punta a un database SQL Server
   su `localhost:1433`/database `parcoauto` con credenziali di sviluppo — sovrascrivibile con le variabili
   d'ambiente descritte più sotto (in particolare `DB_HOST`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`).
3. **Frontend**, in un secondo terminale, da `frontend/`:
   ```bash
   npm install
   npm start
   ```
   Apre il dev server Angular su `http://localhost:4200`, con le chiamate a `/api` inoltrate al backend
   su `:8080` (vedi `frontend/proxy.conf.json`).

In alternativa, per lavorare su un'unica origine senza dev server separato: `mvn clean package` compila
anche il frontend e lo include nel jar, poi `java -jar target/gestione-pattuglie-*.jar` serve tutto da
`http://localhost:8080`.

## Build per produzione

```bash
mvn clean package -Pprod
```

Usa `application-prod.properties` come profilo Spring impresso nel jar (in alternativa, su un jar già
compilato, si può forzare a runtime senza ricompilare con `SPRING_PROFILES_ACTIVE=prod`). In produzione
`spring.jpa.hibernate.ddl-auto=validate`: lo schema **non** viene mai creato o modificato automaticamente,
va sempre applicato a mano con `db/schema.sql` e gli script di migrazione.

Per saltare la build del frontend (utile in un ambiente senza accesso a Internet, se il frontend è già
compilato altrove):
```bash
mvn clean package -DskipFrontendBuild
```

## Configurazione (variabili d'ambiente)

Tutte le variabili hanno un default in dev; in produzione `CORS_ALLOWED_ORIGINS` e `JWT_SECRET` sono
obbligatorie (l'avvio fallisce subito se mancanti, invece di partire con un segreto di sviluppo).

| Variabile | Descrizione | Default (dev) |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | Profilo Spring attivo (`dev` / `prod`), ha sempre precedenza sul profilo impresso in build | quello scelto in fase di build |
| `DB_HOST`, `DB_PORT`, `DB_NAME` | Host/porta/nome del database SQL Server | `localhost` / `1433` / `parcoauto` |
| `DB_USERNAME`, `DB_PASSWORD` | Credenziali del database | `sa` / vedi `application-dev.properties` |
| `DB_ENCRYPT`, `DB_TRUST_SERVER_CERT` | Parametri di connessione JDBC | `false` / `true` |
| `DB_POOL_MAX`, `DB_POOL_MIN_IDLE`, `DB_POOL_CONNECTION_TIMEOUT_MS` | Pool di connessioni HikariCP | `10` / `2` / `20000` |
| `SERVER_PORT` | Porta HTTP del backend | `8080` |
| `CORS_ALLOWED_ORIGINS` | Origine consentita per le richieste cross-origin | `http://localhost:4200` |
| `JWT_SECRET` | Segreto di firma dei token JWT (min. 32 caratteri) | segreto di sviluppo, **non usare in produzione** |
| `JWT_EXPIRATION_MINUTES` | Durata dei token JWT, in minuti | `480` (dev) / `120` (prod) |
| `APP_WHATSAPP_NOMEAZIENDA`, `APP_WHATSAPP_TEMPLATECHECK` | Nome azienda e testo del messaggio WhatsApp inviato dopo il check (placeholder `{azienda}`, `{obiettivo}`, `{data}`, `{ora}`) | vedi `application.properties` |
| `JPA_DDL_AUTO` | Comportamento Hibernate verso lo schema (solo profilo dev) | `validate` |
| `JPA_SHOW_SQL` | Log delle query SQL (solo profilo dev) | `true` |

## Struttura del progetto

```
src/main/java/com/vigilanza/pattuglie/
  entity/        Entità JPA (Obiettivo, Pattuglia, Utente, FasciaOraria, GruppoPattuglie, ...)
  dto/           Oggetti di trasferimento restituiti dalle API (mai le entità direttamente)
  repository/    Repository Spring Data JPA
  service/       Logica applicativa
  controller/    Endpoint REST (/api/**)
  security/      Autenticazione JWT (password e tag NFC)
  config/        Configurazione (risorse statiche del frontend, ecc.)
frontend/src/app/
  core/          Servizi HTTP condivisi, guardie di rotta, interceptor
  features/      Pagine (login, selezione pattuglia, lista obiettivi, area admin)
db/
  schema.sql               Schema completo, per un'installazione nuova
  migrazione_*.sql          Script di migrazione incrementali (idempotenti), da eseguire in ordine
```

Per una descrizione più approfondita dell'architettura (build integrata, modello delle fasce orarie,
accorpamento pattuglie, integrazioni esterne) vedi [`CLAUDE.md`](./CLAUDE.md).

## Funzionalità principali

- **Pattuglia:** login con username/password o tag NFC (anche digitato a mano); lista degli obiettivi del
  giorno con stato dei flag per fascia oraria; registrazione del passaggio con posizione GPS (o stimata
  dalla rete se il GPS non è disponibile); ottimizzazione del percorso con apertura diretta in Google Maps;
  avviso WhatsApp all'obiettivo dopo il check; selezione tra pattuglie preferite o tutte quelle attive.
- **Amministrazione:** gestione utenti e pattuglie; creazione/modifica obiettivi con fasce orarie multiple
  per giorno (anche a cavallo di mezzanotte) e turni predefiniti (Mattina/Pomeriggio/Notte) configurabili;
  accorpamento di più pattuglie in un gruppo che condivide gli obiettivi; ricerca pattuglie/obiettivi;
  aggiornamento massivo delle coordinate da indirizzo; log di sistema filtrabile per data.

## Note

- Non è presente una suite di test automatici.
- Le migrazioni dello schema sono script SQL manuali (`db/migrazione_*.sql`), non uno strumento come
  Flyway/Liquibase: vanno applicati a mano e in ordine.
