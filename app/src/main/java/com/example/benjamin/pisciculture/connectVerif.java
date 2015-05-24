/*
 * Application : Pisciculture
 * Version : v1 du 24.05.2015
 * Auteur : RABOT DAMORE Benjamin
 * Support : benjamin.damore@outlook.com
 * Fichier : connectVerif.java
 * Description : Il s'agit du code qui permet de vérifier si le smartphone est connecté ou non
 * au réseau Wifi configuré dans les paramètres de l'application.
 * Fichier .xml associé : Aucun
 */

package com.example.benjamin.pisciculture;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class connectVerif {
        private Context _context;

    /**
     *  Construction de la classe connectVerif
     * @param context - Pour l'attribution du context
     **/
    public connectVerif(Context context){
        this._context = context;
     }

    /**
     * Fonction qui permet retourne l'état de la connexion Wifi sur le réseau Pisciculture
     * @return true ou false - "true" si la connexion réussie, "false" dans le cas contraire
     **/
    public boolean isConnectingToPisciculture() {

        // Récupération du nom du réseau Wifi définie dans les paramètres
        SharedPreferences sharedPreferences = _context.getSharedPreferences("settings", 0);
        final String ssid_wifi = sharedPreferences.getString("wifi", "Pisciculture");

        // Test de l'activiation ou non du Wifi sur le smartphone
        WifiManager wifi = (WifiManager) _context.getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled()) {
            // Vérification de la connexion à un réseau Wifi
            ConnectivityManager connManager = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo Wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (Wifi.isConnected()) {
                // Obtention du SSID du réseau Wifi
                WifiManager wifiManager = (WifiManager) _context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ssid = wifiInfo.getSSID();
                ssid = ssid.substring(1, ssid.length() - 1);
                // Vérification du SSID du réseau Wifi
                if (ssid.equals(ssid_wifi)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }
}
