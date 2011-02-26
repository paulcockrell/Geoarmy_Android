package geoarmy.android;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class DynamicItemizedOverlay extends ItemizedOverlay<OverlayItem> {
	
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	Context mContext;
	
 	public DynamicItemizedOverlay(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		mContext = context;
	}

 	public void addOverlay(OverlayItem overlay) {
 		this.clearOverlay(); // clear other overlays, otherwise have duplications
 		mOverlays.add(overlay); // add new overlay
 		populate(); // commit
 	}
 	
 	public void clearOverlay() {
 		mOverlays.clear(); // empty array
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
