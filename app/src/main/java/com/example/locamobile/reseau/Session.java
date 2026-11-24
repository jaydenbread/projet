package com.example.locamobile.reseau;

import android.content.Context;
import android.content.SharedPreferences;

public class Session {
    private static final String NOM_FICHIER = "session";
    private static final String CLE_JETON = "jeton";
    private static final String CLE_NOM = "nom";

    public static void enregistrer(Context context, String jeton, String nom) {
        SharedPreferences prefs = context.getSharedPreferences(NOM_FICHIER, Context.MODE_PRIVATE);
        prefs.edit()
                .putString(CLE_JETON, jeton)
                .putString(CLE_NOM, nom)
                .apply();
    }

    public static String jeton(Context context) {
        return context.getSharedPreferences(NOM_FICHIER, Context.MODE_PRIVATE)
                .getString(CLE_JETON, null);
    }

    public static String nom(Context context) {
        return context.getSharedPreferences(NOM_FICHIER, Context.MODE_PRIVATE)
                .getString(CLE_NOM, null);
    }
}