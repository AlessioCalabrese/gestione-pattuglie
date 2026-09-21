package com.vigilanza.pattuglie.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "obiettivo")
public class Obiettivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pattuglia_id", nullable = false)
    private Pattuglia pattuglia;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, length = 150)
    private String via;

    @Column(nullable = false, length = 100)
    private String comune;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal latitudine;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal longitudine;

    @Column(name = "ordine_visita")
    private Integer ordineVisita; // popolato dal servizio di ottimizzazione rotta

    @Column(nullable = false)
    private boolean priorita = false; // se true, l'ottimizzatore lo visita prima degli altri

    /** Giorni della settimana in cui l'obiettivo è in servizio. Vuoto = mai visibile per le pattuglie (va configurato esplicitamente). */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "obiettivo_giorno_attivo", joinColumns = @JoinColumn(name = "obiettivo_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "giorno")
    private Set<GiornoSettimana> giorniAttivi = new HashSet<>();

    @Column(name = "ora_inizio")
    private LocalTime oraInizio; // null = nessun vincolo di fascia oraria

    @Column(name = "ora_fine")
    private LocalTime oraFine;

    @Column(name = "ripetizioni_giornaliere", nullable = false)
    private int ripetizioniGiornaliere = 1; // quante volte va flaggato nella giornata/fascia

    /**
     * Cellulare di riferimento dell'obiettivo, in formato internazionale di sole cifre
     * (es. 393331234567). Se presente, dopo il flag la pattuglia può avvisare l'obiettivo via WhatsApp.
     */
    @Column(name = "telefono_riferimento", length = 20)
    private String telefonoRiferimento;

    @Column(nullable = false)
    private boolean attivo = true;

    @Column(name = "data_creazione", nullable = false, updatable = false)
    private LocalDateTime dataCreazione = LocalDateTime.now();

    public Obiettivo() {
    }

    public Obiettivo(Pattuglia pattuglia, String nome, String via, String comune,
                      BigDecimal latitudine, BigDecimal longitudine) {
        this.pattuglia = pattuglia;
        this.nome = nome;
        this.via = via;
        this.comune = comune;
        this.latitudine = latitudine;
        this.longitudine = longitudine;
    }

    /** Indirizzo completo ricostruito dai singoli campi, usato per geocodifica e visualizzazione. */
    public String getIndirizzoCompleto() {
        return via + ", " + comune;
    }

    /**
     * Vero se, alla data e ora indicate, l'obiettivo è in servizio secondo la
     * pianificazione configurata (giorni attivi + fascia oraria). Un obiettivo
     * senza NESSUN giorno configurato non è mai in servizio per le pattuglie:
     * i giorni di servizio vanno sempre impostati esplicitamente in fase di
     * creazione. La fascia oraria, invece, se non configurata (oraInizio/oraFine
     * nulli) non pone vincoli, cioè l'obiettivo è attivo tutto il giorno.
     */
    public boolean isInServizio(java.time.LocalDate data, LocalTime ora) {
        boolean giornoOk = !giorniAttivi.isEmpty() && giorniAttivi.contains(GiornoSettimana.daDayOfWeek(data.getDayOfWeek()));
        boolean orarioOk = (oraInizio == null || !ora.isBefore(oraInizio))
                && (oraFine == null || !ora.isAfter(oraFine));
        return giornoOk && orarioOk;
    }

    // Getters e setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pattuglia getPattuglia() {
        return pattuglia;
    }

    public void setPattuglia(Pattuglia pattuglia) {
        this.pattuglia = pattuglia;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getVia() {
        return via;
    }

    public void setVia(String via) {
        this.via = via;
    }

    public String getComune() {
        return comune;
    }

    public void setComune(String comune) {
        this.comune = comune;
    }

    public BigDecimal getLatitudine() {
        return latitudine;
    }

    public void setLatitudine(BigDecimal latitudine) {
        this.latitudine = latitudine;
    }

    public BigDecimal getLongitudine() {
        return longitudine;
    }

    public void setLongitudine(BigDecimal longitudine) {
        this.longitudine = longitudine;
    }

    public Integer getOrdineVisita() {
        return ordineVisita;
    }

    public void setOrdineVisita(Integer ordineVisita) {
        this.ordineVisita = ordineVisita;
    }

    public boolean isPriorita() {
        return priorita;
    }

    public void setPriorita(boolean priorita) {
        this.priorita = priorita;
    }

    public Set<GiornoSettimana> getGiorniAttivi() {
        return giorniAttivi;
    }

    public void setGiorniAttivi(Set<GiornoSettimana> giorniAttivi) {
        this.giorniAttivi = giorniAttivi;
    }

    public LocalTime getOraInizio() {
        return oraInizio;
    }

    public void setOraInizio(LocalTime oraInizio) {
        this.oraInizio = oraInizio;
    }

    public LocalTime getOraFine() {
        return oraFine;
    }

    public void setOraFine(LocalTime oraFine) {
        this.oraFine = oraFine;
    }

    public int getRipetizioniGiornaliere() {
        return ripetizioniGiornaliere;
    }

    public void setRipetizioniGiornaliere(int ripetizioniGiornaliere) {
        this.ripetizioniGiornaliere = ripetizioniGiornaliere;
    }

    public String getTelefonoRiferimento() {
        return telefonoRiferimento;
    }

    public void setTelefonoRiferimento(String telefonoRiferimento) {
        this.telefonoRiferimento = telefonoRiferimento;
    }

    public boolean isAttivo() {
        return attivo;
    }

    public void setAttivo(boolean attivo) {
        this.attivo = attivo;
    }

    public LocalDateTime getDataCreazione() {
        return dataCreazione;
    }

    public void setDataCreazione(LocalDateTime dataCreazione) {
        this.dataCreazione = dataCreazione;
    }
}
