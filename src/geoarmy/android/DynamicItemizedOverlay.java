package geoarmy.android;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class DynamicItemizedOverlay extends ItemizedOverlay<OverlayItem> {
	
	private static final String TAG = "DynamicItemizedOverlay";
	
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	Context mContext;
	
 	public DynamicItemizedOverlay(Drawable defaultMarker, Context context) {
 		super(boundCenterBottom(defaultMarker));
 		Log.d(TAG, "DynamicItemizedOverlay ");
		mContext = context;
	}

 	public void addOverlay(OverlayItem overlay) {
 		Log.d(TAG, "addOverlay ");
		this.clearOverlay(); // clear other overlays, otherwise have duplications
 		mOverlays.add(overlay); // add new overlay
 		populate(); // commit
 	}
 	
 	public void clearOverlay() {
 		Log.d(TAG, "clearOverlay ");
		mOverlays.clear(); // empty array
 	}
 	
	@Override
	protected OverlayItem createItem(int i) {
		Log.d(TAG, "OverlayItem ");
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		Log.d(TAG, "size ");
		return mOverlays.size();
	}
	
	@Override
	protected boolean onTap(int index) {
	  Log.d(TAG, "onTap ");
	  OverlayItem item = mOverlays.get(index);
	  AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
	  dialog.setTitle(item.getTitle());
	  dialog.setMessage(item.getSnippet());
	  dialog.setCancelable(false);
	  dialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
		  	public void onClick(DialogInterface dialog, int id) {
		  		dialog.cancel();
		  	}
	  });
	  dialog.show();
	  return true;
	}

}
