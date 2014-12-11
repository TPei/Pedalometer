package fhfl.jawutpei.pedalometerserver;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

public class PedaloView extends View{

	private PedaloModel model;
	private Paint backgroundColor;
	private Paint paintingColor;
	private boolean isInitialized = false;
	
	private ArrayList<Double> yCoords;
	
	public PedaloView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public void init(PedaloModel model){
		/* 
		 * some dummy coordinates 
		 */
		// ------------------------------ //
		// ------------------------------ //
		// ------------------------------ //
		
		//yCoords = model.getData();
		
		yCoords = new ArrayList<Double>();
		
		
		
		int length = (int)(Math.random()*100);
		int maxVal = (int)(Math.random()*100);
		for(int i = 0; i < length; i++){
			yCoords.add(Math.random()*maxVal);
		}
		
		
		// ------------------------------ //
		// ------------------------------ //
		// ------------------------------ //
		
		this.model = model;
		
		backgroundColor = new Paint();
		backgroundColor.setColor(Color.WHITE);
		
		paintingColor = new Paint();
		paintingColor.setColor(Color.BLACK);
		
		this.isInitialized = true;
		this.invalidate();
	}
	
	/**
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@Override
	public void onDraw(Canvas canvas){
		super.onDraw(canvas);
		if(!isInitialized){
			canvas.drawARGB(128, 255, 0, 0);
			return;
		}
		
		int width = this.getWidth();
		int height = this.getHeight();
		
		Rect backGround = new Rect();
		backGround.right = this.getWidth();
		backGround.bottom = this.getHeight();
		canvas.drawRect(backGround, backgroundColor);
		
		// calculate relative size of graphs by 
		double yMax = Integer.MIN_VALUE;
		for(int i = 0; i < yCoords.size(); i++){
			
			// we're not gonna show values below 0
			if(yCoords.get(i) > yMax){
				if(yCoords.get(i) < 0){
					yCoords.set(i, (double)0);
				}
				yMax = yCoords.get(i);
			}
		}
		
		System.out.println(yMax);
		
		double xRatio = width / (double)yCoords.size();
		double yRatio = height / (double)yMax;
		
		System.out.println(xRatio + ", " + yRatio);
		
		canvas.drawCircle(0, yCoords.get(0).floatValue(), 2, paintingColor);
		
		// draw circle at yPosition and line to last yPosition
		for(int i = 1; i < yCoords.size(); i++){
			canvas.drawCircle(i, (yCoords.get(i).floatValue()), 2, paintingColor);
			canvas.drawLine((int)((i-1) * xRatio), (int)(height - yCoords.get(i-1)*yRatio), (int)(i*xRatio), (int)(height - yCoords.get(i)*yRatio), paintingColor);
		}
	}
}
