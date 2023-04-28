package com.golkov.inventv.model.entities;

import jakarta.persistence.*;

@Entity
@Table(name="Typ")
public class TypEntity {

    //region getters and setters

    public int getID() {
        return ID;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    //endregion

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="TypID", nullable = false)
    private int ID;

    @Column(name="Bezeichnung", length=50, nullable = false)
    private String bezeichnung;

}
