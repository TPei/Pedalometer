package fhfl.jawutpei.pedalometerserver;

import java.util.ArrayList;

/**
 * Model-Klasse zum Speichern und Verarbeiten von Daten des Beschleunigungssensors
 *
 */
public class PedaloModel 
{
	private ArrayList<double[]> rawData;
	private ArrayList<double[]> proData;
	
	public PedaloModel()
	{
		this.rawData = new ArrayList<double[]>();
		this.proData = new ArrayList<double[]>();
	}
	
	/**
	 * wandelt das Byte-Array in String zur�ck und extrahiert daraus die X/Y/Z-Werte um diese in rawData zu speichern
	 * @param raw Byte-Array mit der �bermittelten Nachricht
	 * @param len L�nge der empfangenen Nachricht
	 */
	public void addRawData(byte[] raw, int len)
	{	
		//process rawData
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