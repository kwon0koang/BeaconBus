package com.dgssm.beaconbus.db;

import java.util.Collection;
import java.util.Collections;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.dgssm.beaconbus.FragmentFavorites;
import com.dgssm.beaconbus.FragmentSearch;
import com.dgssm.beaconbus.Main;
import com.dgssm.beaconbus.R;
import com.dgssm.beaconbus.custom.DataFavorites;
import com.dgssm.beaconbus.utils.Constants;

public class FavoritesDbOpenHelper extends SQLiteOpenHelper {

	private static final String TAG = "FavoriteDbOpenHelper";
	
	private static FavoritesDbOpenHelper favoriteDbOpenHelperInstance;
	private SQLiteDatabase mDB;
	private Context mContext;
	
	public FavoritesDbOpenHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.mContext = context;
    }
	
	public static FavoritesDbOpenHelper getInstance(Context context){
		if(favoriteDbOpenHelperInstance == null){
			favoriteDbOpenHelperInstance = new FavoritesDbOpenHelper(context, Constants.FAVORITES_DB_NAME, null, Constants.FAVORITES_DB_VERSION);
		}
		return favoriteDbOpenHelperInstance;
	}
	
	
	
	
	public void getWritableDB(){
		mDB = favoriteDbOpenHelperInstance.getWritableDatabase();
	}
	
	public void createTableFavorites(){
		String sql = "CREATE TABLE IF NOT EXISTS favorites (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "separator TEXT, " +
                "title TEXT, " +
                "subtitle TEXT, " +
                "id TEXT, " +
                "type TEXT, " +
                "UNIQUE(title));";
		mDB.execSQL(sql);
	}
	
	public void dropTableFavorites(){
		String sql = "DROP TABLE favorites";
		mDB.execSQL(sql);		
	}
	
	public void createTableSearchHistory(){
		String sql = "CREATE TABLE IF NOT EXISTS searchhistory (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "separator TEXT, " +
                "title TEXT, " +
                "subtitle TEXT, " +
                "id TEXT, " +
                "type TEXT);";
		mDB.execSQL(sql);
	}
	
	public void dropTableSearchHistory(){
		String sql = "DROP TABLE searchhistory";
		mDB.execSQL(sql);		
	}
	
	
	
	
	
	
	public void insertFavorites(String separator, String title, String subtitle, String id, int type){
        ContentValues values = new ContentValues();
        values.put("separator", separator);
        values.put("title", title);
        values.put("subtitle", subtitle);
        values.put("id", id);
        values.put("type", type);
        mDB.insert("favorites", null, values);
        selectFavorites();
	}
	
	public void deleteFavorites(String title){
		mDB.delete("favorites", "title=?", new String[]{title});
		selectFavorites();
	}
	
	public void selectFavorites(){
        Cursor c = mDB.query("favorites", null, null, null, null, null, null);
 
        Main.favoritesList.clear();
        while (c.moveToNext()) {
            String separator = c.getString(c.getColumnIndex("separator"));
            String title = c.getString(c.getColumnIndex("title"));
            String subtitle = c.getString(c.getColumnIndex("subtitle"));
            String id = c.getString(c.getColumnIndex("id"));
            int type = c.getInt(c.getColumnIndex("type"));
            
            // 버스
            if(separator.equals(Constants.SEPARATOR_BUS)){
            	Main.favoritesList.add(new DataFavorites(separator, R.drawable.icon_bus, title, subtitle, id, type));
            }
            // 버스 정류장 
            else if(separator.equals(Constants.SEPARATOR_BUSSTOP)){
            	Main.favoritesList.add(new DataFavorites(separator, R.drawable.icon_busstop, title, subtitle, id, type));
            }
        }
        FragmentFavorites.customAdapterFavorites.notifyDataSetChanged();
	}
	
	public void insertSearchHistory(String separator, String title, String subtitle, String id, int type){
        ContentValues values = new ContentValues();
        values.put("separator", separator);
        values.put("title", title);
        values.put("subtitle", subtitle);
        values.put("id", id);
        values.put("type", type);
        mDB.insert("searchhistory", null, values);
        selectSearchHistory();
	}
	
	public void deleteSearchHistory(String title){
		mDB.delete("searchhistory", "title=?", new String[]{title});
		selectSearchHistory();
	}

	public void selectSearchHistory(){
        Cursor c = mDB.query("searchhistory", null, null, null, null, null, null);
 
        Main.searchHistoryList.clear();
        while (c.moveToNext()) {
            String separator = c.getString(c.getColumnIndex("separator"));
            String title = c.getString(c.getColumnIndex("title"));
            String subtitle = c.getString(c.getColumnIndex("subtitle"));
            String id = c.getString(c.getColumnIndex("id"));
            int type = c.getInt(c.getColumnIndex("type"));
            
            // 버스
            if(separator.equals(Constants.SEPARATOR_BUS)){
            	Main.searchHistoryList.add(new DataFavorites(separator, R.drawable.icon_bus, title, subtitle, id, type));
            }
            // 버스 정류장 
            else if(separator.equals(Constants.SEPARATOR_BUSSTOP)){
            	Main.searchHistoryList.add(new DataFavorites(separator, R.drawable.icon_busstop, title, subtitle, id, type));
            }
        }
        // 최근 조회 기록이 위로 떠야하니까
        // Main.searchHistoryList 아이템의 순서를 역순으로
        Collections.reverse(Main.searchHistoryList);
        FragmentFavorites.customAdapterSearchHistory.notifyDataSetChanged();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public boolean isExist(String s){
		boolean ret = false;
		Cursor c = mDB.query("favorites", null, null, null, null, null, null);
		
        while (c.moveToNext()) {
            String title = c.getString(c.getColumnIndex("title"));
            if(title.equals(s)){
            	ret = true;
            	break;
            }
        }
        
		return ret;
	}
	
	
	
	public synchronized void close(){
		if(mDB != null){
			mDB.close();
		}
		super.close();
	}
	
	
	//==================================================================================
	
	@Override
	public void onCreate(SQLiteDatabase db) {	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String sql = "DROP TABLE IF EXISTS favorites";
        db.execSQL(sql);
        sql = "DROP TABLE IF EXISTS searchhistory";
        db.execSQL(sql);
        
        onCreate(db); // 테이블을 지웠으므로 다시 테이블을 만들어주는 과정
	}
	
	
	
}