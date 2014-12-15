package fhfl.jawutpei.pedalometerserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

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
		model.addObserver(new Observer() {
			@Override
			public void update(Observable observable, Object data) {
				postInvalidate();
			}
		});
		
		yCoords = new ArrayList<Double>();
		
		
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
		backGround.right = width;
		backGround.bottom = height;
		
		canvas.drawRect(backGround, backgroundColor);
		canvas.drawRect(1f, 0f, 5f, (float)(height-1), paintingColor);
		canvas.drawRect(0f, (float)(height-5), (float)width, (float)(height-1), paintingColor);
		
		// subtract axis lines
		width = width - 5;
		height = height - 5;
		
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
