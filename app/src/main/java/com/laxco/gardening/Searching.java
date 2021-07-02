package com.laxco.gardening;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.laxco.gardening.AdaptersModels.Plant;
import com.laxco.gardening.AdaptersModels.PlantAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Searching extends AppCompatActivity {
    private RecyclerView mRecyclerViewMain;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Toolbar toolbar;
    private SearchView searchView;
    private List<Plant> plants;
    private PlantAdapter plantAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching);

        plants = new ArrayList<>();
        plantAdapter = new PlantAdapter(plants,this);

        toolbar = findViewById(R.id.toolbar_main);
        getSupportActionBar();
        setSupportActionBar(toolbar);

        toolbar.setTitle("Searching");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mSwipeRefreshLayout = findViewById(R.id.swipe_main);
        mRecyclerViewMain = findViewById(R.id.recycler_main);
        searchView = findViewById(R.id.searchview);



        int mNoOfColumns = Searching.Utility.calculateNoOfColumns(this, 180);

        final LinearLayoutManager layoutManager = new GridLayoutManager(this, mNoOfColumns, LinearLayoutManager.VERTICAL, false);
        mRecyclerViewMain.setLayoutManager(layoutManager);
        mRecyclerViewMain.setItemAnimator(new DefaultItemAnimator());


        mSwipeRefreshLayout.setColorSchemeResources(R.color.primaryColor,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);

                //loadData
                getData();
            }
        });


        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                plantAdapter.clear();
                plantAdapter.addAll(plants);

                //loadData
                getData();

            }
        });

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                plantAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                plantAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }



    private void getData(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.getAllPlants, new Response.Listener<String>() {
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
                            mRecyclerViewMain.setAdapter(plantAdapter);

                        }
                        plantAdapter.notifyDataSetChanged();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("res_near_you", "[" + e.getMessage() + "]");
                    mSwipeRefreshLayout.setRefreshing(false);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("res_near_you", "[" + error.getMessage() + "]");
                mSwipeRefreshLayout.setRefreshing(false);

            }


        });
        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //adding the string request to request queue
        requestQueue.add(stringRequest);
    }


    public static class Utility {
        public static int calculateNoOfColumns(Context context, float columnWidthDp) {
            // For example columnWidthdp=180
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
            return (int) (screenWidthDp / columnWidthDp + 0.5);
        }
    }
}