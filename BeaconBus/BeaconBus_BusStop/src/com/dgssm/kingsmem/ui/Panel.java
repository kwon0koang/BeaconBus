package com.dgssm.kingsmem.ui;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

abstract public class Panel {
	private static JFrame mMainFrame = null;
	private static Dimension mScreenSize = null;

	private static int screenWidth = 0;
	private static int screenHeight = 0;
	
	public Panel() {
		if (mMainFrame == null) {
			mMainFrame = new JFrame();			
		}
		
		if (mScreenSize == null) {
			mScreenSize = Toolkit.getDefaultToolkit().getScreenSize();
			screenWidth = mScreenSize.width;
			screenHeight = mScreenSize.height;
		}
	}
	
	public JFrame getMainFrame() {
		return mMainFrame;
	}
	
	public int getScreenWidth() {
		return screenWidth;
	}
	
	public int getScreenHeight() {
		return screenHeight;
	}
	
	protected void build() {
		if (mMainFrame.isVisible() == false) {
			mMainFrame.setLayout(null);
			mMainFrame.setUndecorated(true);
			mMainFrame.setSize(screenWidth, screenHeight);
			mMainFrame.setVisible(true);
		}
	}
}

// End of Panel