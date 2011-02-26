package geoarmy.android;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

public class compass extends Activity {
	private SensorManager mgr=null;
	private TextView degrees=null;
	private float[] lastMagFields=null;
	private float[] lastAccels=null;
	private float[] rotationMatrix=new float[9];
	private float[] orientation=new float[3];
	private int height, width;
	private Bitmap bitmapOrg;
	private Matrix matrix = new Matrix();
	private Bitmap rotateBitmap;
	private BitmapDrawable bmd;
	private ImageView image;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.compass);
		
		image = (ImageView) findViewById(R.id.compass);
		degrees=(TextView)findViewById(R.id.lbl_degrees);

		mgr=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
		mgr.registerListener(magListener,
													mgr.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
													SensorManager.SENSOR_DELAY_UI);
		mgr.registerListener(accListener,
													mgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
													SensorManager.SENSOR_DELAY_UI);
		
		/*this.btnClose = (Button)findViewById(R.id.closeButton);
		this.btnClose.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();				
			}
		});*/
		
		bitmapOrg = BitmapFactory.decodeResource(getResources(), R.drawable.compass);
		
		height = bitmapOrg.getHeight();
		width  = bitmapOrg.getWidth();
		
		rotateCompass(0);
	}
	
	private void rotateCompass(float degrees) {
		matrix.reset();
		matrix.postRotate(degrees);
		
		rotateBitmap = Bitmap.createBitmap(bitmapOrg, 0,0, width, height, matrix, true);
		bmd = new BitmapDrawable(rotateBitmap);
		
		image.setImageDrawable(bmd);
		image.setScaleType(ScaleType.CENTER);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		mgr.unregisterListener(accListener);
		mgr.unregisterListener(magListener);
	}
	
	private void computeOrientation() {
		if (SensorManager.getRotationMatrix(rotationMatrix, null,
																				lastMagFields, lastAccels)) {
			SensorManager.getOrientation(rotationMatrix, orientation);
			
			float north=orientation[0]*57.2957795f;
			
			if (north<0) {
				north=360.0f+north;
			}
			
			north = (float) Math.ceil(north);
			degrees.setText(String.valueOf(north) + " degrees");
			rotateCompass(north);
		}
	}
	
	private SensorEventListener magListener=new SensorEventListener() {
		public void onSensorChanged(SensorEvent e) {
			if (lastMagFields==null) {
				lastMagFields=new float[3];
			}
			
			System.arraycopy(e.values, 0, lastMagFields, 0, 3);
			
			if (lastAccels!=null) {
				computeOrientation();
			}
		}
		
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// unused
		}
	};
	
	private SensorEventListener accListener=new SensorEventListener() {
		public void onSensorChanged(SensorEvent e) {
			if (lastAccels==null) {
				lastAccels=new float[3];
			}
			
			System.arraycopy(e.values, 0, lastAccels, 0, 3);
			
			if (lastMagFields!=null) {
				computeOrientation();
			}
		}
		
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// unused
		}
	};
}