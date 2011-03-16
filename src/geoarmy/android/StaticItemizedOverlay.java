package geoarmy.android;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class StaticItemizedOverlay extends ItemizedOverlay<OverlayItem> {
	
	private static final String TAG = "StaticItemizedOverlay";
	
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private ArrayList<location> mLocations = new ArrayList<location>();
	Context mContext;
	
 	public StaticItemizedOverlay(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		mContext = context;
	}

 	public void addOverlay(OverlayItem overlay, location l) {
 		mOverlays.add(overlay); // add new overlay
 		mLocations.add(l); // add new overlay
 		populate(); // commit
 	}
 	
	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}
	
	@Override
	protected boolean onTap(int index) {
	  Log.d(TAG, "onTap");
	  OverlayItem item = mOverlays.get(index);
	  final location l = mLocations.get(index);
	  AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
	  dialog.setTitle(item.getTitle());
	  dialog.setMessage(item.getSnippet());
	  dialog.setCancelable(false);
	  dialog.setPositiveButton("Details", new DialogInterface.OnClickListener() {
		  	public void onClick(DialogInterface dialog, int id) {
		  		Log.d(TAG, "onTap id:" + Integer.toString(id));
		    	Bundle bundle = new Bundle();
		    	bundle.putString("gId", Integer.toString(l.getId()));
		    	Intent i = new Intent(mContext, GeocacheShow.class);
		    	i.putExtras(bundle);
		    	mContext.startActivity(i);
		  	}
	  });
	  dialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
		  	public void onClick(DialogInterface dialog, int id) {
		  		dialog.cancel();
		  	}
	  });
	  dialog.show();
	  return true;
	}

}
