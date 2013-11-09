package com.mango.mapcomponent;

import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.mango.mapcomponent.ui.CustomTapControlledMapView;
import com.mango.mapcomponent.ui.MapItem;
import com.mango.mapcomponent.ui.onBalloonTapListner;

public class MainActivity extends MapActivity implements onBalloonTapListner {
    /** Called when the activity is first created. */
	CustomTapControlledMapView mapview;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mapview = (CustomTapControlledMapView) findViewById(R.id.mapview);
        MapItem item = new MapItem(0, new GeoPoint((int)(12.971598*1e6), (int)(77.594562*1e6)), "Bangalore", "India");
        mapview.addMarkerOnMap(item,true);
    }   
    
	@Override
	protected void onPause() {
		mapview.stopCurrentLocationTracking();
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mapview.startCurrentLocationTracking();
		mapview.setOnBalloonTapListner(this);
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onBalloonTap(MapItem item) {
		// TODO Auto-generated method stub		
	}
}