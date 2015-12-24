package com.dgssm.kingsmem.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import com.dgssm.kingsmem.callback.Callback;
import com.dgssm.kingsmem.data.BusData;
import com.dgssm.kingsmem.etc.Constants;

public class SettingPanel extends Panel {
	// Debug
	private static final	String			TAG				= "SettingPanel";
	
	// Key Value
	private final			String[][]		mKey			= { {"1", "2", "3"}, {"4", "5", "6"},
																{"7", "8", "9"}, {"0", "B", ""} };
	
	// Panel
	private					JPanel			mPanel			= null;
	
	// Button
	private					JButton[][]		mKeyButtons		= null;
	private					JButton			mConfirmButton	= null;
	
	// Label
	private					JLabel			mIDLabel		= null;
	private					JLabel			mNumberLabel	= null;
	private					JLabel			mSeatLabel		= null;
	
	// Text Field
	private					JTextField		mIDField		= null;
	private					JTextField		mNumberField	= null;
	private					JTextField		mSeatField		= null;
	
	// Text Area
	private					JTextArea		mDisplayArea	= null;
	
	// Scroll Panel
	private					JScrollPane		mScrollPane		= null;
		
	// Callback
	private					Callback		mCallback		= null;

	/**
	 * @param callback : BusMain에 정의되어 있는 CallbackImpl 객체
	 */
	public SettingPanel(Callback callback) {
		this.mCallback = callback;
		
		mPanel			= new JPanel();
		
		mIDLabel		= new JLabel();
		mNumberLabel	= new JLabel();
		mSeatLabel		= new JLabel();
		
		mIDField		= new JTextField();
		mNumberField	= new JTextField();
		mSeatField		= new JTextField();
		
		mDisplayArea	= new JTextArea();
		mScrollPane		= new JScrollPane(mDisplayArea);
		
		mKeyButtons		= new JButton[4][3];
		for (int i = 0; i < 4; i++) {
			mKeyButtons[i] = new JButton[3];
			
			for (int j = 0; j < 3; j++) {
				String key = mKey[i][j];
				
				if (key == "B") {
					mKeyButtons[i][j] = new JButton(new ImageIcon());
				}
				else if (key == "") {
					break;
				}
				else {
					mKeyButtons[i][j] = new JButton(key);
				}
			}
		}
		mConfirmButton	= new JButton("수 정");
		
		build();
	}
	
	/**
	 * SettingPanel의 Visible을 지정한다.
	 * @param visible : SettingPanel을 보이게 하려면 true 반대면 false
	 */
	public void setVisible(boolean visible) {
		mPanel.setVisible(visible);
	}
	
	/**
	 * SettingPanel의 Visible 상태 값을 반환한다.
	 * @return SettingPanel의 Visible 상태 값
	 */
	public boolean getVisible() {
		return mPanel.isVisible();
	}
	
	/**
	 * SettingPanel의 IDField의 텍스트를 반환한다.
	 * @return String 형태의 IDField의 내부 텍스트 값
	 */
	public String getIDFieldText() {
		return mIDField.getText();
	}
	
	/**
	 * SettingPanel의 NumberField의 텍스트를 반환한다.
	 * @return String 형태의 NumberField의 내부 텍스트 값
	 */
	public String getNumberFieldText() {
		return mNumberField.getText();
	}
	
	/**
	 * SettingPanel의 SeatField의 텍스트를 반환한다.
	 * @return String 형태의 SeatField의 내부 텍스트 값
	 */
	public String getSeatFieldText() {
		return mSeatField.getText();
	}
	
	/**
	 * SettingPanel의 DisplayArea를 반환한다.
	 * @return SettingPanel의 Display Area를 반환 
	 */
	public JTextArea getmDisplayArea() {
		return this.mDisplayArea;
	}

	@Override
	protected void build() {
		System.out.println(TAG + " : build() -> UI 구성");
		
		mPanel.setLayout(null);
		mPanel.setBounds(0, 0, getScreenWidth() - ((getScreenHeight() / 3) / 2), getScreenHeight());
		mPanel.setBackground(Color.DARK_GRAY);
		mPanel.setVisible(false);
		
		mIDField.setBounds(300, 100, 500, 100);
		mIDField.setBackground(Color.LIGHT_GRAY);
		mIDField.setFont(new Font("굴림", Font.BOLD, 50));
		mIDField.setHorizontalAlignment(JTextField.CENTER);
		mIDField.setDocument(new PlainDocument() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {				
				if ((getLength() + str.length()) <= Constants.LIMIT_WORD_1) {					
					super.insertString(offs, str, a);
				}
			}
		});
		mIDField.setText("0000000000");
		mIDField.setEnabled(false);
		
		mIDLabel.setBounds(mIDField.getBounds().x, mIDField.getBounds().y - 50, mIDField.getBounds().width, 50);
		mIDLabel.setFont(new Font("����", Font.BOLD, Constants.SETTING_LABLE_FONT));
		mIDLabel.setForeground(Color.WHITE);
		mIDLabel.setText("버스 아이디");
		
		mNumberField.setBounds(mIDField.getBounds().x + mIDField.getBounds().width + 50, mIDField.getBounds().y, 300, 100);
		mNumberField.setBackground(Color.LIGHT_GRAY);
		mNumberField.setFont(new Font("굴림", Font.BOLD, 50));
		mNumberField.setHorizontalAlignment(JTextField.CENTER);
		mNumberField.setDocument(new PlainDocument() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {				
				if ((getLength() + str.length()) <= Constants.LIMIT_WORD_2) {					
					super.insertString(offs, str, a);
				}
			}
		});
		mNumberField.setText("0000");
		mNumberField.setEnabled(false);
		
		mNumberLabel.setBounds(mNumberField.getBounds().x, mNumberField.getBounds().y - 50, mNumberField.getBounds().width, 50);
		mNumberLabel.setFont(new Font("굴림", Font.BOLD, Constants.SETTING_LABLE_FONT));
		mNumberLabel.setForeground(Color.WHITE);
		mNumberLabel.setText("차 번호");
		
		mSeatField.setBounds(mNumberField.getBounds().x + mNumberField.getBounds().width + 50, mNumberField.getBounds().y, 100, 100);
		mSeatField.setBackground(Color.LIGHT_GRAY);
		mSeatField.setFont(new Font("굴림", Font.BOLD, 50));
		mSeatField.setHorizontalAlignment(JTextField.CENTER);
		mSeatField.setDocument(new PlainDocument() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {				
				if ((getLength() + str.length()) <= Constants.LIMIT_WORD_3) {
					super.insertString(offs, str, a);
				}
			}
		});
		mSeatField.setText("00");
		mSeatField.setEnabled(false);
		
		mSeatLabel.setBounds(mSeatField.getBounds().x, mSeatField.getBounds().y - 50, mSeatField.getBounds().width, 50);
		mSeatLabel.setFont(new Font("굴림", Font.BOLD, Constants.SETTING_LABLE_FONT));
		mSeatLabel.setForeground(Color.WHITE);
		mSeatLabel.setText("좌석 수");
		
		mScrollPane.setBounds(mIDField.getBounds().x, 250, 500, 600);
		
		mDisplayArea.setBounds(0, 0, mScrollPane.getBounds().width, mScrollPane.getBounds().height);
		mDisplayArea.setColumns(20);
		mDisplayArea.setRows(50);
		mDisplayArea.setEditable(false);
		
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 3; j++) {
				if (mKey[i][j] == "") {
					break;
				}
				
				mKeyButtons[i][j].setBounds(mScrollPane.getBounds().x + mScrollPane.getBounds().width + 50 + (150 * j * ((mKey[i][j] == "B") ? 2 : 1)), (150 * i) + 250,
											150 * ((mKey[i][j] == "0") ? 2 : 1), 150);
				mKeyButtons[i][j].setBackground(Color.LIGHT_GRAY);
				
				final String key = mKey[i][j];
				
				if (key == "B") {
					mKeyButtons[i][j].addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							if (mIDField.isEnabled() && mNumberField.isEnabled() && mSeatField.isEnabled()) {
								mSeatField.requestFocus();
								
								String seat = mSeatField.getText();
								
								if (seat.length() > 0) {
									mSeatField.setText(seat.substring(0, seat.length() - 1));
								}
								else {
									mNumberField.requestFocus();
									
									String number = mNumberField.getText();
									
									if (number.length() > 0) {
										mNumberField.setText(number.substring(0, number.length() - 1));
									}
									else {
										mIDField.requestFocus();
										
										String id = mIDField.getText();
										
										if (id.length() > 0) {
											mIDField.setText(id.substring(0, id.length() - 1));
										}
										else {
											mConfirmButton.requestFocus();
										}
									}
								}
							}
						}
					});
				}
				else {
					mKeyButtons[i][j].setFont(new Font("굴림", Font.BOLD, 100));
					mKeyButtons[i][j].setForeground(Color.WHITE);
					mKeyButtons[i][j].addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							if (mIDField.isEnabled() && mNumberField.isEnabled() && mSeatField.isEnabled()) {
								mIDField.requestFocus();
								
								String id = mIDField.getText();
								
								if ((id + key).length() <= Constants.LIMIT_WORD_1) {
									mIDField.setText(id + key);
								}
								else {
									mNumberField.requestFocus();
									
									String number = mNumberField.getText();
									
									if ((number + key).length() <= Constants.LIMIT_WORD_2) {
										mNumberField.setText(number + key);
									}
									else {
										mSeatField.requestFocus();
										
										String seat = mSeatField.getText();
										
										if ((seat + key).length() <= Constants.LIMIT_WORD_3) {
											mSeatField.setText(seat + key);
										}
										else {
											mConfirmButton.requestFocus();
										}
									}
								}
							}
						}
					});
				}
				
				mPanel.add(mKeyButtons[i][j]);
			}			
		}
		
		mConfirmButton.setBounds(mSeatField.getBounds().x + mSeatField.getBounds().width + 50, mSeatField.getBounds().y, 100, 100);
		mConfirmButton.setFont(new Font("굴림", Font.BOLD, 20));
		mConfirmButton.setOpaque(false);
		mConfirmButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (mConfirmButton.getText() == "수 정") {
					mConfirmButton.setText("확 인");
					
					mDisplayArea.setText("");
					
					if (mIDField.getText().equals("0000000000")) {
						mIDField.setText("");
					}
					
					if (mNumberField.getText().equals("0000")) {
						mNumberField.setText("");
					}
					
					if (mSeatField.getText().equals("00")) {
						mSeatField.setText("");
					}
					
					mIDField.setBackground(Color.WHITE);
					mNumberField.setBackground(Color.WHITE);
					mSeatField.setBackground(Color.WHITE);
					
					mIDField.setEnabled(true);
					mNumberField.setEnabled(true);
					mSeatField.setEnabled(true);
				}
				else {					
					mConfirmButton.setText("수 정");
					
					if (mIDField.getText().length() == 0) {
						mIDField.setText("0000000000");
					}
					
					if (mNumberField.getText().length() == 0) {
						mNumberField.setText("0000");
					}
					
					if (mSeatField.getText().length() == 0) {
						mSeatField.setText("00");
					}
					
					// 데이터 업데이트
					BusData.ID = mIDField.getText();
					BusData.CarNumber = mNumberField.getText();
					BusData.Seat = mSeatField.getText();
					
					mDisplayArea.append("Bus ID = "	+ BusData.ID +
							 ", Number = "	+ BusData.CarNumber +
							 ", Seat = "	+ BusData.Seat + "\n");
					
					String[] data = { BusData.ID, BusData.CarNumber, BusData.Seat };
					mCallback.callback(Constants.SEND_BUS_SETTING_INFO, 0, null, data);
					
					mIDField.setBackground(Color.LIGHT_GRAY);
					mNumberField.setBackground(Color.LIGHT_GRAY);
					mSeatField.setBackground(Color.LIGHT_GRAY);
					
					mIDField.setEnabled(false);
					mNumberField.setEnabled(false);
					mSeatField.setEnabled(false);
					mCallback.callback(Constants.START_BUS_BEACON, 0, null, null);
				}
			}
		});
		
		this.addComponents();
		
		super.build();
	}
	
	/**
	 * build() 에서 속성이 정해진 Components 들을 Frame에 추가하는 작업
	 */
	public void addComponents() {
		mPanel.add(mIDLabel);
		mPanel.add(mNumberLabel);
		mPanel.add(mSeatLabel);
		mPanel.add(mIDField);
		mPanel.add(mNumberField);
		mPanel.add(mSeatField);
		mPanel.add(mScrollPane);
		mPanel.add(mConfirmButton);
		
		getMainFrame().add(mPanel);
	}
}

// End of SettingPanel