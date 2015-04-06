/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mangadul.project;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;


public class CameraDemo extends Activity {
	private static final String TAG = "CameraDemo";
	Camera camera;
	Preview preview;
	Button buttonClick;
        static final int FOTO_MODE = 0;
        
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ic_camera);

		preview = new Preview(this);
		((FrameLayout) findViewById(R.id.preview)).addView(preview);

		buttonClick = (Button) findViewById(R.id.buttonClick);
		buttonClick.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				preview.camera.takePicture(shutterCallback, rawCallback,
						jpegCallback);
			}                        
                });
		Log.d(TAG, "onCreate'd");
	}

        @Override
        protected void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
        }

	ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {
			Log.d(TAG, "onShutter'd");
		}
	};

	/** Handles data for raw picture */
	PictureCallback rawCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			Log.d(TAG, "onPictureTaken - raw");
		}
	};

	/** Handles data for jpeg picture */
	PictureCallback jpegCallback = new PictureCallback() {
                long tt;

		public void onPictureTaken(byte[] data, Camera camera) {
		    FileOutputStream outStream = null;
                    if (data != null)
                    {
                        //Intent mIntent = new Intent();
			try {
                                tt = System.currentTimeMillis();
                                String photoPath = String.format("/sdcard/DCIM/%d.jpg", tt);
                                outStream = new FileOutputStream(photoPath);
                                BitmapFactory.Options opt = new BitmapFactory.Options();
                                opt.inSampleSize = 7;   
                                Bitmap e = BitmapFactory.decodeByteArray(data, 0, data.length, opt);
                                Bitmap thumb = Bitmap.createScaledBitmap (e, e.getWidth(), e.getHeight(), false);                                
                                thumb.compress(Bitmap.CompressFormat.JPEG, 70, outStream);                                
                                /*
                                outStream.write(data);
				outStream.close();
                                 */                                
			} catch (FileNotFoundException e) {
				//e.printStackTrace();
			} catch (IOException e) {
				//e.printStackTrace();
			} finally {
			}

                        SystemClock.sleep(200);
                        camera.startPreview();

                        //setResult(FOTO_MODE,mIntent);
                        Log.d(TAG, "onPictureTaken - jpeg");
                        
                    }
		}

	};

        @Override
        protected void onPause()
        {
                Log.d(getClass().getSimpleName(), "onPause");
                super.onPause();
                if(camera != null)
                {
                    camera.stopPreview();
                    camera.release();
                    camera = null;                       
                }
        }

        /*
        @Override
        protected void onResume()
        {
                Log.d(getClass().getSimpleName(), "onResume");
                super.onResume();
                //camera.startPreview();
        }
         */

        @Override
        protected void onStop()
        {
            super.onStop();
            if(camera != null)
            {
                camera.stopPreview();
                camera.release();
                camera = null;                       
            } 
        }
        
}

/*
 http://developer.android.com/resources/samples/ApiDemos/src/com/example/android/apis/graphics/CameraPreview.html
 http://code.google.com/p/ksoap2-android/wiki/SourceCodeHosting
 http://krvarma-android-samples.googlecode.com/svn/trunk
 http://www.anddev.org/multimedia-problems-f28/refresh-the-preview-in-autofocus-or-continuous-autofocus-t15793.html
 */