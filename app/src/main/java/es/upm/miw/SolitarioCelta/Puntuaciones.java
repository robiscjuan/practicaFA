package es.upm.miw.SolitarioCelta;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

public class Puntuaciones extends Activity {

    private final String FICHERO_PUNTUACIONES = "puntuaciones";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.puntuaciones);
        ArrayList<String> data = new ArrayList<String>();
        try {
            BufferedReader fin = new BufferedReader(
                    new InputStreamReader(openFileInput(FICHERO_PUNTUACIONES)));
            String linea = fin.readLine();
            while (linea != null) {
                data.add(linea);
                linea = fin.readLine();
            }
            fin.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Ordena los resultados por
        Collections.sort(data.subList(0, data.size()));
        Collections.reverse(data);

        PuntuacionesAdapter adapter = new PuntuacionesAdapter(this,
                R.layout.item_puntuaciones, data);

        ListView listView = (ListView) findViewById(R.id.listViewPuntuaciones);
        listView.setAdapter(adapter);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.opciones_puntuaciones, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
