package fhfl.jawutpei.pedalometerserver;

import java.io.IOException;
import java.io.InputStream;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

/**
 * Sehr einfache Threadklasse zum Lesen vom BluetoothSocket
 * 
 * Empfangene Daten werden im Rohformat an die Model-Klasse übergeben
 * Die Referenz zur View wird zum Invalidaten dieser gebraucht
 * 
 * Vieles direkt von http://developer.android.com/guide/topics/connectivity/bluetooth.html übernommen
 *
 */
public class BTSocketReader extends Thread
{
	private static final String TAG = "fhfl.jawutpei.pedalometerServer.BTSocketReader";
    private final InputStream inStream;    
    private final PedaloModel model;
    private final PedaloView view;
    
    public BTSocketReader (BluetoothSocket socket, PedaloModel model, PedaloView view) 
    {
    	Log.v(TAG, "Konstruktor(): ");
        this.model = model;
        this.view = view;
        InputStream tmpIn = null;

        try 
        {
            tmpIn = socket.getInputStream();
        } 
        catch (IOException e) { }
 
        inStream = tmpIn;
    }
    
    public void run() 
    {
    	Log.v(TAG, "run(): SocketCommunicator gestartet");
        byte[] buffer = new byte[1024];
        String messageBuffer = "";
        int bytes;
        while (true) 
        {
            try 
            {
                bytes = inStream.read(buffer);
                String message = new String(buffer, 0, bytes);
                if (messageBuffer.length() != 0)
                {
                	message = messageBuffer + message;
                	messageBuffer = "";
                }
                String newData = message.substring(0, message.indexOf("]")+1);
                messageBuffer = message.substring(message.indexOf("]")+1);
                newData = newData.substring(1, newData.length()-1);
                model.addRawData(newData);
                //invalidate view here when view is implemented

            } 
            catch (IOException e) 
            {
                break;
            }
        }
    }
}
