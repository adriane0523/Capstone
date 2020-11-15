package com.example.capstonephoneapp;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST = 100;
    private int PICK_IMAGE_FROM_GALLERY_REQUEST = 1;

    List<MultipartBody.Part> parts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST);
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(
                        Intent.createChooser(intent, "Select picture"),
                        PICK_IMAGE_FROM_GALLERY_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_FROM_GALLERY_REQUEST && resultCode == RESULT_OK && data != null){
            ClipData clipData = data.getClipData();
            Log.d("MAIN", (clipData == null) + "");
            ArrayList<Uri> fileUris = new ArrayList<Uri>();
            for(int i = 0; i < clipData.getItemCount(); i++){
                ClipData.Item item = clipData.getItemAt(i);
                Uri uri = item.getUri();
                fileUris.add(uri);
            }
            uploadFiles(fileUris);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                          String permissions[], int[] grantResults){
        switch(requestCode){
            case MY_PERMISSIONS_REQUEST: {
                if(grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // perform file-related task
                }
                else{
                    // permission denied
                }
                return;
            }
        }
    }

    @NonNull
    private RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(
                descriptionString,
                okhttp3.MultipartBody.FORM);
    }

    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        // https://github.com/iPaulPro/aFileChooser/blob/master/aFileChooser/src/com/ipaulpro/afilechooser/utils/FileUtils.java
        File file = FileUtils.getFile(this, fileUri);

        RequestBody requestFile =
                RequestBody.create(
                        file,
                        MediaType.parse(getContentResolver().getType(fileUri))
                );

        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }


    private void uploadFiles(List<Uri> fileUris){
        // URL goes here, Flask localhost is at http://10.0.2.2:5000/
        Log.d("uploadFiles", "Files uploading");
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:5000/")
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();
        FileUploadService service = retrofit.create(FileUploadService.class);

        RequestBody description = createPartFromString("description speaking");

        for(int i = 0; i < fileUris.size(); i++){
            Log.d("uploadFiles", "Uploading file" + fileUris.get(i));
            parts.add(prepareFilePart("" + i, fileUris.get(i)));
        }

        ArrayList<String> stringList = new ArrayList<>();
        stringList.add("Hello World");

        Call<ResponseBody> call = service.uploadMultipleFilesDynamic(description, parts);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("onResponse", "response coming back!");
                Toast.makeText(MainActivity.this, "response received!", Toast.LENGTH_SHORT);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("onFaliure", "response failed, " + t.getMessage() + " " + t.getLocalizedMessage());
                t.printStackTrace();
                Toast.makeText(MainActivity.this, "response failed.", Toast.LENGTH_SHORT);
            }
        });
    }
}