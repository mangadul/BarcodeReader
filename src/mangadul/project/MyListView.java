package mangadul.project;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MyListView extends ListActivity  {
    
    private DBCatatMeter dbHelper;
    Cursor cursor = null;
    private Cursor currentcursor = null;
    
    private ListView listView = null;
    private ListView mainListView = null;
    
    SimpleCursorAdapter list;    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_data);
        ImageView image = (ImageView) findViewById(R.id.bg_image);
        
        listView = getListView();
        listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setCacheColorHint(0);
                
        dbHelper = new DBCatatMeter(this);
        dbHelper.openDataBase();
        cursor = dbHelper.getDataView();
        fillData(cursor);
        if(dbHelper != null)
        {
            dbHelper.closedb();
        }

    }

    private void fillData(Cursor cs) {
        startManagingCursor(cs);        
        String[] from = new String[] {
            DBCatatMeter.COLUMN_PELANGGAN,
            DBCatatMeter.COLUMN_METER,
            DBCatatMeter.COLUMN_VNAMA,
            DBCatatMeter.COLUMN_VALAMAT,
            DBCatatMeter.COLUMN_TGLCATAT};
        int[] to = new int[] {  R.id.idpelanggan,
                                R.id.meter,
                                R.id.nama,
                                R.id.alamat,
                                R.id.tglcatat};
        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter lists = new SimpleCursorAdapter(this,
                        R.layout.list_row, cs, from, to);
        setListAdapter(lists);                
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	super.onListItemClick(l, v, position, id);
    	Object o = this.getListAdapter().getItem(position);
    	String pen = o.toString();
    	Toast.makeText(this, "You have chosen: " + " " + pen, Toast.LENGTH_LONG).show();
    }
    
    /*
    public class SetListView extends AsyncTask<String, Void, String> {

        //private final ProgressDialog dialog = new ProgressDialog(MyListView.this);
        
        @Override
        protected void onPreExecute()
        {
            //this.dialog.setMessage("Loading...");
            //this.dialog.show();
        }
        
        @Override
        protected String doInBackground(final String... args) {
            MyListView.this.dbHelper.openDataBase();
            MyListView.this.currentcursor = MyListView.this.dbHelper.getDataView();
            //MyListView.this.currentcursor = MyListView.this.dbHelper.getDataView();                                
            return null;
        }
        
        @Override
        protected void onPostExecute(final String result)
        {
            startManagingCursor(MyListView.this.currentcursor);        
            String[] from = new String[] {
                DBCatatMeter.COLUMN_PELANGGAN,
                DBCatatMeter.COLUMN_METER,
                DBCatatMeter.COLUMN_VNAMA,
                DBCatatMeter.COLUMN_VALAMAT,
                DBCatatMeter.COLUMN_TGLCATAT};
            int[] to = new int[] {  R.id.idpelanggan,
                                    R.id.meter,
                                    R.id.nama,
                                    R.id.alamat,
                                    R.id.tglcatat};

            MyListView.this.list = new SimpleCursorAdapter(
                            MyListView.this,
                            R.layout.list_row, 
                            MyListView.this.currentcursor, 
                            from, 
                            to);
            
            setListAdapter(MyListView.this.list);      

            if(MyListView.this.dbHelper != null)
            {
                MyListView.this.dbHelper.closedb();
            }        
            
            if(this.dialog.isShowing())
            {
                this.dialog.dismiss();
            } 
        }

    }
    */
    
    
}

/*
 * http://appfulcrum.com/?p=351
 */