package fhfl.jawutpei.pedalometerserver;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class MainActivity extends Activity {
	
	private static final String TAG = "fhfl.jawutpei.pedalometerServer.MainActivity";
	
	private BluetoothAdapter btAdapter = null;
	private BluetoothSocket btSocket = null;
	private PedaloModel model = null;
	private PedaloView view = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        model = new PedaloModel();
        view = new PedaloView();
        
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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
		makeDiscoverable();
	}
    	
    private void makeDiscoverable()
    {
    	Log.v(TAG, "makeDiscoverable(): ");
    	Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
    	startActivity(discoverableIntent);
    	new BTServerSocket(btAdapter, this).start();
    }

    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		Log.v(TAG, "onActivityResult(): " + resultCode);
		if (resultCode !=  RESULT_CANCELED)
		{
			Toast.makeText(this, R.string.needBluetooth, Toast.LENGTH_LONG).show();
			makeDiscoverable();
		}
		else
		{
			Log.v(TAG, "onActivityResult(): Gerät ist sichtbar");
		}

	}	

    protected void setSocket(BluetoothSocket socket)
    {
    	Log.v(TAG, "setSocket(): ");
    	btSocket = socket;
    	new BTSocketReader(btSocket, model, view).start();
    }

}
