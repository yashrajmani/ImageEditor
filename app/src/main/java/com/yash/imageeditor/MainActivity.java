package com.yash.imageeditor;

import androidx.activity.result.ActivityResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.dsphotoeditor.sdk.activity.DsPhotoEditorActivity;
import com.dsphotoeditor.sdk.utils.DsPhotoEditorConstants;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {

    Button selectbtn;
    Button camerabtn;
    ImageView imageView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectbtn=findViewById(R.id.selectbtn);
        camerabtn=findViewById(R.id.camerabtn);
        imageView=findViewById(R.id.imageView);

        selectbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("SELECT Button CLICKED");
                callImage();
            }
        });
        camerabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Camera Button CLICKED");
                callCamera();
            }
        });

    }

    private void callImage() {
        int permission= ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            pickImage();
        }
        else {
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);

            } else
            {
                pickImage();
            }

        }


    }
    private void callCamera() {
        int permission= ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            openCam();
        }
        else {
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 1888);

            } else
            {
                openCam();
            }

        }


    }

    private void pickImage() {
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,100);
    }
    private void openCam() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, 1888);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==100 && grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {
            pickImage();
        }
        else if(requestCode==1888 && grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {
            openCam();
        }
        else
        {
            Toast.makeText(getApplicationContext(),
                    "  Permission Denied ! \n Please Allow in Settings ",Toast.LENGTH_SHORT).show();

        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK)
        {
            Uri uri =data.getData();
            switch (requestCode)
            {
                case 100:
                    Intent intent=new Intent(MainActivity.this, DsPhotoEditorActivity.class);
                    intent.setData(uri);
                    intent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_OUTPUT_DIRECTORY,"Images");
                    intent.putExtra(DsPhotoEditorConstants.DS_TOOL_BAR_BACKGROUND_COLOR, Color.parseColor("#4581b0"));
                    intent.putExtra(DsPhotoEditorConstants.DS_MAIN_BACKGROUND_COLOR, Color.parseColor("#f2efb3"));
                    startActivityForResult(intent,101);
                    break;

                case 1888:
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    String path = MediaStore.Images.Media.insertImage(MainActivity.this.getContentResolver(), photo, "Image", null);
                    Intent intent2=new Intent(MainActivity.this, DsPhotoEditorActivity.class);
                    intent2.setData(Uri.parse(path));

                    intent2.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_OUTPUT_DIRECTORY,"Images");
                    intent2.putExtra(DsPhotoEditorConstants.DS_TOOL_BAR_BACKGROUND_COLOR, Color.parseColor("#5CE1E6"));
                    intent2.putExtra(DsPhotoEditorConstants.DS_MAIN_BACKGROUND_COLOR, Color.parseColor("#5CE1E6"));
                    startActivityForResult(intent2,101);
                    break;


                case 101:
                    imageView.setImageURI(uri);
                    Toast.makeText(getApplicationContext(),
                            "IMAGE SAVED IN GALLERY \n    See Preview Above !",Toast.LENGTH_LONG).show();
                    break;


            }



        }
    }
}