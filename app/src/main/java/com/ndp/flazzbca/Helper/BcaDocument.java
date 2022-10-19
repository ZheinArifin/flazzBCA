package com.ndp.flazzbca.Helper;

import static com.ndp.flazzbca.Helper.Constant.CERT_PATH;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ndp.flazzbca.MainActivity;
import com.ndp.flazzbca.Paxstore.DocumentBase;
import com.ndp.flazzbca.Paxstore.DownloadManager;
import com.ndp.flazzbca.dbms.MyDatabaseHelper;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class BcaDocument extends DocumentBase {
    private SessionManager sessionManager;
    private String merchant_name,address1,address2,address3,address4,mid,tid,curr,authuser,authpass,firstname,phone,url,email, timeout;
    private Context context;
    File file;
    boolean deleted;

    public BcaDocument(String filePath, Context context) {
        super(filePath);
        this.context = context;
        sessionManager = new SessionManager(context);
    }

    @Override
    public int parse() {
        Document document = getDocument();
        if (document == null) {
            return -1;
        }
        Log.i("PR", "parse" );

        Log.e("Document", String.valueOf(document.getChildNodes()));
        NodeList root = document.getChildNodes();
        Node param = root.item(0);
        Log.e("TAG", "root: "+root.item(0) );
        NodeList node = param.getChildNodes();
        int index;
        String text;
        for (int i = 1; i < node.getLength(); i = index) {
            index = i;

            Log.e("TAG", "node: "+node.item(i+1).getTextContent() );
            //NAME
            text = node.item(index).getTextContent();
            if (text == null || text.isEmpty()) {
                return -1;
            }
            index += 2;
            sessionManager.setMerchantName(text);
            Log.i("PR","MERCHANT NAME " + sessionManager.getMerchantName());
            Log.e("TAG", "parse: "+index );

            //ADDRESS
            text = node.item(index).getTextContent();
            if (text == null || text.isEmpty()) {
                return -1;
            }
            index += 2;
            sessionManager.setAddress(text);
            Log.i("PR","ADDRESS " + sessionManager.getAddress());


            //MID
            text = node.item(index).getTextContent();
            if (text == null || text.isEmpty()) {
                return -1;
            }
            index += 2;
            sessionManager.setMid(text);
            Log.i("PR","MID " + sessionManager.getMid());

            //TID
            text = node.item(index).getTextContent();
            if (text == null || text.isEmpty()) {
                return -1;
            }
            index += 2;
            sessionManager.setTid(text);
            Log.i("PR","TID " + sessionManager.getTid());


            //FTP PORT
            text = node.item(index).getTextContent();
            if (text == null || text.isEmpty()) {
                return -1;
            }
            index += 2;
            sessionManager.setFTPPORT(text);
            Log.i("PR","FTP PORT" + sessionManager.getFTPPORT());

            //FTP HOST
            text = node.item(index).getTextContent();
            if (text == null || text.isEmpty()) {
                return -1;
            }
            index += 2;
            sessionManager.setFTPHost(text);
            Log.i("PR","FTP Host " + sessionManager.getFTPHost());

            //FTP User
            text = node.item(index).getTextContent();
            if (text == null || text.isEmpty()) {
                return -1;
            }
            index += 2;
            sessionManager.setFTPUser(text);
            Log.i("PR","FTP User " + sessionManager.getFTPUser());

            //FTP pass
            text = node.item(index).getTextContent();
            if (text == null || text.isEmpty()) {
                return -1;
            }
            index += 2;
            sessionManager.setFTPPass(text);
            Log.i("PR","FTP Pass " + sessionManager.getFTPPass());

            //Directory Path
            text = node.item(index).getTextContent();
            if (text == null || text.isEmpty()) {
                return -1;
            }
            index += 2;
            sessionManager.setFTPPath(text);
            Log.i("PR","Path" + sessionManager.getFTPPath());

            //SAMS SLOT
            text = node.item(index).getTextContent();
            if (text == null || text.isEmpty()) {
                return -1;
            }
            index += 2;
            sessionManager.setSams(text);
            Log.i("PR","SAMS " + sessionManager.getSams());

//            text = node.item(index).getTextContent();
//            System.out.println("cert " + text);
//            if (text == null || text.isEmpty()) {
//                return -1;
//            }
//            File DirectoryFrom = new File (DownloadManager.getInstance().getFilePath() + File.separator + text);
//            //Copy Filenya dimana..
//            File DirectoryTarget = new File (CERT_PATH);
//            try {
//                int actionChoice = 2;
//                if(actionChoice==1){
//                    if(DirectoryFrom.renameTo(DirectoryTarget)){
//                        Log.v("TAG_CERT", "Move file successful.");
//                    }else{
//                        Log.v("TAG_CERT", "Move file failed.");
//                    }
//
//                }
//                else{
//                    if(DirectoryFrom.exists()){
//                        InputStream in = new FileInputStream(DirectoryFrom);
//                        OutputStream out = new FileOutputStream(DirectoryTarget);
//                        byte[] buf = new byte[1024];
//                        int len;
//                        while ((len = in.read(buf)) > 0) {
//                            out.write(buf, 0, len);
//                        }
//                        in.close();
//                        out.close();
//                        DirectoryFrom.delete();
//                        Log.v("TAG_CERT", "Copy file successful.");
//                    }else{
//                        Log.v("TAG_CERT", "Copy file failed. Source file missing.");
//                    }
//
//                }
//
//            } catch (NullPointerException e) {
//                e.printStackTrace();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            index += 2;

            //UrlAuth

        }
        return 0;
    }


    @Override
    public void save(Context context) {
        //simpan ke database pandu
//        Log.i("PR","update data set tid='" + tid + "',mid='" + mid + "',pin='" + pin + "',ip='" + ip+ "',port='" + port + "' where idno='1'" );
//        DataHelper dbHelper = new DataHelper(context);
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        db.execSQL("update data set name='" + merchant_name + "',add1='" + address1 + "',add2='" + address2 + "',add3='" + address3 + "',mid='" + mid + "',tid='" + tid + "', serverurl='" + url +"',curr='" + curr +"',client_id='" + authuser + "',client_secret='" + authpass + "',cust_name='" + firstname + "',cust_email='" + email + "',cust_phone='" + phone + "',timeout='" + timeout + "'");

        MyDatabaseHelper db;
        db = new MyDatabaseHelper(context);
        Cursor cursor = db.readAllParam();
        Log.e("Paramsxc", sessionManager.getSams()+" add: "+ sessionManager.getAddress()+" tid: "+sessionManager.getTid()+" mid: "+sessionManager.getMid()+" ftp port: "+sessionManager.getFTPPORT()+" ftp host: "+sessionManager.getFTPHost()+" ftp user: "+sessionManager.getFTPUser()+" ftp pass: "+sessionManager.getFTPPass()+" ftp addres: "+sessionManager.getAddress()+" ftp merchant: "+sessionManager.getMerchantName());

        //new update Params
        if (cursor.getCount() != 0){
            Log.e("Tag", "update");

            db.updateParams(sessionManager.getMid(), sessionManager.getTid(), sessionManager.getFTPPORT(), sessionManager.getFTPHost(), sessionManager.getFTPUser(), sessionManager.getFTPPass(), sessionManager.getFTPPath(), sessionManager.getMerchantName(), sessionManager.getAddress(), sessionManager.getSams());
        }else{
            Log.e("Tag", "tambah");

            db.addParam(sessionManager.getMid(), sessionManager.getTid(), sessionManager.getFTPPORT(), sessionManager.getFTPHost(), sessionManager.getFTPUser(), sessionManager.getFTPPass(), sessionManager.getFTPPath(), sessionManager.getMerchantName(), sessionManager.getAddress(), sessionManager.getSams());
        }
    }
}
