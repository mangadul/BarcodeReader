package mangadul.project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
//import android.widget.TextView;
import android.widget.Toast;

public class Main extends Activity {
    //private TextView tv;
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        //setContentView(R.layout.bglogin);
        
        startActivityForResult(new Intent(Main.this, Login.class), 0);
        
       // tv = new TextView(this);
        //setContentView(tv);
    }
    
    protected void startup(Intent i) {
        boolean success = i.getBooleanExtra("islogin",false);
        if(success)
        {
            Toast.makeText(getApplicationContext(), "Login Sukses", Toast.LENGTH_LONG).show();                         
        } //else Toast.makeText(getApplicationContext(), "Login Gagal", Toast.LENGTH_SHORT).show();               
        startActivity(new Intent(Main.this, BarcodeReaderActivity.class));            
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	//Toast.makeText(getApplicationContext(), Integer.toString(resultCode).toString(), Toast.LENGTH_LONG).show();
        if(requestCode == 1 || resultCode == RESULT_CANCELED) 
        	finish(); 
        else 
            startup(data);
    }
    
    @Override
    public void onStop()
    {
    	super.onStop();
    	this.finish();
    }

    
}
