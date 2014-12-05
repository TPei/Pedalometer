package fhfl.jawutpei.pedalometerserver;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class BTServerSocket extends Thread 
{
	private static final String TAG = "fhfl.jawutpei.pedalometerServer.BTServerSocket";
    private final BluetoothServerSocket btServerSocket;
    private final UUID uuid = new UUID(123, 123);
    private MainActivity activity;
 
    public BTServerSocket(BluetoothAdapter btAdapter, MainActivity activity) 
    {
    	Log.v(TAG, "Konstruktor(): ");
    	this.activity = activity;
    	
        BluetoothServerSocket tmp = null;
        try 
        {
        	tmp = btAdapter.listenUsingRfcommWithServiceRecord("Pedalometer", uuid);
        } 
        catch (IOException e) 
        { }
        btServerSocket = tmp;
    }
 
    public void run() {
    	Log.v(TAG, "run(): ");
        BluetoothSocket socket = null;
        while (true) 
        {
            try 
            {
                socket = btServerSocket.accept();
            } 
            catch (IOException e) 
            {
                break;
            }
            // If a connection was accepted
            if (socket != null) 
            {
            	Log.v(TAG, "run(): Connectedt to client");
                activity.setSocket(socket);
                try 
                {
					btServerSocket.close();
				} 
                catch (IOException e) 
                {
				}
                break;
            }
        }
    }
}