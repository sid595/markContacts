package com.acad_example.nilea.mapmarker;

import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class selectTarget extends AppCompatActivity {

    Button startDownload, writeContact,gotoMaps ;
    String url = "http://www.cs.columbia.edu/~coms6998-8/assignments/homework2/contacts/contacts.txt";
    String path  = "sdcard/Assignment/targets.txt";
    TextView statusUpdate;
    ArrayList data = new ArrayList();
    ArrayList names = new ArrayList();
    public static final String Origin = "com.acad_example.nilea.selectTarget";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_target);
        startDownload = (Button) findViewById(R.id.Download);
        writeContact = (Button) findViewById(R.id.button2);
        statusUpdate = (TextView) findViewById(R.id.text1);
        writeContact.setEnabled(false);
        gotoMaps = (Button) findViewById(R.id.button3);
        gotoMaps.setEnabled(false);
        writeContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readData(path);
                setContacts();
                gotoMaps.setEnabled(true);
            }
        });

        gotoMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                names.add(((ArrayList) data.get(0)).get(0).toString());
                names.add(((ArrayList) data.get(1)).get(0).toString());
                names.add(((ArrayList) data.get(2)).get(0).toString());
                names.add(((ArrayList) data.get(3)).get(0).toString());
                names.add(((ArrayList) data.get(4)).get(0).toString());
                startMap();

            }
        });


//        setContacts();
    }

    public void readData( String address){
        int temp = 0, count = 0;
        String tempString = "";
        File file = new File(address);
        if (!file.exists()){
            statusUpdate.setText("File does not exists yet, try Again");
            return;
        }
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            temp = fileInputStream.read();
            while(temp!= -1){
                data.add(new ArrayList());
                tempString = "";
                while(temp!= (int)' '){
                    tempString+=(char)temp;
                    temp = fileInputStream.read();
                }
                ((ArrayList) data.get(count)).add(tempString);
                tempString = "";
                temp = fileInputStream.read();
                while(temp!= (int)' '){
                    tempString+=(char)temp;
                    temp = fileInputStream.read();
                }
                ((ArrayList) data.get(count)).add(tempString);
                tempString = "";
                temp = fileInputStream.read();
                while(temp!= (int)' '){
                    tempString+=(char)temp;
                    temp = fileInputStream.read();
                }
                ((ArrayList) data.get(count)).add(Integer.parseInt(tempString));
                tempString = "";
                temp = fileInputStream.read();
                while(temp!= (int)'\n'){
                    tempString+=(char)temp;
                    temp = fileInputStream.read();
                    if(temp == -1) break;
                }
                ((ArrayList) data.get(count)).add(Integer.parseInt(tempString));
                if(temp == -1) break;
                temp = fileInputStream.read();
                count++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void setContacts(){
        //Initialize the data that are relevant to contact:
        statusUpdate.setText("Writing Contacts to your Contacts List:");
        statusUpdate.append("\nPease note that if the contacts already exists, they may or may " +
                "or may not be added again depending upon the behviour of ContactsContract.RawContact" +
                "table. The Best suggestion isto delete the earlier contacts");
        int tSize = data.size(), i = 0;
        String name = null,numberHome = null,numberMobile = null,email = null;
        for(;i<tSize;i++) {
            name = ((ArrayList) data.get(i)).get(0).toString();
            email = ((ArrayList) data.get(i)).get(1).toString();
            numberMobile = ((ArrayList) data.get(i)).get(2).toString();
            numberHome = ((ArrayList) data.get(i)).get(3).toString();

            try {
                ArrayList<ContentProviderOperation> ops =
                        new ArrayList<ContentProviderOperation>();
                int rawContactInsertIndex = ops.size();
                ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                        .build());

                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                        .build());

                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, numberMobile)
                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                        .build());

                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, numberHome)
                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                        .build());

                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, email)
                        .build());


                getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                statusUpdate.append("\nAdded " + name+ "'s Data" );
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (OperationApplicationException e) {
                e.printStackTrace();
            }
        }
    }

    public void startDownloading(View view) {
        DownloadTask downloadTask = new DownloadTask(url);
        downloadTask.execute();
        writeContact.setEnabled(true);
        statusUpdate.setText("Download is Running in AsyncTask, so that it does not hangs up the " +
                "UI of main thread. However, if you are able to see this text, the download is " +
                "COMPLETED :)");
    }

    public void startMap(){
        Intent intent = new Intent(this,trackTarget.class);
        intent.putExtra(Origin,names);
        startActivity(intent);
    }

    public class DownloadTask extends AsyncTask<Void,Integer,Void>{

        ProgressDialog progressDialog;
        int fileLength= 0;
        String download_path = "";

        public boolean done = false;
        public File folder;
        public File download_file;

        public DownloadTask(String url) {
            download_path = url;
        }

        @Override

        protected Void doInBackground(Void... path) {

            try {
                URL url = new URL(download_path);
                URLConnection urlConnection = url.openConnection();
                urlConnection.connect();
                fileLength = urlConnection.getContentLength();

                folder = new File("sdcard/Assignment");
                if (!folder.exists()){
                    folder.mkdir();
                }

                download_file = new File(folder,"targets.txt");
                if (download_file.exists()){
                    download_file.delete();
                }
                //Open the buffer of 4kB initially. After tweaking, I will find the optimum buffer size
                InputStream inputStream = new BufferedInputStream(url.openStream(),4096);

                // The input to the file is being given by internet by url through a buffer. Now it
                //is our job to actually put it in the file whch we have created. Note that here
                //we know the type of the file.

                byte[] downloaded_data = new byte[1024];
                int total = 0, count = 0;

                OutputStream outputStream = new FileOutputStream(download_file);
                //reading data from download buffer and putting it in output stream
                for (count = inputStream.read(downloaded_data);(count)!= -1;count = inputStream.read(downloaded_data)){
                    outputStream.write(downloaded_data,0,count);
                    total += count;
                    int currStatus = (total/fileLength)*100;
                    publishProgress(currStatus);
                }
                done = true;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute(){

            progressDialog = new ProgressDialog(selectTarget.this);
            progressDialog.setTitle("Downloading");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMax(100);
            progressDialog.setProgress(0);
            progressDialog.show();
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.hide();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressDialog.setProgress(values[0]);
            //    super.onProgressUpdate(values);
        }
    }
}