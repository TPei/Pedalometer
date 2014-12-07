package fhfl.jawutpei.pedalometerserver;

import java.io.IOException;
import java.io.InputStream;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

/**
 * Sehr einfache Threadklasse zum Lesen vom BluetoothSocket
 * 
 * Empfangene Daten werden als String an die Model-Klasse übergeben
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
    
    /**
     * liest in Endlosschleife Daten vom Socket
     * holt Werttupel [x;y;z] aus empfangenen String heraus und gibt diese einzeln an das Model weiter
     * 
     */
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
                Log.v(TAG, "run(): Empfagen: " + message);
                if (messageBuffer.length() != 0)
                {
                	message = messageBuffer + message;
                	messageBuffer = "";
                }
                boolean moreData = false;
                do
                {
                	String newData = message.substring(0, message.indexOf("]")+1);
                    model.addRawData(newData.substring(1, newData.length()-1));
                    message = message.substring(message.indexOf("]")+1);
                    if (message.contains("]"))
                    {
                    	moreData = true;
                    }         
                    else
                    {
                    	moreData = false;
                    }
                } while(moreData);
                
                messageBuffer = message;

                //invalidate view here when view is implemented

            } 
            catch (IOException e) 
            {
                break;
            }
        }
    }
}
