package com.golkov.inventv.model.entities;

import java.security.Timestamp;

public class ObjektEntity {

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getTypID() {
        return typID;
    }

    public void setTypID(int typID) {
        this.typID = typID;
    }

    public int getAblageortID() {
        return ablageortID;
    }

    public void setAblageortID(int ablageortID) {
        this.ablageortID = ablageortID;
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

    private int ID;
    private int typID;
    private int ablageortID;
    private int inventarnummer;
    private String hersteller;
    private String modell;
    private Timestamp kaufdatum;
    private float einzelpreis;

}
