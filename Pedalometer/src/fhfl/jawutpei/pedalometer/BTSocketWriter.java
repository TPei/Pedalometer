package fhfl.jawutpei.pedalometer;

import java.io.IOException;
import java.io.OutputStream;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

/**
 * Sehr einfache Threadklasse zum Schreiben auf BluetoothSocket
 * 
 * Vieles direkt von http://developer.android.com/guide/topics/connectivity/bluetooth.html übernommen
 *
 */
public class BTSocketWriter extends Thread
{
	private static final String TAG = "fhfl.jawutpei.pedalometer.BTSocketWriter";
    private final OutputStream outStream;
    private final String message;
    
    public BTSocketWriter (BluetoothSocket socket, String message) 
    {
    	Log.v(TAG, "Konstruktor(): ");
        OutputStream tmpOut = null;

        try 
        {
            tmpOut = socket.getOutputStream();
        } 
        catch (IOException e) { }
 
        outStream = tmpOut;
        this.message = message;
    }
    
    public void run() 
    {
    	Log.v(TAG, "run(): Sende Daten");

        try 
        {
            outStream.write(message.getBytes());
            outStream.flush();

        } 
        catch (IOException e) 
        {
        	Log.v(TAG, "run(): Fehler beim Senden");
        }
    }
}
