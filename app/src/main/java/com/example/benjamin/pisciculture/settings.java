/*
 * Application : Pisciculture
 * Version : v1 du 24.05.2015
 * Auteur : RABOT DAMORE Benjamin
 * Support : benjamin.damore@outlook.com
 * Fichier : settings.java
 * Description : Il s'agit du code qui permet l'affichage et la modification des paramètres de
 * l'application (Réseau Wifi, adresse et port du serveur, mot de passe d'accès)
 * Fichier .xml associé : settings.xml
 */

package com.example.benjamin.pisciculture;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class settings extends Activity {

    // Initialisation de la variable isConnect, qui contiendra l'état de la connexion
    Boolean isConnect = false;
    // Création d'un objet de la classe connectVerif, qui permettra de vérifier l'état de la connexion
    connectVerif cf;
    // Variable qui contiendra l'identifiant d'une eventuelle erreur
    String id_erreur;
    // Création d'un objet de la classe messageData, qui permettra l'affiche de message
    displayMessage messageInfoConnexion;
    // Initialisation du boutton "obtenir_statut_connexion", qui permettra de vérifier l'état de la connexion
    Button btn_obtenir_statut_connexion;
    // Initialisation du boutton "modifier_parametres", qui permettra d'enregistrer les paramètres
    Button btn_modifier_parametres;
    // Initialisation des différents champs de saisies des paramètres
    EditText var_field_ip_serveur;
    EditText var_field_port_serveur;
    EditText var_field_wifi;
    EditText var_field_password;

    /**
     * Cette méthode est appelée à la création de l'activité
     * Elle sert à initialiser l'activité ainsi que toutes les données nécessaires à cette dernière.
     * @param savedInstanceState - Dernier état de l'activité connu
     **/
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Récupération de la variable "id_bassin"
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id_erreur = extras.getString("id_erreur");
            // Si l'identifiant de l'erreur est "-1"
            if (id_erreur.equals("-1")) {
                // Initialisation d'une boite d'information et affichage d'un message d'erreur
                messageInfoConnexion = new displayMessage();
                messageInfoConnexion.displayAlertDialog(settings.this, "Erreur", "Impssible de contacter le serveur, vérifiez les paramètres", false);
            }
        }

        // Association avec le fichier.xml pour l'affichage
        setContentView(R.layout.settings);

        // Attribution des différents éléments de l'UI à une variable
        btn_obtenir_statut_connexion = (Button) findViewById(R.id.btn_verif_connexion);
        btn_modifier_parametres = (Button) findViewById(R.id.btn_modifier_parametres);
        var_field_ip_serveur = (EditText) findViewById(R.id.field_ip);
        var_field_port_serveur = (EditText) findViewById(R.id.field_port);
        var_field_wifi = (EditText) findViewById(R.id.field_wifi);
        var_field_password = (EditText) findViewById(R.id.field_password);
    }

    /**
     * Début d’éxecution de l'activité (passage au premier plan)
     **/
    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences sharedPreferences;
        // Affichage du nom du réseau Wifi enregistré
        sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        var_field_wifi.setGravity(Gravity.CENTER);
        var_field_wifi.setText(sharedPreferences.getString("wifi", "Pisciculture"));
        var_field_wifi.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (hasFocus)
                    var_field_wifi.setText("");
            }
        });
        // Affichage de l'IP serveur enregistrée
        sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        var_field_ip_serveur.setGravity(Gravity.CENTER);
        var_field_ip_serveur.setText(sharedPreferences.getString("ip_serveur", "192.168.66.164"));
        var_field_ip_serveur.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (hasFocus)
                    var_field_ip_serveur.setText("");
            }
        });
        // Affichage du port serveur enregistré
        sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        var_field_port_serveur.setGravity(Gravity.CENTER);
        var_field_port_serveur.setText(sharedPreferences.getString("port_serveur", "1234"));
        var_field_port_serveur.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (hasFocus)
                    var_field_port_serveur.setText("");
            }
        });
        // Affichage du mot de passe enregistré
        sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        var_field_password.setGravity(Gravity.CENTER);
        var_field_password.setText(sharedPreferences.getString("password", "0000"));
        var_field_password.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (hasFocus)
                    var_field_password.setText("");
            }
        });

        // "Mise sur écoute" du bouton pour obtenir le statut de la connexion
        final SharedPreferences finalSharedPreferences = sharedPreferences;
        btn_obtenir_statut_connexion.setOnClickListener(new View.OnClickListener() {

            /** Fonction qui s'éxécute lorsque l'on appuie sur le bouton login
             * @param v - Vue associée à l'activité
             */
            @Override
            public void onClick(View v) {
                // Vérification du statut de la connexion sur le réseau Wifi Pisciculture
                cf = new connectVerif(getApplicationContext());
                isConnect = cf.isConnectingToPisciculture();
                if (isConnect) {
                    // Initialisation d'une boite d'information et affichage d'un message de succès
                    messageInfoConnexion = new displayMessage();
                    messageInfoConnexion.displayAlertDialog(settings.this, "Succès !", "Vous êtes correctement connecté au réseau Wifi " + finalSharedPreferences.getString("wifi", ""), true);
                } else {
                    // Initialisation d'une boite d'information et affichage d'un message d'erreur
                    messageInfoConnexion = new displayMessage();
                    messageInfoConnexion.displayAlertDialog(settings.this, "Erreur", "Vous n'êtes pas connecté au réseau Wifi " + finalSharedPreferences.getString("wifi", ""), false);
                }
            }
        });

        // "Mise sur écoute" du bouton pour enregistrer les paramètres
        btn_modifier_parametres.setOnClickListener(new View.OnClickListener() {

            /** Fonction qui s'éxécute lorsque l'on appuie sur le bouton "Enregistrer les parametres"
             * @param v - Vue associée à l'activité
             */
            @Override
            public void onClick(View v) {
                // Obtention de la saisie de l'utilisateur
                String val_input_ip_serveur = var_field_ip_serveur.getText().toString();
                String val_imput_port_serveur = var_field_port_serveur.getText().toString();
                String val_input_wifi = var_field_wifi.getText().toString();
                String val_imput_password = var_field_password.getText().toString();

                // Si un des champs est vide, affichage d'un message d'erreur
                if (val_input_wifi.length() == 0 || val_input_ip_serveur.length() == 0 || val_imput_port_serveur.length() == 0 || val_imput_password.length() == 0) {
                    // Initialisation d'une boite d'information et affichage d'un message d'erreur
                    messageInfoConnexion = new displayMessage();
                    messageInfoConnexion.displayAlertDialog(settings.this, "Erreur", "Tous les paramètres doivent être renseignés", false);                    // Enregistrement des paramètres
                }
                // Sinon la saisie est enregistrée
                else {
                    SavePreferences("ip_serveur", val_input_ip_serveur);
                    SavePreferences("port_serveur", val_imput_port_serveur);
                    SavePreferences("wifi", val_input_wifi);
                    SavePreferences("password", val_imput_password);
                    // Initialisation d'une boite d'information et affichage d'un message de succès
                    messageInfoConnexion = new displayMessage();
                    messageInfoConnexion.displayAlertDialog(settings.this, "Succès !", "Le réseau Wifi est                      " + val_input_wifi + "\nL'adresse IP du serveur est " + val_input_ip_serveur + "\nLe port est " + val_imput_port_serveur + "\nLe mot de passe est " + val_imput_password, true);
                }
            }

        });

    }

    /**
     * Cette méthode est appelée après OnStart
     **/
    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Cette méthode est appelée si une autre activité passe au premier plan
     **/
    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * Cette méthode est appelée quand l'activité n’est plus du tout visible quelque soit la raison
     **/
    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * Cette méthode est appelée quand l'application est totalement fermée (Processus terminé)
     **/
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Fonction qui permet d'enregistrer les paramètres de l'application
     * @param key - Nom du paramètre
     * @param value - Valeur à enregistrée
     **/
    private void SavePreferences(String key, String value){
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

}
