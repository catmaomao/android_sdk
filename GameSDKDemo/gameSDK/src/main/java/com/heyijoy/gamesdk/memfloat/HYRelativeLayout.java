package com.heyijoy.gamesdk.memfloat;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class HYRelativeLayout extends RelativeLayout{ 
	
	@Override  
	public boolean dispatchTouchEvent(MotionEvent ev) {  
//		Log.e("Demo:","父View的dispatchTouchEvent方法执行了");
		return super.dispatchTouchEvent(ev);  
	}  
	float x1 = 0 ;
	float x2 = 0 ;
	float y1 = 0 ;
	float y2 = 0 ;
	Boolean isOnTouch = false;
	@Override  
	public boolean onInterceptTouchEvent(MotionEvent event) {  
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			isOnTouch = false;
			x1 = event.getX(); 
            y1 = event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			if(!isOnTouch){
				x2 = event.getX(); 
                y2 = event.getY(); 
                if((Math.abs(x1 - x2)+Math.abs(y1 - y2)) >18){  // 真正的onTouch事件 
                	isOnTouch = true;
                } 
			}
			return isOnTouch;

		case MotionEvent.ACTION_UP:
			break;
		}
		return super.onInterceptTouchEvent(event);  
	}  
	
	@Override  
	public boolean onTouchEvent(MotionEvent event) {  
//		Log.e("Demo:","父View的onTouchEvent方法执行了");  
		return super.onTouchEvent(event);  
	}  
	
	public HYRelativeLayout(Context context) {  
		super(context);  
	}  
	
	public HYRelativeLayout(Context context,AttributeSet attr) {  
		super(context,attr);  
	}  
}
