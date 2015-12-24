package com.dgssm.beaconbus.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import com.dgssm.beaconbus.R;
import com.dgssm.beaconbus.custom.DataBus;
import com.dgssm.beaconbus.custom.DataBusStop;
import com.dgssm.beaconbus.utils.Constants;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbOpenHelper extends SQLiteOpenHelper {

	private static final String TAG = "DbOpenHelper";
	
	private static DbOpenHelper dbOpenHelperInstance ;
	private SQLiteDatabase mDB;
	private Context mContext;
	private String mPath = Constants.DB_PATH + Constants.DB_NAME;
	
	private DbOpenHelper(Context context) {
		super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
		this.mContext = context;
	}
	
	public static DbOpenHelper getInstance(Context context){
		if(dbOpenHelperInstance == null){
			dbOpenHelperInstance = new DbOpenHelper(context);
		}
		return dbOpenHelperInstance;
	}
	
	public void createDatabase() {
		SQLiteDatabase chkDB = null;
		
		try{
			chkDB = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);	
		} catch(SQLiteException e){	}
		
		if(chkDB != null){
			chkDB.close();
		}
		
		boolean dbNotExist = chkDB == null ? true : false;
		if(dbNotExist){
			this.getReadableDatabase();
			try {
				InputStream mInput = mContext.getAssets().open(Constants.DB_NAME);
				String outFileName = mPath;
				OutputStream mOutput = new FileOutputStream(outFileName);
				byte[] buffer = new byte[1024];
				int length;
				while( (length = mInput.read(buffer)) > 0 ){
					mOutput.write(buffer, 0, length);
				}
				mOutput.flush();
				mOutput.close();
				mInput.close();
			} catch (IOException e) {
				throw new Error("Error copying database");
			}
			
		}
	}
	
	public void openDatabase(){
		mDB = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
	}
	
	public void removeDatabase(){
		File mFile = new File(mPath);
		mFile.delete();
	}
	
	public synchronized void close(){
		if(mDB != null){
			mDB.close();
		}
		super.close();
	}
	
	public void getBus(ArrayList<DataBus> ar){
		Cursor c = mDB.rawQuery("SELECT * FROM DAEGU_Route", null);
		//Cursor c = mDB.rawQuery("SELECT * FROM bus", null);
		while(c.moveToNext()){
			String busNum = c.getString(3);
			String busDirection = c.getString(4);
			String busId = c.getString(1);
			int busType = Integer.parseInt(c.getString(11));
			ar.add(new DataBus(R.drawable.icon_bus, busNum, busDirection, busId, busType));
		}
	}
	
	public void getBusStop(ArrayList<DataBusStop> ar){
		Cursor c = mDB.rawQuery("SELECT * FROM DAEGU_Stop", null);
		while(c.moveToNext()){
			String busStopName = c.getString(3);
			String busStopNum = c.getString(1);
			String busStopId = c.getString(2);
			
			StringBuffer tmpLat = new StringBuffer(c.getString(6));		// geoY : 128737024
			StringBuffer tmpLng = new StringBuffer(c.getString(5));		// geoX : 35825511
			tmpLat.insert(2, ".");		// 128.737024
			tmpLng.insert(3, ".");	// 35.825511
			double lat = Double.parseDouble(tmpLat.toString());
			double lng = Double.parseDouble(tmpLng.toString());
			ar.add(new DataBusStop(R.drawable.icon_busstop, busStopName, busStopNum, busStopId, lat, lng));
		}
	}
	
	public String getBusType(int id){
//		Cursor c = mDB.rawQuery("SELECT DISTINCT * FROM BusType WHERE _id = " + id, null);
//		String busType = null;
//		while(c.moveToNext()){
//			busType = c.getString(1);
//		}
		
		// 매번 위처럼 DB 쿼리날리기 좀 그래서 일단 임시방편
		String busType = null;
		switch (id){
		case 41: busType = "급행";
			break;
		case 43: busType = "간선";
			break;
		case 70: busType = "순환";
			break;
		case 71: busType = "지선";
			break;
		case 72: busType = "칠곡";
			break;
		case 73: busType = "경산";
			break;
		}
		return busType;
	}
	
	
	public String[] getBusNameByUsingId(String id){
		Cursor c = mDB.rawQuery("SELECT * FROM DAEGU_Route WHERE routeId1 = " + id, null);
		String ret[] = new String[2];		// 0 : 버스명, 1 : 방면
		while(c.moveToNext()){
			ret[0] = c.getString(3);
			ret[1] = c.getString(4);
		}
		
		return ret;
	}
	
	
	
	
	
	
	
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// DB 만들거 아니라서 생략
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// 이거도 생략
	}
	
	
	
	
}