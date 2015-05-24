/*
 * Application : Pisciculture
 * Version : v1 du 24.05.2015
 * Auteur : RABOT DAMORE Benjamin
 * Support : benjamin.damore@outlook.com
 * Fichier : bassinX.java
 * Description : Il s'agit du code de la page qui permet de visualiser l'état d'un bassin,
 * ainsi que de modifier l'état d'une vanne. Ce code est valable pour l'ensemble des bassins,
 * l'identifiant du bassin concerné étant passé et récupérer en paramètres à l'initialisation
 * de ce code.
 * Fichier .xml associé : bassinx.xml
 */

package com.example.benjamin.pisciculture;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class bassinx extends Activity {

    // Variable qui contiendra l'état de la connexion
    Boolean isConnect = false;
    // Variable qui contiendra l'identifiant du bassin à surveiller
    String id_bassin;
    // Création d'un objet de l a classe "connectVerif", qui permettra de vérifier l'état de la connexion
    connectVerif cf;
    // Création d'un objet de la classe "displayMessage", qui permettra l'affiche de message
    displayMessage messageInfoConnexion;
    // Initialisation du bouton "send", qui permettra de valider une action
    Button btn_send;
    // Initialisation du spinner, qui est la liste déroulante des actions possibles sur les vannes
    Spinner spinner_action;
    // Initialisation du label d'information qui affichera la température du bassin
    TextView etat_temp;
    // Initialisation du label d'information qui affichera le niveau d'eau
    TextView etat_jauge;
    // Initialisation du label d'information qui affichera la position de la vanne amont
    TextView etat_vam;
    // Initialisation du label d'information qui affichera la position de la vanne aval
    TextView etat_vav;
    // Initialisation du label d'information qui affichera la position de la vanne de vidage
    TextView etat_vvi;
    // Initialisation d'un ScheduledExecutorService qui permettra l'exécution d'un code périodiquement
    final ScheduledExecutorService update_etat_bassin = Executors.newSingleThreadScheduledExecutor();

    /**
     * Cette méthode est appelée à la création de l'activité
     * Elle sert à initialiser l'activité ainsi que toutes les données nécessaires à cette dernière.
     * @param savedInstanceState - Dernier état de l'activité connu
     **/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Récupération de la variable "id_bassin"
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id_bassin = extras.getString("id_bassin");
        }
        // Définition du titre affiché en fonction du bassin demandé
        setTitle("Gestion du bassin "+id_bassin);

        // Association avec le fichier.xml pour l'affichage
        setContentView(R.layout.bassinx);

        // Attribution des différents éléments de l'UI à une variable
        btn_send = (Button) findViewById(R.id.btn_send);
        spinner_action = (Spinner) findViewById(R.id.spinner_action);
        etat_temp = (TextView) findViewById(R.id.etat_temp);
        etat_jauge = (TextView) findViewById(R.id.etat_jauge);
        etat_vam = (TextView) findViewById(R.id.etat_vam);
        etat_vav = (TextView) findViewById(R.id.etat_vav);
        etat_vvi = (TextView) findViewById(R.id.etat_vvi);
    }

    /**
     * Début d’éxecution de l'activité (passage au premier plan)
     **/
    @Override
    protected void onStart() {
        super.onStart();

        // Récupération des paramètres
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        // Récupération de l'adresse IP du serveur depuis les paramètres
        final String ip_serveur = sharedPreferences.getString("ip_serveur", "192.168.66.164");
        // Récupération du port du serveur depuis les paramètres
        final String port_serveur = sharedPreferences.getString("port_serveur", "1234");

        // Mise à jour de l'état du bassin toute les 30 secondes
        update_etat_bassin.scheduleWithFixedDelay(new Runnable() {

            @Override
            public void run() {
                // Vérification du statut de la connexion sur le réseau Wifi configuré dans les paramètres
                cf = new connectVerif(getApplicationContext());
                isConnect = cf.isConnectingToPisciculture();

                // Si la connexion est correcte
                if (isConnect) {
                    // Démarrage d'une tâche asynchrone
                    final String getEtat = "getState(" + id_bassin + ")";
                    new AsyncTask<Void, Void, String>() {

                        /**
                         * Fonction asynchrone (thread) qui permet d'envoyer l'action a éxécutée au serveur, puis de traiter sa réponse
                         * @param p - De type Void, pour signifier que la fonction n'attend rien en entrée
                         * @return true ou false - Résultat du bon déroulement de l'éxécution de l'opération demandée
                         */
                        @Override
                        protected String doInBackground(Void... p) {
                            // Initialisation de la socket
                            Socket socket;
                            String etatFinal;

                            try {
                                // Connexion de la socket
                                socket = new Socket(ip_serveur, Integer.parseInt(port_serveur));
                                // Ecriture dans le flux de sortie (vers le serveur) de la demande de mise à jour
                                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                                out.print(getEtat);
                                out.println();
                                out.flush();
                                // Initialisation d'un buffer d'entrée sur la socket
                                BufferedReader receiveData;
                                receiveData = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                                // Tant que la socket est connectée, on attend la réponse du serveur
                                while (socket.isConnected()) {
                                    while ((etatFinal = receiveData.readLine()) != null) {
                                        socket.close();
                                        return etatFinal;
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                return "-1";
                            }
                            // Dans le cas ou la mise à jour ne c'est pas correctement déroulé, la valeur "0" est retournée
                            return "0";
                        }

                        /**
                         * Fonction exécuté à la suite du thread précédent
                         * @param etatFinal - Il s'agit du retour du thread précédent
                         */
                        protected void onPostExecute(String etatFinal) {
                            // Si la mise à jour ne c'est pas correctement déroulée, un message d'erreur est affiché
                            if (etatFinal.length() != 10) {
                                if (etatFinal.equals("-1")) {
                                    // Initialisation d'une boite d'information et affichage d'un message d'erreur
                                    messageInfoConnexion = new displayMessage();
                                    messageInfoConnexion.displayAlertDialog(bassinx.this, "Erreur", "Une erreur est survenue au moment de contacter le serveur, vérifiez les paramètres", false);
                                    // Initialisation d'un intent sur l'activié "settings"
                                    Intent intent_settings = new Intent(bassinx.this, settings.class);
                                    // La variable "id_erreur" qui vaudra "-1" sera passé en "extra"
                                    intent_settings.putExtra("id_erreur", "-1");
                                    // Ouverture de l'activité
                                    startActivity(intent_settings);
                                    // Fin des mises à jour
                                    update_etat_bassin.shutdown();
                                }
                                else {
                                    // Initialisation d'une boite d'information et affichage d'un message d'erreur
                                    messageInfoConnexion = new displayMessage();
                                    messageInfoConnexion.displayAlertDialog(bassinx.this, "Erreur", "Une erreur est survenue au moment de la mise à jour de l'état du bassin " + id_bassin + "", false);
                                    // Fin des mises à jour
                                    update_etat_bassin.shutdown();
                                }
                            } else {
                                // Attribution de la valeur de la température
                                int temperature_finale = Integer.parseInt(etatFinal.substring(1, 6));
                                String temperature_entier = etatFinal.substring(1, 4);
                                String temperature_decimal = etatFinal.substring(4, 6);
                                // Attribution de la valeur du niveau d'eau, selon l'état de la jauge
                                String jauge = etatFinal.substring(6, 7);
                                String niveau_eau;
                                switch (jauge) {
                                    case "1":
                                        niveau_eau = "Haut";
                                        break;
                                    case "2":
                                        niveau_eau = "Inter.";
                                        break;
                                    case "3":
                                        niveau_eau = "Bas";
                                        break;
                                    default:
                                        niveau_eau = "--";
                                }
                                //Attribution de la valeur de la positionde la vanne amont
                                String vam = etatFinal.substring(7, 8);
                                String position_vam;
                                switch (vam) {
                                    case "1":
                                        position_vam = "Fermée";
                                        break;
                                    case "2":
                                        position_vam = "Inter.";
                                        break;
                                    case "3":
                                        position_vam = "Ouverte";
                                        break;
                                    default:
                                        position_vam = "--";
                                }
                                //Attribution de la valeur de la positionde la vanne aval
                                String vav = etatFinal.substring(8, 9);
                                String position_vav;
                                switch (vav) {
                                    case "1":
                                        position_vav = "Fermée";
                                        break;
                                    case "2":
                                        position_vav = "Inter.";
                                        break;
                                    case "3":
                                        position_vav = "Ouverte";
                                        break;
                                    default:
                                        position_vav = "--";
                                }
                                //Attribution de la valeur de la positionde la vanne de vidage
                                String vvi = etatFinal.substring(9, 10);
                                String position_vvi;
                                switch (vvi) {
                                    case "1":
                                        position_vvi = "Fermée";
                                        break;
                                    case "2":
                                        position_vvi = "Ouverte";
                                        break;
                                    default:
                                        position_vvi = "--";
                                }
                                // Mise à jour de l'UI selon les valeurs reçues
                                int color_alert_niv_0 = Color.parseColor("#F2F2F2");
                                int color_alert_niv_1 = Color.parseColor("#FF8000");
                                int color_alert_niv_2 = Color.parseColor("#DF0101");
                                int color_text = Color.parseColor("#000000");
                                int color_alert_text = Color.parseColor("#FFFFFF");
                                etat_temp.setText(temperature_entier + "," + temperature_decimal + "°C");
                                // Gestion de l'affichage des valeurs pour la température
                                if (temperature_finale > 1650 || temperature_finale < 800) {
                                    etat_temp.setBackgroundColor(color_alert_niv_2);
                                    etat_temp.setTextColor(color_alert_text);
                                } else if (temperature_finale > 1550 || temperature_finale < 900) {
                                    etat_temp.setBackgroundColor(color_alert_niv_1);
                                    etat_temp.setTextColor(color_alert_text);
                                } else {
                                    etat_temp.setBackgroundColor(color_alert_niv_0);
                                    etat_temp.setTextColor(color_text);
                                }
                                // Gestion de l'affichage des valeurs pour le niveau de l'eau
                                if (jauge.equals("1") || jauge.equals("3")) {
                                    etat_jauge.setBackgroundColor(color_alert_niv_1);
                                    etat_jauge.setTextColor(color_alert_text);
                                } else {
                                    etat_jauge.setBackgroundColor(color_alert_niv_0);
                                    etat_jauge.setTextColor(color_text);
                                }
                                etat_jauge.setText(niveau_eau);
                                etat_vam.setText(position_vam);
                                etat_vav.setText(position_vav);
                                etat_vvi.setText(position_vvi);
                            }
                        }
                    }.execute();

                }
                // Si la connexion n'est pas correcte
                else {
                    // Ouverture de l'activité principale, avec la liste des bassins
                    Intent intent_openMain = new Intent(bassinx.this, main.class);
                    startActivity(intent_openMain);
                    // Fin des mises à jour
                    update_etat_bassin.shutdown();
                }
            }
        }, 0, 10, TimeUnit.SECONDS);

        // "Mise sur écoute" du bouton send, qui permet d'envoyer une action a exécuter
        btn_send.setOnClickListener(new View.OnClickListener() {

            /** Fonction qui s'éxécute lorsque l'on appuie sur le bouton send
             * @param v - Vue associée à l'activité
             */
            @Override
            public void onClick(View v) {

                // Mise à jour de l'UI (désactivation du bouton send ...)
                btn_send.setEnabled(false);
                btn_send.setBackgroundColor(-7829368);
                btn_send.setText("Modification en cours...");
                spinner_action.setEnabled(false);

                // Récupération et mémorisation de l'action à éxécuter, contenue dans la liste déroulante
                final String actionUI = spinner_action.getSelectedItem().toString();
                String actionData = null;
                switch (actionUI) {
                    case "Vanne amont -> Haute":
                        actionData = "setState("+id_bassin+"13)";
                        break;
                    case "Vanne amont -> Semi-ouverte":
                        actionData = "setState("+id_bassin+"12)";
                        break;
                    case "Vanne amont -> Basse":
                        actionData = "setState("+id_bassin+"11)";
                        break;
                    case "Vanne aval -> Haute":
                        actionData = "setState("+id_bassin+"23)";
                        break;
                    case "Vanne aval -> Semi-ouverte":
                        actionData = "setState("+id_bassin+"22)";
                        break;
                    case "Vanne aval -> Basse":
                        actionData = "setState("+id_bassin+"21)";
                        break;
                    case "Vanne de vidage -> Haute":
                        actionData = "setState("+id_bassin+"12)";
                        break;
                    case "Vanne de vidage -> Basse":
                        actionData = "setState("+id_bassin+"11)";
                        break;
                }

                // Vérification du statut de la connexion sur le réseau Wifi Pisciculture
                cf = new connectVerif(getApplicationContext());
                isConnect = cf.isConnectingToPisciculture();

                // Si la connexion est correcte
                if (isConnect) {
                    // Démarrage d'une tâche asynchrone
                    final String finalActionData = actionData;
                    new AsyncTask <Void, Void, Boolean> () {

                        /**
                         * Fonction asynchrone (thread) qui permet d'envoyer l'action a éxécutée au serveur, puis de traiter sa réponse
                         * @param p - De type Void, pour signifier que la fonction n'attend rien en entrée
                         * @return true ou false - Résultat du bon déroulement de l'éxécution de l'opération demandée
                         */
                        @Override
                        protected Boolean doInBackground(Void... p) {
                            // Initialisation de la socket
                            Socket socket;
                            try {
                                //Connexion de la socket
                                socket = new Socket(ip_serveur, Integer.parseInt(port_serveur));
                                // Ecriture dans le flux de sortie (vers le serveur) de l'action à éxéuter
                                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                                out.print(finalActionData);
                                out.println();
                                out.flush();
                                // Initialisation d'un buffer d'entrée sur la socket
                                BufferedReader receiveData;
                                receiveData = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                                String messageString;

                                // Tant que la socket est connectée, on attend la réponse du serveur
                                while (socket.isConnected()) {
                                    while ((messageString = receiveData.readLine()) != null) {
                                        // Traitement de la réponse si elle est égale à 0
                                        if (messageString.equals("0")) {
                                            socket.close();
                                            return false;
                                        }
                                        // Traitement de la réponse si elle est égale à 1
                                        if (messageString.equals("1")) {
                                            socket.close();
                                            return true;
                                        }
                                        // Traitement de la réponse dans les autres cas
                                        else
                                            return false;
                                    }
                                }
                            }
                            catch (IOException e) {
                                e.printStackTrace();
                                return false;
                            }
                            return false;
                        }

                        /**
                         * Fonction exécuté à la suite du thread précédent
                         * @param result - Il s'agit du retour du thread précédent
                         */
                        @Override
                        protected void onPostExecute(Boolean result) {
                            // Initialisation d'une boite d'information
                            messageInfoConnexion = new displayMessage();

                            // Si la réponse du serveur était true, affichage d'un message de succès
                            if (result)
                                messageInfoConnexion.displayAlertDialog(bassinx.this, "Succès", "L'opération demandée s'est correctement exécutée", true);
                            // Si la réponse du serveur était false, affichage d'un message d'erreur
                            else
                                messageInfoConnexion.displayAlertDialog(bassinx.this, "Erreur", "L'opération demandée ne s'est pas correctement exécutée", false);
                            // Le bouton send et la liste déroulante sont visibles
                            int color = Color.parseColor("#35c419");
                            btn_send.setEnabled(true);
                            btn_send.setBackgroundColor(color);
                            spinner_action.setEnabled(true);
                            // Mise à jour du label d'information
                            btn_send.setText("Valider");
                        }
                    }.execute();

                }
                // Si la connexion n'est pas correcte
                else {
                    // Ouverture de l'activité principale, avec la liste des bassins
                    Intent intent_openMain = new Intent(bassinx.this, main.class);
                    startActivity(intent_openMain);
                    bassinx.this.finish();
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
        // Fin des mises à jour
        update_etat_bassin.shutdown();
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


