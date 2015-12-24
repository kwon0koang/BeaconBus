package com.dgssm.kingsmem.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.dgssm.kingsmem.callback.Callback;
import com.dgssm.kingsmem.data.BusStopData;
import com.dgssm.kingsmem.etc.Constants;

public class MainPanel extends Panel {
	// Debug
	private static final	String	TAG	= "MainPanel";
	
	// Panel
	private JPanel			mMainPanel		= null;
		
	// Button
	private JButton			mSettingButton	= null;
	private JButton			mQuitButton		= null;

	// Text Field
	private JTextField		mBusStopName	= null;
	private JTextField		mPeopleCount	= null;
	
	// SettingPanel
	private SettingPanel	mSettingPanel	= null;
	
	
	// Callback
	private Callback		mCallback		= null;
	
	/**
	 * @param callback : BusMain에 정의되어 있는 CallbackImpl 객체
	 */
	public MainPanel(Callback callback) {
		this.mCallback		= callback;
						
		this.mMainPanel		= new JPanel();
				
		this.mQuitButton	= new JButton(Constants.QUIT_BUTTON_IMG);
		this.mSettingButton = new JButton(Constants.SETTING_BUTTON_IMG);
		
		this.mBusStopName	= new JTextField("정류장 이름");
		this.mPeopleCount	= new JTextField("00 명");
		
		this.mSettingPanel	= new SettingPanel(mCallback);
		
		this.build();
	}
	
	@Override
	protected void build() {
		final int Y2 = getScreenHeight() / 2;
		final int Y3 = getScreenHeight() / 3;
		
		mMainPanel.setLayout(null);
		mMainPanel.setBackground(Color.DARK_GRAY);
		mMainPanel.setBounds(0, 0, getScreenWidth(), getScreenHeight());
		
		mQuitButton.setBounds(getScreenWidth() - (Y2 / 3), 0, Y2 / 3, Y2);
		mQuitButton.setOpaque(false);
		mQuitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		mSettingButton.setBounds(mQuitButton.getBounds().x, mQuitButton.getBounds().y + Y2,
								 mQuitButton.getBounds().width, mQuitButton.getBounds().height);
		mSettingButton.setOpaque(false);
		mSettingButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {				
				if (!mSettingPanel.getVisible()) {
					mSettingPanel.setVisible(true);
					
					allEnable(false);
				}
				else {
					mSettingPanel.setVisible(false);
					
					allEnable(true);
					
					mCallback.callback(Constants.UPDATE_BUSSTOP_NAME_DATA, 0, null, null);
				}
			}
		});

		mBusStopName.setBounds(65, Y3, 1525, 170);
		mBusStopName.setForeground(Color.WHITE);
		mBusStopName.setBackground(Color.LIGHT_GRAY);
		mBusStopName.setFont(Constants.FONT_SIZE_150);
		mBusStopName.setHorizontalAlignment(JLabel.CENTER);
		mBusStopName.setEditable(false);
		
		mPeopleCount.setBounds((getScreenWidth() / 2) - 300, mBusStopName.getBounds().y + mBusStopName.getBounds().height + 50, 400, 120);
		mPeopleCount.setForeground(Color.WHITE);
		mPeopleCount.setBackground(Color.LIGHT_GRAY);
		mPeopleCount.setFont(Constants.FONT_SIZE_100);
		mPeopleCount.setHorizontalAlignment(JLabel.CENTER);
		mPeopleCount.setEditable(false);
		
		this.addComponents();
				
		super.build();
	}
	
	/**
	 * build() 에서 속성이 정해진 Components 들을 Frame에 추가하는 작업
	 */
	public void addComponents() {
		mMainPanel.add(mSettingButton);
		mMainPanel.add(mQuitButton);
		mMainPanel.add(mBusStopName);
		mMainPanel.add(mPeopleCount);
		
		getMainFrame().add(mMainPanel);
	}
	
	/**
	 * Main 화면에서 데이터가 수정 가능한 부분을 Enable 하거나 Disable 한다.
	 * @param enabled : true, false
	 */
	public void allEnable(boolean enabled) {
		mQuitButton.setEnabled(enabled);
		mBusStopName.setEnabled(enabled);
		mPeopleCount.setEnabled(enabled);
	}
	
	/**
	 * 정류장 대기 인원 정보를 업데이트하는 함수
	 */
	public void updateCount(int count) {
		System.out.println(TAG + " : updateCount() -> " + count);
		
		if (mPeopleCount != null) {
			String text = "";
			
			if (BusStopData.Count < 0) {
				text = "00 명";
			}		
			else if (BusStopData.Count < 10) {
				text = "0" + count + " 명";
			}
			else {
				text = count + " 명";
			}
			
			mPeopleCount.setText(text);
		}
	}
	
	/**
	 * 정류장 이름을 업데이트하는 함수
	 */
	public void updateName() {
		if (mBusStopName != null) {
			mBusStopName.setText(BusStopData.Name);
		}
	}
}

// End of MainPanel