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
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONObject;
import geoarmy.android.account;
import android.content.Context;
import android.location.LocationManager;
import android.os.Handler;
import android.util.Log;

public class NetworkTools {
    private static HttpClient mHttpClient;
    public static final int REGISTRATION_TIMEOUT = 30 * 1000; // ms
    private static final String TAG        = "NetworkUtilities";
	private static String tokenUrl         = "login/get_auth_token";
	private static String authenticateURL  = "login/login";
	private static String geocacheURL      = "geocaches";
	private static String addFoundURL         = "found/add_found";
	private static String addFavoriteURL      = "favorite/add_favorite";
	private static String delFoundURL         = "found/delete_found";
	private static String delFavoriteURL      = "favorite/delete_favorite";
	public static final String BASEURL     = "http://www.geoarmy.net/";
    public static HttpContext localContext = new BasicHttpContext();
    
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
    	final ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
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
                return respString;
            }
        } catch (final IOException e) {
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "IOException when getting authtoken", e);
            }
            return "false";
        } finally {
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "getAuthtoken completing");
            }
        }
	}
	  
    public static Thread attemptAuth(final String name, final String password, final Handler handler, final Context context, final boolean onSuccessMsg) {
            final Runnable runnable = new Runnable() {
                public void run() {
                    authenticate(name, password, handler, context, onSuccessMsg);
                }
            };
            // run on background thread.
            return NetworkTools.performOnBackgroundThread(runnable);
        }

	public static boolean authenticate(String name, String password, Handler handler, final Context context, boolean onSuccessMsg) {
    	final HttpResponse resp;
        String respString;
        String url = BASEURL + authenticateURL;
        boolean isLoggedIn= false;
        //get token
		String token = getToken();
    	// post vars
    	final ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
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
            resp = mHttpClient.execute(post, localContext);
                        
            if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                if (Log.isLoggable(TAG, Log.VERBOSE)) {
                    Log.v(TAG, "Successful authentication");
                }
                HttpEntity hEntity = resp.getEntity();
                InputStream instream = hEntity.getContent();
                respString = convertStreamToString(instream,false);
                isLoggedIn = connectionResult(respString);
                if (onSuccessMsg) {
                	sendResult(isLoggedIn, handler, context);
                } 
            } else {
                if (Log.isLoggable(TAG, Log.VERBOSE)) {
                    Log.v(TAG, "Error authenticating" + resp.getStatusLine());
                }
                UserPreferences.setLoggedIn(false);
                sendNetworkError("Error authenticating, check you username and password", handler, context);
                return false;
            }
        } catch (final IOException e) {
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "IOException when getting authtoken", e);
            }
            UserPreferences.setLoggedIn(false);
            sendNetworkError("IO Exception when authenticating", handler, context);
            return false;
        } finally {
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "getAuthtoken completing");
            }
        }
        UserPreferences.setLoggedIn(isLoggedIn);
		return isLoggedIn;
    }
    
	public static boolean getLocations(double latitude, double longitude, Handler handler, Context context) {
		locationList treasureLocations = new locationList();
    	final HttpResponse resp;
        String respString;
        String url = BASEURL + geocacheURL;
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
                sendNetworkError("Error getting geocache data", handler, context);
                return false;
            }
        } catch (final IOException e) {
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "IOException when getting authtoken", e);
            }
            sendNetworkError("IO Exception when getting geocache data", handler, context);
            return false;
        } finally {
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "getAuthtoken completing");
            }
        }
    }
	
	public static boolean getGeocache(int geocacheId, Handler handler, Context context) {
		location geocache = new location();
    	final HttpResponse resp;
        String respString;
        String url = BASEURL + geocacheURL;
        // get token
        String token = getToken();
    	// get vars
        url = url + "/" + geocacheId + "?authenticity_token="+token + "mobile=true";
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
                    Log.v(TAG, "Successful get of geocache");
                }
                HttpEntity hEntity = resp.getEntity();
                InputStream instream = hEntity.getContent();
                respString = convertStreamToString(instream,false);
                Log.d(TAG, respString.toString());
                geocache = newGeocache(respString) ;
                sendGeocacheResult(geocache, handler, context);
                return true;   
            } else {
                if (Log.isLoggable(TAG, Log.VERBOSE)) {
                    Log.v(TAG, "Error getting geocache data" + resp.getStatusLine());
                }
                sendNetworkError("Error getting geocache data", handler, context);
                return false;
            }
        } catch (final IOException e) {
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "IOException when getting geocache data", e);
            }
            sendNetworkError("IO Exception when getting geocache data", handler, context);
            return false;
        } finally {
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "getGeocache completing");
            }
        }
    }
	
	public static boolean geocacheAction(int geocacheID, String action, Handler handler, final Context context) {
    	final HttpResponse resp;
        String respString;
		boolean result;
        String url = BASEURL;
        if (action == "addfound") {
        	url = url + addFoundURL;
        } else if (action == "addfavorite") {
        	url = url + addFavoriteURL;
        } else if (action == "delfound") {
        	url = url + delFoundURL;
        } else if (action == "delfavorite") {
        	url = url + delFavoriteURL;
        }
        
        boolean isLoggedIn= false;
        //get token
		String token = getToken();
    	// post vars
    	final ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
    	params.add(new BasicNameValuePair("authenticity_token", token));
    	params.add(new BasicNameValuePair("id", ""+geocacheID));
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
            resp = mHttpClient.execute(post, localContext);
                        
            if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                if (Log.isLoggable(TAG, Log.VERBOSE)) {
                    Log.v(TAG, "Successful geocache action");
                }
                HttpEntity hEntity = resp.getEntity();
                InputStream instream = hEntity.getContent();
                respString = convertStreamToString(instream,false);
                result = connectionResult(respString);
               	sendGeocacheActionResult(result, action, handler, context); 
            } else {
                if (Log.isLoggable(TAG, Log.VERBOSE)) {
                    Log.v(TAG, "Error setting geocache action" + resp.getStatusLine());
                }
                UserPreferences.setLoggedIn(false);
                sendNetworkError("Error setting geocache action", handler, context);
                return false;
            }
        } catch (final IOException e) {
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "IOException when setting geocache action", e);
            }
            UserPreferences.setLoggedIn(false);
            sendNetworkError("IO Exception when setting geocache action", handler, context);
            return false;
        } finally {
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "Setting geocache action completed");
            }
        }
        UserPreferences.setLoggedIn(isLoggedIn);
		return result;
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
	    	    	l.setId(Integer.parseInt(another_json_object.getString("id").trim()));
	    	    	l.setName(another_json_object.getString("name"));
	    			l.setLat(another_json_object.getString("lat"));
	    			l.setLon(another_json_object.getString("lon"));
	    			l.setNotes(another_json_object.getString("notes"));
	    	    	mylocationList.addLocation(l);
	    		}
    		}
    	}
    	catch (Exception je) {
    	}
    	
    	return mylocationList;
    }

	private static location newGeocache(String JSONString) {
    	location geocache = new location();
    	try {
    		JSONObject obj = new JSONObject(JSONString);
    		JSONArray jsonArray = obj.getJSONArray("geocache");
    		
    		int size = jsonArray.length();
    		if (size > 0) {
	    		for (int i = 0; i < size; i++) {
	    			JSONObject another_json_object = jsonArray.getJSONObject(i);
	    	    	geocache.setId(Integer.parseInt(another_json_object.getString("id").trim()));
	    	    	geocache.setName(another_json_object.getString("name"));
	    			geocache.setLat(another_json_object.getString("lat"));
	    			geocache.setLon(another_json_object.getString("lon"));
	    			geocache.setNotes(another_json_object.getString("notes"));
	    			geocache.setOwner(another_json_object.getString("owner"));
	    			geocache.setSize(another_json_object.getString("size"));
	    			geocache.setStatus(another_json_object.getString("status"));
	    			geocache.setTerrain(another_json_object.getString("terrain"));
	    			geocache.setDifficulty(another_json_object.getString("difficulty"));
	    			geocache.setFavorite(another_json_object.getBoolean("favorite"));
	    			geocache.setFound(another_json_object.getBoolean("found"));
	    		}
    		}
    	}
    	catch (Exception je) {
    	}
    	
    	return geocache;
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
    private static void sendGeocacheActionResult(final Boolean result, final String action, final Handler handler,
        final Context context) {
        if (handler == null || context == null) {
            return;
        }
        handler.post(new Runnable() {
            public void run() {
                ((GeocacheShow) context).onGeocacheActionResult(result, action);
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
    
    /**
     * Sends the authentication response from server back to the caller main UI
     * thread through its handler.
     * 
     * @param result The boolean holding authentication result
     * @param handler The main UI thread's handler instance.
     * @param context The caller Activity's context.
     */
    private static void sendGeocacheResult(final location result, final Handler handler,
        final Context context) {
        if (handler == null || context == null) {
            return;
        }
        handler.post(new Runnable() {
            public void run() {
                ((GeocacheShow) context).onGeocacheResult(result);
            }
        });
    }
    
    private static void sendNetworkError(final String result, final Handler handler,
            final Context context) {
        if (handler == null || context == null) {
            return;
        }
        handler.post(new Runnable() {
            public void run() {
                ((CurrentLocation) context).onNetworkError(result);
            }
        });
    }
    
	public static boolean gpsEnabled(Context context) {
    	LocationManager locManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);  
    	return locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}
}