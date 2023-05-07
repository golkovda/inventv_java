package com.golkov.inventv;

public class Globals {

    //region Getters and setters

    public static String getCurrent_user() {
        return current_user;
    }

    public static void setCurrent_user(String current_user) {
        Globals.current_user = current_user;
    }


    //endregion

    public static String current_user;

}
