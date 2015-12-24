package com.dgssm.beaconbus.custom;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

public class MarqueeTextView extends TextView{
	public MarqueeTextView(Context context) {
		super(context);
	}
	public MarqueeTextView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
	}
	@Override
	protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
	    if(focused)
	        super.onFocusChanged(focused, direction, previouslyFocusedRect);
	}
	
	@Override
	public void onWindowFocusChanged(boolean focused) {
	    if(focused)
	        super.onWindowFocusChanged(focused);
	}
	
	@Override
	public boolean isFocused() {
	   return true;
	}
}