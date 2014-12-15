package fhfl.jawutpei.pedalometerserver;

import java.util.ArrayList;
import java.util.Observable;

import android.util.Log;

/**
 * Model-Klasse zum Speichern und Verarbeiten von Daten des Beschleunigungssensors
 *
 */
public class PedaloModel extends Observable
{
	private static final String TAG = "fhfl.jawutpei.pedalometerServer.PedaloModel";
	//enthält die Rohdaten mit Timestamps
	private ArrayList<SensorData> rawData;
	//alle 2 Sekunden wird hier die aktuelle Umdrehungszahl hinzugefügt
	private ArrayList<Double> proData;
	public long timestamp;
	
	private MainActivity activity;
	
	public PedaloModel(MainActivity activity)
	{
		this.rawData = new ArrayList<SensorData>();
		this.proData = new ArrayList<Double>();
		
		this.activity = activity;
	}
	
	/**
	 * wandelt das Byte-Array in String zurück und extrahiert daraus die X/Y/Z-Werte um diese in rawData zu speichern
	 * @param raw Byte-Array mit der übermittelten Nachricht
	 * @param len Länge der empfangenen Nachricht
	 */
	public void addRawData(String dataString)
	{	
		String dataStrings[] = dataString.split(";");
		float[] data = {Float.parseFloat(dataStrings[0]), Float.parseFloat(dataStrings[1]), Float.parseFloat(dataStrings[2])};
		rawData.add(new SensorData(data[0], data[1], data[2]));
		//neue Umdrehungszahl berechnen
		processData();
	}
	
	/**
	 * Getter
	 * @return proData
	 */
	public ArrayList<Double> getData()
	{
		return proData;
	}
	
	/**
	 * Leert beide ArrayLists
	 */
	public void clear()
	{
		rawData.clear();
		proData.clear();
	}
	
	/**
	 * Berechnet aus den Rohdaten die Umdrehungen pro Minute und speichert diese in proData
	 */
	private void processData()
	{
		final int TIMEWINDOW = 6000;
		final int PRODATAUPDATERATE = 2000;
		
		long currentTimeStamp = System.currentTimeMillis();
		int index = rawData.size()-1;
		
		if ((proData.size() == 0) || (currentTimeStamp - this.timestamp >= PRODATAUPDATERATE))
		{			
			long timeDiff = 0;
			boolean positive = true;
			int count = -1;
			while ((timeDiff < TIMEWINDOW) && (index > 0))
			{
				index--;
				double currentY = rawData.get(index).y - 9.81;				
				timeDiff = currentTimeStamp - rawData.get(index).timestamp;				
				
				if ((currentY < 3 && positive) || (currentY > 3 && !positive))
				{
					positive = !positive;
					count++;
				}
			}

			double result = ((double)count / 2.0) * (60000.0 / TIMEWINDOW);
			//proData.add(new UMinData(result));
			proData.add(result);
			this.notifyObservers();
			this.timestamp = System.currentTimeMillis();
			Log.v(TAG, "processData(): " + result + " U/min");
			activity.updateUMin(String.valueOf(result));
		}
	}
	
	/**
	 * Innere Klasse zum Speichern der Sensordaten zusammen mit einem Timestamp
	 *
	 */
	private class SensorData
	{
		public float x;
		public float y;
		public float z;
		public long timestamp;
		
		public SensorData(float x, float y, float z)
		{
			this.x = x;
			this.y = y;
			this.z = z;
			this.timestamp = System.currentTimeMillis();
		}
	}
}