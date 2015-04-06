package mangadul.project;

import java.io.IOException;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.logging.Level;
import java.util.logging.Logger;

class Preview extends SurfaceView implements SurfaceHolder.Callback {
	private static final String TAG = "Preview";
        Parameters mParameters;
	SurfaceHolder mHolder;
	public Camera camera;
        boolean mPreviewRunning = false;
        

	Preview(Context context) {
		super(context);
		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}


        public void surfaceCreated(SurfaceHolder holder) {
            if(camera != null)
            {
                camera.stopPreview();
                mPreviewRunning = false;
                camera.release();
                camera = null;                
            } 
            
            camera = Camera.open();
            Camera.Parameters parameters = camera.getParameters();
            parameters.set("orientation", "portrait");
            camera.setParameters(parameters);
            camera.setDisplayOrientation(90);
            
            try {
                camera.setPreviewDisplay(holder);
                camera.startPreview();
            } catch (IOException ex) {
                Logger.getLogger(Preview.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            if (camera != null) {
                camera.stopPreview();
                mPreviewRunning = false;
                camera.release();
		camera = null;                
            }
        }
        
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
                Camera.Parameters p = camera.getParameters();
	        p.setPictureFormat(PixelFormat.JPEG); 
                p.setPreviewFormat(ImageFormat.JPEG);
	        camera.setParameters(p);                                
		mPreviewRunning = true;
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		Paint p = new Paint(Color.RED);
		Log.d(TAG, "draw");
		canvas.drawText("PREVIEW", canvas.getWidth() / 2,
				canvas.getHeight() / 2, p);
	}
     
	/*
	 * http://sleepyrea.blogspot.com/2010/10/orientation-of-samsung-galaxy-s-camera.html	 
	 * 
	 * */
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		WindowManager w = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		Display d = w.getDefaultDisplay();
		int dwidth = d.getWidth();
		int dheight = d.getHeight();
		if (dwidth < dheight)
		{
			// HACK: to stretch the portrait view so that the camera preview fills the screen + 200
			setMeasuredDimension(dwidth, dheight); 
			ViewGroup.LayoutParams lp = getLayoutParams();
		if (lp.getClass() == FrameLayout.LayoutParams.class)
			((FrameLayout.LayoutParams)lp).gravity = Gravity.CENTER_HORIZONTAL;
		else if (lp.getClass() == LinearLayout.LayoutParams.class)
			((LinearLayout.LayoutParams)lp).gravity = Gravity.CENTER_HORIZONTAL;
		else if (lp.getClass() == RelativeLayout.LayoutParams.class)
			((RelativeLayout.LayoutParams)lp).addRule(RelativeLayout.CENTER_HORIZONTAL);
			setLayoutParams(lp);
		}
		else
		{
			setMeasuredDimension(dwidth, dheight);
		}
		getHolder().setFixedSize(dwidth, dheight);
	} // onMeasure()
	
}