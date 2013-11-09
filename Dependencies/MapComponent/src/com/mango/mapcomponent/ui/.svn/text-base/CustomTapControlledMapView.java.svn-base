package com.mango.mapcomponent.ui;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.mango.mapcomponent.R;
import com.mango.mapcomponent.maps.libs.BalloonItemizedOverlay;
import com.mango.mapcomponent.maps.libs.FixedMyLocationOverlay;
import com.mango.mapcomponent.maps.libs.OnSingleTapListener;
import com.mango.mapcomponent.maps.libs.TapControlledMapView;

public class CustomTapControlledMapView extends TapControlledMapView{
	private MyLocationOverlay myLocationOverlay;

	private onBalloonTapListner listner;
	private MyDefaultItemizedOverlay mItemizedOverlay;

	private enum MAP_TYPE  {
		DEFAULT("Default"),
		SATELLITE("Satellite");
		MAP_TYPE(String type) {
			atype = type;
		}
		String atype;
		public String getValue() {
			// TODO Auto-generated method stub
			return atype;
		}
		
	}
	private MAP_TYPE currentType = MAP_TYPE.DEFAULT; // 0- default 1 - satellite


	public CustomTapControlledMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context, attrs);
	}  

	public CustomTapControlledMapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context, attrs);
	}

	public CustomTapControlledMapView(Context context, String apiKey) {
		super(context, apiKey);
		initView(context);
	}

	private void initView(Context context) {
		setBuiltInZoomControls(true);
		// Showing current location of user Marker
		myLocationOverlay = new FixedMyLocationOverlay(context, this);
		getOverlays().add(myLocationOverlay);

		// Adding Current Location Of user and SelectMapType Button on Map

		//    	LinearLayout layout = new LinearLayout(context);
		//    	MapView.LayoutParams lparams = new MapView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		//    	
		//    	
		//    	ImageView currentLocationView = new ImageView(context);    	
		//    	LinearLayout.LayoutParams iparams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		//		iparams.gravity = Gravity.CENTER_HORIZONTAL|Gravity.TOP;
		//		currentLocationView.setLayoutParams(iparams);
		//		layout.addView(currentLocationView);
		//		
		//		ImageView mapTypeSelectView = new ImageView(context);
		//		LinearLayout.LayoutParams tparams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		//		tparams.gravity = Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM;
		//		mapTypeSelectView.setLayoutParams(tparams);
		//		layout.addView(mapTypeSelectView);

		// add this empty overlay to the MapView and refresh it		
		Drawable drawable2 = context.getResources().getDrawable(
				R.drawable.point);
		mItemizedOverlay = new MyDefaultItemizedOverlay(drawable2, this);
		mItemizedOverlay.setShowClose(false);
		mItemizedOverlay.setShowDisclosure(true);
		mItemizedOverlay.setSnapToCenter(false);
		getOverlays().add(mItemizedOverlay);
		// TODO: If you dont want this feature just comment it for now. Later will come from XML
		setOnSingleTapListener(new OnSingleTapListener() {		
			@Override
			public boolean onSingleTap(MotionEvent e) {
				mItemizedOverlay.hideAllBalloons();
				return true;
			}
		});
		initialize();
	}

	private void initView(Context context, AttributeSet attrs) {
		initView(context);	
	}

	/**
	 * Call in onCreate()
	 */
	public void initialize() {
		setMapType(currentType);
		postInvalidate();
		getController().setZoom(11);
	}

	/**
	 * Call in onResume()
	 */
	public void startCurrentLocationTracking() {
		myLocationOverlay.enableMyLocation();
		myLocationOverlay.enableCompass();
	}


	/**
	 * Call in onPause()
	 */
	public void stopCurrentLocationTracking() {
		myLocationOverlay.disableMyLocation();
		myLocationOverlay.disableCompass();
	}

	/**
	 * This method zooms to the user's location with a zoom level of 10.
	 */
	public void zoomToMyLocation(final Context ctx) {
		GeoPoint myLocationGeoPoint = myLocationOverlay.getMyLocation();
		if (myLocationGeoPoint == null) {
			final ProgressDialog pd = new ProgressDialog(ctx);
			myLocationOverlay.runOnFirstFix(new Runnable() {

				@Override
				public void run() {
					pd.dismiss();
					zoomToMyLocation(ctx);
				}
			});

			pd.setCancelable(true);
			pd.setMessage("Getting your location! Please wait");
			// pd.show();

		} else {
			getController().animateTo(myLocationGeoPoint);
		}
	}

	public void setMapType(MAP_TYPE type) {
		currentType = type;
		if (type == MAP_TYPE.DEFAULT) {
			setSatellite(false);
		} else {
			setSatellite(true);
		}
	}

	

	/** While writing this method call mItemizedOverlay.populateT(); or else you will get nullPointExceptions
	 * default_true
	 * @param mapItem
	 */
	public void addMarkerOnMap(MapItem mapItem) {
		addMarkerOnMap(mapItem, true);
	}

	/** While writing this method call mItemizedOverlay.populateT(); or else you will get nullPointExceptions
	 * @param mapItem
	 */
	public void addMarkerOnMap(MapItem mapItem, boolean animateTo) {
		mItemizedOverlay.addOverlay(mapItem);
		if(animateTo) {
			getController().animateTo(mapItem.getGeoPoint());
		}
	}

	public void cleaseMarkersOnMap() {
		mItemizedOverlay.clear();
	}
	
	public void showBaloon(MapItem item) {
		mItemizedOverlay.setFocus(item);
	}

	public void setOnBalloonTapListner(onBalloonTapListner listner) {
		this.listner = listner;
	}



	private class MyDefaultItemizedOverlay extends BalloonItemizedOverlay<MapItem>{
		private ArrayList<MapItem> items = new ArrayList<MapItem>();

		public MyDefaultItemizedOverlay(Drawable defaultMarker, MapView mapView) {
			super(boundCenter(defaultMarker), mapView);
			populate();
		}


		public void clear() {
			items.clear();
		}

		public void addOverlay(MapItem mapItem) {
			items.add(mapItem);
			populate();
		}

		@Override
		protected MapItem createItem(int i) {
			return items.get(i);
		}

		@Override
		public int size() {
			return items.size();
		}

		@Override
		protected boolean onBalloonTap(int index, MapItem item) {
			if(listner!=null) {
				listner.onBalloonTap(items.get(index));
			}
			return true;
		}
	}
	
	public void setMapType(int item) {
		if(item==0)
		 {
			 setSatellite(false);
		 }
		 else
		 {
			 setSatellite(true);
		 }
	}

	public void showMapTypeDialog() {
		final AlertDialog.Builder alertbox = new AlertDialog.Builder(getContext());
		final CharSequence[] Photo_options = {MAP_TYPE.DEFAULT.getValue(), MAP_TYPE.SATELLITE.getValue()};

		alertbox.setItems(Photo_options, new DialogInterface.OnClickListener() {   

			public void onClick(DialogInterface dialog, int item) 
			{   
				if(item==0) {
					setMapType(MAP_TYPE.DEFAULT);
				}
				else if(item==1){
					setMapType(MAP_TYPE.SATELLITE);
				}

			}
		});

		AlertDialog alert = alertbox.create();
		alert.show();
	}

	public GeoPoint getCurrentLocationOfuser() {
		return myLocationOverlay.getMyLocation();
	}
}
