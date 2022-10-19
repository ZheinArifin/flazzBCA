package com.ndp.flazzbca.menu;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ndp.flazzbca.BuildConfig;
import com.ndp.flazzbca.R;
import com.ndp.flazzbca.base.DemoApp;
import com.ndp.flazzbca.dbms.MyDatabaseHelper;
import com.ndp.flazzbca.menu.RecyclerVIew.RecyclerViewAdapter;
import com.ndp.flazzbca.menu.assets.MyAssets;
import com.pax.dal.IPrinter;
import com.pax.dal.exceptions.PrinterDevException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;

public class Settelment extends AppCompatActivity implements View.OnClickListener {
    private MyDatabaseHelper db;
    private RecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView recyclerView;
    private MyAssets assets;
    ArrayList<String> id;
    ArrayList<String> referensi;
    ArrayList<String> tgl;
    ArrayList<String> amount;
    ArrayList<String> pan;
    ArrayList<String> data;
    ArrayList<String> batch;
    String[] bulk       = new String[19];
    String[] sett       = new String[19];
    String[] lengRes    = {"16","6","14","10","10","1","8","8","16","16","8","6","4","12","8","16","2","4","20"};
    EditText search;
    Button btnSett;
    String dt1, dt2, dt3;
    String time = "HHmmss";
    String date = "MMdd";
    String year = "yyyy";
    Cursor regis, trans, params;
    SweetAlertDialog dialog;
    TextView txtMerchant, txtTerminal, txtTime, txtDate, txtBatch, txtCount, txtAmunt, MerchantName, Address;
    IPrinter printer;
    int amounts = 0;

    LinearLayout alert_layout, layout_view;
    /*********  work only for Dedicated IP ***********/
    String FTP_HOST;
    /*********  FTP USERNAME ***********/
    String FTP_USER;
    /*********  FTP PASSWORD ***********/
    String FTP_PASS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settelment);
        db = new MyDatabaseHelper(Settelment.this);
        regis = db.readAllRegistry();
        trans = db.readAllTransaksi();
        params = db.readAllParam();
        params.moveToFirst();
        regis.moveToFirst();
        printer = DemoApp.getDal().getPrinter();
        //view
        setView();
        assets = new MyAssets();
        storeDataToArray();

        //Format Date
        SimpleDateFormat Ftime = new SimpleDateFormat(time);
        dt1 = Ftime.format(new Date());
        SimpleDateFormat Fdate = new SimpleDateFormat(date);
        dt2 = Fdate.format(new Date());
        SimpleDateFormat Fyear = new SimpleDateFormat(year);
        dt3 = Fyear.format(new Date());

        recyclerViewAdapter = new RecyclerViewAdapter(Settelment.this, id, referensi, tgl, amount, pan, data, batch);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(new RecyclerViewAdapter(Settelment.this, id, referensi, tgl, amount, pan, data, batch));

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                id.clear();
                referensi.clear();
                tgl.clear();
                amount.clear();
                pan.clear();
                batch.clear();
                Cursor cursor;
                cursor = db.SearchData(charSequence.toString());
                while (cursor.moveToNext()){
                    cetakTrx(cursor.getString(2));
                    id.add(cursor.getString(0));
                    referensi.add(cursor.getString(1));
                    tgl.add( assets.formatDate(bulk[2]));
                    amount.add("Rp."+ assets.formatRupiah(Double.parseDouble(bulk[4])));
                    pan.add(bulk[0]);
                }
                recyclerView.setAdapter(recyclerViewAdapter);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }

        });
    }


    private void setView() {
        alert_layout    = findViewById(R.id.alert);
        layout_view     = findViewById(R.id.buktiTransaksi);
        recyclerView    = findViewById(R.id.listSettelment);
        search          = findViewById(R.id.search);
        btnSett         = findViewById(R.id.btnSettle);

        btnSett.setOnClickListener(this);

        id          = new ArrayList<>();
        referensi   = new ArrayList<>();
        tgl         = new ArrayList<String>();
        amount      = new ArrayList<>();
        pan         = new ArrayList<>();
        data        = new ArrayList<>();
        batch       = new ArrayList<>();
    }

    void initViewSettlement(String batch, int count, int amount){
        txtTerminal     = findViewById(R.id.txtIDTerminal);
        txtMerchant     = findViewById(R.id.txtIDMerchant);
        txtDate         = findViewById(R.id.txtDate);
        txtTime         = findViewById(R.id.txtTime);
        txtBatch        = findViewById(R.id.sBatch);
        txtCount        = findViewById(R.id.txtCountSale);
        txtAmunt        = findViewById(R.id.txtAmountSale);
        layout_view     = findViewById(R.id.buktiTransaksi);
        MerchantName    = findViewById(R.id.txtMerchant);
        Address         = findViewById(R.id.txtAlamat);

        SimpleDateFormat Fdate  = new SimpleDateFormat("dd MMM yyy");
        String date             = Fdate.format(new Date());
        SimpleDateFormat Ftime  = new SimpleDateFormat("HH:mm:ss");
        String time             = Ftime.format(new Date());
        params.moveToFirst();

        MerchantName.setText(params.getString(7));
        Address.setText(params.getString(8));
        txtTerminal.setText(params.getString(2));
        txtMerchant.setText(params.getString(1));
        txtDate.setText("DATE: "+date);
        txtTime.setText("TIME: "+time);
        txtBatch.setText("BATCH "+batch);
        txtCount.setText(String.valueOf(count));
        txtAmunt.setText("Rp."+assets.formatRupiah(Double.parseDouble(String.valueOf(amount))));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnSettle:
                Cursor cursor = db.readAllTransaksi();
                if (cursor.getCount() != 0){
                    dialog = new SweetAlertDialog(Settelment.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
                    dialog.setTitleText("Upload Started ...");
                    dialog.setCustomImage(R.drawable.upload);
                    dialog.hideConfirmButton();
                    dialog.show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            sendSettlemets();
                        }
                    }, 1000);
                }else{
                    dialog = new SweetAlertDialog(Settelment.this, SweetAlertDialog.WARNING_TYPE);
                    dialog.setTitleText("Tidak Ada Data Transaksi Untuk Di Settlement!");
                    dialog.setCustomImage(R.drawable.upload);
                    dialog.hideConfirmButton();
                    dialog.setCancelable(false);
                    dialog.show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            dialog.dismiss();
                        }
                    }, 2000);
                }
                break;
        }
    }

    //menampilkan data transaksi S40
    void storeDataToArray(){

        Cursor cursor   = db.readAllTransaksi();
        Cursor reg      = db.readAllRegistry();
        reg.moveToFirst();
        if(cursor.getCount() == 0){
            alert_layout.setVisibility(View.VISIBLE);
        }else{
            alert_layout.setVisibility(View.GONE);

            String batchs = String.format("%06d", Integer.parseInt(regis.getString(4)));
            SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            sweetAlertDialog.setTitleText("Processing Data ..");
            sweetAlertDialog.setCustomImage(R.drawable.loading);
            sweetAlertDialog.setCancelable(false);
            sweetAlertDialog.show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    sweetAlertDialog.dismiss();
                    while (cursor.moveToNext()){
                        cetakTrx(cursor.getString(2));
                        data.add(cursor.getString(2));
                        id.add(cursor.getString(0));
                        String ref = String.format("%06d", Integer.parseInt(cursor.getString(1)));
                        referensi.add(ref);
                        tgl.add( assets.formatDate(bulk[2]));
                        amount.add("Rp."+ assets.formatRupiah(Double.parseDouble(bulk[4])));
                        pan.add(bulk[0]);
                        batch.add(String.format("%06d", Integer.parseInt(reg.getString(4))));
                        amounts  += Integer.parseInt(bulk[4]);
                    }
                    initViewSettlement(batchs, cursor.getCount(), amounts);
                }
            }, 2000);

        }


    }

    //split data transaksi to array S50
    void cetakTrx(String data){
        String datax = data.substring(4);
        int x = 19;
        for (int i = 0; i<x; i++){
            bulk[i] = datax.substring(0, Integer.parseInt(lengRes[i]));
            datax = datax.substring(Integer.parseInt(lengRes[i]));
        }
    }

    //split data settlement to array S50
    void settement(String data){
        String datax = data.substring(4);
        int x = 19;
        for (int i = 0; i<x; i++){
            sett[i] = datax.substring(0, Integer.parseInt(lengRes[i]));
            datax = datax.substring(Integer.parseInt(lengRes[i]));
        }
    }

    //send settlement S60
    private void sendSettlemets() {

        SQLiteDatabase dbs = db.getReadableDatabase();
        Cursor cursor = dbs.rawQuery("SELECT * FROM transaksi ORDER BY trace ASC", null);
        String batch = String.format("%06d", Integer.parseInt(regis.getString(4)));
        String sale_no = String.format("%03d", Integer.parseInt(regis.getString(1)));

        String trx = "";
        String trx1 = "";
        String header = "BULKFILE02";

        while (cursor.moveToNext()){
            settement(cursor.getString(2));
            String dateTime = sett[2].substring(4);
            String trace_no = String.format("%06d", Integer.parseInt(cursor.getString(1)));
            String invoice_no = String.format("%06d", Integer.parseInt(cursor.getString(0)));

            trx +="0220"
                    +sett[0]
                    +sett[17]
                    +sett[4]+"00"
                    +trace_no
                    +dateTime.substring(4)
                    +dateTime.substring(0, 4)
                    +"02100600"
                    +sett[14]
                    +String.format("%015d", Long.parseLong(sett[13]))
                    +trace_no //invoice_no
                    +sett[5]
                    +sett[0].substring(6,8)
                    +sett[1]
                    +sett[0]
                    +sett[3]+"00"
                    +sett[0].substring(6)
                    +sett[8]
                    +sett[11]
                    +sett[10]
                    +sett[9]
                    +sett[7]
                    +sett[12]
                    +sett[15]
                    +sett[2].substring(0,4)
                    +sett[6]
                    +sett[16]
                    +sett[18];
            trx1 +="0220."
                    +sett[0]+"."
                    +sett[17]+"."
                    +sett[4]+"00"+"."
                    +trace_no+"."
                    +dateTime.substring(4)+"."
                    +dateTime.substring(0, 4)+"."
                    +"02100600"+"."
                    +sett[14]+"."
                    +String.format("%015d", Long.parseLong(sett[13]))+"."
                    +trace_no+"." //invoice_no
                    +sett[5]+"."
                    +sett[0].substring(6,8)+"."
                    +sett[1]+"."
                    +sett[0]+"."
                    +sett[3]+"00"+"."
                    +sett[0].substring(6)+"."
                    +sett[8]+"."
                    +sett[11]+"."
                    +sett[10]+"."
                    +sett[9]+"."
                    +sett[7]+"."
                    +sett[12]+"."
                    +sett[15]+"."
                    +sett[2].substring(0,4)+"."
                    +sett[6]+"."
                    +sett[16]+"."
                    +sett[18]+".";
        }
        String settlement = "0500"
                +params.getString(2)
                +params.getString(1)
                +batch
                +sale_no
                +String.format("%010d", Long.parseLong(regis.getString(2)))+"00"
                +dt1
                +dt2
                +dt3
                +"                                                                                                                                                                                      ";
        ;

        String datax = header + trx + settlement;
        String datax2 = header + trx + settlement;
        Log.e( "sendSettlemets: ", datax2);

        String FileName = dt3+dt2+params.getString(2)+String.format("%03d", Integer.parseInt(regis.getString(4)))+dt1+".txn";
        String path = this.getFilesDir().getAbsolutePath() + "/"+FileName;
        File file = new File(path);
        try {
            FileOutputStream fileout = openFileOutput(FileName, MODE_PRIVATE);
            OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
            outputWriter.write(datax);
            outputWriter.close();

            Toast.makeText(getBaseContext(), "Catatan berhasil disimpan di " + path, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        uploadFile(file);

    }

    //upload file S70
    public void uploadFile(File fileName){

        FTPClient client = new FTPClient();

        try {
            client.connect(params.getString(4), Integer.parseInt(params.getString(3)));
            client.login(params.getString(5), params.getString(6));
            client.setType(FTPClient.TYPE_BINARY);
            client.changeDirectory(params.getString(10));

            client.upload(fileName, new MyTransferListener());

        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "Gagal Terhubung Dengan Host", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            e.printStackTrace();
            try {
                client.disconnect(true);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    //convert layout to bitmap S80
    private Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        }   else{
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }

    /*******  Used to file upload and show progress  **********/
    //progres upload s90
    public class MyTransferListener implements FTPDataTransferListener {

        public void started() {
            Log.d("Upload", "Upload Started ...");
        }

        public void transferred(int length) {
            Log.d("Upload", "Upload Transferred ...");
        }

        public void completed() {
            Toast.makeText(getBaseContext(), " completed ...", Toast.LENGTH_SHORT).show();
            printSett();
            dialog.dismiss();
            db.deleteTrx();
            db.updateBatch(String.format("%06d", Integer.parseInt(regis.getString(4))+1), String.valueOf(trans.getCount()));
            Intent in = new Intent(Settelment.this, Settelment.class);
            startActivity(in);
            finish();

        }

        public void aborted() {
            Toast.makeText(getBaseContext()," transfer aborted , please try again...", Toast.LENGTH_SHORT).show();
        }

        public void failed() {
            System.out.println(" failed ..." );
        }

    }

    //print struk settlement S100
    void printSett(){
        Bitmap image = getBitmapFromView(layout_view);
        new Thread(new Runnable() {
            public void run() {

                try {
                    printer.init();
                } catch (PrinterDevException e) {
                    e.printStackTrace();
                }
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inScaled = false;
                //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.check,options);

                if (true) {
                    try {
                        printer.leftIndent(Short.parseShort("0")); // posisi kiri
                    } catch (PrinterDevException e) {
                        e.printStackTrace();
                    }
                    try {
                        printer.printBitmap(image);
                    } catch (PrinterDevException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    printer.spaceSet(Byte.parseByte("0"), // space word
                            Byte.parseByte("0")); // line space
                } catch (PrinterDevException e) {
                    e.printStackTrace();
                }
                try {
                    printer.leftIndent(Short.parseShort("0")); // posisi kiri
                } catch (PrinterDevException e) {
                    e.printStackTrace();
                }
                try {
                    printer.setGray(Integer.parseInt("1")); // warna
                } catch (PrinterDevException e) {
                    e.printStackTrace();
                }
                try {
                    printer.step(Integer.parseInt("150".trim()));
                } catch (PrinterDevException e) {
                    e.printStackTrace();
                }
                try {
                    printer.start();
                } catch (PrinterDevException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}