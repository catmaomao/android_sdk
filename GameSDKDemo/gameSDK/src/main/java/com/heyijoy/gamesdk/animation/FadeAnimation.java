/**
 * FadeAnimation.java
 * com.heyijoy.gamesdk.animation
 *
 * Function： TODO 
 *
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2014-10-11 		msh
 *
 * Copyright (c) 2014, TNT All Rights Reserved.
*/

package com.heyijoy.gamesdk.animation;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

/**
 * ClassName:FadeAnimation
 *
 * @author   msh
 * @version  
 * @since    Ver 1.1
 * @Date	 2014-10-11		下午2:04:24
 */
public class FadeAnimation {

	private Animation alpfade =  new AlphaAnimation(1.0f,0.0f);
	private int times = 2;
	private boolean isFade = false;
	private int duration = 600;
	private int startOffset = 0;
	public FadeAnimation(int duration,int times,boolean isFade) {
		this.duration = duration;
		this.alpfade.setDuration(this.duration);
		this.alpfade.setStartOffset(startOffset);
		this.isFade = isFade;
		this.times = times;
	}
	public FadeAnimation(int startOffset) {
		this.alpfade.setDuration(this.duration);
		this.alpfade.setStartOffset(startOffset);
	}
	public void start(final View view){
		view.setAnimation(alpfade);
			alpfade.setAnimationListener(new android.view.animation.Animation.AnimationListener(){
				  int k = 0;
				  public void onAnimationStart(Animation animation)
				  {
					  k++;
					  
				  }
				  
				  public void onAnimationRepeat(Animation animation)
				  {
					  
				  }
				  
				  public void onAnimationEnd(Animation animation)
				  {
					  if(isFade&&k>=times){
						  view.setVisibility(View.INVISIBLE);
					  }else if(k< times){
						  animation.setStartOffset(0);
						  view.startAnimation(animation);
					  }
				  }
				});
	}
     

}

