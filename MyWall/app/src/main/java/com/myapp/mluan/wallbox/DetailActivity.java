package com.myapp.mluan.wallbox;

import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {
    private static final String KEY_POSITION_DOWNLOAD = "KEY_POSITION_DOWNLOAD" ;
    private String KEY_ARRAY  = "KEY_ARRAY";
    private String KEY_ARRAY_POSITION = "KEY_ARRAY_POSITION";
    private ArrayList<Item> array;
    private ArrayList<Item> detailArray = new ArrayList<>();
    private String HEAD_URL = "http://www.hdwallpapers.in/";
    private String URL_KEY_DISPLAY = "walls";
    private String URL_KEY_DOWNLOAD = "download";
    private String TAIL_URL = "wide.jpg";
    private String stringDownload;
    private ArrayList<String> imageName = new ArrayList<>();
    private int downloadWidth = 1600;
    private int downloadHeight = 900;
    private int pos;
    int actualPos;
    ViewPager myPage;
    boolean isPermissionGranted = false;
    private static final int WRITE_EXTERNAL_PERMISSION_REQUEST = 1;


    FloatingActionMenu materialDesignFAM;
    FloatingActionButton floatingActionButton1,
            floatingActionButton2, floatingActionButton3;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        setStatusBarTranslucent(true);


        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);


        materialDesignFAM = (FloatingActionMenu) findViewById(R.id.social_floating_menu);
        floatingActionButton1 = (FloatingActionButton) findViewById(R.id.floating_download);
        floatingActionButton2 = (FloatingActionButton) findViewById(R.id.floating_share);
        floatingActionButton3 = (FloatingActionButton) findViewById(R.id.floating_setwall);



        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        myPage = (ViewPager) findViewById(R.id.viewPager);

        final Intent intent = this.getIntent();
        if(intent != null && intent.hasExtra(KEY_ARRAY)){
            array = intent.getParcelableArrayListExtra(KEY_ARRAY);

            handleNewStringtoDisplay();
            actualPos = intent.getIntExtra(KEY_ARRAY_POSITION,0);
            ViewPagerAdapter adapter = new ViewPagerAdapter(this,detailArray);
            myPage.setAdapter(adapter);
            myPage.setCurrentItem(actualPos);
            //pos = position;


            //String temp = detailArray.get(position).getImageId().toString();
           // stringDownload = handleNewStringtoDownload(temp);
        }


        floatingActionButton1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //TODO something when floating action menu first item clicked
                Toast.makeText(DetailActivity.this, "Download", Toast.LENGTH_SHORT).show();




                updateValuesFromSharedPref();
                String temp = detailArray.get(pos).getImageId().toString();
                stringDownload = handleNewStringtoDownload(temp);
                /*new DownloadFile().execute(stringDownload);*/
                new DownloadFile().execute(stringDownload);


            }
        });
        floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu second item clicked
                Toast.makeText(DetailActivity.this, "Share", Toast.LENGTH_SHORT).show();
                Intent facebookIntent = getOpenFacebookIntent(DetailActivity.this);
                startActivity(facebookIntent);

            }
        });
        floatingActionButton3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu third item clicked
                Toast.makeText(DetailActivity.this, "Set wallpaper", Toast.LENGTH_SHORT).show();
                updateValuesFromSharedPref();
                String temp = detailArray.get(pos).getImageId().toString();
                stringDownload = handleNewStringtoDownload(temp);
                new setWallpapers().execute(Uri.parse(stringDownload));

            }
        });



    }




    private void updateValuesFromSharedPref(){
        SharedPreferences sharedPref = DetailActivity.this.
                getSharedPreferences(ViewPagerAdapter.KeySharedReferences,Context.MODE_PRIVATE);
        pos = sharedPref.getInt(ViewPagerAdapter.KEY_POSITION_DOWNLOAD, 0);




        if(pos != 0) {

            if (pos >= actualPos) {
                pos = pos - 1; /*case swipe to the right, image id increase, so viewpager will load image to the right and pass in sharedpref*/
            } else {

                pos = pos + 1; /*case swipe to the left, image id decrease, so viewpager will load image to the left and pass in sharedpref*/
            }
        }
        else{
            pos = 0;
        }
    }




    protected void setStatusBarTranslucent(boolean makeTranslucent) {
        if (makeTranslucent) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }




    public static Intent getOpenFacebookIntent(Context context) {

        try {
            context.getPackageManager()
                    .getPackageInfo("com.facebook.katana", 0); //Checks if FB is even installed.
            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.facebook.com/")); //Trys to make intent with FB's URI
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.facebook.com/")); //catches and opens a url to the desired page
        }
    }


    public class setWallpapers extends AsyncTask<Uri, Void, Void> {

        @Override
        protected Void doInBackground(Uri... params) {
            try {
                Bitmap result = Picasso.with(DetailActivity.this).load(params[0]).get();
                WallpaperManager wallpaperManager = WallpaperManager.getInstance(DetailActivity.this);
                wallpaperManager.setBitmap(result);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


    }

    protected void handleNewStringtoDisplay(){
        for(int i = 0; i < array.size();i++){
            String temp = array.get(i).getImageId().toString();
            String temp2 = temp.substring(33);
            imageName.add(temp2);
            String fullImageUrl = HEAD_URL + URL_KEY_DISPLAY + temp2;
            String urlAfterReplace = fullImageUrl.replace("-t1.jpg","-HD.jpg");
            Uri uri = Uri.parse(urlAfterReplace);
            Item item = new Item(uri);
            detailArray.add(item);

        }
    }

    private String handleNewStringtoDownload(String temp){
        String changeWallsToDownload = temp.replace("walls", "download");
        String newURL = changeWallsToDownload.replace("HD",downloadWidth +"x" + downloadHeight);
        return newURL;
    }


    class DownloadFile extends AsyncTask<String,Integer,Long> {
        ProgressDialog mProgressDialog = new ProgressDialog(DetailActivity.this);// Change Mainactivity.this with your activity name.
        String strFolderName;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.setMessage("Downloading");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setCancelable(true);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.show();
        }
        @Override
        protected Long doInBackground(String... aurl) {
            int count;
            try {
                URL url = new URL((String) aurl[0]);
                URLConnection conexion = url.openConnection();
                conexion.connect();
                String targetFileName= imageName.get(pos);//Change name and subname
                int lenghtOfFile = conexion.getContentLength();
                String PATH = Environment.getExternalStorageDirectory()+ "/"+ "MyWalls" +"/";
                File folder = new File(PATH);
                if(!folder.exists()){
                    folder.mkdir();//If there is no folder it will be created.
                }
                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(PATH+targetFileName);
                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress ((int)(total*100/lenghtOfFile));
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {}
            return null;
        }
        protected void onProgressUpdate(Integer... progress) {
            mProgressDialog.setProgress(progress[0]);
            if(mProgressDialog.getProgress()== mProgressDialog.getMax()){
                mProgressDialog.dismiss();
                Toast.makeText(DetailActivity.this, "File Downloaded", Toast.LENGTH_SHORT).show();

            }
        }
        protected void onPostExecute(String result) {
        }
    }


}
