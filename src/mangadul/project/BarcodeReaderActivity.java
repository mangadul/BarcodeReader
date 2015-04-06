package mangadul.project;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewDebug.IntToString;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class BarcodeReaderActivity extends Activity implements AdapterView.OnItemSelectedListener {

	private static final String POST_URL = "http://ccs.web.id/pln/catatmeter.php";
    
	EditText idpel;
	Boolean islogin = false;
	private static final int MY_DIALOG = 0;
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 2425;
    public static final String PREFS_NAME = "PLNCaterPrefs";

    EditText txtIDPelanggan, txtcatatMeter, txtloclong, txtloclat, txtimgname;
    Button btnCatatMeter;
    Button btnIDPelanggan, button;
    Integer pos=null, nstat, idpetugas;

    //Location loc;
    private LocationManager lm;
    private LocationListener locationListener;
    Double loclong=null, loclat=null;
    private DBCatatMeter dbmeter = null;    

    public Button btnfoto;
    
    Context context = this;
    
	private ProgressDialog dialog;
    private String idpl="", mtr="";
    private Uri imageUri;

    public SharedPreferences prefs;

	protected static final String PHOTO_TAKEN	= "ambil_foto";
	protected ImageView _image;
	protected String _path;
	protected boolean _taken;
	public String foto_path;
    
    	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	Button btnmeter;
    	
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        findViewById(R.id.button1).setOnClickListener(scanQRCode);
        //findViewById(R.id.afoto).setOnClickListener(new fotoClick());
        txtIDPelanggan = (EditText) findViewById(R.id.idpelanggan);
        txtcatatMeter = (EditText) findViewById(R.id.txtmeter);
        txtloclong = (EditText) findViewById(R.id.loclong);
        txtloclat = (EditText) findViewById(R.id.loclat);
        
        btnmeter = (Button) findViewById(R.id.btnmeter);
        
        btnfoto = (Button) findViewById(R.id.afoto);
        btnfoto.setOnClickListener(new fotoClick());

        //button = (Button) findViewById(R.id.button1);

        _image = ( ImageView ) findViewById( R.id.image);        
        txtimgname = (EditText) findViewById(R.id.imgname);
    	//_image.setVisibility(View.GONE);


        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        //Button btnpht = (Button)findViewById(R.id.btnphoto);
        //btnpht.setOnClickListener(ambilgambar);
        
        if (dbmeter == null) {
            dbmeter = new DBCatatMeter(this);
        }
    
        try
        {
    	    dbmeter.openDataBase();        	
        } catch (Exception e) {
        	Log.e("Error Opening Database: ", e.getMessage());
        	Toast.makeText(getApplicationContext(), "Error membuka database, silahkan periksa kembali!", Toast.LENGTH_LONG).show();
		}
	    
	    Cursor cur = dbmeter.getStatus();
	    startManagingCursor(cur);
	    String[] from = new String[]{DBCatatMeter.COLUMN_SSTATUS};
	    int[] to = new int[]{android.R.id.text1};
	    SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
	            android.R.layout.simple_spinner_item, cur, from, to);
	    adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
	    Spinner spinner = (Spinner) findViewById(R.id.spinner);
	    spinner.setAdapter(adapter);
	    //dbmeter.closedb();
	
	    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
	        
	        public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
	            setPos(position);
	        }
	
	        public void onNothingSelected(AdapterView<?> arg0) {
	            //do nothing
	        }
	    });
    
    	lm = (LocationManager)
	        getSystemService(Context.LOCATION_SERVICE);
	
	    //loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	
	    locationListener = new MyLocationListener();
	
	    lm.requestLocationUpdates(
	        LocationManager.GPS_PROVIDER,
	        0,
	        0,
	        locationListener);
				    	    

    	btnmeter.setOnClickListener(new OnClickListener() {

	        public void onClick(View v) {

	        	/*
	        	if(idpl == null)
	        	{
		        	idpl = txtIDPelanggan.getText().toString();	        		
	        	}
	        	*/
	        	
	            idpl = txtIDPelanggan.getText().toString(); 
	            mtr = txtcatatMeter.getText().toString();     
	            nstat = BarcodeReaderActivity.this.getPos();

	        	dialog = ProgressDialog.show(BarcodeReaderActivity.this, "", 
                        "Menyimpan data...", true);
	            dialog.setCancelable(false);
	            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);	            	            

	            TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
	            String deviceId = telephonyManager.getDeviceId();

	            try{	            	
		            int uid = prefs.getInt("user_id", 0);	            
		            String fid = txtimgname.getText().toString();
		            simpanData(uid, idpl, mtr, loclong, loclat, nstat, deviceId, fid);
	            } catch (Exception e) {
	            	Log.e("Simpan data Error: ", e.getMessage());
				}
	            
	            idpl = null;
	            txtIDPelanggan.setText("");
	            txtcatatMeter.setText("");
	            txtimgname.setText("");
	        }	
	    });       
    }
    // end oncreate
    
    public class fotoClick implements View.OnClickListener 
    {
    	public void onClick( View view ){
    		Integer isidpel = txtIDPelanggan.getText().toString().replace(" ", "").length();
    		if(isidpel > 0)
    		{
        		String fidpel = txtIDPelanggan.getText().toString();
        		Log.i("Ambil Gambar Manual", "fotoClick.onClick()" );
        		startCameraActivity(setfname(fidpel));    			
    		} else 
    			Toast.makeText(getApplicationContext(), 
    					"Silahkan masukan ID Pelanggan terlebih dahulu", 
    					Toast.LENGTH_LONG).show();
    	}
    }
    

    private void simpanData(final Integer sidp, final String sidpl, final String smtr, final Double slong, final Double slat, final Integer stats, final String devid, final String fotos)
    {        
		Thread t = new Thread() {
		    public void run() {
	        Looper.prepare();
	    	try {
	    		
		    if(sidpl.length() > 0 &&
		       sidpl.length() == 12 &&		    		
		       smtr.length() > 0 &&
		       slong.toString().length() > 0 &&
		       slat.toString().length() > 0 &&
		       fotos.length() > 0
		       )
		    {
		        if(dbmeter != null)
		        {
		            dbmeter.insertData(
		                    Long.parseLong(sidpl), //idpelanggan
		                    sidp, //idpetugas
		                    Integer.parseInt(smtr), //meter
		                    slong.toString(), //long
		                    slat.toString(), //lat
		                    stats,
		                    devid,
		                    fotos
		                    );
		        } else
		        {
		            dbmeter.openDataBase();
		            dbmeter.insertData(
		                    Long.parseLong(sidpl), //idpelanggan
		                    sidp, //idpetugas
		                    Integer.parseInt(smtr), //meter
		                    slong.toString(), //long
		                    slat.toString(), //lat
		                    stats,
		                    devid,
		                    fotos
		                    );	                	
		        }
		
		        /*
		        if(dbmeter != null)
		        {
		            dbmeter.closedb();
		        } 
		        */
		        
				        	                			                
		        try {
		            boolean pd = BarcodeReaderActivity.this.postData(
		                    sidpl, 
		                    smtr, 
		                    slong.toString(), 
		                    slat.toString(), 
		                    stats.toString(),
		                    devid,
		                    sidp.toString(),
		                    fotos
		                    );
		            if(pd) 
		            {
				        Toast.makeText(getApplicationContext(),
		                        "Data berhasil dikirim ke server!",
		                        Toast.LENGTH_LONG).show();
		                
		            	//dialog.setMessage("Data berhasil dikirim ke server");		   		                
		            } else
		            {
		                Toast.makeText(getApplicationContext(),
		                        "Data GAGAL dikirim ke server!",
		                        Toast.LENGTH_LONG).show();
		            }
					dialog.dismiss();		            
		            
		        } catch (IOException ex) {
		            Logger.getLogger(BarcodeReaderActivity.class.getName()).log(Level.SEVERE, null, ex);
		        }
		        
				dialog.dismiss();		            		        
		
		    } else {
		        Toast.makeText(getApplicationContext(),
		                "Silahkan lengkapi data terlebih dahulu, atau anda salah melakukan pengisian!",		                
		                Toast.LENGTH_LONG).show();		        
				dialog.dismiss();
		    }
			Thread.sleep(30);    
		} catch (Exception e) {
			Log.e("Error Simpan Data {blok SimpaData}: ", e.getMessage());
			dialog.dismiss();
		}
			Looper.loop();
		} // end run
		}; // end thread
		t.start();
		
    }
        
    /*
    private class SaveData extends AsyncTask<String, Void, String> {

    	@Override
    	protected String doInBackground(String... params) {
    		// perform long running operation operation
    		return null;
    	}

    	@Override
    	protected void onPostExecute(String result) {
    		// execution of result of Long time consuming operation
    	}

    	@Override
    	protected void onPreExecute() {
    	// Things to be done before execution of long running operation. For example showing ProgessDialog
    	}

    	@Override
    	protected void onProgressUpdate(Void... values) {
          // Things to be done while execution of long running operation is in progress. For example updating ProgessDialog
    	 }
    }
    */

    protected void setFotoid(String fid)
    {
        BarcodeReaderActivity.this.foto_path = fid;
    }
    
    public String getFotoid()
    {
        return BarcodeReaderActivity.this.foto_path;
    }
    
    private Integer setPos(Integer position)
    {
        return this.pos = position;
    }
    
    public Integer getPos()
    {
        return this.pos;
    }

    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        setPos(position);
    }

    public void onNothingSelected(AdapterView<?> arg0) {
    }
 
    private class MyLocationListener implements LocationListener
    {
        public void onLocationChanged(Location loc) {
            if (loc != null) {
                loclong = loc.getLongitude();
                loclat = loc.getLatitude();
                txtloclat.setText(String.format("%s", loclong.toString()));
                txtloclong.setText(String.format("%s", loclat.toString()));
            }
        }

        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
        }

        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub
        }
        
    }
    
    private boolean postData(String idp, String meter, String slong, String slat, String stat, String devid, String idptgs, String foto) throws IOException {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(POST_URL);
        Boolean ret = false;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(6);
            nameValuePairs.add(new BasicNameValuePair("idp", idp));
            nameValuePairs.add(new BasicNameValuePair("tglcatat", dateFormat.format(new Date()).toString()));
            nameValuePairs.add(new BasicNameValuePair("meter", meter));
            nameValuePairs.add(new BasicNameValuePair("long", slong));
            nameValuePairs.add(new BasicNameValuePair("lat", slat));
            nameValuePairs.add(new BasicNameValuePair("stat", stat));
            nameValuePairs.add(new BasicNameValuePair("dev_id", devid));
            nameValuePairs.add(new BasicNameValuePair("id_petugas", idptgs));
            nameValuePairs.add(new BasicNameValuePair("photo_id", foto));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            
            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                    InputStream instream = entity.getContent();
                    String result = convertStreamToString(instream);
                    Log.i("Response from server", "Result of conversion: [" + result + "]");
                    instream.close();
                    ret = true;
            }            

        } catch (ClientProtocolException e) {
            Log.e("ClientProtocolException", e.getMessage());
            ret = false;
            // TODO Auto-generated catch block
        } catch (IOException e) {
            Log.e("IOException PostData", e.getMessage());
            ret = false;
            // TODO Auto-generated catch block
        }
        return ret;
        
    } 

    private String convertStreamToString(InputStream is) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();

            String line = null;
            try {
                while ((line = reader.readLine()) != null) {
                        sb.append(line);
                }
            } catch (IOException e) {
            } finally {
                    try {
                            is.close();
                    } catch (IOException e) {
                    }
            }
            return sb.toString();
    }
    
    /*
    private OnClickListener ambilgambar = new OnClickListener() {
        public void onClick(View v) {
        	String fileName = "new-photo-name.jpg";    	
        	ContentValues values = new ContentValues();
        	values.put(MediaStore.Images.Media.TITLE, fileName);
        	values.put(MediaStore.Images.Media.DESCRIPTION,"Image capture by camera");
        	//imageUri is the current activity attribute, define and save it for later usage (also in onSaveInstanceState)
        	//imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        	imageUri = getContentResolver().insert(
        			MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        	//create new Intent
        	Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        	intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        	//intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        	startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);    	
        }
    };
    
    public static File convertImageUriToFile (Uri imageUri, Activity activity)  {
    	Cursor cursor = null;
    	try {
    	    String [] proj={MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID, MediaStore.Images.ImageColumns.ORIENTATION};
    	    cursor = activity.managedQuery( imageUri,
    	            proj, // Which columns to return
    	            null,       // WHERE clause; which rows to return (all rows)
    	            null,       // WHERE clause selection arguments (none)
    	            null); // Order-by clause (ascending by name)
    	    int file_ColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
    	    int orientation_ColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.ORIENTATION);
    	    if (cursor.moveToFirst()) {
    	        String orientation =  cursor.getString(orientation_ColumnIndex);
    	        return new File(cursor.getString(file_ColumnIndex));
    	    }
    	    return null;
    	} finally {
    	    if (cursor != null) {
    	        cursor.close();
    	    }
    	}
    }
    */    

    public Button.OnClickListener scanQRCode = new Button.OnClickListener() {
        public void onClick(View v) {
            IntentIntegrator.initiateScan(BarcodeReaderActivity.this); 
        }
    };
    
    protected void startCameraActivity(String fn)
    {
        String _spath = Environment.getExternalStorageDirectory() + "/pln/foto/" + fn + ".jpg";    	
    	Log.i("AmbilGambar", "startCameraActivity()" );
    	File file = new File(_spath);
    	Uri outputFileUri = Uri.fromFile( file );
    	Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE );
    	intent.putExtra( MediaStore.EXTRA_OUTPUT, outputFileUri ); 
    	setFotoid(txtimgname.getText().toString());
    	txtimgname.setText(fn + ".jpg");
    	startActivityForResult( intent, 0 );
    }
    
    protected void setImages(String img_name)
    {
        String im_path = Environment.getExternalStorageDirectory() + "/pln/foto/" + img_name + ".jpg";    	
    	Bitmap bitmap = BitmapFactory.decodeFile(im_path);
    	/*
		Matrix mat = new Matrix();
		mat.postRotate(90);
		Bitmap bMapRotate = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mat, true);
    	_image.setImageBitmap(bMapRotate);   
		*/    	
    	_image.setImageBitmap(bitmap);   
    	
    }
    
    /*
    @Override 
    protected void onRestoreInstanceState( Bundle savedInstanceState){
    	Log.i( "BarcodeReader onRestoreInstanceState", "onRestoreInstanceState()");
    	if( savedInstanceState.getBoolean( BarcodeReaderActivity.PHOTO_TAKEN ) ) {
    		onPhotoTaken();
    	}
    }
    
    @Override
    protected void onSaveInstanceState( Bundle outState ) {
    	outState.putBoolean( BarcodeReaderActivity.PHOTO_TAKEN, _taken );
    }    
    */
    
    protected String setfname(final String ctn)
    {
	    Calendar c = Calendar.getInstance();
		int month = c.get(Calendar.MONTH);
		int year = c.get(Calendar.YEAR);
		int date = c.get(Calendar.DATE);
		int tm = c.get(Calendar.HOUR);
		int mm = c.get(Calendar.MINUTE);
		int ss = c.get(Calendar.SECOND);
		String pewaktu = Integer.toString(month)+ Integer.toString(year); 
		String jam = Integer.toString(date) + "-" + Integer.toString(tm) + Integer.toString(mm) + Integer.toString(ss);
		String fimg = ctn + "-" + pewaktu.toString() + "-" + jam.toString();
		return fimg;    	
    }
    
	public void onActivityResult(int requestCode, int resultCode, Intent data) {		
		Log.i("onActivityResult", "requestcode=" + requestCode + ":resultCode=" + resultCode);
		
		/*
	    Calendar c = Calendar.getInstance();
		int month = c.get(Calendar.MONTH);
		int year = c.get(Calendar.YEAR);
		int date = c.get(Calendar.DATE);
		int tm = c.get(Calendar.HOUR);
		int mm = c.get(Calendar.MINUTE);
		int ss = c.get(Calendar.SECOND);
		*/
		
		switch(requestCode) {
		
			case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE: {
			    if (resultCode == -1) {			    	
			        //Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
			        //ImageView image = (ImageView) findViewById(R.id.btnphoto);  
			        //image.setImageBitmap(thumbnail);  			        
			    } else if (resultCode == RESULT_CANCELED) {
			        Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT);
			    } else {
			        Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT);
			    }				
			}
			
			case IntentIntegrator.REQUEST_CODE: { 
				if (resultCode != RESULT_CANCELED) {
					IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
					if (scanResult != null) {
						String content = scanResult.getContents();
						txtIDPelanggan.setText(content);
						//btnfoto.setClickable(false);
						String fname = setfname(content);
						/*
						String pewaktu = Integer.toString(month)+ Integer.toString(year); 
						String jam = Integer.toString(date) + "-" + Integer.toString(tm) + Integer.toString(mm) + Integer.toString(ss);
						String fimg = content + "-" + pewaktu.toString() + "-" + jam.toString();
						*/
						startCameraActivity(fname);
						//setImages(fname);
		    			//onPhotoTaken();						
        			}
				}
				else {
					// else handling 
				}					
				break;
			}
	   }
	}
	
   public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add("Hasil Catat");
        menu.add("RBM");
        menu.add("Keluar");
        menu.add("Download Data");
        menu.add("Info Tagihan");
        menu.add("Setting");
        menu.add("Tentang Program");
        return true;
    }

    // handle menu selected
    public boolean onOptionsItemSelected(MenuItem item){

   	 if (item.getTitle().equals("Login")){
   		 	/*
            Intent intent = new Intent(this, Login.class);
            startActivityForResult(intent, MY_DIALOG);
            return true;
            */
   	 }

   	 if (item.getTitle().equals("Info Tagihan")){
         return true;
   	 }

   	 if (item.getTitle().equals("RBM")){
   		 Toast.makeText(getApplicationContext(), "RBM - Under Construction", Toast.LENGTH_SHORT).show();
         return true;
   	 }

   	 if (item.getTitle().equals("Download Data")){
   		 /* dipilih dulu koked, maks 3 koked */
         Intent intent = new Intent(this, Download.class);
         startActivityForResult(intent, 0);
         return true;
   	 }

   	 
   	 if (item.getTitle().equals("Hasil Catat")){
         Intent intent = new Intent(this, MyListView.class);
         startActivityForResult(intent, MY_DIALOG);
         return true;
	 }

   	 if (item.getTitle().equals("Keluar")){
         Editor editor = prefs.edit();
         editor.clear();
   		 finish();
         return true;
	 }
   	 
   	 if (item.getTitle().equals("Ambil Gambar")){
         Intent intent = new Intent(this, CameraDemo.class);
         startActivityForResult(intent, MY_DIALOG);
         return true;
	 }
   	 
   	 
   	 if (item.getTitle().equals("Setting")){
         Intent intent = new Intent(this, UserSetting.class);
         startActivityForResult(intent, 0);
         return true;
	 }
   	 
   	 if (item.getTitle().equals("Tentang Program")){
         AlertDialog.Builder builder = new AlertDialog.Builder(this);
         builder.setTitle(getString(R.string.title_about));
         builder.setMessage(getString(R.string.msg_about) + "\n\n" + getString(R.string.about_url));
         //builder.setIcon(R.drawable.launcher_icon);
         builder.setPositiveButton(R.string.button_open_browser, aboutListener);
         builder.setNegativeButton(R.string.button_cancel, null);
         builder.show();   		 
         return true;
	 }
   	 
        return false;
    }	
    
    private final DialogInterface.OnClickListener aboutListener =
        new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialogInterface, int i) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.about_url)));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        startActivity(intent);
      }
    };     
    
 
    /*
    @Override
    public void onStop()
    {
    	super.onStop();
    	this.finish();
    }
    */
 
    @Override
    public void onBackPressed() {
    }   
    
}

/* cek tagihan listrik */ 

/*
 * http://developer.android.com/guide/topics/ui/menus.html
 * http://developer.android.com/guide/topics/resources/menu-resource.html
 */
