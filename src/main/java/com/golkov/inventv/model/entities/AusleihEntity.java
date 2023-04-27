package com.golkov.inventv.model.entities;

import javax.persistence.*;
import java.security.Timestamp;

@Entity
@Table(name = "Ausleihe")
public class AusleihEntity {

    //region getters and setters
    public int getID() {
        return ID;
    }

    public BenutzerEntity getBenutzer() {
        return benutzer;
    }

    public void setBenutzer(BenutzerEntity benutzer) {
        this.benutzer = benutzer;
    }

    public ObjektEntity getObjekt() {
        return objekt;
    }

    public void setObjekt(ObjektEntity objekt) {
        this.objekt = objekt;
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

    //endregion

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="AusleihID", nullable = false)
    private int ID;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="BenutzerID",referencedColumnName = "BenutzerID")
    private BenutzerEntity benutzer;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="ObjektID",referencedColumnName = "ObjektID")
    private ObjektEntity objekt;
    @Column(name="Ausleihdatum", nullable = false)
    @Temporal(TemporalType.DATE)
    private Timestamp ausleihdatum;
    @Column(name="Abgegeben", nullable = false)
    private boolean abgegeben;

}
