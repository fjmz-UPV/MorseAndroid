package es.upv.etsit.ait.paco.martinez.morse.decodificador1;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import kotlin.collections.UArraySortingKt;

public class Morse {

    static final char PUNTO = '.';
    static final char RAYA = '-';

    // umbrales en dits
    static final int UMBRAL_INTERLETRAS   = 3;
    static final int UMBRAL_INTERPALABRAS = 7;

    static int wpm = 15;
    static float tdi_ms = 60.f/50.f/wpm*1000.f;
    //long tdi_us = (long)tdi_ms*1000;

    static final float T_UMBRAL_PUNTO_RAYA_ms = 2 * tdi_ms;

    static public long tiempo_ant;

    static public long t_inic_flanco_bajada;

    static final int MAX_PUNTOS_RAYAS = 16;
    static char [] puntosRayas = new char[MAX_PUNTOS_RAYAS];
    static int cont;

    public static void decodificar(JSONObject evento, Activity actividad) {
        try {
            String tipoEvento = evento.getString("e");
            long tiempo = evento.getLong("t");
            ((ComunicacionBT) actividad).evento.setText("tipoEvento: " + tipoEvento + " en instante: " + tiempo);
            char simbolo;
            String base;
            switch (tipoEvento) {
                case "B":   t_inic_flanco_bajada = tiempo;
                            tiempo_ant = tiempo;
                            break;

                case "S":   if ( (tiempo-t_inic_flanco_bajada) > T_UMBRAL_PUNTO_RAYA_ms ) {
                                simbolo = RAYA;
                            } else {
                                simbolo = PUNTO;
                            }
                            tiempo_ant = tiempo;
                            base = ((ComunicacionBT) actividad).puntosRayas.getText().toString();
                            ((ComunicacionBT) actividad).puntosRayas.setText(base+" "+simbolo);
                            puntosRayas[cont++] = simbolo;
                            if (cont==MAX_PUNTOS_RAYAS) cont=0;
                            break;

                case "L":
                            char letra = caracter(puntosRayas);
                            base = ((ComunicacionBT) actividad).letras.getText().toString();
                            ((ComunicacionBT) actividad).letras.setText(base+letra);
                            cont = 0;
                            break;

                case "P":
                            base = ((ComunicacionBT) actividad).letras.getText().toString();
                            ((ComunicacionBT) actividad).letras.setText(base+" ");

            }


        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText((Context)actividad, "Problemas con JSON recibido", Toast.LENGTH_SHORT);
        }
    }






    public static char caracter(char [] arrayPuntosRayas) {
        String simbolos = new String(puntosRayas, 0, cont);
        if (simbolos.equals("."))       return 'E';
        else if (simbolos.equals("-"))  return 'T';

        else if (simbolos.equals(".-")) return 'A';
        else if (simbolos.equals("..")) return 'I';
        else if (simbolos.equals("-.")) return 'N';
        else if (simbolos.equals("--")) return 'M';

        else if (simbolos.equals("...")) return 'S';
        else if (simbolos.equals("..-")) return 'U';
        else if (simbolos.equals(".-.")) return 'R';
        else if (simbolos.equals(".--")) return 'W';
        else if (simbolos.equals("-..")) return 'D';
        else if (simbolos.equals("-.-")) return 'K';
        else if (simbolos.equals("--.")) return 'G';
        else if (simbolos.equals("---")) return 'O';

        else if (simbolos.equals("....")) return 'H';
        else if (simbolos.equals("...-")) return 'V';
        else if (simbolos.equals("..-.")) return 'F';
            //else if (simbolos.equals("..--")) return 'S';
        else if (simbolos.equals(".-..")) return 'L';
            //else if (simbolos.equals(".-.-")) return 'S';
        else if (simbolos.equals(".--.")) return 'P';
        else if (simbolos.equals(".---")) return 'J';
        else if (simbolos.equals("-...")) return 'B';
        else if (simbolos.equals("-..-")) return 'X';
        else if (simbolos.equals("-.-.")) return 'C';
        else if (simbolos.equals("-.--")) return 'Y';
        else if (simbolos.equals("--..")) return 'Z';
        else if (simbolos.equals("--.-")) return 'Q';
            //else if (simbolos.equals("---.")) return 'S';
            //else if (simbolos.equals("----")) return 'S';

        else if (simbolos.equals(".----")) return '1';
        else if (simbolos.equals("..---")) return '2';
        else if (simbolos.equals("...--")) return '3';
        else if (simbolos.equals("....-")) return '4';
        else if (simbolos.equals(".....")) return '5';
        else if (simbolos.equals("-....")) return '6';
        else if (simbolos.equals("--...")) return '7';
        else if (simbolos.equals("---..")) return '8';
        else if (simbolos.equals("----.")) return '9';
        else if (simbolos.equals("-----")) return '0';

        else if (simbolos.equals(".-.-.-")) return '.';
        else if (simbolos.equals("--..--")) return ',';
        else if (simbolos.equals("..--..")) return '?';
        else if (simbolos.equals("-.-.--")) return '!';
        else if (simbolos.equals(".----.")) return '\'';
        else if (simbolos.equals(".-..-.")) return '"';
        else if (simbolos.equals("-.--.")) return '(';
        else if (simbolos.equals("-.--.-")) return ')';
        else if (simbolos.equals(".-...")) return '&';
        else if (simbolos.equals("---...")) return ':';
        else if (simbolos.equals("-.-.-.")) return ';';
        else if (simbolos.equals("-..-.")) return '/';
        else if (simbolos.equals("..--.-")) return '_';
        else if (simbolos.equals("-...-")) return '=';
        else if (simbolos.equals(".-.-.")) return '+';
        else if (simbolos.equals("-....-")) return '-';
        else if (simbolos.equals("...-..-")) return '$';
        else if (simbolos.equals(".--.-.")) return '@';

        else return '*';


    }


}
