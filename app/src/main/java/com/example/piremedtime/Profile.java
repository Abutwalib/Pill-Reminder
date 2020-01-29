package com.example.piremedtime;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class Profile extends AppCompatActivity {
ImageView img1;
EditText name,location;
Button Update;
ProgressDialog pd;
    private Uri mImageUri;
    private ImageView imageView;
    private static  final int PICK_IMAGE_REQUEST=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        img1=findViewById(R.id.background);
        imageView=findViewById(R.id.profile);
        name=findViewById(R.id.name);
        location=findViewById(R.id.location);
        Update=findViewById(R.id.update);
        pd=new ProgressDialog(this);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
        Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.setTitle("Updating Profile");
                pd.show();
               String fname=name.getText().toString().trim();
               String loca=location.getText().toString().trim();
               name.setText(fname);
               location.setText(loca);
                Toast.makeText(getApplicationContext(),"Profile Updated",Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        });
    }

    private void  openFileChooser(){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }
    @Override
    protected  void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri=data.getData();
            Picasso.get().load(mImageUri).into(imageView);
        }
    }
}
