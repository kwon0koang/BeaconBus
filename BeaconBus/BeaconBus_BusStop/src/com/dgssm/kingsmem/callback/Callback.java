package com.dgssm.kingsmem.callback;

public interface Callback {
	/**
	 * @param cmd  : callback이 어떤 것을 수행할지 구분해주는 파라미터 : Constants.java 참고 
	 * @param arg0 : int 데이터용 파라미터
	 * @param arg1 : String 데이터용 파라미터
	 * @param arg2 : 각종 Object 데이터용 파라미터
	 */
	public void callback(int cmd, int arg0, String arg1, Object arg2);
}

//End of Callback