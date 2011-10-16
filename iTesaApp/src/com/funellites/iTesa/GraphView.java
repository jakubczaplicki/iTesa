package com.funellites.iTesa;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class GraphView extends View {

	private Paint xPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint yPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint zPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public GraphView(Context context) {
        super(context);
        initGraphView();
    }

    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initGraphView();
    }

    public GraphView(Context context, 
                     AttributeSet ats, 
                     int defaultStyle) {
        super(context, ats, defaultStyle);
        initGraphView();
    }	

    private int h = 100, w = 100;
    
    private void initGraphView() {
    	Log.d("iTesa", "GraphView:initGraphView()");
		setFocusable(true);

	    Resources r = this.getResources();
	    
	    xPaint.setColor(r.getColor(R.color.yellow));
	    yPaint.setColor(r.getColor(R.color.red));
	    zPaint.setColor(r.getColor(R.color.green));
	}

    public int i = 5, n = 10, x = 0, y = 0, z = 0;
    
    @Override
	protected void onDraw(Canvas canvas) {
      	//canvas.save();  // <-- what is that ?
    	
	    h = getHeight(); // putting h and w inside initGraphView()
	    w = getWidth();  // returns zero (too early for getting size?)

	    canvas.drawCircle(n, x+100, 5, xPaint);
	    canvas.drawCircle(n, y+100, 5, yPaint);
	    canvas.drawCircle(n, z+100, 5, zPaint);
	    n += i;
	    if ( n >= w - 100 || n <= 10 ) { i *= -1; }

	    //canvas.restore();

	    //Log.d("iTesa", "GraphView:onDraw(), h: " + h + " w: " + w);
    }
    
	/*public void updateGraph(float bX, float bY) {
		x = (int) ((bX * 8) + 25);
		y = (int) ((bY * 8) + 25);
		invalidate();
	}*/
}
