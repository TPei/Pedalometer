package fhfl.jawutpei.pedalometer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Userdokumentation:
 * App zum Auslesen der Beschleunigungssensordaten und dem Versenden dieser an eine Server-Anwendung via Bluetooth
 * 
 * Es werden alle erreichbaren Ger�te in einer RadioGroup angezeigt
 * Nach Auswahl eines Ger�ts kann per Touch auf "Mit Ger�t verbinden" eine Verbindung hergestellt werden
 * �ber ernueten Druck auf den Button kann die Verbindung beendet werden
 * 
 * Mit Druck auf "Daten�bertragung starten" kann das Auslesen und Versenden der Sensordaten gestartet werden
 * �ber erneuten Druck auf den Button kann das Auslesen und Versenden der Daten gestoppt werden
 * 
 * Entwicklerdokumentation:
 * - MainActivity handlet GUI, Bluetooth-Initialisierung und Sensordaten-Listening sowie Datenversand
 * - Klasse BTSocketConnector baut die Verbindung mit dem gew�nschten Ger�t in eigenem Thread auf
 * 
 * bekannte Probleme:
 * - keine
 * 
 * @author Janneck Wullschleger, Thomas Peikert
 * @version 1.0
 *
 */
public class MainActivity extends Activity implements SensorEventListener {
	private static final String TAG = "fhfl.jawutpei.pedalometer.MainActivity";
	
	private SensorManager sensorManager;
	private Sensor accelerometer;
	
	private BluetoothAdapter btAdapter = null;
	private BluetoothSocket btSocket = null;	
	private final int REQUEST_ENABLE_BT = 10;
	private ArrayList<BluetoothDevice> deviceList = new ArrayList<BluetoothDevice>();
	
	private RadioGroup deviceListRadio;	
	private TextView connectionStatus;
	private Button connectButton;
	private Button dataButton;
	
	private boolean sendingData = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //Sensor-Referenzen holen
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        
        //View-Referenzen holen
        deviceListRadio = (RadioGroup) (findViewById(R.id.deviceList));
        connectionStatus = (TextView) (findViewById(R.id.statusText));
        connectButton = (Button) (findViewById(R.id.connectButton));
        dataButton = (Button) (findViewById(R.id.dataButton));
         
        //Bluetooth initialisieren
        initializeBT();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

	@Override
	public void onSensorChanged(SensorEvent event) {
		String values = "[" + event.values[0] + ";" + event.values[1] + ";" + event.values[2] + "]";
		//wenn Verbindung zum Server besteht, sende Daten an diesen
		if (btSocket != null)
		{
			try 
			{
				btSocket.getOutputStream().write(values.getBytes());
				btSocket.getOutputStream().flush();
			} catch (IOException e) 
			{
				Log.e(TAG, "onSensorChange(): Error while sending data");
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Initialisiert Bluetooth
	 * -Erstellt Referenz zum BluetoothAdapter, wenn vorhanden
	 * -Aktiviert Bluetooth, falls deaktiviert
	 * -Startet Ger�te-Discovery
	 */
	private void initializeBT() 
	{
		Log.v(TAG, "initializeBT(): ");
		//Bluetooth-Adapter referenzieren, wenn vorhanden
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		if (btAdapter == null) 
		{
			Toast.makeText(this, R.string.noBluetoothToast, Toast.LENGTH_LONG).show();
		    finish();
		}
		
		//Bluetooth aktivieren, wenn deaktiviert
		if (!btAdapter.isEnabled()) 
		{
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
		else
			discoverDevices();
		
	}
	
	/**
	 * Empf�ngt Result von der Bluetooth-Aktivierungs-Anfrage
	 * -bei Erfolg wird Ger�te-Discovery gestartet
	 * -ansonsten wird User �ber Bluetooth-Abh�ngigkeit informiert und erneut versucht Bluetooth zu aktivieren
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		Log.v(TAG, "onActivityResult(): " + resultCode);
		if (resultCode !=  RESULT_OK)
		{
			Toast.makeText(this, R.string.needBluetoothToast, Toast.LENGTH_LONG).show();
			initializeBT();
		}
		else
			discoverDevices();
	}	
	
	/**
	 * Startet Bluetooth-Ger�te-Discovery
	 * -listet alle gefundenen Ger�te in der View auf
	 * -speichert Ger�te-Objekte in ArrayList
	 */
	private void discoverDevices()
	{
		Log.v(TAG, "discoverDevices(): ");
		btAdapter.startDiscovery();
		Toast.makeText(this, R.string.discoverDevicesToast, Toast.LENGTH_LONG).show();
		
		final BroadcastReceiver bReceiver = new BroadcastReceiver() 
		{
		    public void onReceive(Context context, Intent intent) 
		    {
		        String action = intent.getAction();
		        // When discovery finds a device
		        if (BluetoothDevice.ACTION_FOUND.equals(action)) 
		        {
		            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);		        
		            deviceList.add(device);
			    	addDeviceToView(device, deviceList.size()-1);	
		            Log.v(TAG, "discoverDevices(): device found: " + device.getName());
		        }
		    }
		};
		
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(bReceiver, filter); 
	}
	
	/**
	 * �berladene Methode von discoverDevices f�r den Aufruf durch die View
	 * @param view
	 */
	public void discoverDevices(View view)
	{
		discoverDevices();
	}

	/**
	 * Verbindet sich mit dem ausgew�hlten Bluetooth-Device
	 * Wenn bereits Verbindung besteht, wird diese abgebaut
	 * @param view
	 */
	public void connect(View view)
	{
		//Verbinden, wenn keine Verbindung besteht
		if (btSocket == null)
		{
			btAdapter.cancelDiscovery();
			int selectedDevice = deviceListRadio.getCheckedRadioButtonId();
			Log.v(TAG, "connect(): DeviceList-ID: " + selectedDevice);
			if (selectedDevice != -1)
				new BTSocketConnector(deviceList.get(selectedDevice), this).start();
			else
				Toast.makeText(this, "Kein Ger�t ausgew�hlt", Toast.LENGTH_SHORT).show();
		}
		//Verbindung abbauen, wenn diese besteht 
		else
		{
			Log.v(TAG, "connect(): disconnect");
			try 
			{
				btSocket.close();
				btSocket = null;
				connectButton.setText(R.string.connect);
				connectionStatus.setText(R.string.statusNotConnected);
			} 
			catch (IOException e) 
			{
				Log.e(TAG, "connect(): error while closing socket");
			}
		}
		
	}
	
	/**
	 * Registriert den Listener f�r den Beschleunigungssensor
	 * wenn bereits registriert, wird dieser entfernt
	 * @param view
	 */
	public void sendData(View view)
	{
		if (btSocket != null)
		{
			if (!sendingData)
			{
				Log.v(TAG, "sendData(): start sending data");
				sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
				dataButton.setText(R.string.sendNoData);
				sendingData = true;
			}
			else
			{
				Log.v(TAG, "sendData(): stop sending data");
				sensorManager.unregisterListener(this, accelerometer); 
				dataButton.setText(R.string.sendData);
				sendingData = false;
			}
		}
		else
		{
			Toast.makeText(this, R.string.notConnectedToast, Toast.LENGTH_SHORT).show();
		}
		
	}
	
	/**
	 * Setzt die Referenz zum Bluetooth-Socket
	 * wird vom BTSocketConnector-Thread aufgerufen
	 * @param socket
	 */
	public void setSocket(BluetoothSocket socket)
	{
		Log.v(TAG, "setSocket(): ");
		btSocket = socket;
		runOnUiThread(new Runnable() {
		    @Override
		    public void run() {
				connectButton.setText(R.string.disconnect);
				connectionStatus.setText(R.string.statusConnected);
		    }
		  });


	}
	
	/**
	 * F�gt ein Bluetooth-Device zur RadioGroup in der View hinzu
	 * @param device das hinzuzuf�gende Ger�t
	 * @param id stellt den index des Ger�ts in der ArrayList dar
	 */
	private void addDeviceToView(BluetoothDevice device, int id) 
	{
		RadioButton rb = new RadioButton(this);
    	rb.setText(device.getName());
    	rb.setId(id);
    	deviceListRadio.addView(rb);		
	}

}
