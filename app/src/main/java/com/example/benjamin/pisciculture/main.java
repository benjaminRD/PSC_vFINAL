/*
 * Application : Pisciculture
 * Version : v1 du 24.05.2015
 * Auteur : RABOT DAMORE Benjamin
 * Support : benjamin.damore@outlook.com
 * Fichier : main.java
 * Description : Il s'agit du code de la page principale de l'application, qui donne accès aux 8 bassins et
 * aux paramètres. Si la connexion Wifi est incorrecte, c'est également sur cette page que l'erreur
 * s'affichera
 * Fichier .xml associé : main.xml
 */

package com.example.benjamin.pisciculture;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class main extends Activity {

    // Variable qui contiendra l'état de la connexion
    Boolean isConnect = false;
    // Création d'un objet de la classe "connectVerif", qui permettra de vérifier l'état de la connexion
    connectVerif cV;
    // Initialisation du bouton "openSettings", qui permet d'accéder aux paramètres
    Button btn_openSettings;
    // Initialisation des boutons d'accès aux bassins
    Button btn_bassin1;
    Button btn_bassin2;
    Button btn_bassin3;
    Button btn_bassin4;
    Button btn_bassin5;
    Button btn_bassin6;
    Button btn_bassin7;
    Button btn_bassin8;
    // Initialisation du label d'information si la connexion est incorrecte
    TextView lbl_info_connexion;
    // Initialisation de l'image d'erreur qui apparaittra si la connexion est incorrecte
    ImageView img_erreur;

    /**
     * Cette méthode est appelée à la création de l'activité
     * Elle sert à initialiser l'activité ainsi que toutes les données nécessaires à cette dernière.
     * @param savedInstanceState - Dernier état de l'activité connu
     **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Association avec le fichier.xml pour l'affichage
        setContentView(R.layout.main);

        // Attribution des différents éléments de l'UI à une variable
        btn_bassin1 = (Button) findViewById(R.id.btn_bassin1);
        btn_bassin2 = (Button) findViewById(R.id.btn_bassin2);
        btn_bassin3 = (Button) findViewById(R.id.btn_bassin3);
        btn_bassin4 = (Button) findViewById(R.id.btn_bassin4);
        btn_bassin5 = (Button) findViewById(R.id.btn_bassin5);
        btn_bassin6 = (Button) findViewById(R.id.btn_bassin6);
        btn_bassin7 = (Button) findViewById(R.id.btn_bassin7);
        btn_bassin8 = (Button) findViewById(R.id.btn_bassin8);
        lbl_info_connexion = (TextView) findViewById(R.id.lbl_info_connexion);
        lbl_info_connexion.setVisibility(View.GONE);
        // Par défaut, les éléments qui indiquent une erreur de connexion sont cachés
        img_erreur = (ImageView) findViewById(R.id.img_erreur);
        img_erreur.setVisibility(View.GONE);
    }

    /**
     * Début d’éxecution de l'activité (passage au premier plan)
     **/
    @Override
    protected void onStart() {
        super.onStart();

        // Vérification du statut de la connexion sur le réseau Wifi configuré dans les paramètres
        cV = new connectVerif(getApplicationContext());
        isConnect = cV.isConnectingToPisciculture();

        // Si le smartphone n'est pas connecté au réseau Wifi configuré
        if (!isConnect) {
            // L'accès aux bassins est désactivé
            btn_bassin1.setEnabled(false);
            btn_bassin1.setBackgroundColor(-7829368);
            btn_bassin2.setEnabled(false);
            btn_bassin2.setBackgroundColor(-7829368);
            btn_bassin3.setEnabled(false);
            btn_bassin3.setBackgroundColor(-7829368);
            btn_bassin4.setEnabled(false);
            btn_bassin4.setBackgroundColor(-7829368);
            btn_bassin5.setEnabled(false);
            btn_bassin5.setBackgroundColor(-7829368);
            btn_bassin6.setEnabled(false);
            btn_bassin6.setBackgroundColor(-7829368);
            btn_bassin7.setEnabled(false);
            btn_bassin7.setBackgroundColor(-7829368);
            btn_bassin8.setEnabled(false);
            btn_bassin8.setBackgroundColor(-7829368);
            // Les éléments qui indiquent une erreur de connexion sont visibles
            lbl_info_connexion.setVisibility(View.VISIBLE);
            img_erreur.setVisibility(View.VISIBLE);
        }

        // "Mise sur écoute" du bouton d'accès aux paramètres
        btn_openSettings = (Button) findViewById(R.id.btn_openSettings);
        btn_openSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initialisation d'un intent sur l'activié "settings"
                Intent intent_openSettings = new Intent(v.getContext(), settings.class);
                // Ouverture de l'activité
                v.getContext().startActivity(intent_openSettings);
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
    protected void onPause() { super.onPause(); }

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
     * Fonction qui ouvre permet d'avoir accès à la gestion d'un bassin
     * @param button, qui correspond à la vue d'un bouton pour pouvoir obtenir ses paramètres
     **/
    public void selectBassin(View button) {
        // Récupération de l'identifiant du bassin sélectionné
        Button btn_select = (Button)button;
        String bassin_select = btn_select.getText().toString();
        bassin_select =  bassin_select.substring(bassin_select.length() - 1);
        // Initialisation d'un intent sur l'activié "bassinx"
        Intent intent_bassin = new Intent(this, bassinx.class);
        // La variable "id_bassin" qui vaudra "bassin_select" (identifiant du bassin passé en "extra")
        intent_bassin.putExtra("id_bassin", bassin_select);
        // Ouverture de l'activité
        startActivity(intent_bassin);
    }

}

