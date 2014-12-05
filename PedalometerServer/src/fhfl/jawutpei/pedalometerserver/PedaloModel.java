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
	private ArrayList<double[]> rawData;
	private ArrayList<double[]> proData;
	
	public PedaloModel()
	{
		this.rawData = new ArrayList<double[]>();
		this.proData = new ArrayList<double[]>();
	}
	
	/**
	 * wandelt das Byte-Array in String zurück und extrahiert daraus die X/Y/Z-Werte um diese in rawData zu speichern
	 * @param raw Byte-Array mit der übermittelten Nachricht
	 * @param len Länge der empfangenen Nachricht
	 */
	public void addRawData(byte[] raw, int len)
	{	
		//Byte-Array in String umwandeln
		String data = new String(raw, 0, len);
		Log.v(TAG, "addRawData(): empfangen: " + data);
		//double[] data = {xCord, yCord, zCord};
		//rawData.add(data);
		processData();
	}
	
	/**
	 * Getter
	 * @return proData
	 */
	public ArrayList<double[]> getData()
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
		//calculate U/min from rawData and add to proData
	}
}