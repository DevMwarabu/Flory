package com.laxco.gardening;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ActionMenuView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.laxco.gardening.PageAdapter.PagerAdapter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Objects;

public class Dashboard extends AppCompatActivity {
    private AppBarLayout mAppBarLayout;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PagerAdapter adapter;
    private Toolbar toolbar;
    private CardView mCardViewSearch;
    private ActionMenuView actionMenuView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        tabLayout = findViewById(R.id.tablayout_main);
        viewPager = findViewById(R.id.viewPager);
        toolbar = findViewById(R.id.toolbar_main);
        mCardViewSearch = findViewById(R.id.card_saerch);

        actionMenuView = findViewById(R.id.amvMenu);
        actionMenuView.setOnMenuItemClickListener(new ActionMenuView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return onOptionsItemSelected(item);
            }
        });

        mCardViewSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Dashboard.this,Searching.class));
            }
        });


        getSupportActionBar();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        //adding tabs
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //setting viewPager adapter
        adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        //setting tab titles
        tabLayout.getTabAt(0).setText("Indoor");
        tabLayout.getTabAt(1).setText("Outdoor");
        tabLayout.getTabAt(2).setText("Gardening");

        viewPager.addOnPageChangeListener((ViewPager.OnPageChangeListener) new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @SuppressLint("NewApi")
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,actionMenuView.getMenu());
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_whats_app){
            //opening whatsApp
            String text = "Hi..! I wanted to enquire something about your flowers.";
            try {
                openWhatsApp("+254799092727",text);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        if (id == R.id.nav_call){
            //making a call
            makeCall("+254716698513");
        }
        if (id == R.id.nav_upload){
            //uploading
            startActivity(new Intent(Dashboard.this,NewPlant.class));
        }
        return super.onOptionsItemSelected(item);
    }


    private void openWhatsApp(String number,String text) throws UnsupportedEncodingException {
        String url = Constants.whatsAppUrl+number+"&text=" + URLEncoder.encode(text, "UTF-8");;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);

    }

    private void makeCall(String number){
        Intent callIntent = new Intent(Intent.ACTION_CALL); //use ACTION_CALL class
        callIntent.setData(Uri.parse("tel:"+number));    //this is the phone number calling
        //check permission
        //If the device is running Android 6.0 (API level 23) and the app's targetSdkVersion is 23 or higher,
        //the system asks the user to grant approval.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            //request permission from user if the app hasn't got the required permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE},   //request specific permission from user
                    10);
            return;
        }else {     //have got permission
            try{
                startActivity(callIntent);  //call activity and make phone call
            }
            catch (android.content.ActivityNotFoundException ex){
                Toast.makeText(getApplicationContext(),"yourActivity is not founded",Toast.LENGTH_SHORT).show();
            }
        }

    }
}