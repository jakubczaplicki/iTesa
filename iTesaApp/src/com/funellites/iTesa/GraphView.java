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

    private void initGraphView() {
		setFocusable(true);

	    Resources r = this.getResources();
	    
	    xPaint.setColor(r.getColor(R.color.x_color));
	    yPaint.setColor(r.getColor(R.color.y_color));
	    zPaint.setColor(r.getColor(R.color.z_color));
	}


    public int n = 0, x = 0, y = 0, z = 0;
    
    @Override
	protected void onDraw(Canvas canvas) {
      	//canvas.save();

    	//int a = (int)(Math.random()*200);
	    canvas.drawCircle(++n, x+100, 5, xPaint);
	    canvas.drawCircle(++n, y+100, 5, yPaint);
	    canvas.drawCircle(++n, z+100, 5, zPaint);
	    if ( n == 300 ) { n = 0; }

	    //canvas.restore();

	    //Log.w("iTesa GraphView:onDraw", "x: " + x + " y: " + y);

    }
    
	/*public void updateGraph(float bX, float bY) {
		x = (int) ((bX * 8) + 25);
		y = (int) ((bY * 8) + 25);
		invalidate();
	}*/
}
