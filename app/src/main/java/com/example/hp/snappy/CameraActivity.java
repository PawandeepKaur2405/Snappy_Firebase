package com.example.hp.snappy;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class CameraActivity extends AppCompatActivity {

    ImageView imageView;
    Bitmap bitmap;
    String image;
    static Button sendButton;
    static ProgressBar spinner ;

    //Adding image to firebase
    public void send(View view)
    {

        spinner.setVisibility(View.VISIBLE);
        sendButton.setEnabled(false);

        // Get the data from an ImageView as bytes
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();



        UploadTask uploadTask = FirebaseStorage.getInstance().getReference().child("images").child(image).putBytes(data);


        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads

                Toast.makeText(CameraActivity.this, "Upload Failed", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {

                Uri downloadUrl = taskSnapshot.getDownloadUrl();

                Intent intent = new Intent(CameraActivity.this,UsersActivity.class);
                intent.putExtra("imageName",image);
                intent.putExtra("imageUrl",downloadUrl.toString());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        spinner = (ProgressBar) findViewById(R.id.progressBar);
        spinner.setVisibility(View.INVISIBLE);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        imageView = (ImageView)findViewById(R.id.imageView);
        sendButton = (Button)findViewById(R.id.sendButton);

        image = UUID.randomUUID().toString() + ".jpg";

        if(SnapsActivity.flag==1)
        {
            imageView.setImageBitmap(SnapsActivity.bm);
        }
        else if(SnapsActivity.flag==0)
        {
            bitmap = getIntent().getParcelableExtra("bitmap");
            imageView.setImageBitmap(bitmap);
        }
        else
        {
            Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
        }

    }

}
