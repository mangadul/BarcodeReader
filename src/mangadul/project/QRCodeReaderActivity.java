package mangadul.project;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
//import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

public class QRCodeReaderActivity extends Activity {

	TextView textview;
	String text;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		ImageView image = (ImageView)findViewById(R.id.image);
	
		try{
			String path = Environment.getExternalStorageDirectory().getAbsolutePath();
			Log.i("images path: ", path);
			
			Bitmap bMap = BitmapFactory.decodeFile(path+"/DCIM/Camera/qrc.jpg");
			image.setImageBitmap(bMap);
			
			LuminanceSource source = new RGBLuminanceSource(bMap);
			BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
			 
			//Reader reader = new MultiFormatReader();
			Reader reader = new QRCodeReader();
			com.google.zxing.Result result;
			
			result = reader.decode(bitmap);
			text = result.getText();
			textview.setText("Decode Result: "+text);
		
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (ChecksumException e) {
			e.printStackTrace();
		} catch (FormatException e) {
			e.printStackTrace();
		}
	}

}