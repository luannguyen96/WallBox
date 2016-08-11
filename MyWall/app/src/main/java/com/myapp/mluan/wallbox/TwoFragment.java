package com.myapp.mluan.wallbox;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;


public class TwoFragment extends android.support.v4.app.Fragment {

    private ImageView img;
    private TextView txt;
    private ListView mListView;
    private ImgAdapterOfFragment2 imgAdapter;
    private String URL = "http://www.mobileswall.com/tag/";
    private String KEY[] ={"ABSTRACT","ART","NATURE","ANIMALS", "PLACES","SIMPLE"};
    private String SIMPLEURL = "http://simpledesktops.com/browse/";



    private ArrayList<ItemOfFragment2> item = new ArrayList<>();

    String arrayUri[] = {"https://goo.gl/B7VTuD", "http://goo.gl/AnPDKS" ,
            "http://goo.gl/UxdZ4e", "http://goo.gl/wJz7JU",
            "https://goo.gl/B7VTuD", "http://goo.gl/Mc3zKY"};

    String arrayTxt[] = {"ABSTRACT","ART","NATURE","ANIMALS", "PLACES", "SIMPLE"};



    public TwoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_two, container, false);
        img = (ImageView) rootView.findViewById(R.id.img_listview);
        txt = (TextView) rootView.findViewById(R.id.text_listview);
        mListView = (ListView) rootView.findViewById(R.id.listview_item);

        new loadImageListView().execute();


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                intent = new Intent(getActivity(), OverviewActivity.class);
                switch (position){
                    case 0:
                        intent.putExtra(KEY[0], URL + "abstract/");
                        break;
                    case 1:
                        intent.putExtra(KEY[1], URL + "art/");
                        break;
                    case 2:
                        intent.putExtra(KEY[2], URL + "nature/");
                        break;
                    case 3:
                        intent.putExtra(KEY[3], URL + "animals/");
                        break;
                    case 4:
                        intent.putExtra(KEY[4], URL + "places/");
                        break;
                    case 5:
                        intent.putExtra(KEY[5], SIMPLEURL);
                        break;

                    default: break;
                }
                startActivity(intent);


            }
        });

        return rootView;

    }


    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    private class loadImageListViewSource2 extends AsyncTask<Void,Void,String[]>{

        @Override
        protected String[] doInBackground(Void... params) {
            return new String[0];
        }
    }

    private class loadImageListView extends AsyncTask<Void,Void,String[]>{
        String[] arrayListView = new String[KEY.length];
        ProgressDialog mDialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.setMessage("Loading");
            mDialog.setIndeterminate(false);
            mDialog.setMax(100);
            mDialog.setCancelable(true);
            mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mDialog.show();
        }



        @Override
        protected String[] doInBackground(Void... params) {
            int k = 0;
            String ARRAY_URL[] = {"http://www.mobileswall.com/tag/", "http://simpledesktops.com/browse/"};
            for(int h = 0; h < ARRAY_URL.length ;h++) {

                if(ARRAY_URL[h].equals(URL)) {

                    for (int i = 0; i < KEY.length; i++) {

                        String realURL = URL + KEY[i] + "/";
                        try {
                            Document doc = Jsoup.connect(realURL).get();
                            Element item = doc.select("div.pics").first();

                            for (Element childItem : item.select("div.pic")) {

                                Elements tempItem = childItem.select("div.pic-body");
                                Elements imgItem = tempItem.select("img");
                                String temp = imgItem.attr("src");
                                if (temp.length() > 0) {
                                    arrayListView[k] = "" + (Uri.parse(temp));

                                    k++;
                                    break;
                                }

                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else{
                    try {
                        Document doc = Jsoup.connect(ARRAY_URL[h]).get();
                        Element item = doc.select("div[class=desktops column span-24 archive]").first();
                        Elements childItem = item.select("div.edge");
                        Elements tempItem = childItem.select("div.desktop");
                        Elements imgItem = tempItem.select("img");
                        String temp = imgItem.attr("src");
                        if(temp.length() > 0){
                            arrayListView[k] = "" + (Uri.parse(temp));
                            k++;
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return arrayListView;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            for(int i = 0, j = 0; i <arrayTxt.length && j < strings.length; i++ , j++) {

                ItemOfFragment2 itemFragment = new ItemOfFragment2(strings[j], arrayTxt[i]);
                item.add(itemFragment);
                imgAdapter = new ImgAdapterOfFragment2(getActivity(),item);
                mListView.setAdapter(imgAdapter);



            }

            if(mDialog.isShowing()){
                mDialog.dismiss();
            }
        }
    }





}
