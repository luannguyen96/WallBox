package com.myapp.mluan.wallbox;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TabLayout mTabLayout;
    private ViewPager mPager;
    private int[] tabIcons = {R.drawable.heart,
                            R.drawable.cloud,
                            R.drawable.star
    };
    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle actionBarDT;
    NavigationView navigationView;
    android.support.v4.app.FragmentTransaction fragmentTrans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDT = new ActionBarDrawerToggle(this, mDrawerLayout,toolbar,
                R.string.drawer_open,R.string.drawer_close);
        mDrawerLayout.setDrawerListener(actionBarDT);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mPager = (ViewPager) findViewById(R.id.viewPager);
        setupViewPager(mPager);

        mTabLayout = (TabLayout) findViewById(R.id.tab);
        mTabLayout.setupWithViewPager(mPager);


        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.about_id:
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("About me")
                                .setMessage("Developed by Minh Luan" +
                                        "\nFor more information, please check my website \n" +
                                        Uri.parse("http://mluanstore.tk"))
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).show();
                        item.setChecked(true);
                        break;
                    case R.id.seting_id:
                        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                        item.setChecked(true);
                        break;

                    case R.id.send_email:
                        Intent emailItent = new Intent(Intent.ACTION_SEND);
                        emailItent.setData(Uri.parse("mailto: donguyenminhluan96@gmail.com"));
                        emailItent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                        emailItent.setType("text/plain");
                        startActivity(Intent.createChooser(emailItent, "Send feedback"));
                        item.setChecked(true);
                        break;

                    case R.id.rate_id:
                        Uri uri = Uri.parse("market://details?id=" + MainActivity.this.getPackageName());
                        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);

                        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        try {
                            startActivity(goToMarket);
                        } catch (ActivityNotFoundException e) {
                            startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://play.google.com/store/apps/details?id=" +  MainActivity.this.getPackageName())));
                        }
                        break;


                }
                return false;
            }
        });

        if (!isNetworkAvailable(MainActivity.this)) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Cannot access to the internet")
                    .setMessage("Please check your internet connection and try again!")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).show();



        }


    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDT.syncState();
    }



    private void setupViewPager(ViewPager pager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new TwoFragment(), "FAVORITES");
        adapter.addFragment(new OneFragment(), "OTHERS");


        pager.setAdapter(adapter);

    }

    class ViewPagerAdapter extends FragmentPagerAdapter{
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }


        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title){
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/



    public boolean isNetworkAvailable(final Context context) {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }



}
