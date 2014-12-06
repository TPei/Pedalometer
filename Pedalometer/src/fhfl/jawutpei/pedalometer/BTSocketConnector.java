package fhfl.jawutpei.pedalometer;

import java.io.IOException;
import java.util.UUID;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

/**
 * Klasse zum Erstellen und Verbinden eines Bluetooth-Sockets mit übergebenen Gerät
 * Läuft in eigenem Thread, da zeitintensiv und bei Fehler bis zu einem Timeout gewartet wird
 *
 */
public class BTSocketConnector extends Thread
{
	private static final String TAG = "fhfl.jawutpei.pedalometer.BTSocketConnector";
	private final BluetoothSocket btSocket;
    private final UUID uuid = new UUID(123, 123);
    private MainActivity activity;
 
    public BTSocketConnector(BluetoothDevice device, MainActivity activity) 
    {
    	Log.v(TAG, "Konstruktor(): ");
        BluetoothSocket tmp = null;
        this.activity = activity;
 
        try 
        {
        	tmp = device.createRfcommSocketToServiceRecord(uuid);
        } 
        catch (IOException e) 
        { 
        	Log.e(TAG, "Konstruktor(): error while creating socket");
        }
        btSocket = tmp;
    }
 
    public void run() 
    {
    	Log.v(TAG, "run(): ");
        try 
        {
            btSocket.connect();
        } 
        catch (IOException connectException) 
        {
        	Log.e(TAG, "run(): error while connecting socket");
            try 
            {
                btSocket.close();
            } 
            catch (IOException closeException) 
            { 
            	Log.e(TAG, "run(): error while closing socket");
            }
            return;
        }
        activity.setSocket(btSocket);
    }
}
