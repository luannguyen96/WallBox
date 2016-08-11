package com.myapp.mluan.wallbox;


import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class OverviewActivity extends AppCompatActivity {

    private GridView mGridView;
    private ImageAdapter mImageAdapter;
    private ArrayList<Item> mItemArrayList = new ArrayList<>();
    boolean loadingMore = true;
    boolean stopLoadingData = false;
    private String KEY_ABSTRACT = "ABSTRACT";
    private String KEY_ANIMALS ="ANIMALS";
    private String KEY_ART="ART";
    private String KEY_NATURE="NATURE";
    private String KEY_PLACES="PLACES";
    private String KEY_SIMPLE="SIMPLE";
    private String URL;
    int PAGECOUNT = 0;
    int currentPos;
    int currentPage = 1;
    private static final int WRITE_EXTERNAL_PERMISSION_REQUEST = 1;
    private static final int READ_PHONE_STATE_PERMISSION_REQUEST = 1;
    private String KEY_ARRAY = "KEY_ARRAY";
    private String KEY_ARRAY_POSITION = "KEY_ARRAY_POSITION";

    public OverviewActivity() {
        // Required empty public constructor
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
        mGridView = (GridView) findViewById(R.id.gridview_fragment2);
        mImageAdapter = new ImageAdapter(this, mItemArrayList);
        mGridView.setAdapter(mImageAdapter);
        Intent intent = this.getIntent();

        chooseKEY(intent);
        if (isNetworkAvailable(this)) {
            new loadImageMD().execute(URL);

        } else {
            new AlertDialog.Builder(OverviewActivity.this)
                    .setTitle("Cannot access to the internet")
                    .setMessage("Please check your internet connection and try again!")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).show();


        }



        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {


            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int lastItemInScreen = firstVisibleItem + visibleItemCount;
                if ((lastItemInScreen == totalItemCount) && !loadingMore) {
                    if (stopLoadingData == false) {
                        new loadImageMD().execute(URL);
                    }
                }

            }
        });


        mGridView.setOnItemClickListener(new AbsListView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentPos = position;
                getPermission();
            }
        });

    }


    private void clickItem(){
        Intent tempIntent = new Intent(this, DetailActivityFragment2.class);
        tempIntent.putParcelableArrayListExtra(KEY_ARRAY, mItemArrayList);
        tempIntent.putExtra(KEY_ARRAY_POSITION, currentPos);

        startActivity(tempIntent);
    }


    public boolean isNetworkAvailable(final Context context) {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }



    public class loadImageMD extends AsyncTask<String,Void,ArrayList<Uri>>{

        @Override
        protected ArrayList<Uri> doInBackground(String... params) {
            loadingMore = true;
            currentPage++;
            PAGECOUNT++;
            ArrayList<Uri> array = new ArrayList<>();
            if(URL.contains("mobileswall.com")) {

                String realURL = URL + "page/" + PAGECOUNT + "/";


                try {
                    Document doc = Jsoup.connect(realURL).get();
                    int i = 0;
                    Element item = doc.select("div.pics").first();

                    for (Element childItem : item.select("div.pic")) {

                        Elements tempItem = childItem.select("div.pic-body");
                        Elements imgItem = tempItem.select("img");
                        String temp = imgItem.attr("src");
                        if (temp.length() > 0) {
                            array.add(Uri.parse(temp));
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else{
                String realURL = URL + PAGECOUNT +"/";
                try {
                    Document doc = Jsoup.connect(realURL).get();
                    Element item = doc.select("div[class=desktops column span-24 archive]").first();
                    for(Element childItem : item.select("div[class^=edge")){
                        Elements tempItem = childItem.select("div.desktop");
                        Elements imgItem = tempItem.select("img");
                        String temp = imgItem.attr("src");
                        if(temp.length() > 0){
                            array.add(Uri.parse(temp));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }


            return array;
        }

        @Override
        protected void onPostExecute(ArrayList<Uri> uris) {
            super.onPostExecute(uris);

            for(int i = 0; i <uris.size();i++){

                Item temp = new Item(uris.get(i));
                mItemArrayList.add(temp);
            }
            int currentPosition = mGridView.getFirstVisiblePosition();
            mImageAdapter = new ImageAdapter(OverviewActivity.this, mItemArrayList);
            mGridView.setAdapter(mImageAdapter);
            mGridView.setSelection(currentPosition + 1);
            mImageAdapter.notifyDataSetChanged();
            loadingMore = false;


        }
    }

    private void chooseKEY(Intent intent){
        if(intent != null && intent.hasExtra(KEY_ABSTRACT)){
            URL = intent.getStringExtra(KEY_ABSTRACT);


        }
        if(intent != null && intent.hasExtra(KEY_ANIMALS)){
            URL = intent.getStringExtra(KEY_ANIMALS);


        }
        if(intent != null && intent.hasExtra(KEY_ART)){
            URL = intent.getStringExtra(KEY_ART);


        }


        if(intent != null && intent.hasExtra(KEY_NATURE)){
            URL = intent.getStringExtra(KEY_NATURE);


        }

        if(intent != null && intent.hasExtra(KEY_PLACES)){
            URL = intent.getStringExtra(KEY_PLACES);


        }
        if(intent != null && intent.hasExtra(KEY_SIMPLE)){
            URL = intent.getStringExtra(KEY_SIMPLE);


        }

    }





    public void getPermission(){
        if((ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)) {

            clickItem();


        }
        else{
            if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                Toast.makeText(this, "External storage permission required to save images", Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},WRITE_EXTERNAL_PERMISSION_REQUEST);

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == WRITE_EXTERNAL_PERMISSION_REQUEST){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Write external storage granted",Toast.LENGTH_SHORT).show();
                clickItem();

            }
            else{
                Toast.makeText(this,"External storage permission required to save images",Toast.LENGTH_SHORT).show();

            }
        }
        else{
            super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        }
    }

}
