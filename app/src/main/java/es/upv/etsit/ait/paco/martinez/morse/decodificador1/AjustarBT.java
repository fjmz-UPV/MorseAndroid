package es.upv.etsit.ait.paco.martinez.morse.decodificador1;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import android.Manifest;

public class AjustarBT extends AppCompatActivity {


    public static final String TAG = "AjustarBT";


    final int REQUEST_ENABLE_CODE = 1;
    final int REQUEST_DISCOVERABLE_CODE = 2;


    Button habilitarBT;
    TextView miNombre;
    Button buscar;
    Spinner spinnerListaDispositivosBT;
    Button conectar;


    private final BroadcastReceiver broadcastReceiver_BT = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ConexionBluetooth.bluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, ConexionBluetooth.bluetoothAdapter.ERROR);
                switch(state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "Broadcast receiver: STATE OFF");
                        Toast.makeText(AjustarBT.this, "Broadcast receiver: STATE OFF", Toast.LENGTH_LONG).show();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "Broadcast receiver: STATE TURNING OFF");
                        Toast.makeText(AjustarBT.this, "Broadcast receiver: STATE TURNING OFF", Toast.LENGTH_LONG).show();
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "Broadcast receiver: STATE ON");
                        Toast.makeText(AjustarBT.this, "Broadcast receiver: STATE ON", Toast.LENGTH_LONG).show();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "Broadcast receiver: STATE TURNING ON");
                        Toast.makeText(AjustarBT.this, "Broadcast receiver: STATE TURNING ON", Toast.LENGTH_LONG).show();
                        break;
                } // switch
            } // if

        }
    };



    ActivityResultLauncher<Intent> btHabilitarLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode()==RESULT_OK) {
                        Toast.makeText(AjustarBT.this, "BT habilitado!!!", Toast.LENGTH_SHORT);
                        Log.d(TAG, "BT habilitado!!!");
                    }
                }
            });



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ENABLE_CODE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permiso para habilitar concedido!!!..");
                    Toast.makeText(AjustarBT.this, "Permiso para habilitar concedido!!!", Toast.LENGTH_SHORT).show();
                    btHabilitarLauncher.launch(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                }  else {
                    Log.d(TAG, "El permiso NO ha sido conceido...");
                    Toast.makeText(AjustarBT.this, "El permiso para habilitar no fue concedido..", Toast.LENGTH_SHORT).show();
                    // Explain to the user that the feature is unavailable because
                    // the feature requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                }
                return;
            case REQUEST_DISCOVERABLE_CODE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permiso para descubrir concedido.");
                    Toast.makeText(AjustarBT.this, "Permiso para descubrir concedido", Toast.LENGTH_SHORT).show();
                    btHabilitarLauncher.launch(new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE));
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                }  else {
                    Log.d(TAG, "El permiso NO ha sido conceido...");
                    Toast.makeText(AjustarBT.this, "El permiso para descubrir no fue concedido..", Toast.LENGTH_SHORT).show();
                    // Explain to the user that the feature is unavailable because
                    // the feature requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                }
                return;

        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustar_bt);

        habilitarBT = (Button) findViewById(R.id.boton_HabilitarBT);
        miNombre = (TextView) findViewById(R.id.textView_miNombre);
        buscar = (Button) findViewById(R.id.boton_Buscar);
        spinnerListaDispositivosBT = (Spinner) findViewById(R.id.spinner_ListaDispositivosBT);
        conectar = (Button) findViewById(R.id.boton_Conectar);

        miNombre.setText(ConexionBluetooth.miNombre);

        ConexionBluetooth.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(broadcastReceiver_BT, BTIntent);








        habilitarBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controlBT();
            }
        });


    }


    private void habilitarBT() {
        requestPermissions(//AjustarBT.this,
                new String[] { Manifest.permission.BLUETOOTH_CONNECT },
                REQUEST_ENABLE_CODE);
    }


    private void controlBT() {


        if ( ConexionBluetooth.bluetoothAdapter==null) {
            Log.d(TAG, "Bluetooth no disponible");
            Toast.makeText(AjustarBT.this,"Dispositivo bluetooth no soportado", Toast.LENGTH_SHORT).show();
        } else {

            Log.d(TAG, "Bluetooth disponible");

            //Toast.makeText(AjustarBT.this,"Dispositivo bluetooth soportado", Toast.LENGTH_SHORT).show();

            if (!ConexionBluetooth.bluetoothAdapter.isEnabled()) {
                if (ContextCompat.checkSelfPermission(AjustarBT.this, Manifest.permission.BLUETOOTH_CONNECT)
                        == PackageManager.PERMISSION_GRANTED) {
                    // You can use the API that requires the permission.
                    Log.d(TAG, "El permiso NO estaba concedido");
                    Toast.makeText(AjustarBT.this, "Se van a pedir permisos.", Toast.LENGTH_SHORT).show();
                    habilitarBT();
                }  else if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this, Manifest.permission.BLUETOOTH_CONNECT)) {
                    // In an educational UI, explain to the user why your app requires this
                    // permission for a specific feature to behave as expected, and what
                    // features are disabled if it's declined. In this UI, include a
                    // "cancel" or "no thanks" button that lets the user continue
                    // using your app without granting the permission.
                    Toast.makeText(AjustarBT.this, "Debes dar permisos... a mano en config.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "Se va a pedir permiso");
                    Toast.makeText(AjustarBT.this, "Se van a pedir permisos.", Toast.LENGTH_SHORT).show();
                    requestPermissions(//AjustarBT.this,
                            new String[] { Manifest.permission.BLUETOOTH_CONNECT },
                            REQUEST_ENABLE_CODE);
                }
            }
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(broadcastReceiver_BT);
    }
}