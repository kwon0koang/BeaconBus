package com.dgssm.kingsmem.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import com.dgssm.kingsmem.callback.Callback;
import com.dgssm.kingsmem.data.BusStopData;
import com.dgssm.kingsmem.etc.Constants;

public class SettingPanel extends Panel {
	// Debug
	private static final	String		TAG				= "KeyPadPanel";
	
	// Key Value
	private final			String[][]	mKey			= { {"1", "2", "3"}, {"4", "5", "6"}, 
												 			{"7", "8", "9"}, {"0", "B", ""} };

	// Panel
	private					JPanel		mPanel			= null;
	
	// Text Field
	private					JTextField	mTextField		= null;
	
	// Button
	private					JButton[][]	mKeyButtons		= null;
	private					JButton		mConfirmButton	= null;	
	
	// Callback
	private					Callback	mCallback		= null;
	
	/**
	 * @param callback : BusMain에 정의되어 있는 CallbackImpl 객체
	 */
	public SettingPanel(Callback callback) {
		this.mCallback = callback;
		
		if (mPanel == null) {
			mPanel = new JPanel();
		}
		
		if (mTextField == null) {
			mTextField = new JTextField();
		}
		
		if (mKeyButtons == null) {
			mKeyButtons = new JButton[4][3];
			
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
		}
		
		if (mConfirmButton == null) {
			mConfirmButton = new JButton("수 정");
		}
		
		this.build();
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
	
	@Override
	protected void build() {
		mPanel.setLayout(null);
		mPanel.setBounds(0, 0, getScreenWidth() - 200, getScreenHeight());
		mPanel.setBackground(Color.DARK_GRAY);
		mPanel.setVisible(false);
		
		mTextField.setBounds((getScreenWidth() / 3) - 220, 100, 800, 100);
		mTextField.setBackground(Color.LIGHT_GRAY);
		mTextField.setFont(Constants.FONT_SIZE_50);
		mTextField.setHorizontalAlignment(JTextField.CENTER);
		mTextField.setDocument(new PlainDocument() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {				
				if ((getLength() + str.length()) <= Constants.LIMIT_WORD) {
					super.insertString(offs, str, a);
				}
			}
		});
		mTextField.setEditable(false);
		
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 3; j++) {
				if (mKey[i][j] == "") {
					break;
				}
				
				mKeyButtons[i][j].setBounds((150 * j * ((mKey[i][j] == "B") ? 2 : 1)) + 650, (150 * i) + 250,
											150 * ((mKey[i][j] == "0") ? 2 : 1), 150);
				mKeyButtons[i][j].setBackground(Color.LIGHT_GRAY);
				
				final String key = mKey[i][j];
				
				if (key == "B") {
					mKeyButtons[i][j].addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {							
							if (mTextField.isEditable()) {
								String text = mTextField.getText();
								
								if (text.length() > 0) {
									mTextField.setText(text.substring(0, text.length() - 1));
								}
							}
						}
					});
				}
				else {
					mKeyButtons[i][j].setFont(Constants.FONT_SIZE_100);
					mKeyButtons[i][j].setForeground(Color.WHITE);
					mKeyButtons[i][j].addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {							
							if (mTextField.isEditable()) {
								String text = mTextField.getText();
								
								if ((text + key).length() <= Constants.LIMIT_WORD) {
									mTextField.setText(text + key);
								}
							}
						}
					});
				}
				
				mPanel.add(mKeyButtons[i][j]);
			}			
		}
		
		mConfirmButton.setBounds(mTextField.getBounds().x + mTextField.getWidth() + 30, mTextField.getBounds().y, 100, 100);
		mConfirmButton.setFont(Constants.FONT_SIZE_20);
		mConfirmButton.setOpaque(false);
		mConfirmButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (mConfirmButton.getText() == "수 정") {
					mConfirmButton.setText("확 인");
					
					mTextField.setBackground(Color.WHITE);
					mTextField.setEditable(true);
				}
				else {
					String text = mTextField.getText();
					
					System.out.println(TAG + " : " + text);
					
					if (text.length() == Constants.LIMIT_WORD) {
						BusStopData.ID = mTextField.getText();
						
						mCallback.callback(Constants.SEND_BUSSTOP_SETTING_INFO, 0, BusStopData.ID, null);
					}
					else {
						mTextField.setText("");
					}
					
					mConfirmButton.setText("수 정");
					
					mTextField.setBackground(Color.LIGHT_GRAY);
					mTextField.setEditable(false);
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
		mPanel.add(mTextField);
		mPanel.add(mConfirmButton);
		
		getMainFrame().add(mPanel);
	}
}

// End of KeyPadPanel