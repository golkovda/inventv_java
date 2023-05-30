package com.golkov.inventv.model.entities;

import jakarta.persistence.*;

@Entity
@Table(name="Ablageort")
public class AblageortEntity implements TAEntity{

    public AblageortEntity(int ID, String bezeichnung) {
        this.ID = ID;
        this.bezeichnung = bezeichnung;
    }

    public AblageortEntity() {}

    //region getters and setters

    public int getID() {
        return ID;
    }

    public void setID(int id){
        this.ID = id;
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
    @Column(name="AblageortID", nullable = false)
    private int ID;

    @Column(name="Bezeichnung", length=50, nullable = false)
    private String bezeichnung;

}
