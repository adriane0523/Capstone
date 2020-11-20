package com.example.capstonephoneapp;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST = 100;
    private int PICK_IMAGE_FROM_GALLERY_REQUEST = 1;
    private TextView tvClassifyResult;
    private TextView tvDescription;

    List<MultipartBody.Part> parts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // permissions check
        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST);
        }

        tvClassifyResult = findViewById(R.id.tvClassifyResult);
        tvDescription = findViewById(R.id.tvDescription);

        // Open image storage gallery upon pressing button
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
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
            ArrayList<Uri> fileUris = new ArrayList<Uri>();
            if(clipData == null){
                Uri uri = data.getData();
                fileUris.add(uri);
            }
            uploadFiles(fileUris);
//            Log.d("MAIN", (clipData == null) + "");
//            for(int i = 0; i < clipData.getItemCount(); i++){
//                ClipData.Item item = clipData.getItemAt(i);
//                Uri uri = item.getUri();
//                fileUris.add(uri);
//            }
//            uploadFiles(fileUris);
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
        // HttpClient to set connect, write, and read timeouts
        Log.d("uploadFiles", "Files uploading");
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();

        // URL goes here, Flask localhost is at http://10.0.2.2:5000/
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://206.189.213.242:5000/")
                .client(okHttpClient)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();
        FileUploadService service = retrofit.create(FileUploadService.class);

        RequestBody description = createPartFromString("description speaking");

        for(int i = 0; i < fileUris.size(); i++){
            Log.d("uploadFiles", "Uploading file" + fileUris.get(i));
            parts.add(prepareFilePart("image", fileUris.get(i)));
        }

        // Response handler from the server, returns a String detailing results of the classification API
        Call<ResponseBody> call = service.uploadMultipleFilesDynamic(description, parts);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("onResponse", "response coming back!");
                if(response.isSuccessful()){
                    if(response.body() != null){
                        try {
                            String responseData = response.body().string();
                            try{
                                JSONObject classificationResult = new JSONObject(responseData);
                                Log.d("JSONResponse", classificationResult.toString());
                                String classifyName = classificationResult.getString("result");
                                tvClassifyResult.setText(classifyName);

                                String classifyName = classificationResult.getString("name");
                                double classifyProb = classificationResult.getDouble("probability");
                                String classifyRelation = classificationResult.getString("relation");
                                String description = classificationResult.getString("description");
                                tvClassifyResult.setText(classifyName + ": " + classifyProb);
                                tvDescription.setText(classifyRelation + ", " + description);
                                for(int i = 0; i < parts.size(); i++){
                                    Log.d("Parts List", parts.get(i).toString());
                                }

                                parts.clear();
                            }catch(JSONException e){}
                        } catch (IOException e) { e.printStackTrace(); }
                    }else{
                        Log.d("onEmptyResponse", "Returned Empty Response");
                    }
                }
//                Log.d("onResponse", response.body().toString());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("onFaliure", "response failed, " + t.getMessage());
            }
        });
    }
}