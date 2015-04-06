
package mangadul.project;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
//import android.widget.ImageView;
import android.widget.TextView;
//import de.demo.main.R;

public class Login extends Activity {
    private static final String UPDATE_URL = "http://demo.alphamedia.web.id/fieldreport/login.php";
    public ProgressDialog progressDialog; 
    private EditText UserEditText;
    private EditText PassEditText;
    Boolean islogin = false;

    public static final String PREFS_NAME = "PLNCaterPrefs";
    public SharedPreferences prefs;

        
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.login);
        //ImageView bgimg = (ImageView) findViewById(R.id.bg_image);   
        
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = telephonyManager.getDeviceId();
        TextView imei = (TextView) findViewById(R.id.imei);
        imei.setText(deviceId.toString());
        final String txtimei = deviceId.toString();
        
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Login ke server...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        
        UserEditText = (EditText) findViewById(R.id.username);
        PassEditText = (EditText) findViewById(R.id.password);
                
        Button button = (Button) findViewById(R.id.okbutton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int usersize = UserEditText.getText().length();
                int passsize = PassEditText.getText().length();
                if(usersize > 0 && passsize > 0) {
                    progressDialog.show();
                    String user = UserEditText.getText().toString();
                    String pass = PassEditText.getText().toString();
                    doLogin(user, pass, txtimei);
                } else createDialog("Error","Masukan user / password anda!");
            }
        });
        
        /*
        button = (Button) findViewById(R.id.cancelbutton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            }
        });
        */
    }

    private void quit(boolean success, Intent i) {
        setResult( (success) ? -1:0, i);
        //this.finishActivity(0);
        this.finish();
    }
    
    private void createDialog(String title, String text) {
        AlertDialog ad = new AlertDialog.Builder(this)
        .setPositiveButton("Ok", null)
        .setTitle(title)
        .setMessage(text)
        .create();
        ad.show();
    }
    
    private void doLogin(final String login, final String pass, final String simei) {
        final String pw = md5(pass);
        Thread t = new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                DefaultHttpClient client = new DefaultHttpClient(); 
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 15000);                 
                HttpResponse response;
                HttpEntity entity;         
                try {
                    HttpPost post = new HttpPost(UPDATE_URL);
                    List <NameValuePair> nvps = new ArrayList <NameValuePair>();
                    nvps.add(new BasicNameValuePair("username", login));
                    nvps.add(new BasicNameValuePair("password", pw));
                    nvps.add(new BasicNameValuePair("imei", simei));
                    post.setHeader("Content-Type", "application/x-www-form-urlencoded");
                    post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
                    response = client.execute(post);
                    entity = response.getEntity();
                    InputStream is = entity.getContent();
                    read(is);
                    is.close();
                    if (entity != null) entity.consumeContent();
                } catch (Exception e) {
                    progressDialog.dismiss();
                    createDialog("Error", "Couldn't establish a connection");
                    Log.e("Error Koneksi: ", e.getMessage());
                }
                Looper.loop();                
            }
        };
        t.start();
    }
    
    private void read(InputStream in) {
        SAXParserFactory spf = SAXParserFactory.newInstance(); 
        SAXParser sp;
        try {
            sp = spf.newSAXParser();
            XMLReader xr = sp.getXMLReader(); 
            LoginContentHandler uch = new LoginContentHandler(); 
            xr.setContentHandler(uch); 
            xr.parse(new InputSource(in)); 
        } catch (ParserConfigurationException e) {
        } catch (SAXException e) {
        } catch (IOException e) {}
    }    
    
    private String md5(String in) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.reset();        
            digest.update(in.getBytes());
            byte[] a = digest.digest();
            int len = a.length;
            StringBuilder sb = new StringBuilder(len << 1);
            for (int i = 0; i < len; i++) {
                sb.append(Character.forDigit((a[i] & 0xf0) >> 4, 16));
                sb.append(Character.forDigit(a[i] & 0x0f, 16));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {}
        return null;
    }
    
    private class LoginContentHandler extends DefaultHandler {
        private boolean in_loginTag = false;
        private int userID;
        private boolean error_occured = false;

        @Override
        public void startElement(String n, String l, String q, Attributes a) 
            throws SAXException 
        {
            //if(l == "login") in_loginTag = true;   
            if("login".equals(l)) {
                in_loginTag = true;
                islogin = true;
                Intent i = new Intent();
                i.putExtra("islogin", true);
            }
            //if(l == "error") {
            if("error".equals(l)) {
                progressDialog.dismiss();
                if(Integer.parseInt(a.getValue("value")) == 1) 
                    createDialog("Error", "Couldn't connect to Database");
                if(Integer.parseInt(a.getValue("value")) == 2) 
                    createDialog("Error", "Error in Database: Table missing");
                if(Integer.parseInt(a.getValue("value")) == 3) 
                    createDialog("Error", "Invalid username and/or password");
                error_occured = true;
            }
            //if(l == "user" && in_loginTag && a.getValue("id") != "")
            // "user".equals(l) && 
            if("user".equals(l) && in_loginTag && !"".equals(a.getValue("id")))   
            {
                userID = Integer.parseInt(a.getValue("id"));
                Editor editor = prefs.edit();
                editor.putInt("user_id", userID);
                editor.commit();
            }
        } 
          
        @Override
        public void endElement(String n, String l, String q) throws SAXException {
            //if(l == "login") {
            if("login".equals(l)) {
                in_loginTag = false;
                if(!error_occured) {
                    progressDialog.dismiss();
                    Intent i = new Intent();
                    i.putExtra("userid", userID);
                    quit(true,i);
                }
            }
        }
         
        @Override
        public void characters(char ch[], int start, int length) { }
        
        @Override
        public void startDocument() throws SAXException { } 
        
        @Override
        public void endDocument() throws SAXException { } 
        
    }    
    
    @Override
    public void onBackPressed() {
        AlertDialog ad = new AlertDialog.Builder(Login.this).setMessage(
                R.string.exit_message).setTitle(
                R.string.exit_title).setCancelable(false)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int whichButton) {
	                        		setResult(RESULT_CANCELED);
	                                Intent i = new Intent();
	                                quit(false,i);
                            	
                            		//Login.this.finish();
                            }
                        }).setNeutralButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int whichButton) {
                                // User selects Cancel, discard all changes
                            }
                        }).show();

    }   

    
}

/*
 * http://www.anddev.org/simple_login_for_android_g1-t5688.html
 */