/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mangadul.project;

import java.io.IOException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Toast;
import java.io.FileOutputStream;



public class AmbilGambar extends Activity implements SurfaceHolder.Callback,
		OnClickListener {
	static final int FOTO_MODE = 0;
	private static final String TAG = "CameraTest";
	Camera mCamera;
	boolean mPreviewRunning = false;
	private Context mContext = this;

        @Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		Log.e(TAG, "onCreate");

		Bundle extras = getIntent().getExtras();
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.ic_tab_cameraview);
		mSurfaceView = (SurfaceView) findViewById(R.id.surface_camera);
		mSurfaceView.setOnClickListener(this);
		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.addCallback(this);
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
                private Activity ACT;
                private Context CON;
		public void onPictureTaken(byte[] imageData, Camera c) {

                if (imageData != null) {
                        Intent mIntent = new Intent();
                        try {
                            FileOutputStream  out = new FileOutputStream("/sdcard/gb01.jpg");
                            Bitmap e = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                            e.compress(Bitmap.CompressFormat.JPEG, 65, out);
                            out.write(imageData);
                            out.close();
                            Intent i = new Intent(ACT, MediaStore.class);
                            ACT.startActivity(i);
                            setResult(FOTO_MODE,mIntent);                            
                        } catch (Exception e) {
                            Toast.makeText(
                                    CON, "Data berhasil disimpan",
                                    Toast.LENGTH_LONG).show();
                            ACT.finish();
                        }
                        
                        SystemClock.sleep(100);
                        mCamera.startPreview();
                        
                        /*
                            Intent mIntent = new Intent();
                            FileUtilities.StoreByteImage(mContext, imageData, 50, "ImageName");
                            mCamera.startPreview();
                            setResult(FOTO_MODE,mIntent);
                         */
			}
		}
	};

        @Override
	protected void onResume() {
		Log.e(TAG, "onResume");
		super.onResume();
	}

        @Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

        @Override
	protected void onStop() {
		Log.e(TAG, "onStop");
		super.onStop();
	}

	public void surfaceCreated(SurfaceHolder holder) {
		Log.e(TAG, "surfaceCreated");
		mCamera = Camera.open();

	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		Log.e(TAG, "surfaceChanged");

		// XXX stopPreview() will crash if preview is not running
		if (mPreviewRunning) {
			mCamera.stopPreview();
		}

		Camera.Parameters p = mCamera.getParameters();
		p.setPreviewSize(w, h);
		mCamera.setParameters(p);
		try {
			mCamera.setPreviewDisplay(holder);
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
		mCamera.startPreview();
		mPreviewRunning = true;
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.e(TAG, "surfaceDestroyed");
		mCamera.stopPreview();
		mPreviewRunning = false;
		mCamera.release();
	}

	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;

	public void onClick(View arg0) {

		mCamera.takePicture(null, mPictureCallback, mPictureCallback);

	}
}
