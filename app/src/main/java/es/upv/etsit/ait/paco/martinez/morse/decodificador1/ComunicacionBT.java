package es.upv.etsit.ait.paco.martinez.morse.decodificador1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.media.TimedText;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class ComunicacionBT extends AppCompatActivity {

    final String NOMBRE_DISPOSITIVO_LLAVE_MORSE = "LlaveMorse";

    TextView recibido;
    ScrollView scrollView;
    TextView evento;

    TextView puntosRayas;

    TextView letras;

    final int REQUEST_CODE_ENABLE_BLUETOOTH = 1;

    BluetoothDevice mmDevice;
    BluetoothSocket mmSocket;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comunicacion_bt);

        recibido = (TextView) findViewById(R.id.recibido);
        //scrollView = (ScrollView)findViewById(R.id.textoscroll);
        //scrollView.addView(recibido);
        evento = (TextView) findViewById(R.id.evento);
        puntosRayas = (TextView)findViewById(R.id.puntosRayas);
        letras = (TextView) findViewById(R.id.letras);

        buscarLlaveMorse();
        openBT();

    }


    private void buscarLlaveMorse()
    {
        ConexionBluetooth.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(ConexionBluetooth.bluetoothAdapter == null)
        {
            Toast.makeText(this, "No existe ningún adaptador Bluetooth disponible", Toast.LENGTH_SHORT);
            retornar(Activity.RESULT_CANCELED);

        }

        if(!ConexionBluetooth.bluetoothAdapter.isEnabled())
        {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, REQUEST_CODE_ENABLE_BLUETOOTH);
        }

        Set<BluetoothDevice> pairedDevices = ConexionBluetooth.bluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0) {
            mmDevice = null;
            for(BluetoothDevice device : pairedDevices) {
                if(device.getName().equals(NOMBRE_DISPOSITIVO_LLAVE_MORSE)) {
                    mmDevice = device;
                    break;
                }
            }
            if (mmDevice==null) {
                Toast.makeText(this, "No se ha encontrado el dispositivo bluetooth "+NOMBRE_DISPOSITIVO_LLAVE_MORSE, Toast.LENGTH_SHORT);
                retornar(Activity.RESULT_CANCELED);
            }

        }
        Toast.makeText(this, "Dispositivo "+NOMBRE_DISPOSITIVO_LLAVE_MORSE+" encontrado!!!", Toast.LENGTH_SHORT);
    }


    private void retornar(int codigo) {
        Intent intentoRetorno = new Intent();
        setResult(codigo, intentoRetorno );
        finish();
    }

    void openBT()
    {
        try {
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();

            beginListenForData();

        }catch (IOException e) {
            e.printStackTrace();
            retornar(Activity.RESULT_CANCELED);
        }
    }



    void beginListenForData()
    {
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable() {
            public void run() {
                while(!Thread.currentThread().isInterrupted() && !stopWorker) {
                    try {
                        int bytesAvailable = mmInputStream.available();
                        if(bytesAvailable > 0) {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++) {
                                byte b = packetBytes[i];
                                if(b == delimiter) {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;
                                    handler.post(new Runnable() {
                                        public void run() {
                                            JSONObject evento = null;
                                            recibido.append(data+"\n");
                                            try {
                                                evento = new JSONObject(data);
                                            } catch(JSONException e) {
                                                e.printStackTrace();
                                                Toast.makeText(ComunicacionBT.this, "Error en JSON recibido", Toast.LENGTH_SHORT);
                                            }
                                            Morse.decodificar(evento, ComunicacionBT.this);

                                        }
                                    });
                                } else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException ex) {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }



}