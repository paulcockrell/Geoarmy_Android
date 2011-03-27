package geoarmy.android;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import geoarmy.android.locationList;
import geoarmy.android.CurrentLocation;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ParseException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class geocacheListActivity extends ListActivity {
	private LayoutInflater mInflater;
	private Vector<RowData> data;
	RowData rd;
	/*private Integer[] imgid = {
	  R.drawable.ruby,R.drawable.center_marker_male,R.drawable.head_bubble,
	  R.drawable.androidmarker
	};*/
	
	/** get geocache list **/
	locationList locations = CurrentLocation.getCurrentLocationList();
	ArrayList<location> localArray = locations.getLocations();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  setContentView(R.layout.list);
	  mInflater = (LayoutInflater) getSystemService(
	  Activity.LAYOUT_INFLATER_SERVICE);
	  data = new Vector<RowData>();
	  location localLocation = new location();
	  String lat, lon, name, id, title, details;
	  for(int i=0;i<locations.length();i++){
	    try {
		  localLocation = localArray.get(i);
		  id = localLocation.getId().toString();
		  name = localLocation.getName();
		  lat =localLocation.getLat();
		  lon =localLocation.getLon();
		  title = "[" + id + "] " + name;
		  details = "Lat: " + lat + ", Lon: " + lon;
	 	  rd = new RowData(i,title,details);
	    } catch (ParseException e) {
		  e.printStackTrace();
	    }
	    data.add(rd);
	  }
	  
	  CustomAdapter adapter = new CustomAdapter(this, R.layout.list,
	                                     R.id.title, data);
	  setListAdapter(adapter);
	  getListView().setTextFilterEnabled(true);
	}
   
    private final void showView(long id) {
  	  location localLocation = localArray.get((int) id);
    	Bundle bundle = new Bundle();
    	bundle.putString("gId", localLocation.getId().toString());
    	Intent i = new Intent(this, GeocacheShow.class);
    	i.putExtras(bundle);
    	startActivityForResult(i, 0);
    }
    
	public void onListItemClick(ListView parent, View v, int position, long id) {       	
		showView(id);
	}
	
	private class RowData {
	   protected int mId;
	   protected String mTitle;
	   protected String mDetail;
	   RowData(int id,String title,String detail){
	   mId=id;
	   mTitle = title;
	   mDetail=detail;
	}
	
	@Override
	public String toString() {
	   return mId+" "+mTitle+" "+mDetail;
	}
}

private class CustomAdapter extends ArrayAdapter<RowData> {

	public CustomAdapter(Context context, int resource,
	                        int textViewResourceId, List<RowData> objects) {               
	  super(context, resource, textViewResourceId, objects);
	}
	 
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {   
		ViewHolder holder = null;
	       TextView title = null;
	       TextView detail = null;
	       ImageView i11=null;
	       RowData rowData= getItem(position);
	       if(null == convertView){
	            convertView = mInflater.inflate(R.layout.row, null);
	            holder = new ViewHolder(convertView);
	            convertView.setTag(holder);
	       }
           holder = (ViewHolder) convertView.getTag();
           title = holder.gettitle();
           title.setText(rowData.mTitle);
           detail = holder.getdetail();
           detail.setText(rowData.mDetail);                                                     
           i11=holder.getImage();
           i11.setImageResource(R.drawable.ruby);
           return convertView;
	}

	private class ViewHolder {
		private View mRow;
	    private TextView title = null;
	    private TextView detail = null;
	    private ImageView i11=null; 
	    
	    public ViewHolder(View row) {
	      mRow = row;
	    }
	    
	    public TextView gettitle() {
	      if(null == title){
	        title = (TextView) mRow.findViewById(R.id.title);
	      }
	    return title;
	    }     
	    
	    public TextView getdetail() {
	      if(null == detail){
	        detail = (TextView) mRow.findViewById(R.id.detail);
	      }
	    return detail;
	    }
	    
	    public ImageView getImage() {
	      if(null == i11){
	        i11 = (ImageView) mRow.findViewById(R.id.img);
	      }
	    return i11;
	    }   
	} 
} 
}