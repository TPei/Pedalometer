package fhfl.jawutpei.pedalometer;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class BTSocketConnector extends Thread
{
	private static final String TAG = "fhfl.jawutpei.pedalometer.BTSocketConnector";
	private final BluetoothSocket btSocket;
    private final BluetoothDevice btDevice;
    private final UUID uuid = new UUID(123, 123);
    private MainActivity activity;
 
    public BTSocketConnector(BluetoothDevice device, MainActivity activity) 
    {
    	Log.v(TAG, "Konstruktor(): ");
        BluetoothSocket tmp = null;
        btDevice = device;
        this.activity = activity;
 
        try 
        {
        	tmp = device.createRfcommSocketToServiceRecord(uuid);
        } 
        catch (IOException e) 
        { 
        	
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
            try 
            {
                btSocket.close();
            } 
            catch (IOException closeException) 
            { 
            	
            }
            return;
        }
        activity.setSocket(btSocket);
    }
}
