package com.laxco.gardening;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.clans.fab.FloatingActionButton;
import com.laxco.gardening.AdaptersModels.Plant;
import com.laxco.gardening.AdaptersModels.PlantAdapterRelated;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DeepLinkedUi extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView mImageView;
    private TextView mName,mDesc,mOrigin,mHeight;
    private RecyclerView mRecyclerView;
    private List<Plant> plants;
    private PlantAdapterRelated plantAdapter;
    private FloatingActionButton mCall, mWhatsApp,mShare;
    private ProgressDialog progressDialog;

    private String strId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deep_linked_ui);

        plants = new ArrayList<>();
        plantAdapter = new PlantAdapterRelated(plants,this);
        Intent intent = getIntent();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait..");
        progressDialog.setCancelable(false);


        toolbar = findViewById(R.id.toolbar_main);
        getSupportActionBar();
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Details");
        //backpressed
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mImageView = findViewById(R.id.image_main);
        mRecyclerView = findViewById(R.id.recycler_main);
        mName = findViewById(R.id.tv_name);
        mDesc = findViewById(R.id.tv_desc);
        mOrigin = findViewById(R.id.tv_origination);
        mHeight = findViewById(R.id.tv_height);
        mCall = findViewById(R.id.menu_calls);
        mWhatsApp = findViewById(R.id.menu_whatsapp);
        mShare = findViewById(R.id.menu_share);


        final LinearLayoutManager layoutManager = new GridLayoutManager(this, 1, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //loading related plants
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri uri = intent.getData();
            strId = uri.getQueryParameter("strId");
            getData(strId);
        }

        mShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharingProduct();
            }
        });
    }

    private void sharingProduct(){

        String message = "Hi....!! Click the link below to view the shared product from Kericho Flora\nLink:\nhttps://kflora.app.link?strId="+strId;

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,message);
        sendIntent.setType("text/plain");
        Intent.createChooser(sendIntent,"Share Product via");
        startActivity(sendIntent);
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


    private void getData(String strIdMain){
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.getPlantSpecific, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.i("res_near_you", "[" + response + "]");
                    JSONObject object = new JSONObject(response);

                    JSONArray jsonArray = object.getJSONArray("plants");
                    if (jsonArray.length()>0) {

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            String strName = jsonObject.getString("strName");
                            String strDesc = jsonObject.getString("strDesc");
                            String strOrigin = jsonObject.getString("strOrigin");
                            String strHeight = jsonObject.getString("strHeight");
                            String strImageUrl = jsonObject.getString("strImageUrl");
                            String strCatId = jsonObject.getString("strCatId");
                            String strId = jsonObject.getString("strId");

                            Plant plant = new Plant(strName,strDesc,strOrigin,strHeight,strImageUrl,strCatId,strId);
                            plants.add(plant);
                            mRecyclerView.setAdapter(plantAdapter);

                            //load other data
                            if (i == (jsonArray.length()-1)){
                                getData(strCatId,strIdMain);


                                //setting data to respective views
                                mName.setText(strName);
                                mOrigin.setText(strOrigin);
                                mHeight.setText(strHeight);
                                mDesc.setText(strDesc);
                                //setting image
                                Glide.with(getApplicationContext())
                                        .asBitmap()
                                        .load(strImageUrl)
                                        .placeholder(R.drawable.ic_leaf)
                                        .error(R.drawable.ic_leaf)
                                        .into(new CustomTarget<Bitmap>() {
                                            @Override
                                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                mImageView.setImageBitmap(resource);
                                            }

                                            @Override
                                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                            }
                                        });

                                mCall.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //making a call
                                        makeCall("+254725877810");

                                    }
                                });


                                mWhatsApp.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        //opening whatsApp
                                        String text = "Hi..! I wanted to enquire about the "+strName+" plant on your app.";
                                        try {
                                            openWhatsApp("+254725877810",text);
                                        } catch (UnsupportedEncodingException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }

                        }
                        plantAdapter.notifyDataSetChanged();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    Log.i("res_near_you", "[" + e.getMessage() + "]");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Log.i("res_near_you", "[" + error.getMessage() + "]");

            }


        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("strId", strIdMain);
                return map;
            }

        };
        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //adding the string request to request queue
        requestQueue.add(stringRequest);
    }


    private void getData(String strCatId,String strId){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.getRelatedPlats, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.i("res_near_you", "[" + response + "]");
                    JSONObject object = new JSONObject(response);

                    JSONArray jsonArray = object.getJSONArray("plants");
                    if (jsonArray.length()>0) {
                        progressDialog.dismiss();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            String strName = jsonObject.getString("strName");
                            String strDesc = jsonObject.getString("strDesc");
                            String strOrigin = jsonObject.getString("strOrigin");
                            String strHeight = jsonObject.getString("strHeight");
                            String strImageUrl = jsonObject.getString("strImageUrl");
                            String strCatId = jsonObject.getString("strCatId");
                            String strId = jsonObject.getString("strId");

                            Plant plant = new Plant(strName,strDesc,strOrigin,strHeight,strImageUrl,strCatId,strId);
                            plants.add(plant);
                            mRecyclerView.setAdapter(plantAdapter);

                        }
                        plantAdapter.notifyDataSetChanged();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("res_near_you", "[" + e.getMessage() + "]");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("res_near_you", "[" + error.getMessage() + "]");

            }


        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("strCatId", strCatId);
                map.put("strId", strId);
                return map;
            }

        };
        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //adding the string request to request queue
        requestQueue.add(stringRequest);
    }
}