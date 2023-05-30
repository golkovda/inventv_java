package com.golkov.inventv.model.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Ausleihe")
public class AusleihEntity {

    public AusleihEntity(int ID, BenutzerEntity benutzer, ObjektEntity objekt, LocalDate ausleihdatum, boolean abgegeben) {
        this.ID = ID;
        this.benutzer = benutzer;
        this.objekt = objekt;
        this.ausleihdatum = ausleihdatum;
        this.abgegeben = abgegeben;
    }

    public AusleihEntity() {}

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

    public LocalDate getAusleihdatum() {
        return ausleihdatum;
    }

    public void setAusleihdatum(LocalDate ausleihdatum) {
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

    @ManyToOne()
    @JoinColumn(name="BenutzerID",referencedColumnName = "BenutzerID")
    private BenutzerEntity benutzer;

    @OneToOne()
    @JoinColumn(name="ObjektID",referencedColumnName = "ObjektID")
    private ObjektEntity objekt;

    @Column(name="Ausleihdatum", nullable = false)
    @Temporal(TemporalType.DATE)
    private LocalDate ausleihdatum;

    @Column(name="Abgegeben", nullable = false)
    private boolean abgegeben;

}
