package com.golkov.inventv;

public class Globals {

    //region Getters and setters

    public static String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        Globals.url = url;
    }

    public static String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        Globals.username = username;
    }

    public static String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        Globals.password = password;
    }

    //endregion

    public static String url = "jdbc:jtds:sqlserver://localhost:1433/InventV2";
    public static String username = "inventvadmin";
    public static String password = "ynbayw";

}
