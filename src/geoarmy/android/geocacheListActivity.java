package geoarmy.android;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.net.ParseException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class geocacheListActivity extends ListActivity {
	private LayoutInflater mInflater;
	private Vector<RowData> data;
	RowData rd;
	static final String[] title = new String[] {
		    "*New*Apple iPad Wi-Fi (16GB)", "7 Touch Tablet -2GB Google Android",	
	"Apple iPad Wi-Fi (16GB) Rarely Used ","Apple iPad Wi-Fi (16GB) AppleCase"    	};
	static final String[] detail = new String[] {
			"1h 37m Shipping: $10.00","1h 39m Shipping: Free",
			"58m 6s Shipping:$10.00","59m 30s Shipping: $10.95"	};
	private Integer[] imgid = {
	  R.drawable.blip,R.drawable.center_marker_male,R.drawable.head_bubble,
	  R.drawable.androidmarker
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.list);
	mInflater = (LayoutInflater) getSystemService(
	Activity.LAYOUT_INFLATER_SERVICE);
	data = new Vector<RowData>();
	for(int i=0;i<title.length;i++){
	try {
	 	rd = new RowData(i,title[i],detail[i]);
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
	   public void onListItemClick(ListView parent, View v, int position,
	                                                                long id) {        	
	   Toast.makeText(getApplicationContext(), "You have selected "
	                    +(position+1)+"th item",  Toast.LENGTH_SHORT).show();
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
	             i11.setImageResource(imgid[rowData.mId]);
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