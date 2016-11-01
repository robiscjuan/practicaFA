package es.upm.miw.SolitarioCelta;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class PuntuacionesAdapter extends ArrayAdapter<String> {

    public PuntuacionesAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public PuntuacionesAdapter(Context context, int resource, List<String> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater;
            inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.item_puntuaciones, null);
        }

        String itemData = getItem(position);

        if (itemData != null) {
            String[] dataSplited = itemData.split(",");
            String numero = dataSplited[0];
            String nombreJugador = dataSplited[1];
            String fecha = dataSplited[2];
            TextView textViewNumero = (TextView) view.findViewById(R.id.textView);
            TextView textViewJugador = (TextView) view.findViewById(R.id.textView1);
            TextView textViewFecha = (TextView) view.findViewById(R.id.textView2);

            if (textViewJugador != null) {
                textViewJugador.setText(nombreJugador);
            }

            if (textViewNumero != null) {
                textViewNumero.setText(numero);
            }

            if (textViewFecha != null) {
                textViewFecha.setText(fecha);
            }
        }

        return view;
    }
}
