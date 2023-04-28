package com.golkov.inventv.model.entities;

import jakarta.persistence.*;
import java.security.Timestamp;

@Entity
@Table(name="Objekt")
public class ObjektEntity {

    //region getters and setters

    public int getID() {
        return ID;
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

    public Timestamp getKaufdatum() {
        return kaufdatum;
    }

    public void setKaufdatum(Timestamp kaufdatum) {
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
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="TypID",referencedColumnName = "TypID")
    private TypEntity typ;
    @OneToOne(cascade = CascadeType.ALL)
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
    private Timestamp kaufdatum;
    @Column(name="Einzelpreis", nullable = false)
    private float einzelpreis;

}
