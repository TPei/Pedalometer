package fhfl.jawutpei.pedalometerserver;

import java.util.ArrayList;

import android.util.Log;

/**
 * Model-Klasse zum Speichern und Verarbeiten von Daten des Beschleunigungssensors
 *
 */
public class PedaloModel 
{
	private static final String TAG = "fhfl.jawutpei.pedalometerServer.PedaloModel";
	//enthält die Rohdaten mit Timestamps
	private ArrayList<SensorData> rawData;
	//alle 2 Sekunden wird hier die aktuelle Umdrehungszahl hinzugefügt
	private ArrayList<Double> proData;
	
	public PedaloModel()
	{
		this.rawData = new ArrayList<SensorData>();
		this.proData = new ArrayList<Double>();
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
		if (rawData.size() > 1000)
		{
			int index = rawData.size()-1;
			long lastTimeStamp = rawData.get(index).timestamp;
			long timeDiff = 0;
			boolean directionUp = true;
			int count = 0;
			while (timeDiff < 2000)
			{
				double currentY = rawData.get(index).y - 9.81f;
				index--;
				double lastY	 = rawData.get(index).y - 9.81f;				
				timeDiff = lastTimeStamp - rawData.get(index).timestamp;
				
				if ((currentY < lastY && directionUp) || (currentY > lastY && !directionUp))
				{
					directionUp = !directionUp;
					count++;
				}
			}
			double result = (double)(count/2.0) * 30.0;
			proData.add(result);
			Log.v(TAG, "processData(): " + result + " U/min");
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