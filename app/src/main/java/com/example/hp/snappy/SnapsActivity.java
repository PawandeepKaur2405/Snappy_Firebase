package com.example.hp.snappy;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class SnapsActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    static Uri selectedImage ;
    static  Bitmap bm;
    static int flag = 0;    //0-Camera and 1 for Gallery

    //**Menu Start
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.logout:

                mAuth.signOut();
                finish();
                return true;

            default:
                return false;

        }


    }
    //Menu finish**

    //**Camera start
    public void camera(View view) {
        MainActivity.i = 1;

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, 0);

    }
    //Camera finish**

    public void mySnaps(View view)
    {
        Intent intent = new Intent(SnapsActivity.this,mySnapsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 0 && data != null) {
            flag =0;
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");

            Intent intent = new Intent(SnapsActivity.this, CameraActivity.class);
            intent.putExtra("bitmap", bitmap);
            startActivity(intent);
        }

        else if(requestCode ==1 && resultCode == RESULT_OK && data != null)
        {

            selectedImage = data.getData();
            flag =1;

            try
            {
                Intent intent = new Intent(SnapsActivity.this,CameraActivity.class);
                bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedImage);
                startActivity(intent);

            }
            catch (Exception E)
            {

                E.printStackTrace();
            }
        }
    }

    //**Gallery start
    public void gallery(View view)
    {

        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},101);
        }
        else
        {
            getPhoto();
        }
    }

    public void getPhoto()
    {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==101)
        {
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                getPhoto();
            }
            else
            {
                Toast.makeText(this, "Grant Gallery Access", Toast.LENGTH_SHORT).show();
            }
        }
    }
    //Gallery Finish**


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snaps);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        MainActivity.i = 1;

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 102);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finishAffinity();
    }
}
