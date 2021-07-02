package com.laxco.gardening;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.drjacky.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class NewPlant extends AppCompatActivity {
    private Toolbar toolbar;
    private Button mUpload;
    private EditText mName,mDesc,mOrigin,mHeight;
    private ProgressDialog progressDialog;
    private Spinner mSpinner;
    private ImageView mImageView;
    private Uri mImageUri;
    private StorageReference storageReference;
    private DocumentReference documentReference;
    private FirebaseFirestore firebaseFirestore;
    private String strName,strDesc,strOrigin,strHeight,strImageUrl,strCatId,strId;
    boolean isImageSelected = false;
    private String[] mimeType = {"image/png","image/jpg","image/jpeg"};
    private int IMAGE_PICKED = 1994;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_plant);

        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        documentReference = firebaseFirestore.collection("images").document();
        strId = documentReference.getId();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        toolbar = findViewById(R.id.toolbar_main);
        getSupportActionBar();
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Post Details");
        //backpressed
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //init other views
        mUpload = findViewById(R.id.btn_upload);
        mName = findViewById(R.id.edt_plant_name);
        mDesc = findViewById(R.id.edt_description);
        mSpinner = findViewById(R.id.spinner_cats);
        mOrigin = findViewById(R.id.edt_origin);
        mHeight = findViewById(R.id.edt_height);
        mImageView = findViewById(R.id.image_main);
        //init spinner selection
        mSpinner.setSelection(0);

        //picking image
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //picking image
                mImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ImagePicker.Companion.with(NewPlant.this)
                                .cropSquare()
                                .compress(1024)
                                .maxResultSize(450,450)
                                .galleryMimeTypes(mimeType)
                                .start(IMAGE_PICKED);
                    }
                });

            }
        });
        
        //uploading 
        mUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid()){
                    progressDialog.show();
                    uploadImage(mImageUri,strName,strDesc,strOrigin,strHeight,strCatId,strId);
                }
            }
        });

    }

    //validating
    private boolean isValid(){
        strName = mName.getText().toString().trim();
        strDesc = mDesc.getText().toString().trim();
        strOrigin = mOrigin.getText().toString().trim();
        strHeight = mHeight.getText().toString().trim();
        strCatId = mSpinner.getSelectedItem().toString();

        if (TextUtils.isEmpty(strName)){
            mName.setError("Enter plant name..!");
            return false;
        }else if (TextUtils.isEmpty(strDesc)){
            mDesc.setError("Enter plant description..!");
            return false;

        }else if (TextUtils.isEmpty(strOrigin)){
            mOrigin.setError("Enter plant place of origin..!");
            return false;

        }else if (TextUtils.isEmpty(strHeight)){
            mHeight.setError("Enter plant aprox height..!");
            return false;

        }else if (mSpinner.getSelectedItemPosition() == 0){
            Toast.makeText(this, "Please select plant category..!", Toast.LENGTH_SHORT).show();
            return false;

        }else if (mImageUri == null){
            Toast.makeText(this, "Please pick image", Toast.LENGTH_SHORT).show();
            return false;

        }else {
            return true;
        }
    }

    private void uploadImage(Uri mImageUri,String strName,String strDesc,String strOrigin,String strHeight,String strCatId,String strId){
        String randomName = UUID.randomUUID().toString();
        StorageReference reference = storageReference.child("images").child(randomName+".png");
        reference.putFile(mImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //getting download url
                        reference.getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        //image download url
                                        strImageUrl = uri.toString();
                                        uploadingPlant(strName,strDesc,strOrigin,strHeight,strImageUrl,strCatId,strId);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {
                                Log.i("TAG", "onFailure: "+e.getMessage());
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Log.i("TAG", "onFailure: "+e.getMessage());
            }
        });
    }

    private void uploadingPlant(String strName,String strDesc,String strOrigin,String strHeight,String strImageUrl,String strCatId,String strId){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.insertPLant,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.i("res-main", "[" + response + "]");
                            JSONObject object = new JSONObject(response);
                            String status = object.getString("status");

                            if (status.equals("0")) {
                                progressDialog.dismiss();
                                onBackPressed();
                            } else {
                                Toast.makeText(NewPlant.this, "Please check your internet and try again..!", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }

                        } catch (Exception e) {
                            Log.i("TAG", "onResponse: "+e.getMessage());

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Log.i("TAG", "onErrorResponse: "+error.getMessage());

                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            //This indicates that the reuest has either time out or there is no connection
                        } else if (error instanceof AuthFailureError) {
                            //Error indicating that there was an Authentication Failure while performing the request
                        } else if (error instanceof ServerError) {
                            //Indicates that the server responded with a error response
                        } else if (error instanceof NetworkError) {
                            Log.i("res", "[" + error.networkResponse + "]");
                        } else if (error instanceof ParseError) {
                            // Indicates that the server response could not be parsed
                        }
                    }
                }) {
            @Override
            protected java.util.Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("strImageUrl", strImageUrl);
                map.put("strName", strName);
                map.put("strDesc", strDesc);
                map.put("strCatId", strCatId);
                map.put("strId", strId);
                map.put("strHeight", strHeight);
                map.put("strOrigin", strOrigin);
                return map;
            }
        };

        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //adding the string request to request queue
        requestQueue.add(stringRequest);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {if (requestCode == IMAGE_PICKED){
        if (resultCode == Activity.RESULT_OK){
            mImageUri = data.getData();
            mImageView.setImageURI(mImageUri);
            isImageSelected = true;
        }else if (requestCode == ImagePicker.RESULT_ERROR){
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }
        super.onActivityResult(requestCode, resultCode, data);
    }
}