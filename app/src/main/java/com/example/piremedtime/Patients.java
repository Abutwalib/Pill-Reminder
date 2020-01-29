package com.example.piremedtime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
EditText uname,idnum,pass,pconfirm;
Button register,proceed;
AutoCompleteTextView ulocation;
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
        String [] locations={"MOMBASA","LAMU","KILIFI","TANA RIVER","KWALE","KILIFI","TAITA TAVETA","GARISSA","WAJIR","MANDERA","MARSABIT","ISIOLO","MERU","THARAKA NITHI","EMBU","KITUI","MACHAKOS","MAKUENI","NYANDARUA","NYERI","KIRINYAGA","MURANG’A","KIAMBU","TURKANA","WEST POKOT","SAMBURU","TRANS-NZOIA","UASIN GISHU","ELGEYO-MARAKWET","NANDI","BARINGO","LAIKIPIA","NAKURU","NAROK","KAJIADO","KERICHO","BOMET","KAKAMEGA","VIHIGA","BUNGOMA","BUSIA","SIAYA","KISUMU","HOMA BAY","MIGORI","KISII",",NYAMIRA","NAIROBI"};
        ArrayAdapter adapter1=new ArrayAdapter(this,android.R.layout.simple_list_item_1,locations);
        ulocation.setAdapter(adapter1);
        ulocation.setThreshold(1);
        pass=findViewById(R.id.patpass);
        pconfirm=findViewById(R.id.patcpass);
       register=findViewById(R.id.user_registration);
        db=FirebaseFirestore.getInstance();
        pd=new ProgressDialog(this);
        proceed=findViewById(R.id.user_proceed);
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Patients.this,Verify2.class);
                startActivity(intent);
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id=UUID.randomUUID().toString().trim();
                final String name=uname.getText().toString().trim();
                String idno=idnum.getText().toString().trim();
                String location=ulocation.getText().toString().trim();
                String pasw=pass.getText().toString().trim();
                String cpasw=pconfirm.getText().toString().trim();
                if(TextUtils.isEmpty(name)){
                    uname.setError("ENTER NAME");

                }else if(TextUtils.isEmpty(idno)){
                    idnum.setError("ENTER ID NUMBER");

                }else if(TextUtils.isEmpty(location)){
                    ulocation.setError("ENTER LOCATION");
                }
                else if(TextUtils.isEmpty(pasw)){
                    pass.setError("ENTER PASSWORD");
                }else if(TextUtils.isEmpty(pasw)){
                    pconfirm.setError("ENTER CONFIRMATORY PASSWORD");
                }else if(!pasw.equals(cpasw)){
                    Toast.makeText(getApplicationContext(),"Passwords Do not Match",Toast.LENGTH_LONG).show();
                }else {
                    pd.setTitle("Adding User");
                    pd.show();
                    Map<String, Object> add = new HashMap<>();
                    add.put("NAME", name);
                    add.put("IDNUMBER", idno);
                    add.put("LOCATION", location);
                    add.put("PASSWORD", pasw);
                    db.collection("Users")
                            .document(idno)
                            .set(add)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(getApplicationContext(), name+" ADDED SUCCESSFULLY", Toast.LENGTH_SHORT).show();
                                    uname.setText("");
                                    idnum.setText("");
                                    ulocation.setText("");
                                    pass.setText("");
                                    pconfirm.setText("");
                                    pd.dismiss();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    pd.dismiss();
                                }
                            });
                }
            }
        });
    }
}
