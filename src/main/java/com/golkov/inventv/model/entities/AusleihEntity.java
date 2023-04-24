package com.golkov.inventv.model.entities;

import java.security.Timestamp;

public class AusleihEntity {

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getBenutzerID() {
        return benutzerID;
    }

    public void setBenutzerID(int benutzerID) {
        this.benutzerID = benutzerID;
    }

    public int getObjektID() {
        return objektID;
    }

    public void setObjektID(int objektID) {
        this.objektID = objektID;
    }

    public Timestamp getAusleihdatum() {
        return ausleihdatum;
    }

    public void setAusleihdatum(Timestamp ausleihdatum) {
        this.ausleihdatum = ausleihdatum;
    }

    public boolean isAbgegeben() {
        return abgegeben;
    }

    public void setAbgegeben(boolean abgegeben) {
        this.abgegeben = abgegeben;
    }

    private int ID;
    private int benutzerID;
    private int objektID;
    private Timestamp ausleihdatum;
    private boolean abgegeben;

}
