package es.upm.miw.SolitarioCelta;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;

public class MainActivity extends Activity {

    JuegoCelta juego;
    private final String GRID_KEY = "GRID_KEY";
    private final String FICHERO_PARTIDA = "partidaGuardada";
    private final String FICHERO_PUNTUACIONES = "puntuaciones";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        juego = new JuegoCelta();
        mostrarTablero();
    }

    /**
     * Se ejecuta al pulsar una ficha
     * Las coordenadas (i, j) se obtienen a partir del nombre, ya que el botón
     * tiene un identificador en formato pXY, donde X es la fila e Y la columna
     *
     * @param v Vista de la ficha pulsada
     */
    public void fichaPulsada(View v) {
        String resourceName = getResources().getResourceEntryName(v.getId());
        int i = resourceName.charAt(1) - '0';   // fila
        int j = resourceName.charAt(2) - '0';   // columna

        juego.jugar(i, j);
        mostrarTablero();
        if (juego.juegoTerminado()) {
            guardarResultado();
            new AlertDialogFragment().show(getFragmentManager(), "ALERT DIALOG");
        }
    }

    /**
     * Muestra el numero de fichas restantes en la actividad
     */
    public void mostrarFichasRestantes() {
        TextView barraEstado = (TextView) findViewById(R.id.barraEstado);
        String fichasRestantes = Integer.toString(juego.numeroFichas());
        barraEstado.setText(getResources().getString(R.string.fichasRestantesText) + " " + fichasRestantes);
    }

    /**
     * Visualiza el tablero
     */
    public void mostrarTablero() {
        RadioButton button;
        String strRId;
        String prefijoIdentificador = getPackageName() + ":id/p"; // formato: package:type/entry
        int idBoton;

        for (int i = 0; i < JuegoCelta.TAMANIO; i++)
            for (int j = 0; j < JuegoCelta.TAMANIO; j++) {
                strRId = prefijoIdentificador + Integer.toString(i) + Integer.toString(j);
                idBoton = getResources().getIdentifier(strRId, null, null);
                if (idBoton != 0) { // existe el recurso identificador del botón
                    button = (RadioButton) findViewById(idBoton);
                    button.setChecked(juego.obtenerFicha(i, j) == JuegoCelta.FICHA);
                }
            }
        //Actualiza la barra que muestra las fichas restantes
        mostrarFichasRestantes();
    }

    /**
     * Guarda el estado del tablero (serializado)
     *
     * @param outState Bundle para almacenar el estado del juego
     */
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(GRID_KEY, juego.serializaTablero());
        super.onSaveInstanceState(outState);
    }

    /**
     * Recupera el estado del juego
     *
     * @param savedInstanceState Bundle con el estado del juego almacenado
     */
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String grid = savedInstanceState.getString(GRID_KEY);
        juego.deserializaTablero(grid);
        mostrarTablero();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.opciones_menu, menu);
        return true;
    }

    /**
     * Guarda el numero de fichas con el jugador y la fecha en un archivo
     */
    public void guardarResultado() {
        SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(this);
        String resultadoNumero = Integer.toString(juego.numeroFichas());
        if(juego.numeroFichas() < 10)
            resultadoNumero = "0"+resultadoNumero.trim();
        String resultadoNombreJugador = preferencias.getString("nombreJugador", "");
        String resultadoFecha = (String) android.text.format.DateFormat.format("dd-MM-yyyy hh:mm", Calendar.getInstance().getTime());
        String resultado = resultadoNumero + "," + resultadoNombreJugador.trim() + "," + resultadoFecha + "\n";
        guardar(FICHERO_PUNTUACIONES, resultado, Context.MODE_APPEND);
    }

    /**
     * Guardar el estado del tablero
     */
    public void guardarPartida() {
        String juegoSerializado = juego.serializaTablero();
        guardar(FICHERO_PARTIDA, juegoSerializado, Context.MODE_PRIVATE);
    }

    /**
     * Funcion supletoria para guardar ficheros
     * @param nombreFichero
     * @param datos
     * @param mode Tipo de modo en el que se guardan los datos, por ejemplo PRIVATE o APPEND
     */
    private void guardar(String nombreFichero, String datos, int mode) {
        try {
            FileOutputStream fos = openFileOutput(nombreFichero, mode);
            if (datos != null) {
                fos.write(datos.getBytes());
            }
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Cargar las puntuaciones
     */
    public void cargarResultado() {
        Intent intent = new Intent(this, Puntuaciones.class);
        startActivity(intent);
    }

    public void cargarPartida() {
        String partidaSerializada = cargar(FICHERO_PARTIDA);
        juego.deserializaTablero(partidaSerializada);
        juego.reCalcularNumFichas();
        mostrarTablero();
    }

    private String cargar(String nombreFichero) {
        String linea = "";
        try {
            BufferedReader fin = new BufferedReader(
                    new InputStreamReader(openFileInput(nombreFichero)));
            linea = fin.readLine();
            if (linea == null) {
                linea = "";
            }
            fin.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return linea;
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.opcAjustes:
                startActivity(new Intent(this, SCeltaPrefs.class));
                return true;
            case R.id.opcAcercaDe:
                startActivity(new Intent(this, AcercaDe.class));
                return true;
            case R.id.opcGuardarPartida:
                guardarPartida();
                return true;
            case R.id.opcRecuperarPartida:
                cargarPartida();
                return true;
            case R.id.opcReiniciarPartida:
                new RestartDialogFragment().show(getFragmentManager(), "ALERT DIALOG");
                return true;
            case R.id.opcMejoresResultados:
                cargarResultado();
                return true;
            case R.id.opcBorrarResultados:
                getApplicationContext().deleteFile(FICHERO_PUNTUACIONES);
                return true;
            default:
                Toast.makeText(
                        this,
                        getString(R.string.txtSinImplementar),
                        Toast.LENGTH_SHORT
                ).show();
        }
        return true;
    }
}
