package geoarmy.android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

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
}