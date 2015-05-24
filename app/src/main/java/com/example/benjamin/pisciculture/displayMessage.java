/*
 * Application : Pisciculture
 * Version : v1 du 24.05.2015
 * Auteur : RABOT DAMORE Benjamin
 * Support : benjamin.damore@outlook.com
 * Fichier : displayMessage.java
 * Description : Il s'agit du code qui permet l'affichage de message sur l'UI, par exemple
 * un message d'erreur
 * Fichier .xml associé : Aucun
 */

package com.example.benjamin.pisciculture;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class displayMessage {

    /**
     * Constructeur par défaut de la classe displayMessage
     **/
    public displayMessage() {
    }

    /**
     * Fonction qui affiche une boite de dialogue
     * @param context - Context de l'application
     * @param title - Titre de la boite d'alerte
     * @param message - Message dde la boite d'alerte
    **/
    public void displayAlertDialog(Context context, String title, String message, Boolean etat) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton("Fermer", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }

}
