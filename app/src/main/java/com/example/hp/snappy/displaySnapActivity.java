package com.example.hp.snappy;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class displaySnapActivity extends AppCompatActivity {

    ImageView imageView;
    FirebaseAuth mAuth;

    //DOWNLOADING SNAP
    public class ImageDownloader extends AsyncTask<String,Void,Bitmap>
    {
        @Override
        protected Bitmap doInBackground(String... urls)
        {
            try
            {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.connect();
                InputStream in = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                return bitmap;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return null;
            }

        }
    }

    //DELETING SNAP
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://snap-pie-default-rtdb.firebaseio.com/");
        database.getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("snaps").child(getIntent().getStringExtra("imageKey")).removeValue();

        FirebaseStorage.getInstance().getReference().child("images").child(getIntent().getStringExtra("imageName")).delete();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_snap);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        imageView = (ImageView)findViewById(R.id.imageView);
        mAuth = FirebaseAuth.getInstance();

        ImageDownloader task = new ImageDownloader();

        Bitmap myImage;
        try
        {
            myImage = task.execute(getIntent().getStringExtra("imageUrl")).get();
            imageView.setImageBitmap(myImage);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
