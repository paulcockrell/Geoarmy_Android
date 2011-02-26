package geoarmy.android;

import geoarmy.android.locationList;
import geoarmy.android.location;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

public class RestClient {
	
	public RestClient() {}
	
	public int getVal() {
		return 5;
	}
 
    private static String convertStreamToString(InputStream is){
    	BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    	StringBuilder sb = new StringBuilder();
    	
    	String line = null;
    	try {
    		while ((line = reader.readLine()) != null) {
    			sb.append(line+"\n");
    		}
    	} catch (IOException e) {
    		e.printStackTrace();
    	} finally {
    		try {
    			is.close();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    	return sb.toString();
    }
    
    public locationList getGeolocations(String url) {
    	locationList treasureLocations = new locationList();
    	
    	HttpClient httpclient = new DefaultHttpClient();
    	
    	HttpGet httpget = new HttpGet(url);
    	Log.v("url",url);
    	HttpResponse response;
    	try {
    		response = httpclient.execute(httpget);
    		HttpEntity entity = response.getEntity();
    		
    		if (entity != null) {
    			InputStream instream = entity.getContent();
    			String result = convertStreamToString(instream);
    			treasureLocations = newLocationList(result) ;
    		}
    		
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		return treasureLocations;
    }    
    
    private locationList newLocationList(String JSONString) {
    	locationList mylocationList = new locationList();
    	try {
    		JSONObject obj = new JSONObject(JSONString);
    		JSONArray jsonArray = obj.getJSONArray("locations");
    		
    		int size = jsonArray.length();
    		if (size > 0) {
	    		for (int i = 0; i < size; i++) {
	    			JSONObject another_json_object = jsonArray.getJSONObject(i);
	    	    	location l = new location();
	    			l.setLat(another_json_object.getString("lat"));
	    			l.setLon(another_json_object.getString("lon"));
	    	    	mylocationList.addLocation(l);
	    		}
    		}
    	}
    	catch (Exception je) {
    		// catch?
    	}
    	
    	return mylocationList;
    }
    
}