package com.dgssm.beaconbus;

import java.util.Locale;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentPagerAdapter;
	
/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

	Context c;
	
	public SectionsPagerAdapter(FragmentManager fm, Context c) {
		super(fm);
		this.c = c;
	}

	@Override
	public Fragment getItem(int position) {
		/*// getItem is called to instantiate the fragment for the given page.
		// Return a PlaceholderFragment (defined as a static inner class
		// below).
		return PlaceholderFragment.newInstance(position + 1);*/
		Fragment fragment = null;
	    
	    switch (position) {
	    case 0:         
	        fragment = FragmentFavorites.newInstance(position + 1);
	        break;
	    case 1:         
	    	fragment = FragmentSearch.newInstance(position + 1);
	        break;
	    case 2:         
	    	fragment = FragmentSearchNearbyBusStop.newInstance(position + 1);
	        break;
	    case 3:         
	        fragment = FragmentSetting.newInstance(position + 1);
	        break;
	    }
	    return fragment;
	}

	@Override
	public int getCount() {
		// Show total pages.
		return 4;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		Locale l = Locale.getDefault();
		switch (position) {
		case 0:
			return c.getString(R.string.title_section_favorites).toUpperCase(l);
		case 1:
			return c.getString(R.string.title_section_search).toUpperCase(l);
		case 2:
			return c.getString(R.string.title_section_nearby_busstop).toUpperCase(l);
		case 3:
			return c.getString(R.string.title_section_setting).toUpperCase(l);
		}
		return null;
	}
}
