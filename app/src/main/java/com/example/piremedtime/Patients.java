package com.example.piremedtime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Patients extends AppCompatActivity {
EditText uname,idnum,ulocation;
Button register,proceed;
ProgressDialog pd;
FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patients);
        final String id= UUID.randomUUID().toString().trim();
        uname=findViewById(R.id.patient_name);
        idnum=findViewById(R.id.patient_id);
        ulocation=findViewById(R.id.patient_location);
        register=findViewById(R.id.user_registration);
        db=FirebaseFirestore.getInstance();
        pd=new ProgressDialog(this);
        proceed=findViewById(R.id.user_proceed);
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Patients.this,PatientOptions.class);
                startActivity(intent);
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.setTitle("Adding User");
                pd.show();
                String id=UUID.randomUUID().toString().trim();
                String name=uname.getText().toString().trim();
                String idno=idnum.getText().toString().trim();
                String location=ulocation.getText().toString().trim();
                Map<String,Object> add=new HashMap<>();
                add.put("NAME",name);
                add.put("IDNUMBER",idno);
                add.put("LOCATION",location);
                db.collection("Users")
                        .document(idno)
                        .set(add)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getApplicationContext(),"USER ADDED SUCCESSFULLY",Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }
                        });
            }
        });
    }
}
