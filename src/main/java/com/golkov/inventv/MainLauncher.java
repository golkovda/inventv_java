package com.golkov.inventv;
/*
Notwendig, da maven-packaging diesen Workaround braucht.
Siehe pom.xml:
    Wenn Klasse 'Main' als Startpunkt angegeben wird, kommt Fehlermeldung:
    -> "Fehler: Zum Ausführen dieser Anwendung benötigte JavaFX-Runtime-Komponenten fehlen"
https://stackoverflow.com/questions/70806482/how-can-i-create-an-all-in-one-jar-for-a-javafx-project-with-the-maven-assembly
 */
public class MainLauncher {
    public static void main(String[] args){
        Main.main(args);
    }
}
