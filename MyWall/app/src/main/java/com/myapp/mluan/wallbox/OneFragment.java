package com.myapp.mluan.wallbox;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;


public class OneFragment extends android.support.v4.app.Fragment {

    private ArrayList<Item> mUriArrayList = new ArrayList<>();
    private ImageAdapter adapter;
    private GridView mGridView;
    private String BASE_URL = "http://www.hdwallpapers.in";
    private ImageView mImageView;
    private static final int WRITE_EXTERNAL_PERMISSION_REQUEST = 1;
    private static final int READ_PHONE_STATE_PERMISSION_REQUEST = 1;
    private String DETAIL_URL = "http://www.hdwallpapers.in/page/";
    private int PAGE_COUNT = 0;
    private String pagingURL;
    int currentPage = 1;
    boolean loadingMore = true;
    boolean stopLoadingData = false;
    private String KEY_ARRAY = "KEY_ARRAY";
    private String KEY_ARRAY_POSITION = "KEY_ARRAY_POSITION";
    int pos;

    public OneFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_one, container, false);

        mImageView = (ImageView) rootView.findViewById(R.id.img);

        mGridView = (GridView) rootView.findViewById(R.id.gridview);
        adapter = new ImageAdapter(getActivity(),mUriArrayList);
        mGridView.setAdapter(adapter);

        if (!isNetworkAvailable(getActivity())) {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Cannot access to the internet")
                    .setMessage("Please check your internet connection and try again!")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getActivity().finish();
                        }
                    }).show();



        }

        loadImage mLoad = new loadImage();
        mLoad.execute();

        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int lastItemInScreen = firstVisibleItem + visibleItemCount;
                if ((lastItemInScreen == totalItemCount) && !loadingMore) {
                    if (stopLoadingData == false) {
                        new loadImage().execute();
                    }
                }



            }
        });


        mGridView.setOnItemClickListener(new AbsListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*String tempUri = mUriArrayList.get(position).getImageId().toString();*/
                pos = position;
                getPermission();
            }
        });


        return rootView;
    }


    public boolean isNetworkAvailable(final Context context) {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }


    private void clickItem(){
        Intent tempIntent = new Intent(getActivity(), DetailActivity.class);
        tempIntent.putParcelableArrayListExtra(KEY_ARRAY, mUriArrayList);
        tempIntent.putExtra(KEY_ARRAY_POSITION, pos);

        startActivity(tempIntent);
    }



    public class loadImage extends AsyncTask<Void, Void, ArrayList<Uri> >{



        @Override
        protected ArrayList<Uri> doInBackground(Void... params) {
            loadingMore = true;
            PAGE_COUNT++;
            ArrayList<Uri> array = new ArrayList<>();
            String URL = DETAIL_URL + PAGE_COUNT; //http://www.hdwallpapers.in/page/1/thumbs/perfect_pink_rose-t1.jpg
            //Toast.makeText(getActivity(),URL,Toast.LENGTH_LONG).show();

            try{
                Document doc  = Jsoup.connect(URL).get();
                int i = 0;
                Element items = doc.select("ul.wallpapers").first();
                for(Element childItem : items.select("li.wall")){
                    Elements itemImg = childItem.select("img");
                    String temp = itemImg.attr("src");
                    String newUrl = BASE_URL + temp;
//                    String temp2 = newUrl.substring(33,52);
//                    String fullImageUrl = "http://www.hdwallpapers.in/walls" + temp2 + "wide.jpg";
                    Uri uri = Uri.parse(newUrl);
                    array.add(uri);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            return array;
        }


        @Override
        protected void onPostExecute(ArrayList<Uri> uris) {
            super.onPostExecute(uris);

            for(int i = 0; i < uris.size();i++){
                Item temp = new Item(uris.get(i));
                mUriArrayList.add(temp);

            }

            int currentPosition = mGridView.getFirstVisiblePosition();
            adapter = new ImageAdapter(getActivity(),mUriArrayList);
            mGridView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            mGridView.setSelection(currentPosition + 1);
            loadingMore = false;




        }
    }
    public void getPermission(){
        if((ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
            && (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE)== PackageManager.PERMISSION_GRANTED)) {
            Toast.makeText(getActivity(),"Write external storage granted",Toast.LENGTH_SHORT).show();
            clickItem();


        }
        else{
            if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)&&
                    (shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE))){
                Toast.makeText(getActivity(), "External storage permission required to save images", Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},WRITE_EXTERNAL_PERMISSION_REQUEST);
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},READ_PHONE_STATE_PERMISSION_REQUEST);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == WRITE_EXTERNAL_PERMISSION_REQUEST){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getActivity(),"Write external storage granted",Toast.LENGTH_SHORT).show();
                clickItem();

            }
            else{
                Toast.makeText(getActivity(),"External storage permission required to save images",Toast.LENGTH_SHORT).show();

            }
        }
        else{
            super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        }
    }

}
