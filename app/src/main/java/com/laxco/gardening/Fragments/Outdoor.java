package com.laxco.gardening.Fragments;

import android.os.Bundle;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.laxco.gardening.AdaptersModels.Plant;
import com.laxco.gardening.AdaptersModels.PlantAdapter;
import com.laxco.gardening.Constants;
import com.laxco.gardening.R;
import com.laxco.gardening.Searching;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Outdoor extends Fragment {
    private View view;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private List<Plant> plants;
    private PlantAdapter plantAdapter;
    private NestedScrollView mNestedScrollView;
    private LinearLayout mLinearLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_outdoor, container, false);

        plants = new ArrayList<>();
        plantAdapter = new PlantAdapter(plants,getContext());

        //init views
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_main);
        mRecyclerView = view.findViewById(R.id.recycler_main);
        mLinearLayout = view.findViewById(R.id.linear_main);
        mNestedScrollView = view.findViewById(R.id.scroll_main);
        //showing recycler data




        int mNoOfColumns = Searching.Utility.calculateNoOfColumns(getContext(), 180);
        //showing recycler data

        StaggeredGridLayoutManager staggeredGridLayoutManagerNearYou = new StaggeredGridLayoutManager(mNoOfColumns, LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(staggeredGridLayoutManagerNearYou);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());


        mSwipeRefreshLayout.setColorSchemeResources(R.color.primaryColor,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);

                //loadData
                getData("Outdoor");
            }
        });


        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                plantAdapter.clear();
                plantAdapter.addAll(plants);

                //loadData
                getData("Outdoor");

            }
        });

        return view;
    }

    private void getData(String strCatId){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.getPLants, new Response.Listener<String>() {
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

                        }
                        plantAdapter.notifyDataSetChanged();
                        mSwipeRefreshLayout.setRefreshing(false);
                        mNestedScrollView.setVisibility(View.VISIBLE);
                        mLinearLayout.setVisibility(View.GONE);
                    }else {
                        mLinearLayout.setVisibility(View.VISIBLE);
                        mNestedScrollView.setVisibility(View.GONE);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("res_near_you", "[" + e.getMessage() + "]");
                    mLinearLayout.setVisibility(View.VISIBLE);
                    mNestedScrollView.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setRefreshing(false);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("res_near_you", "[" + error.getMessage() + "]");
                mSwipeRefreshLayout.setRefreshing(false);
                mLinearLayout.setVisibility(View.VISIBLE);
                mNestedScrollView.setVisibility(View.GONE);

            }


        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("strCatId", strCatId);
                return map;
            }

        };
        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        //adding the string request to request queue
        requestQueue.add(stringRequest);
    }
}