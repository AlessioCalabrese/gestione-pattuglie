package com.vigilanza.pattuglie.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_obiettivo", nullable = false, length = 20)
    private TipoObiettivo tipoObiettivo = TipoObiettivo.ISPEZIONE;

    /**
     * Fasce orarie di servizio. Ogni fascia è legata a un giorno della settimana; un giorno può avere più
     * fasce (es. mattina e sera) e giorni diversi possono avere fasce diverse. Vuoto = mai visibile per le
     * pattuglie (va configurato esplicitamente).
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "obiettivo_fascia_oraria", joinColumns = @JoinColumn(name = "obiettivo_id"))
    private List<FasciaOraria> fasceOrarie = new ArrayList<>();

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

    /** Fasce orarie configurate per il giorno indicato, in ordine di orario di inizio (le "aperte" prima). */
    public List<FasciaOraria> fasceDiGiorno(GiornoSettimana giorno) {
        return fasceOrarie.stream()
                .filter(f -> f.getGiorno() == giorno)
                .sorted(Comparator.comparing(f -> f.getOraInizio() == null ? LocalTime.MIN : f.getOraInizio()))
                .toList();
    }

    /** Vero se l'obiettivo ha almeno una fascia oraria configurata per il giorno indicato. */
    public boolean isGiornoAttivo(GiornoSettimana giorno) {
        return fasceOrarie.stream().anyMatch(f -> f.getGiorno() == giorno);
    }

    /**
     * Vero se l'obiettivo riguarda la data indicata: o perché quel giorno della settimana ha almeno una
     * fascia propria, o perché il giorno precedente ha una fascia notturna la cui coda sconfina in questa
     * data (es. una fascia di lunedì 22:00–06:00 riguarda anche il martedì mattina). Un obiettivo senza
     * nessuna fascia configurata non riguarda mai nessuna data: la pianificazione va sempre indicata.
     */
    public boolean isRilevantePer(LocalDate data) {
        GiornoSettimana oggi = GiornoSettimana.daDayOfWeek(data.getDayOfWeek());
        GiornoSettimana ieri = GiornoSettimana.daDayOfWeek(data.minusDays(1).getDayOfWeek());
        return isGiornoAttivo(oggi) || fasceDiGiorno(ieri).stream().anyMatch(FasciaOraria::isNotturna);
    }

    /**
     * Vero se, alla data e ora indicate, l'obiettivo è in servizio secondo la pianificazione configurata.
     * Considera sia le fasce del giorno stesso (per una fascia notturna, solo la parte serale che cade
     * oggi) sia la coda di un'eventuale fascia notturna del giorno precedente che sconfina in questa
     * mattina. Un obiettivo senza nessuna fascia configurata non è mai in servizio per le pattuglie: la
     * pianificazione va sempre impostata esplicitamente in fase di creazione.
     */
    public boolean isInServizio(LocalDate data, LocalTime ora) {
        GiornoSettimana oggi = GiornoSettimana.daDayOfWeek(data.getDayOfWeek());
        GiornoSettimana ieri = GiornoSettimana.daDayOfWeek(data.minusDays(1).getDayOfWeek());

        boolean copertoDaOggi = fasceDiGiorno(oggi).stream().anyMatch(f ->
                f.isNotturna() ? !ora.isBefore(f.getOraInizio()) : f.contiene(ora));
        boolean copertoDallaNotteDiIeri = fasceDiGiorno(ieri).stream()
                .anyMatch(f -> f.isNotturna() && !ora.isAfter(f.getOraFine()));
        return copertoDaOggi || copertoDallaNotteDiIeri;
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

    public List<FasciaOraria> getFasceOrarie() {
        return fasceOrarie;
    }

    public void setFasceOrarie(List<FasciaOraria> fasceOrarie) {
        this.fasceOrarie = fasceOrarie;
    }

    public TipoObiettivo getTipoObiettivo() {
        return tipoObiettivo;
    }

    public void setTipoObiettivo(TipoObiettivo tipoObiettivo) {
        this.tipoObiettivo = tipoObiettivo;
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
