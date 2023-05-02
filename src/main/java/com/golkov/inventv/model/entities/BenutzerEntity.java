package com.golkov.inventv.model.entities;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="Benutzer")
public class BenutzerEntity implements Serializable {

    public BenutzerEntity() {}

    public BenutzerEntity(String kennung, String nachname, String vorname, boolean administrator) {
        this.kennung = kennung;
        this.nachname = nachname;
        this.vorname = vorname;
        this.administrator = administrator;
    }

    //region getters and setters

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getKennung() {
        return kennung;
    }

    public void setKennung(String kennung) {
        this.kennung = kennung;
    }

    public String getNachname() {
        return nachname;
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isAdministrator() {
        return administrator;
    }

    public void setAdministrator(boolean administrator) {
        this.administrator = administrator;
    }

    /*public Set<AusleihEntity> getAusleihen() {
        return ausleihen;
    }

    public void setAusleihen(Set<AusleihEntity> ausleihen) {
        this.ausleihen = ausleihen;
    }*/

    //endregion

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="BenutzerID")
    private int ID;

    @Column(name="Kennung", length=20, nullable = false)
    private String kennung;

    @Column(name="Nachname", length=50, nullable = false)
    private String nachname;

    @Column(name="Vorname", length=50, nullable = false)
    private String vorname;

    @Column(name="Telefon", length=20)
    private String telefon;
    @Column(name="EMail", length=50)
    private String email;

    @Column(name="Administrator", nullable = false)
    private boolean administrator;

   /* @OneToMany(mappedBy="Benutzer", cascade = CascadeType.ALL)
    private Set<AusleihEntity> ausleihen = new HashSet<>();*/


}
