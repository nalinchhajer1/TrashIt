/***
  Copyright (c) 2012 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  From _The Busy Coder's Guide to Android Development_
    http://commonsware.com/Android
 */

package com.gdg.hackathon.trashit;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class TrashMapActivity extends AbstractMapActivity implements
    OnNavigationListener, OnInfoWindowClickListener {
	
	private String TAG = TrashMapActivity.class.getSimpleName();
	
  private static final String STATE_NAV="nav";
  private static final int[] MAP_TYPE_NAMES= { R.string.normal,
      R.string.hybrid, R.string.satellite, R.string.terrain };
  private static final int[] MAP_TYPES= { GoogleMap.MAP_TYPE_NORMAL,
      GoogleMap.MAP_TYPE_HYBRID, GoogleMap.MAP_TYPE_SATELLITE,
      GoogleMap.MAP_TYPE_TERRAIN };
  private GoogleMap map=null;

protected Location currentLatLong;

View detailLayout ;
ImageView imageView1;
TextView textView1, textView2;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (readyToGo()) {
      setContentView(R.layout.activity_trash_map);

      SupportMapFragment mapFrag=
          (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);

      initListNav();

      map=mapFrag.getMap();

      detailLayout = findViewById(R.id.detailLayout);
      imageView1 = (ImageView) findViewById(R.id.imageView1);
      textView1 = (TextView) findViewById(R.id.textView1);
      textView2 = (TextView) findViewById(R.id.textView2);
      detailLayout.setVisibility(View.GONE);
      
      map.setMyLocationEnabled(true);
      
      LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);
      Location latLong = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
      
      if(latLong == null) {
    	  latLong = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
      }
      
      map.setOnMyLocationChangeListener(new OnMyLocationChangeListener() {
		
		@Override
		public void onMyLocationChange(Location arg0) {
			if(arg0!=null) {
				currentLatLong = arg0;
			}
			
		}
	});
      
      map.setOnMapClickListener(new OnMapClickListener() {
		
		@Override
		public void onMapClick(LatLng arg0) {
			// TODO Auto-generated method stub
			Log.e(TAG , ""+arg0.latitude +" "+arg0.longitude );
		}
	});
      
      map.setOnCameraChangeListener(new OnCameraChangeListener() {
		
		@Override
		public void onCameraChange(CameraPosition arg0) {
			detailLayout.setVisibility(View.GONE);
		}
	});
      
      if (savedInstanceState == null) {
          CameraUpdate center=
              CameraUpdateFactory.newLatLng(new LatLng(latLong.getLatitude(),
              		latLong.getLongitude()));
          CameraUpdate zoom=CameraUpdateFactory.zoomTo(14);

          map.moveCamera(center);
          map.animateCamera(zoom);
        }

//      addMarker(map, 40.76866299974387, -73.98268461227417,
//                R.string.lincoln_center,
//                R.string.lincoln_center_snippet);
//      addMarker(map, 40.765136435316755, -73.97989511489868,
//                R.string.carnegie_hall, R.string.practice_x3);
//      addMarker(map, 40.70686417491799, -74.01572942733765,
//                R.string.downtown_club, R.string.heisman_trophy);

      map.setInfoWindowAdapter(new PopupAdapter(getLayoutInflater()));
      map.setOnInfoWindowClickListener(this);
    }
    
    
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getSupportMenuInflater().inflate(R.menu.trash_map, menu);

    return(super.onCreateOptionsMenu(menu));
  }
  
  @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		RequestQueue volley = Volley.newRequestQueue(this);
	    Request<JSONArray> request = new JsonArrayRequest("http://172.16.50.13/trash_can/trash/list",new Listener<JSONArray>() {

			

			@Override
			public void onResponse(JSONArray response) {
				// TODO Auto-generated method stub
				int count = response.length();
				for (int i = 0; i < count; i++) {
					try {
					JSONObject obj = response.getJSONObject(i);
					double lat = obj.getDouble("latitude");
					double lon = obj.getDouble("longitude");
					String title = obj.getString("base_bounty");
					String message = obj.getString("message");
					
					addMarker(map, lat, lon,message,
			                "Bounty - "+title);
					}catch(JSONException e) {
						Log.e(TAG , e.toString());
						Toast.makeText(TrashMapActivity.this, "Server unavailable", Toast.LENGTH_LONG).show();
					}
				}
				
				
			}
		}, new ErrorListener() {

		

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
				addMarker(map, 13.0930851, 80.2618934, "4 trees", "Bounty - 2000");
				addMarker(map, 13.08007438, 80.26236556, "Old Junkies", "Bounty - 50");
				addMarker(map, 13.095599500, 80.27806386, "This area looks messed up", "Bounty - 300");
				Log.e(TAG , error.toString());
				Toast.makeText(TrashMapActivity.this, "Not able to connect - Populating with dummy data", Toast.LENGTH_LONG).show();
			}
		});
	    
	    
	    volley.add(request);
	    volley.start();
	}

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.action_add) {
    	Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		startActivityForResult(intent, 1003);

      return(true);
    }

    return super.onOptionsItemSelected(item);
  }
  
  @Override
protected void onActivityResult(int arg0, int arg1, Intent arg2) {
	// TODO Auto-generated method stub
	super.onActivityResult(arg0, arg1, arg2);
	Toast.makeText(this, "Thanks for tagging, image uploaded.", Toast.LENGTH_LONG).show();
	
	if(currentLatLong!=null) {
		addMarker(map, currentLatLong.getLatitude(), currentLatLong.getLongitude(),"New trash discovered",
                "Bounty - 50");
		CameraUpdate center=
	              CameraUpdateFactory.newLatLng(new LatLng(currentLatLong.getLatitude(),
	            		  currentLatLong.getLongitude()));
	          CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);

	          map.moveCamera(center);
	          map.animateCamera(zoom);
	}
}
  
  @Override
  public boolean onNavigationItemSelected(int itemPosition, long itemId) {
    map.setMapType(MAP_TYPES[itemPosition]);

    return(true);
  }

  @Override
  public void onSaveInstanceState(Bundle savedInstanceState) {
    super.onSaveInstanceState(savedInstanceState);
    
    savedInstanceState.putInt(STATE_NAV,
                              getSupportActionBar().getSelectedNavigationIndex());
  }
  
  @Override
  public void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    
    getSupportActionBar().setSelectedNavigationItem(savedInstanceState.getInt(STATE_NAV));
  }

  @Override
  public void onInfoWindowClick(Marker marker) {
	  
	  detailLayout.setVisibility(View.VISIBLE);
	  
	  textView1.setText(marker.getTitle());
	  textView2.setText(marker.getSnippet());
  }

  private void initListNav() {
    ArrayList<String> items=new ArrayList<String>();
    ArrayAdapter<String> nav=null;
    ActionBar bar=getSupportActionBar();

    for (int type : MAP_TYPE_NAMES) {
      items.add(getString(type));
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
      nav=
          new ArrayAdapter<String>(
                                   bar.getThemedContext(),
                                   android.R.layout.simple_spinner_item,
                                   items);
    }
    else {
      nav=
          new ArrayAdapter<String>(
                                   this,
                                   android.R.layout.simple_spinner_item,
                                   items);
    }

    nav.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    bar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
    bar.setListNavigationCallbacks(nav, this);
  }

  private void addMarker(GoogleMap map, double lat, double lon,
                         String title, String snippet) {
    map.addMarker(new MarkerOptions().position(new LatLng(lat, lon))
                                     .title(title)
                                     .snippet(snippet));
  }
}
