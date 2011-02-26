package geoarmy.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;
import android.util.Log;

public class NetworkTools {
    private static HttpClient mHttpClient;
    public static final int REGISTRATION_TIMEOUT = 30 * 1000; // ms
    private static final String TAG = "NetworkUtilities";

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

	public String getToken(String url) {
	   	final HttpResponse resp;
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
	  
    
	public boolean connection(String url, String name, String password, String token) {
    	final HttpResponse resp;
        String respString;
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
                return connectionResult(respString);   
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
    
    private boolean connectionResult(String JSONString) {
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
      
}