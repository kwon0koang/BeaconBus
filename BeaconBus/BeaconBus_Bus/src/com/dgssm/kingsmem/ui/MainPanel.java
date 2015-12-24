package com.dgssm.kingsmem.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.dgssm.kingsmem.callback.Callback;
import com.dgssm.kingsmem.etc.Constants;

public class MainPanel extends Panel {
	// Debug
	private static final	String			TAG				= "MainPanel";
	
	// Panel
	private					JPanel			mMainPanel		= null;
	
	// Button
	private					JButton			mSettingButton	= null;
	private					JButton			mQuitButton		= null;	
	private					JButton			mBuzzerButton	= null;
	
	// Label
	private					JLabel			mBusNumber1		= null;
	private					JLabel			mCarNumber1		= null;
	private					JLabel			mPeopleCount1	= null;
	private					JLabel			mGetDownCount1	= null;
	private					JLabel			mNextCount1		= null;
	
	// TextField
	private					JTextField		mNextName		= null;
	private					JTextField		mBusNumber2		= null;
	private					JTextField		mCarNumber2		= null;
	private					JTextField		mPeopleCount2	= null;
	private					JTextField		mGetDownCount2	= null;
	private					JTextField		mNextCount2		= null;
	
	// Callback
	private					Callback		mCallback		= null;
	
	// Setting
	private					SettingPanel	mSettingPanel	= null;
	
	// Flag
	private					boolean			mBuzzerFlag		= false;
	
	/**
	 * @param callback : BusMain에 정의되어 있는 CallbackImpl 객체
	 */
	public MainPanel(Callback callback) {
		this.mCallback		= callback;
		
		this.mMainPanel		= new JPanel();
		
		this.mSettingButton = new JButton(Constants.SETTING_BUTTON_IMG);
		this.mQuitButton	= new JButton(Constants.QUIT_BUTTON_IMG);
		this.mBuzzerButton	= new JButton(Constants.NEXT_OFF_IMG);
		
		this.mBusNumber1	= new JLabel("노선 번호 :");
		this.mCarNumber1	= new JLabel("버스 번호 :");
		this.mPeopleCount1	= new JLabel("탑승 인원 :");
		this.mGetDownCount1	= new JLabel("내릴 인원 :");
		this.mNextCount1	= new JLabel("예상 인원 :");
		
		this.mNextName		= new JTextField("다음 정류장");
		this.mBusNumber2	= new JTextField("0000");
		this.mCarNumber2	= new JTextField("0000");
		this.mPeopleCount2	= new JTextField("00 명");
		this.mGetDownCount2	= new JTextField("00 명");
		this.mNextCount2	= new JTextField("00 명");
		
		this.mSettingPanel	= new SettingPanel(callback);
		
		build();
	}
	
	/**
	 * Flag 값에 해당되는 JTextField 객체를 반환한다.
	 * @param flag : (0 > 다음정류장) / (1 > 노선 번호) / (2 > 버스 번호) / (3 > 탑승 인원) / (4 > 내릴 인원) / (5 > 예상 인원)
	 * @return 해당되는 flag의 JTextField
	 */
	public JTextField getTextFields(int flag) {
		JTextField temp = null;
		
		switch (flag) {
			case 0 : temp = mNextName;		break;
			case 1 : temp = mBusNumber2;	break;
			case 2 : temp = mCarNumber2;	break;
			case 3 : temp = mPeopleCount2;	break;
			case 4 : temp = mGetDownCount2;	break;
			case 5 : temp = mNextCount2;	break;
		}
		
		return temp;
	}
	
	/**
	 * BuzzerButton이 클릭된 상태를 표시하는 flag 변수,
	 * 이 flag를 통해 이미지를 교체하는 작업을 수행하게 된다. 
	 * @param flag : button이 클릭된 상태
	 */
	public void setBuzzerFlag(boolean flag) {
		this.mBuzzerFlag = flag;
	}
	
	/**
	 * 다음 정류장의 이름을 지정하는 함수
	 * @param text : 다음 정류장 이름
	 */
	public void setNextBusStop(String text) {
		this.mNextName.setText(text);
	}
	
	/**
	 * SettingPanel 객체를 리턴한다.
	 * @return Setting 쪽 객체
	 */
	public SettingPanel getSettingPanel() {
		return this.mSettingPanel;
	}
	
	/**
	 * 각종 UI 구성에 필요한 Components를 셋팅한다.
	 */
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
				System.out.print(TAG + " : ActionListener() -> Setting Panel ");
				
				if (!mSettingPanel.getVisible()) {
					System.out.println("Visible");
					
					mSettingPanel.setVisible(true);
					
					allEnable(false);
				}
				else {
					System.out.println("Unvisible");
					
					mSettingPanel.setVisible(false);
					
					allEnable(true);
					
					mCallback.callback(Constants.UPDATE_BUS_DATA_INFO, 0, null, null);
				}
			}
		});
				
		mNextName.setBounds(65, Y3 - 220, 1525, 120);
		mNextName.setForeground(Color.WHITE);
		mNextName.setBackground(Color.LIGHT_GRAY);
		mNextName.setFont(Constants.FONT_SIZE_100);
		mNextName.setHorizontalAlignment(JLabel.CENTER);
		mNextName.setEditable(false);
		
		mNextCount1.setBounds(0, Y3 - 50, 600, Y3);
		mNextCount1.setForeground(Color.WHITE);
		mNextCount1.setFont(Constants.FONT_SIZE_100);
		mNextCount1.setVerticalAlignment(JLabel.CENTER);
		mNextCount1.setHorizontalAlignment(JLabel.RIGHT);
		
		mNextCount2.setBounds(mNextCount1.getBounds().x + mNextCount1.getBounds().width + 50,
							  mNextCount1.getBounds().y + 100, 400, 120);
//		mNextCount2.setBounds(mCarNumber1.getBounds().x + mCarNumber1.getBounds().width + 50,
//				  			  (mNextName.getBounds().y * 2), 400, 120);
		mNextCount2.setForeground(Color.WHITE);
		mNextCount2.setBackground(Color.LIGHT_GRAY);
		mNextCount2.setFont(Constants.FONT_SIZE_100);
		mNextCount2.setHorizontalAlignment(JLabel.CENTER);
		mNextCount2.setEditable(false);
		
		mPeopleCount1.setBounds(mNextCount1.getBounds().x, mNextCount1.getBounds().y + Y3,
								mNextCount1.getBounds().width, mNextCount1.getBounds().height);
		mPeopleCount1.setForeground(Color.WHITE);
		mPeopleCount1.setFont(Constants.FONT_SIZE_100);
		mPeopleCount1.setVerticalAlignment(JLabel.TOP);
		mPeopleCount1.setHorizontalAlignment(JLabel.RIGHT);
		
		mPeopleCount2.setBounds(mPeopleCount1.getBounds().x + mPeopleCount1.getBounds().width + 50,
								mPeopleCount1.getBounds().y, 400, 120);
//		mPeopleCount2.setBounds(mPeopleCount1.getBounds().x + mPeopleCount1.getBounds().width + 50,
//			 					(mNextName.getBounds().y * 3), 400, 120);
		mPeopleCount2.setForeground(Color.WHITE);
		mPeopleCount2.setBackground(Color.LIGHT_GRAY);
		mPeopleCount2.setFont(Constants.FONT_SIZE_100);
		mPeopleCount2.setHorizontalAlignment(JLabel.CENTER);
		mPeopleCount2.setEditable(false);
		
		mBuzzerButton.setBounds(mQuitButton.getBounds().x - 575,
								mNextCount1.getBounds().y + mNextCount1.getBounds().height - 320, 500, 500);
		mBuzzerButton.setBackground(Color.WHITE);
		mBuzzerButton.setOpaque(false);
		mBuzzerButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {				
				if (mBuzzerFlag) {					
					mCallback.callback(Constants.PRESH_BUZZER_BUTTON, 0, null, null);
				}
			}
		});
		
		mGetDownCount1.setBounds(mBuzzerButton.getBounds().x, 
								 (mBuzzerButton.getBounds().y + mBuzzerButton.getBounds().height / 2) - 20,
								 300, 60);
		mGetDownCount1.setForeground(Color.WHITE);
		mGetDownCount1.setFont(Constants.FONT_SIZE_50);
		mGetDownCount1.setHorizontalAlignment(JLabel.CENTER);
		
		mGetDownCount2.setBounds(mGetDownCount1.getBounds().x + mGetDownCount1.getBounds().width + 15,
						 mGetDownCount1.getBounds().y, 165, mGetDownCount1.getBounds().height);
		mGetDownCount2.setForeground(Color.WHITE);
		mGetDownCount2.setBackground(Color.LIGHT_GRAY);
		mGetDownCount2.setFont(Constants.FONT_SIZE_50);
		mGetDownCount2.setHorizontalAlignment(JLabel.CENTER);
		mGetDownCount2.setEditable(false);
		
		mBusNumber1.setBounds(mBuzzerButton.getBounds().x,
							  mBuzzerButton.getBounds().y + mBuzzerButton.getBounds().height + 10,
							  300, 60);
		mBusNumber1.setForeground(Color.WHITE);
		mBusNumber1.setFont(Constants.FONT_SIZE_50);
		mBusNumber1.setHorizontalAlignment(JLabel.RIGHT);
		
		mBusNumber2.setBounds(mBusNumber1.getBounds().x + mBusNumber1.getBounds().width + 20,
							  mBusNumber1.getBounds().y, 180, mBusNumber1.getBounds().height);
		mBusNumber2.setFont(Constants.FONT_SIZE_50);
		mBusNumber2.setForeground(Color.WHITE);
		mBusNumber2.setBackground(Color.LIGHT_GRAY);
		mBusNumber2.setHorizontalAlignment(JLabel.CENTER);
		mBusNumber2.setEditable(false);
		
		mCarNumber1.setBounds(mBusNumber1.getBounds().x, mBusNumber1.getBounds().y + mBusNumber1.getBounds().height + 10, 300, 60);
		mCarNumber1.setForeground(Color.WHITE);
		mCarNumber1.setFont(Constants.FONT_SIZE_50);
		mCarNumber1.setHorizontalAlignment(JLabel.RIGHT);
		
		mCarNumber2.setBounds(mCarNumber1.getBounds().x + mCarNumber1.getBounds().width + 20,
							  mCarNumber1.getBounds().y, 180, mCarNumber1.getBounds().height);
		mCarNumber2.setForeground(Color.WHITE);
		mCarNumber2.setBackground(Color.LIGHT_GRAY);
		mCarNumber2.setFont(Constants.FONT_SIZE_50);
		mCarNumber2.setHorizontalAlignment(JLabel.CENTER);
		mCarNumber2.setEditable(false);
		
		this.addComponents();
				
		super.build();
	}
	
	/**
	 * build() 에서 속성이 정해진 Components 들을 Frame에 추가하는 작업
	 */
	public void addComponents() {
		mMainPanel.add(mSettingButton);
		mMainPanel.add(mQuitButton);
		mMainPanel.add(mBuzzerButton, 1);
		mMainPanel.add(mBusNumber1);
		mMainPanel.add(mBusNumber2);
		mMainPanel.add(mCarNumber1);
		mMainPanel.add(mCarNumber2);
		mMainPanel.add(mPeopleCount1);
		mMainPanel.add(mPeopleCount2);
		mMainPanel.add(mNextName, 0);
		mMainPanel.add(mGetDownCount1, 0);
		mMainPanel.add(mGetDownCount2, 0);
		mMainPanel.add(mNextCount1);
		mMainPanel.add(mNextCount2);
		
		getMainFrame().getContentPane().add(mMainPanel);
	}
	
	/**
	 * Main 화면에서 데이터가 수정 가능한 부분을 Enable 하거나 Disable 한다.
	 * @param enabled : true, false
	 */
	public void allEnable(boolean enabled) {
		mQuitButton.setEnabled(enabled);
		mBuzzerButton.setEnabled(enabled);
		mBusNumber2.setEnabled(enabled);
		mCarNumber2.setEnabled(enabled);
		mPeopleCount2.setEnabled(enabled);
		mNextName.setEnabled(enabled);
		mGetDownCount2.setEnabled(enabled);
		mNextCount2.setEnabled(enabled);
	}
	
	/**
	 * 버스 노선 번호를 업데이트하는 함수
	 * @param text : 버스 노선 번호
	 */
	public void updateBusNumber(String text) {
		if (mBusNumber2 != null) {			
			if (!text.equals("선택")) {
				mBusNumber2.setText(text);
			}
			else {
				mBusNumber2.setText("0000");
			}
		}
	}
	
	/**
	 * 버스 차 번호를 업데이트하는 함수
	 * @param text : 버스 차 번호
	 */
	public void updateCarNumber(String text) {
		if (mCarNumber2 != null) {			
			if (!text.equals("")) {
				mCarNumber2.setText(text);
			}
			else {
				mCarNumber2.setText("0000");
			}
		}
	}
	
	/**
	 * Buzzer Flag 값에 따라 Buzzer Button의 이미지를 교체한다.
	 */
	public void changeBuzzerImage() {
		if (mBuzzerButton != null) {
			if (mBuzzerFlag) {
				mBuzzerButton.setIcon(Constants.NEXT_ON_IMG);
			}
			else {
				mBuzzerButton.setIcon(Constants.NEXT_OFF_IMG);
			}			
		}
	}
	
	/**
	 * 탑승인원, 내릴인원, 다음 정류장 인원을 업데이트하는 함수
	 * @param cmd   : bus(탑승인원), next(다음 정류장 인원), down(내릴 인원) 세 가지 명령어를 쓸 수 있다.
	 * @param count : 업데이트 될 승객 수
	 */
	public void updateCount(String cmd, int count) {
		String text = "";
		
		if (count < 0) {
			text = "00 명";
		}		
		else if (count < 10) {
			text = "0" + count + " 명";
		}
		else {
			text = count + " 명";
		}
		
		switch (cmd) {
			case "bus"  : if (mPeopleCount2 != null)  { mPeopleCount2.setText(text); }	break;
			case "next" : if (mNextCount2 != null)	  { mNextCount2.setText(text); }	break;
			case "down" : if (mGetDownCount2 != null) { mGetDownCount2.setText(text); } break;
			default : break;
		}
	}
}

// End of MainPanel