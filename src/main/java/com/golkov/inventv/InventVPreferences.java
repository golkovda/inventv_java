package com.golkov.inventv;

import java.util.prefs.Preferences;

public class InventVPreferences {

    private static final String PREFS_NODE = "com.golkov.inventv"; // Der Name des Knotens

    public static void saveServerCredentials(String shortserverurl, String serverurl, String serverusername, String serverpassword, boolean isdefault) {
        Preferences prefs = Preferences.userRoot().node(PREFS_NODE);
        prefs.put("serverurl", serverurl);
        prefs.put("shortserverurl", shortserverurl);
        prefs.put("serverusername", serverusername);
        prefs.put("serverpassword", serverpassword);
        prefs.put("isdefault", String.valueOf(isdefault));
    }

    public static String getShortServerUrl() {
        Preferences prefs = Preferences.userRoot().node(PREFS_NODE);
        return prefs.get("shortserverurl", "");
    }

    public static String getServerUrl() {
        Preferences prefs = Preferences.userRoot().node(PREFS_NODE);
        return prefs.get("serverurl", "");
    }

    public static String getServerUsername() {
        Preferences prefs = Preferences.userRoot().node(PREFS_NODE);
        return prefs.get("serverusername", "");
    }

    public static String getServerPassword() {
        Preferences prefs = Preferences.userRoot().node(PREFS_NODE);
        return prefs.get("serverpassword", "");
    }

    public static String isDefaultServerSet(){
        Preferences prefs = Preferences.userRoot().node(PREFS_NODE);
        return prefs.get("isdefault", "");
    }
}
