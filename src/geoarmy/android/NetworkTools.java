package geoarmy.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;
import geoarmy.android.account;


import android.content.Context;
import android.os.Handler;
import android.util.Log;

public class NetworkTools {
    private static HttpClient mHttpClient;
    public static final int REGISTRATION_TIMEOUT = 30 * 1000; // ms
    private static final String TAG = "NetworkUtilities";
	private static String tokenUrl = "login/get_auth_token";
	public static final String BASEURL = "http://www.geoarmy.net/";
	
	public NetworkTools() {}
	
    /**
     * Configures the httpClient to connect to the URL provided.
     */
    public static void maybeCreateHttpClient() {
        if (mHttpClient == null) {
            mHttpClient = new DefaultHttpClient();
            final HttpParams params = mHttpClient.getParams();
            HttpConnectionParams.setConnectionTimeout(params,
                REGISTRATION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(params, REGISTRATION_TIMEOUT);
            ConnManagerParams.setTimeout(params, REGISTRATION_TIMEOUT);
        }
    }

    /**
     * Executes the network requests on a separate thread.
     * 
     * @param runnable The runnable instance containing network mOperations to
     *        be executed.
     */
    public static Thread performOnBackgroundThread(final Runnable runnable) {
        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } finally {

                }
            }
        };
        t.start();
        return t;
    }

	public static String getToken() {
	   	final HttpResponse resp;
	   	final String url = BASEURL + tokenUrl;
	   	
        String respString;
    	// post vars
    	final ArrayList<NameValuePair> params = new ArrayList();
    	HttpEntity entity = null;
        try {
            entity = new UrlEncodedFormEntity(params);
        } catch (final UnsupportedEncodingException e) {
            // this should never happen.
            throw new AssertionError(e);
        }
        final HttpPost post = new HttpPost(url);
        post.addHeader(entity.getContentType());
        post.setEntity(entity);
        maybeCreateHttpClient();

	    try {
            resp = mHttpClient.execute(post);
            HttpEntity hEntity = resp.getEntity();
            InputStream instream = hEntity.getContent();
            respString = convertStreamToString(instream, false);
            
            if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                if (Log.isLoggable(TAG, Log.VERBOSE)) {
                    Log.v(TAG, "Successful authentication");
                }
                return respString;
                
            } else {
                if (Log.isLoggable(TAG, Log.VERBOSE)) {
                    Log.v(TAG, "Error authenticating" + resp.getStatusLine());
                }
                //sendResult(false, handler, context);
                return respString;
            }
        } catch (final IOException e) {
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "IOException when getting authtoken", e);
            }
            //sendResult(false, handler, context);
            return "false";
        } finally {
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "getAuthtoken completing");
            }
        }
	}
	  
    public static Thread attemptAuth(final String url,
        final String name, final String password, final String token, final Handler handler, final Context context) {
            final Runnable runnable = new Runnable() {
                public void run() {
                    authenticate(url, name, password, handler, context);
                }
            };
            // run on background thread.
            return NetworkTools.performOnBackgroundThread(runnable);
        }

	public static boolean authenticate(String url, String name, String password, Handler handler, final Context context) {
    	final HttpResponse resp;
        String respString;
        
        //get token
		String token = getToken();
    	// post vars
    	final ArrayList<NameValuePair> params = new ArrayList();
    	params.add(new BasicNameValuePair("authenticity_token", token));
    	params.add(new BasicNameValuePair("name", name));
    	params.add(new BasicNameValuePair("password", password));
    	params.add(new BasicNameValuePair("mobile", "true"));
    	HttpEntity entity = null;
        try {
            entity = new UrlEncodedFormEntity(params);
        } catch (final UnsupportedEncodingException e) {
            // this should never happen.
            throw new AssertionError(e);
        }
        final HttpPost post = new HttpPost(url);
        post.addHeader(entity.getContentType());
        post.setEntity(entity);
        maybeCreateHttpClient();

	    try {
            resp = mHttpClient.execute(post);
                        
            if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                if (Log.isLoggable(TAG, Log.VERBOSE)) {
                    Log.v(TAG, "Successful authentication");
                }
                HttpEntity hEntity = resp.getEntity();
                InputStream instream = hEntity.getContent();
                respString = convertStreamToString(instream,false);
                sendResult(connectionResult(respString), handler, context);   
                return true;
            } else {
                if (Log.isLoggable(TAG, Log.VERBOSE)) {
                    Log.v(TAG, "Error authenticating" + resp.getStatusLine());
                }
                return false;
            }
        } catch (final IOException e) {
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "IOException when getting authtoken", e);
            }
 
            return false;
        } finally {
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "getAuthtoken completing");
            }
        }
    }
    
	public static boolean getLocations(String url, double latitude, double longitude, Handler handler, Context context) {
		locationList treasureLocations = new locationList();
    	final HttpResponse resp;
        String respString;
        // get token
        String token = getToken();
    	// post vars
        url = url + "?authenticity_token="+token+"&lat="+latitude+"&lon="+longitude+"&commit=true&mobile=true";
    	final HttpGet get = new HttpGet(url);
        maybeCreateHttpClient();
        
        try {
			get.setURI(new URI(url));
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
	    try {
	    	
            resp = mHttpClient.execute(get);
                        
            if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                if (Log.isLoggable(TAG, Log.VERBOSE)) {
                    Log.v(TAG, "Successful authentication");
                }
                HttpEntity hEntity = resp.getEntity();
                InputStream instream = hEntity.getContent();
                respString = convertStreamToString(instream,false);
                treasureLocations = newLocationList(respString) ;
                sendGeocachesResult(treasureLocations, handler, context);
                return true;   
            } else {
                if (Log.isLoggable(TAG, Log.VERBOSE)) {
                    Log.v(TAG, "Error authenticating" + resp.getStatusLine());
                }
                return false;
            }
        } catch (final IOException e) {
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "IOException when getting authtoken", e);
            }
 
            return false;
        } finally {
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "getAuthtoken completing");
            }
        }
    }
	
    private static locationList newLocationList(String JSONString) {
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
    
    private static String convertStreamToString(InputStream is, boolean newline){
    	BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    	StringBuilder sb = new StringBuilder();
    	
    	String line = null;
    	try {
    		while ((line = reader.readLine()) != null) {
    			if(newline) {
    				sb.append(line+"\n");
    			} else {
    				sb.append(line);
    			}
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
    
    private static boolean connectionResult(String JSONString) {
    	String strResult = "false";
    	try {
    		JSONObject obj = new JSONObject(JSONString);
    		JSONArray jsonArray = obj.getJSONArray("connection");
    		
    		int size = jsonArray.length();
    		if (size > 0) {
	    		for (int i = 0; i < size; i++) {
	    			JSONObject another_json_object = jsonArray.getJSONObject(i);
	    	    	strResult = another_json_object.getString("success");
	    		}
    		}
    	}
    	catch (Exception je) {
    		//strResult = je.getMessage();
    	}
    	
    	return Boolean.parseBoolean(strResult);
    }
    
    /**
     * Sends the authentication response from server back to the caller main UI
     * thread through its handler.
     * 
     * @param result The boolean holding authentication result
     * @param handler The main UI thread's handler instance.
     * @param context The caller Activity's context.
     */
    private static void sendResult(final Boolean result, final Handler handler,
        final Context context) {
        if (handler == null || context == null) {
            return;
        }
        handler.post(new Runnable() {
            public void run() {
                ((account) context).onAuthenticationResult(result);
            }
        });
    }
    
    /**
     * Sends the authentication response from server back to the caller main UI
     * thread through its handler.
     * 
     * @param result The boolean holding authentication result
     * @param handler The main UI thread's handler instance.
     * @param context The caller Activity's context.
     */
    private static void sendGeocachesResult(final locationList result, final Handler handler,
        final Context context) {
        if (handler == null || context == null) {
            return;
        }
        handler.post(new Runnable() {
            public void run() {
                ((CurrentLocation) context).onGeocachesResult(result);
            }
        });
    }

      
}