package es.upv.etsit.ait.paco.martinez.morse.decodificador1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class AjustarBT2 extends AppCompatActivity {


    final private int REQUEST_ENABLE_BLUETOOTH = 1;

    Button controlBluetooth;

    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustar_bt2);

        controlBluetooth = (Button)findViewById(R.id.controlBT);
        spinner = (Spinner)findViewById(R.id.listaDispositivos);

        ConexionBluetooth.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (ConexionBluetooth.bluetoothAdapter==null) {
            Toast.makeText(this, "Dispositivo Bluetooth no soportado", Toast.LENGTH_SHORT);
            Intent intentoRetorno = new Intent();
            setResult(Activity.RESULT_CANCELED, intentoRetorno );
            finish();
        } else {
            if (!ConexionBluetooth.bluetoothAdapter.isEnabled()) {
                controlBluetooth.setText("Activar");
                //habilitarBluetooth();
            } else {
                controlBluetooth.setText("Desactivar");
                mostrarEmparejamientos();
            }
        }
    }





    private void mostrarEmparejamientos() {
        Set<BluetoothDevice> emparejados = ConexionBluetooth.bluetoothAdapter.getBondedDevices();

        ArrayList lista = new ArrayList();

        for (BluetoothDevice btd : emparejados) {
            lista.add(btd.getName());
        }
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, lista);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent,
                                               android.view.View v, int position, long id) {
                        Toast.makeText(AjustarBT2.this, ""+parent.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                }
        );

    }




}