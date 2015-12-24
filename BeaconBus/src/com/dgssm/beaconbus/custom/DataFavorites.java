package com.dgssm.beaconbus.custom;

import java.io.Serializable;

public class DataFavorites implements Serializable
{
	private static final long serialVersionUID = 1L;
	private String separator;
	private int favorites;
	private String title;
	private String subtitle;
	private String id;
	private int type;

	public DataFavorites(String separator, int favorites, String title, String subtitle, String id, int type)	{
		super();
		this.separator = separator;
		this.favorites = favorites;
		this.title = title;
		this.subtitle = subtitle;
		this.id = id;
		this.type = type;
	}
	
	public String getSeparator()	{
		return separator;
	}
	
	public int getFavoritesImage()	{
		return favorites;
	}
	
	public String getTitle()	{
		return title;
	}
	
	public String getSubtitle()	{
		return subtitle;
	}

	public String getId()	{
		return id;
	}
	
	public int getType(){
		return type;
	}
}
