package com.example.piremedtime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.util.Map;

public class Verify extends AppCompatActivity {
EditText uname,password;
Button proceed,bac;
FirebaseFirestore db;
ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        uname=findViewById(R.id.user_identity);
        password=findViewById(R.id.vpassword);
        proceed=findViewById(R.id.proceed);
        bac=findViewById(R.id.back);
        db=FirebaseFirestore.getInstance();
        bac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(Verify.this,Doctors.class);
                startActivity(intent);
            }
        });
        pd=new ProgressDialog(this);
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.setTitle("LOGGING IN");

                pd.show();

                String name=uname.getText().toString().trim();
                final String passw=password.getText().toString().trim();
                if(TextUtils.isEmpty(name)){
                    uname.setError("Enter User Name");
                }else if(TextUtils.isEmpty(passw)){
                    password.setError("Password Required");

                }else{

                    db.collection("Doctors")
                            .document(name)
                            .get(Source.SERVER)
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                   if(task.isSuccessful()){
                                       DocumentSnapshot doc=task.getResult();
                                       if(doc.exists()){
                                           Map m=doc.getData();
                                           if(passw.equals(m.get("PASSWORD"))){
                                               Intent intent=new Intent(Verify.this,DoctorsOptions.class);
                                               startActivity(intent);
                                               uname.setText("");
                                               password.setText("");
                                               pd.dismiss();
                                           }else{
                                               Toast.makeText(getApplicationContext(),"AUTHENTICATION FAILED",Toast.LENGTH_SHORT).show();
                                               uname.setText("");
                                               password.setText("");
                                               pd.dismiss();

                                           }
                                       }else{
                                           Toast.makeText(getApplicationContext(),"AUTHENTICATION FAILED",Toast.LENGTH_SHORT).show();
                                           uname.setText("");
                                           password.setText("");
                                           pd.dismiss();
                                       }
                                   }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                    pd.dismiss();
                                }
                            });

            }}
        });
    }
}
