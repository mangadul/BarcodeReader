/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mangadul.project;

import android.content.ContentValues;
//import android.database.Cursor;
import android.database.sqlite.*;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
//import android.provider.SyncStateContract.Helpers;
//import android.text.format.Time;
import android.telephony.TelephonyManager;
import android.util.Log;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DBCatatMeter extends SQLiteOpenHelper {

    SQLiteDatabase db;
    
    public static final String DATABASE_NAME = "pdam.sqlite";
    public static final String DBPATH = "/sdcard/pdam/";

    public static final String TABLE_VIEW = "v_pelanggan";
    public static final String COLUMN_VNAMA = "nama";
    public static final String COLUMN_VALAMAT = "alamat";

    public static final String TABLE_STATUS = "status_catat";
    public static final String COLUMN_SIDSTATUS = "_id";
    public static final String COLUMN_SSTATUS = "status";

    public static final String COLUMN_BULAN = "strftime('%m', date('now')) as catatbulan";
    
    public static final String TABLE_NAME = "catatmeter";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PELANGGAN = "idpelanggan";
    public static final String COLUMN_PETUGAS = "idpetugas";
    public static final String COLUMN_TGLCATAT = "tglcatat";
    public static final String COLUMN_METER = "meter";
    public static final String COLUMN_POSISILONG = "posisi_long";
    public static final String COLUMN_POSISILAT = "posisi_lat";
    public static final String COLUMN_TGL_CATAT = "tgl_catat";
    public static final String COLUMN_WAKTU_CATAT = "waktu_catat";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_DEVID = "dev_id";
    public static final String COLUMN_PHID = "photo_id";
    public static final String COLUMN_SELECTED = "is_kirim";
    //public static final String PATHDB = "/sdcard/pdam/pdam.sqlite";
    public static final String PATHDB = "/sdcard/pln/cater.sqlite";

    private final Context myContext;

    public DBCatatMeter(Context context) {
            super(context, DATABASE_NAME, null, 1);
            this.myContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
            // check if exists and copy database from resource
            createDB();
    }
    
    public Cursor fetchAllData() {
        return db.query(TABLE_NAME, new String[] {COLUMN_ID,
                        COLUMN_PELANGGAN, COLUMN_PETUGAS, COLUMN_POSISILAT,
                        COLUMN_POSISILONG, COLUMN_TGLCATAT,
                        COLUMN_SELECTED, COLUMN_STATUS},
                        null, null, null, null, null);
    }

    public boolean DBExists() {

        SQLiteDatabase dbo = null;

        try
        {
            dbo = SQLiteDatabase.openDatabase(
                    PATHDB,
                    null,
                    SQLiteDatabase.OPEN_READWRITE);
            dbo.setLocale(Locale.getDefault());
            dbo.setLockingEnabled(true);
            dbo.setVersion(3);

        } catch(SQLiteException ex)
        {
            Log.e("Gagal membuka database: "+DATABASE_NAME + ", errorMessage: "
                    ,ex.getMessage());
        }

        if (dbo != null) {

                dbo.close();

        }
        return dbo != null ? true : false;
    }

    public void closedb() {
        db.close();
    }

    public void createDatabase()
    {
        createDB();
    }

    private void createDB()
    {
        boolean dbExist = DBExists();

        if (!dbExist) {
                copyDBFromResource();
        }
    }

    /*
     * @description: tidak jalan
     */
    public DBCatatMeter open() throws SQLException {
        DBCatatMeter dbHelper = new DBCatatMeter(myContext);
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void openDataBase() throws SQLException {
        try
        {
            db = SQLiteDatabase.openDatabase(PATHDB, null,
                            SQLiteDatabase.OPEN_READWRITE);
        } catch (SQLiteException exs)
        {
            Log.e("openDatabase Error: ", exs.getMessage());
        }
    }

    @Override
    public synchronized void close() {
            if (db != null)
                    db.close();
            super.close();
    }

    /*
    private void createDB2() {
            boolean dbExist = DBExists();
            if (!dbExist) {
                String tbl_catatmeter = "CREATE TABLE [catatmeter] ("+
                    "[id_catat] INTEGER  PRIMARY KEY AUTOINCREMENT NOT NULL,"+
                    "[idpetugas] INTEGER  NULL,"+
                    "[idpelanggan] INTEGER  NULL,"+
                    "[tglcatat] VARCHAR(50)  NULL,"+
                    "[meter] INTEGER  NULL,"+
                    "[posisi_long] VARCHAR(20)  NULL,"+
                    "[posisi_lat] VARCHAR(20)  NULL,"+
                    "[tgl_catat] DATE DEFAULT CURRENT_DATE NULL,"+
                    "[waktu_catat] TIME DEFAULT CURRENT_TIME NULL,"+
                    "[is_kirim] INTEGER DEFAULT '0' NULL);";

                String tbl_pelanggan = "CREATE TABLE [pelanggan] ("+
                    "[id_pelanggan] INTEGER  PRIMARY KEY AUTOINCREMENT NOT NULL,"+
                    "[id_pdam] INTEGER  NULL,"+
                    "[nama] VARCHAR(200)  NULL,"+
                    "[alamat] TEXT  NULL);";

                String tbl_petugas = "CREATE TABLE [petugaspdam] ("+
                    "[id_petugas] INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,"+
                    "[nama_petugas] VARCHAR(200)  NULL,"+
                    "[user] vaRCHAR(50)  NULL,"+
                    "[passwd] varCHAR(200)  NULL,"+
                    "[last_login] VARCHAR(50)  NULL,"+
                    "[date_login] TIMESTAMP DEFAULT CURRENT_TIMESTAMP NULL,"+
                    "[idpetugaspdam] varCHAR(20)  NULL);";
                try
                {
                    db.execSQL(tbl_catatmeter);
                    db.execSQL(tbl_pelanggan);
                    db.execSQL(tbl_petugas);
                }catch(SQLException es)
                {
                    Log.e("Gagal membuat tabel: ", es.getMessage());
                } finally {
                    db.close();
                }                
            }
    }
    */
    
    private void copyDBFromResource() {

            InputStream inputStream = null;
            OutputStream outStream = null;
            try {

                    inputStream = myContext.getAssets().open(DATABASE_NAME);

                    outStream = new FileOutputStream(DBPATH);

                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inputStream.read(buffer)) > 0) {
                            outStream.write(buffer, 0, length);
                    }
                    outStream.flush();
                    outStream.close();
                    inputStream.close();

            } catch (IOException e) {
                    Log.e("Error copyDBFromResource: ", e.getMessage());
                    throw new Error("Problem copying database from resource file.");
            }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("SqlHelper", "Upgrading database from version " + oldVersion
                        + " to " + newVersion + ", which will destroy all old data");
        createDB();
    }

    public void insertData(Long idpelanggan, Integer idpetugas,
          Integer mtr, String poslong, String poslat, Integer status, String dev_id, String photo_id)
    {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            ContentValues values = new ContentValues();
            values.put(COLUMN_PETUGAS, idpetugas);
            values.put(COLUMN_PELANGGAN, idpelanggan);
            values.put(COLUMN_TGLCATAT,  dateFormat.format(new Date()).toString());
            values.put(COLUMN_METER, mtr);
            values.put(COLUMN_POSISILONG, poslong);
            values.put(COLUMN_POSISILAT, poslat);
            values.put(COLUMN_SELECTED, 0);
            values.put(COLUMN_STATUS, status);
            values.put(COLUMN_DEVID, dev_id);
            values.put(COLUMN_PHID, photo_id);
            
            try {
                this.db.insert(TABLE_NAME, null, values);
            } catch(SQLiteException ei)
            {
                Log.e("Error Insert: ", ei.getMessage());
            }
    }

    public Cursor getData(long id) throws SQLException
    {
                Cursor mCursor = db.query(true, TABLE_NAME, new String[] {
				COLUMN_ID, COLUMN_METER, COLUMN_PELANGGAN, 
                                COLUMN_POSISILAT, COLUMN_POSISILONG },
				COLUMN_ID + "=" + id, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
                
		return mCursor;
    }

    public Cursor getAllData()
    {
        return db.query(TABLE_NAME, new String[] {
                        COLUMN_ID,
                        COLUMN_METER, COLUMN_PELANGGAN, 
                        COLUMN_POSISILAT, COLUMN_POSISILONG }, 
                        null, null, null, null, "tglcatat DESC");
    }

    public void clearSelections() {
            ContentValues values = new ContentValues();
            values.put("is_kirim", 0);
            this.db.update(DBCatatMeter.TABLE_NAME, values, null, null);
    }

    public Cursor getCursor() {

            SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

            queryBuilder.setTables(TABLE_NAME);

            String[] asColumnsToReturn = new String[] { COLUMN_ID, COLUMN_PELANGGAN,
                            COLUMN_PETUGAS, COLUMN_TGLCATAT, COLUMN_METER,
                            COLUMN_POSISILONG, COLUMN_POSISILAT, COLUMN_SELECTED };

            Cursor mCursor = queryBuilder.query(db, asColumnsToReturn, null,
                            null, null, null, "tglcatat ASC");

            return mCursor;
    }

    public Cursor getDataView()
    {
        return db.query(TABLE_VIEW, new String[] {
                        COLUMN_ID,
                        COLUMN_PELANGGAN,
                        COLUMN_METER, 
                        COLUMN_VNAMA,
                        COLUMN_VALAMAT,
                        COLUMN_TGLCATAT,
                        COLUMN_POSISILAT,
                        COLUMN_POSISILONG },
                        null, null, null, null,
                        COLUMN_TGLCATAT + " DESC");
    }

    /*
     * @description validasi input untuk mencegah duplikasi data pelanggan
     */    
    public Cursor validateInsert(Integer id) throws SQLException
    {
        return db.rawQuery("select strftime('%m', date('now')) as "
                + "catatbulan from catatmeter where idpelanggan=?", 
                new String[] { id.toString() }); 
    }

    public Cursor getStatus()
    {                      
        return db.rawQuery("SELECT _id, status from status_catat order by _id asc",
        		new String[] {});                
    }

}