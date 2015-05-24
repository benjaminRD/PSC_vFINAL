/*
 * Application : Pisciculture
 * Version : v1 du 24.05.2015
 * Auteur : RABOT DAMORE Benjamin
 * Support : benjamin.damore@outlook.com
 * Fichier : login.java
 * Description : Il s'agit du code qui permet d'authentifier l'utilisateur
 * Fichier .xml associé : login.xml
 */

package com.example.benjamin.pisciculture;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.widget.EditText;

public class login extends Activity {

    // Création d'un objet de la classe messageData, qui permettra l'affiche de message
    displayMessage messageLogin;
    // Initialisation du champ de saisie du mot de passe
    EditText var_field_password;

    /**
     * Cette méthode est appelée à la création de l'activité
     * Elle sert à initialiser l'activité ainsi que toutes les données nécessaires à cette dernière.
     * @param savedInstanceState - Dernier état de l'activité connu
     **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Association avec le fichier.xml pour l'affichage
        setContentView(R.layout.login);

        // Attribution des différents éléments de l'UI à une variable
        var_field_password = (EditText)findViewById(R.id.field_password);
        var_field_password.setGravity(Gravity.CENTER);
    }

    /**
     * Début d’éxecution de l'activité (passage au premier plan)
     **/
    @Override
    protected void onStart() {
        super.onStart();

        // Vérification du mot de passe lorsque l'utilisateur a entré 4 caractères
        var_field_password.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // La vérification se déclenche lorsqu'il y a 4 caractères saisis
                if (s.length() == 4) {
                    // Obtention de la saisie de l'utilisateur
                    String val_input_passaword = var_field_password.getText().toString();
                    // Récupération du mot de passe définie dans les paramètres
                    SharedPreferences sharedPreferences = getSharedPreferences("settings", 0);
                    final String password = sharedPreferences.getString("password", "0000");
                    // Vérification du mot de passe
                    if (val_input_passaword.equals(password)) {
                        // Ouverture de l'activité principale, avec la liste des bassins
                        Intent intent_openMain = new Intent(login.this, main.class);
                        startActivity(intent_openMain);
                    } else {
                        // Le champs de saisie est vidé
                        var_field_password.setText("");
                        // Initialisation d'une boite d'information et affichage d'un message d'erreur
                        messageLogin = new displayMessage();
                        messageLogin.displayAlertDialog(login.this, "Erreur", "Le mot de passe est incorrect", false);
                    }
                }
            }

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
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

}
