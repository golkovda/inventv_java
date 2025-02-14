package com.golkov.inventv.model.entities;

import jakarta.persistence.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;


@Entity
@Table(name="Objekt")
public class ObjektEntity {
    public ObjektEntity(int ID, TypEntity typ, AblageortEntity ablageort, int inventarnummer, String hersteller, String modell, LocalDate kaufdatum, float einzelpreis) {
        this.ID = ID;
        this.typ = typ;
        this.ablageort = ablageort;
        this.inventarnummer = inventarnummer;
        this.hersteller = hersteller;
        this.modell = modell;
        this.kaufdatum = kaufdatum;
        this.einzelpreis = einzelpreis;
    }

    public ObjektEntity() {}

    //region getters and setters

    public int getID() {
        return ID;
    }

    public void setID(int i) {
        this.ID = i;
    }

    public TypEntity getTyp() {
        return typ;
    }

    public void setTyp(TypEntity typ) {
        this.typ = typ;
    }

    public AblageortEntity getAblageort() {
        return ablageort;
    }

    public void setAblageort(AblageortEntity ablageort) {
        this.ablageort = ablageort;
    }

    public int getInventarnummer() {
        return inventarnummer;
    }

    public void setInventarnummer(int inventarnummer) {
        this.inventarnummer = inventarnummer;
    }

    public String getHersteller() {
        return hersteller;
    }

    public void setHersteller(String hersteller) {
        this.hersteller = hersteller;
    }

    public String getModell() {
        return modell;
    }

    public void setModell(String modell) {
        this.modell = modell;
    }

    public LocalDate getKaufdatum() {
        return kaufdatum;
    }

    public void setKaufdatum(LocalDate kaufdatum) {
        this.kaufdatum = kaufdatum;
    }

    public float getEinzelpreis() {
        return einzelpreis;
    }

    public void setEinzelpreis(float einzelpreis) {
        this.einzelpreis = einzelpreis;
    }

    //endregion

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="ObjektID", nullable = false)
    private int ID;

    @OneToOne
    @JoinColumn(name="TypID",referencedColumnName = "TypID")
    private TypEntity typ;

    @OneToOne
    @JoinColumn(name="AblageortID",referencedColumnName = "AblageortID")
    private AblageortEntity ablageort;

    @Column(name="Inventarnummer", nullable = false)
    private int inventarnummer;

    @Column(name="Hersteller", length=100, nullable = false)
    private String hersteller;

    @Column(name="Modell", length=100, nullable = false)
    private String modell;

    @Column(name="Kaufdatum", nullable = false)
    @Temporal(TemporalType.DATE)
    private LocalDate kaufdatum;

    @Column(name="Einzelpreis", nullable = false)
    private float einzelpreis;


}
