package com.mango.mapcomponent.ui;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class MapItem extends OverlayItem{

	private GeoPoint gp;
	private long id;
	
	public MapItem(long id, GeoPoint gp, String title, String description) {
		super(gp, title, description);
		this.id = id;
		this.gp = gp;
		
		// TODO Auto-generated constructor stub
	}	
	
	public GeoPoint getGeoPoint() {
		return gp;
	}

	public long getId() {
		return id;
	}
	
	public void setGeoPoint(GeoPoint geoPoint) {
		this.gp = geoPoint;
	}
	
	public void setId(long id) {
		this.id = id;
	}
}
