package geoarmy.android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

public class MessageTools {
	
	public MessageTools() {};
	
    public static void alert(String message, Context context) {
   	 // prepare the alert box
       AlertDialog.Builder alertbox = new AlertDialog.Builder(context);

       // set the message to display
       alertbox.setMessage(message);

       // add a neutral button to the alert box and assign a click listener
       alertbox.setNeutralButton("Ok", new DialogInterface.OnClickListener() {

           // click listener on the alert box
           public void onClick(DialogInterface arg0, int arg1) {
               // the button was clicked
           }
       });

       // show it
       alertbox.show();
   }
   
   public static void createGpsDisabledAlert(final Context context){  
		AlertDialog.Builder builder = new AlertDialog.Builder(context);  
	   builder.setMessage("Your GPS is disabled! Would you like to enable it?").setCancelable(false).setPositiveButton("Enable GPS",  
			   new DialogInterface.OnClickListener(){  
    	   	   		public void onClick(DialogInterface dialog, int id){  
    	            showGpsOptions(context);  
    	   	   }  
	   });  
	   builder.setNegativeButton("Do nothing",  
			   new DialogInterface.OnClickListener(){  
		   			public void onClick(DialogInterface dialog, int id){  
		   				dialog.cancel();  
		   			}  
    	  	   }
	   );  
       AlertDialog alert = builder.create();  
       alert.show();  
   }  
   
   private static void showGpsOptions(Context context){
	   Intent gpsOptionsIntent = new Intent(  
			   android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
	   );  
	   context.startActivity(gpsOptionsIntent);  
   }  
}