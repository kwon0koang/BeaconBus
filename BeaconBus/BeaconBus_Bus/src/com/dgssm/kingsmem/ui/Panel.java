package com.dgssm.kingsmem.ui;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

abstract public class Panel {
	// Frame
	private static JFrame		mMainFrame = null;

	// Screen Size
	private static Dimension	mScreenSize = null;
	private static int			mScreenWidth = 0;
	private static int			mScreenHeight = 0;
	
	public Panel() {
		if (mMainFrame == null) {
			mMainFrame = new JFrame();			
		}
		
		if (mScreenSize == null) {
			mScreenSize = Toolkit.getDefaultToolkit().getScreenSize();
			mScreenWidth = mScreenSize.width;
			mScreenHeight = mScreenSize.height;
		}
	}
	
	/**
	 * JFrame 객체를 반환한다.
	 * @return 프로그램의 메인 Frame을 반환한다.
	 */
	public JFrame getMainFrame() {
		return mMainFrame;
	}
	
	/**
	 * 현재 화면의 해상도 가로 값을 반환한다.
	 * @return int 형태의 해상도 가로 길이 값
	 */
	public int getScreenWidth() {
		return mScreenWidth;
	}
	
	/**
	 * 현재 화면의 해상도 세로 값을 반환한다.
	 * @return int 형태의 해상도 세로 길이 값
	 */
	public int getScreenHeight() {
		return mScreenHeight;
	}
	
	/**
	 * 해상도 사이즈만큼 프로그램 화면 크기를 만든다.
	 */
	protected void build() {
		if (mMainFrame.isVisible() == false) {			
			mMainFrame.setLayout(null);
			mMainFrame.setUndecorated(true);
			mMainFrame.setSize(mScreenWidth, mScreenHeight);
			mMainFrame.setVisible(true);
		}
	}
}

// End of Panel