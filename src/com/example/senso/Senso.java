package com.example.senso;

import android.os.Bundle;
import android.content.*;
import android.content.pm.*;
import android.graphics.*;
import android.hardware.*;
import android.app.Activity;
import android.view.*;

public class Senso extends Activity implements SensorEventListener{
	Thread t1;
	boolean isr=true;
	int j=0;
	MyView m;
	GestureDetector gd;
	float x[]=new float[1000],y[]=new float[1000],t[]=new float[1000],k[]=new float[1000],k1[]=new float[1000],s1[]=new float[1000];float r[]=new float[1000];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m=new MyView(this);
        setContentView(m);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        gd=new GestureDetector(this,new Gd());
        SensorManager sm=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
        sm.registerListener(this,sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);
    }
    class Gd extends GestureDetector.SimpleOnGestureListener
    {
    	@Override
    	public boolean onDown(MotionEvent event)
    	{
    		return true;
    	}
    	@Override
    	public boolean onSingleTapUp(MotionEvent event)
    	{
    		return true;
    	}
    	@Override
    	public boolean onFling(MotionEvent e1,MotionEvent e2,float velx,float vely)
    	{
    		x[j]=e1.getX();
    		y[j]=e1.getY();
    		t[j]=0;
    		k1[j]=0;
    		s1[j]=0;
    		r[j]=0;
    		if(e1.getX()<e2.getX())
    		{
    			k[j]=10;
    		}
    		if(e1.getX()>e2.getX())
    			k[j]=-10;
    		j++;
    		return true;
    	}
    }
    public void onAccuracyChanged(Sensor s,int acc)
    {
    	
    }
    public void onSensorChanged(SensorEvent event)
    {
    	float sy=event.values[1];
    	if(j!=0)
    	{
    		if(sy<-4)
    			k[j-1]=-10;
    		if(sy>2)
    			k[j-1]=10;
    	}
    }
    public void onPause()
    {
    	super.onPause();
    	isr=false;
    	while(true)
    	{
    		try
    		{
    			t1.join();
    			break;
    		}
    		catch(Exception ex)
    		{
    			
    		}
    	}
    }
    public void onResume()
    {
    	super.onResume();
    	isr=true;
    	t1=new Thread(m);
    	t1.start();
    }
    class MyView extends SurfaceView implements Runnable
    {
    	int l[]={1,-1};
    	SurfaceHolder sh;
    	Canvas canvas;
    	Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
    	public void run()
    	{
    		while(isr)
    		{
    			if(!sh.getSurface().isValid())
    				continue;
    			canvas=sh.lockCanvas();
    			canvas.drawColor(Color.WHITE);
    			for(int i=0;i<j;i++)
    			{
    				canvas.save();
    				canvas.translate(x[i],y[i]);
    				canvas.rotate(t[i]);
    				canvas.drawLine(0,0,0,k1[i],p);
    				for(int s=0;s<2;s++)
    				{
    					canvas.save();
    					canvas.translate(0, 0);
    					canvas.rotate(l[s]*s1[i]);
    					canvas.drawLine(0,0,0,-k1[i],p);
    					canvas.restore();
    				}
    				canvas.restore();
    				if(r[i]==0)
    				{
    					k1[i]+=5;
    					if(k1[i]>=30)
    						r[i]=1;
    				}
    				if(r[i]==1)
    				{
    					s1[i]+=5;
    					if(s1[i]>=30)
    						r[i]=2;
    				}
    				if(r[i]==2)
    				{
    				t[i]+=k[i];
    				x[i]+=k[i];
    				if(x[i]>canvas.getWidth() || x[i]<0)
    					insert(i);
    				}
    			}
    			sh.unlockCanvasAndPost(canvas);
    			try
    			{
    				Thread.sleep(100);
    			}
    			catch(Exception ex)
    			{
    				
    			}
    		}
    	}
    	public boolean onTouchEvent(MotionEvent event)
    	{
    		return gd.onTouchEvent(event);
    	}
    	public MyView(Context context)
    	{
    		super(context);
    		sh=getHolder();
    		t1=new Thread(this);
    		t1.start();
    	}
    	public void insert(int index)
    	{
    		for(int i=index;i<j-1;i++)
    		{
    			x[i]=x[i+1];
    			y[i]=y[i+1];
    			t[i]=t[i+1];
    			k[i]=k[i+1];
    			s1[i]=s1[i+1];
    			r[i]=r[i+1];
    			k1[i]=k1[i+1];
    		}
    		j--;
    	}
    }
    
}
