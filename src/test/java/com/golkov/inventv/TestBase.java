package com.golkov.inventv;

import com.golkov.inventv.model.entities.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;

public class TestBase {

    private ObservableList<BenutzerEntity> benutzer_mock = FXCollections.observableArrayList();
    private ObservableList<AusleihEntity> ausleih_mock = FXCollections.observableArrayList();
    private ObservableList<ObjektEntity> objekt_mock= FXCollections.observableArrayList();;
    private ObservableList<AblageortEntity> ablageort_mock = FXCollections.observableArrayList();;
    private ObservableList<TypEntity> typ_mock = FXCollections.observableArrayList();;

    public TestBase(){
        generateTypen();
        generateAblageorte();
        generateObjekte();
        generateBenutzer();
        generateAusleihen();
    }

    private void generateBenutzer(){
        benutzer_mock.add(new BenutzerEntity(1,"golkovda", "Golkov", "Daniel", true));
        benutzer_mock.add(new BenutzerEntity(2,"golkovma", "Golkov", "Marc-Leon", false));
        benutzer_mock.add(new BenutzerEntity(3,"golkovju", "Golkov", "Jurij", false));
        benutzer_mock.add(new BenutzerEntity(4,"freukesbe", "Freukes", "Benjamin", false));
    }

    private void generateObjekte(){
        objekt_mock.add(new ObjektEntity(1, typ_mock.get(0), ablageort_mock.get(0), 1, "Sony", "alpha6000", LocalDate.of(2023,5,5), 999.99F));
        objekt_mock.add(new ObjektEntity(2, typ_mock.get(1), ablageort_mock.get(0), 1, "Kingston", "64GB", LocalDate.of(2023,5,2), 22.99F));
        objekt_mock.add(new ObjektEntity(3, typ_mock.get(2), ablageort_mock.get(0), 1, "Apple", "Beats Studio", LocalDate.of(2023,5,2), 240.45F));
    }

    private void generateTypen(){
        typ_mock.add(new TypEntity(1, "Kamera"));
        typ_mock.add(new TypEntity(2, "SD-Karte"));
        typ_mock.add(new TypEntity(3, "Kopfhörer"));
    }

    private void generateAblageorte(){
        ablageort_mock.add(new AblageortEntity(1, "Schrank"));
        ablageort_mock.add(new AblageortEntity(2, "Schublade"));
    }

    private void generateAusleihen(){
        ausleih_mock.add(new AusleihEntity(1,benutzer_mock.get(0),objekt_mock.get(0),LocalDate.of(2023,5,20), false));
        ausleih_mock.add(new AusleihEntity(2,benutzer_mock.get(0),objekt_mock.get(1),LocalDate.of(2023,5,12), true));
        ausleih_mock.add(new AusleihEntity(3,benutzer_mock.get(3),objekt_mock.get(2),LocalDate.of(2023,5,2), false));
    }

    public ObservableList<BenutzerEntity> getBenutzer_mock() {
        return benutzer_mock;
    }

    public ObservableList<AusleihEntity> getAusleih_mock() {
        return ausleih_mock;
    }

    public ObservableList<ObjektEntity> getObjekt_mock() {
        return objekt_mock;
    }

    public ObservableList<AblageortEntity> getAblageort_mock() {
        return ablageort_mock;
    }

    public ObservableList<TypEntity> getTyp_mock() {
        return typ_mock;
    }
}
