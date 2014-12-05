package fhfl.jawutpei.pedalometer;

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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity implements SensorEventListener {
	private static final String TAG = "fhfl.jawutpei.pedalometer.MainActivity";
	
	private SensorManager sensorManager;
	private Sensor accelerometer;
	
	private BluetoothAdapter btAdapter = null;
	private BluetoothSocket btSocket = null;
	
	private final int REQUEST_ENABLE_BT = 10;
	
	private TextView deviceListView;
	private ArrayAdapter<BluetoothDevice> deviceAdapter;
	private ArrayList<BluetoothDevice> deviceList = new ArrayList<BluetoothDevice>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME); 
        deviceListView = (TextView) (findViewById(R.id.deviceList));
        //deviceAdapter = new ArrayAdapter<BluetoothDevice>(this, R.layout.activity_main, deviceList);
        //deviceListView.setAdapter(deviceAdapter);
        
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
		String values = "x:" + event.values[0] + " y: " + event.values[1] + " z: " + event.values[2];
		//Log.v(TAG, "onSensorChange(): " + values);
		//Toast.makeText(this, values, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	
	private void initializeBT() 
	{
		Log.v(TAG, "initializeBT(): ");
		//Bluetooth-Adapter referenzieren, wenn vorhanden
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		if (btAdapter == null) 
		{
			Toast.makeText(this, R.string.noBluetooth, Toast.LENGTH_LONG).show();
		    finish();
		}
		
		//Bluetooth aktivieren, wenn deaktiviert
		if (!btAdapter.isEnabled()) 
		{
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
		else
			showPairedDevices();
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		Log.v(TAG, "onActivityResult(): " + resultCode);
		if (resultCode !=  RESULT_OK)
		{
			Toast.makeText(this, R.string.needBluetooth, Toast.LENGTH_LONG).show();
			initializeBT();
		}
		else
			showPairedDevices();
	}	
	
	private void showPairedDevices() 
	{
		Log.v(TAG, "showPairedDevices(): ");
		Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
		if (pairedDevices.size() > 0) 
		{
		    for (BluetoothDevice device : pairedDevices)
		    {
		    	deviceList.add(device);
		    	deviceListView.setText(deviceListView.getText() + "\n" + device.getName());
		    	Log.v(TAG, "showPairedDevices(): paired device found: " + device.getName());
		    }	
		}
		//deviceAdapter.notifyDataSetChanged();
		discoverDevices();
	}
	
	private void discoverDevices()
	{
		Log.v(TAG, "discoverDevices(): ");
		btAdapter.startDiscovery();
		
		final BroadcastReceiver bReceiver = new BroadcastReceiver() 
		{
		    public void onReceive(Context context, Intent intent) 
		    {
		        String action = intent.getAction();
		        // When discovery finds a device
		        if (BluetoothDevice.ACTION_FOUND.equals(action)) 
		        {
		            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		            // Add the name and address to an array adapter to show in a ListView
		            deviceList.add(device);
		            //deviceAdapter.notifyDataSetChanged();
		            deviceListView.setText(deviceListView.getText() + "\n" + device.getName());
		            Log.v(TAG, "discoverDevices(): device found: " + device.getName());
		        }
		    }
		};
		
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(bReceiver, filter); 
	}
	
	private void connectToServer(BluetoothDevice device)
	{
		Log.v(TAG, "connectToServer(): ");
		btAdapter.cancelDiscovery();
		new BTSocketConnector(device, this).start();
	}
	
	public void setSocket(BluetoothSocket socket)
	{
		Log.v(TAG, "setSocket(): ");
		btSocket = socket;
	}
	
		
	private void sendData(float[] sensorData)
	{
		Log.v(TAG, "sendData(): ");
		//send da data via bluetooth to server
	}	

}
