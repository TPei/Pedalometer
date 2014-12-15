package fhfl.jawutpei.pedalometerserver;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class PedaloView extends View {

	private PedaloModel model;
	private Paint backgroundColor;
	private Paint paintingColor;
	private Paint highlightColor;
	private int highlightSize = 5;
	private boolean isInitialized = false;
	
	private List<Double> yCoords;
	
	private static final String TAG = PedaloView.class.getName();
	
	public PedaloView(Context context) {
		super(context);
		Log.d(TAG, "PedaloView()");
	}

	public PedaloView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.d(TAG, "PedaloView()");
	}

	public PedaloView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		Log.d(TAG, "PedaloView()");
	}
	
	public void init(PedaloModel model){
		/* 
		 * some dummy coordinates 
		 */
		// ------------------------------ //
		// ------------------------------ //
		// ------------------------------ //
		
		//yCoords = model.getData();
		/*
		yCoords = new ArrayList<Double>();
		
		
		
		int length = 1000000;
		int maxVal = (int)(Math.random()*100);
		for(int i = 0; i < length; i++){
			yCoords.add(Math.random()*maxVal);
		}
		
		*/
		// ------------------------------ //
		// ------------------------------ //
		// ------------------------------ //
		
		this.model = model;
		
		backgroundColor = new Paint();
		backgroundColor.setColor(Color.WHITE);
		
		paintingColor = new Paint();
		paintingColor.setColor(Color.BLACK);
		
		highlightColor = new Paint();
		highlightColor.setColor(Color.RED);
		
		this.isInitialized = true;
		this.invalidate();
	}
	
	@Override
	public void onDraw(Canvas canvas){
		super.onDraw(canvas);
		if(!isInitialized){
			canvas.drawARGB(128, 255, 0, 0);
			return;
		}
		
		yCoords = model.getData();
		int size = yCoords.size();
		
		// in case no data is returned
		if(size == 0){
			yCoords = new ArrayList<Double>();
			yCoords.add(0.0);
		}
		
		// in case more than 150 coordinates are returned, limit 
		else if(size > 150)
			yCoords = yCoords.subList(size-150, size);
		
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
		double yRatio = height / (double)(yMax+5);
		
		System.out.println(xRatio + ", " + yRatio);
		
		canvas.drawCircle(0, (float)(height - yCoords.get(0).floatValue()), highlightSize, highlightColor);
		
		// draw circle at yPosition and line to last yPosition
		for(int i = 1; i < yCoords.size(); i++){
			canvas.drawCircle((float)(i * xRatio), (float)(height - (yCoords.get(i) * yRatio)), highlightSize, highlightColor);
			canvas.drawLine((int)((i-1) * xRatio), (int)(height - yCoords.get(i-1)*yRatio), (int)(i*xRatio), (int)(height - yCoords.get(i)*yRatio), paintingColor);
		}
	}
}
