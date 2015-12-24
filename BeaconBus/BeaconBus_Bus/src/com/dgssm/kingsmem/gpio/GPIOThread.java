package com.dgssm.kingsmem.gpio;

import com.dgssm.kingsmem.callback.Callback;
import com.dgssm.kingsmem.etc.Constants;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class GPIOThread implements Runnable {
	// Debug
	private static final	String					TAG			= "GPIOThread";
	
	// Status
	private final 			int 					RUNNING 	= 0;
	private final 			int 					SUSPENDED 	= 1;
	private final 			int 					STOPPED 	= 2;
	private 				int 					state 		= SUSPENDED;
	
	// Thread
	private					Thread					mThread		= null;
	
	// Callback
	private					Callback				mCallback	= null;
	
	// GPIO
	private 				GpioController 			mGpio		= null;
	private 				GpioPinDigitalOutput 	mLed1		= null;
	private 				GpioPinDigitalOutput 	mLed2		= null;
	private 				GpioPinDigitalInput 	mButton		= null;
	
	/**
	 * @param callback : BusMain에 정의되어 있는 CallbackImpl 객체
	 */
	public GPIOThread(Callback callback) {
		this.mThread = new Thread(this);
		this.mCallback = callback;
		
		this.mGpio = GpioFactory.getInstance();
		this.mLed1 = mGpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, PinState.LOW);
		this.mLed2 = mGpio.provisionDigitalOutputPin(RaspiPin.GPIO_05, PinState.LOW);
		this.mButton = mGpio.provisionDigitalInputPin(RaspiPin.GPIO_01, PinPullResistance.PULL_DOWN);
	}

	@Override
	public void run() {
		System.out.println(TAG + " : run() -> GPIOThread Start");
		
		// 버튼 이벤트 추가
		mButton.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
            	System.out.println(TAG + " : GpioPinDigitalStateChangeEvent() -> 버튼 이벤트 발생");
            	
                ledOn();
            }
        });
        
		while (true) {
			if (checkState()) {
				mThread = null;
				break;
			}			
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException ie) {
				System.err.println(TAG + " : run() -> " + ie.getMessage());
			}
		}
	}
	
	/**
	 * LED 1, 2 상태를 high로 바꾸고 Callback을 호출한다. (Constants.GPIO_LED_ON)
	 */
	public void ledOn() {
		System.out.println(TAG + " : ledOn()");
		
		this.mLed1.high();
		this.mLed2.high();
		
		mCallback.callback(Constants.GPIO_LED_ON, 0, null, null);
	}
	
	/**
	 * LED 1, 2 상태를 low로 바꾸고 Callback을 호출한다. (Constants.GPIO_LED_OFF)
	 */
	public void ledOff() {
		System.out.println(TAG + " : ledOff()");
		
		this.mLed1.low();
		this.mLed2.low();
		
		mCallback.callback(Constants.GPIO_LED_OFF, 0, null, null);
	}
	
	/**
	 * LED 1, 2 상태 값을 리턴한다.
	 * @return 둘다 high면 true, low면 false
	 */
	public boolean isOn() {
		boolean state = (mLed1.isHigh() && mLed2.isHigh()) ? true : false;
		
		System.out.println(TAG + " : isOn() -> " + state);
		
		return state; 
	}
	
	/** 
	 * Thread 상태를 지정하고 RUNNING 이외에는 Thread에 interrupt를 건다.
	 * @param state : Thread 상태
	 */
	private synchronized void setState(int state) {
		this.state = state;
		
		if (this.state == RUNNING) {
			notify();
		} else {
			mThread.interrupt();
		}
	}
	
	/**
	 * run() 에서 Thread의 상태를 체크하기 위한 함수
	 * @return state 변수가 STOPPED 인지를 검사해 리턴한다.
	 */
	private synchronized boolean checkState() {
		while (state == SUSPENDED) {
			try {
				wait();
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}
		
		return state == STOPPED;
	}
	
	/**
	 * Thread를 시작하는 함수
	 */
	public void start() {		
		mThread.start();
		
		setState(RUNNING);
	}
	
	/**
	 * Thread를 종료하는 함수
	 */
	public void stop() {
		setState(STOPPED);
	}
}

// End of GPIOThread