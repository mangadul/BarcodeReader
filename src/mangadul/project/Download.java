package mangadul.project;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

public class Download extends Activity   {
	
	ProgressDialog mProgressDialog;
	String filename = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
   		mProgressDialog = new ProgressDialog(Download.this);
   		mProgressDialog.setMessage("Download PDM file");
   		mProgressDialog.setIndeterminate(false);
   		mProgressDialog.setMax(100);
   		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
   		DownloadFile downloadFile = new DownloadFile();
   		filename = "rbm.zip";
   		downloadFile.execute("http://ccs.web.id/pln/downloads/" + filename); 	   		   		    		
    }
    
	private class DownloadFile extends AsyncTask<String, Integer, String>{
	    @Override
	    protected String doInBackground(String... url) {
	        int count;
	        try {
	            URL url2 = new URL(url[0]);
	            URLConnection conexion = url2.openConnection();
	            conexion.connect();
	            // this will be useful so that you can show a tipical 0-100% progress bar
	        int lenghtOfFile = conexion.getContentLength();
	
	        // downlod the file
	        InputStream input = new BufferedInputStream(url2.openStream());
	        OutputStream output = new FileOutputStream("/sdcard/pln/" + filename);
	
	        byte data[] = new byte[1024];
	
	        long total = 0;
	
	        while ((count = input.read(data)) != -1) {
	            total += count;
	                publishProgress((int)(total*100/lenghtOfFile));
	                output.write(data, 0, count);
	            }
	
	            output.flush();
	            output.close();
	            input.close();
	        } catch (Exception e) {}
	        return null;
	    }
	
	     @Override
		 protected void onProgressUpdate(Integer... args){
	         mProgressDialog.setProgress(args[0]);
	         mProgressDialog.show();
	     }

	     @Override
		 protected void onPostExecute(String result) {
		     super.onPostExecute(result);
		     mProgressDialog.dismiss();
		     finish();
		 }		 
	}   	    
    
}

/*
http://www.helloandroid.com/tutorials/how-download-fileimage-url-your-device
http://stackoverflow.com/questions/3028306/download-a-file-with-android-and-showing-the-progress-in-a-progressdialog
http://stackoverflow.com/questions/1926388/android-activity-inside-dialog
http://appfulcrum.com/?p=281
http://achorniy.wordpress.com/2009/12/29/how-to-use-autofocus-in-android/
*/
