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

public class Doctors extends AppCompatActivity {
EditText Docname,MedId,Idno,Pno,pass,pconfirm;
Button register,Proceed;
AutoCompleteTextView simple,Location;
FirebaseFirestore db;
ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctors);
        simple=findViewById(R.id.specialisation);
        String[] specialisations={"Allergists","Immunologists","Anesthesiologists","Cardiologists","Colon and Rectal Surgeon","Dermatologists","Endocrinologists","General Practitioner","Gastroenterologists","Crital Care Medical Specialists"};
        ArrayAdapter adapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,specialisations);
        simple.setAdapter(adapter);
        simple.setThreshold(1);//start searching from character one
        simple.setAdapter(adapter);
        db=FirebaseFirestore.getInstance();
        Docname=findViewById(R.id.doctor_name);
        pd=new ProgressDialog(this);
        MedId=findViewById(R.id.medical_id);
        Idno=findViewById(R.id.doctor_idnumber);
        Pno=findViewById(R.id.doctor_phone);
        Location=findViewById(R.id.doctor_current_location);
        String [] locations={"MOMBASA","LAMU","KILIFI","TANA RIVER","KWALE","KILIFI","TAITA TAVETA","GARISSA","WAJIR","MANDERA","MARSABIT","ISIOLO","MERU","THARAKA NITHI","EMBU","KITUI","MACHAKOS","MAKUENI","NYANDARUA","NYERI","KIRINYAGA","MURANG’A","KIAMBU","TURKANA","WEST POKOT","SAMBURU","TRANS-NZOIA","UASIN GISHU","ELGEYO-MARAKWET","NANDI","BARINGO","LAIKIPIA","NAKURU","NAROK","KAJIADO","KERICHO","BOMET","KAKAMEGA","VIHIGA","BUNGOMA","BUSIA","SIAYA","KISUMU","HOMA BAY","MIGORI","KISII",",NYAMIRA","NAIROBI"};
        ArrayAdapter adapter1=new ArrayAdapter(this,android.R.layout.simple_list_item_1,locations);
        Location.setAdapter(adapter1);
        Location.setThreshold(1);
        pass=findViewById(R.id.pass);
        pconfirm=findViewById(R.id.cpass);
        register=findViewById(R.id.doctor_registration);
        Proceed=findViewById(R.id.doctor_proceed);
        Proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Doctors.this,Verify.class);
                startActivity(intent);
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id= UUID.randomUUID().toString();
                final String name=Docname.getText().toString().trim();
                String med=MedId.getText().toString().trim();
                String spec=simple.getText().toString().trim();
                String idn=Idno.getText().toString().trim();
                String pn=Pno.getText().toString().trim();
                String loca=Location.getText().toString().trim();
                String pasw=pass.getText().toString().trim();
                String cpasw=pconfirm.getText().toString().trim();

                if(TextUtils.isEmpty(name)){
                    Docname.setError("ENTER NAME");

                }else if(TextUtils.isEmpty(idn)){
                    Idno.setError("ENTER ID NUMBER");

                }else if(TextUtils.isEmpty(loca)){
                    Location.setError("ENTER LOCATION");
                }
                else if(TextUtils.isEmpty(pasw)){
                    pass.setError("ENTER PASSWORD");
                }else if(TextUtils.isEmpty(pasw)){
                    pconfirm.setError("ENTER CONFIRMATORY PASSWORD");
                }else if(!pasw.equals(cpasw)){
                    Toast.makeText(getApplicationContext(),"Passwords Do not Match",Toast.LENGTH_LONG).show();
                }else{
                Map<String,Object>map=new HashMap<>();
                map.put("NAME",name);
                map.put("MEDICAL_ID",med);
                map.put("SPECIALISATION",spec);
                map.put("IDENTITY_NUMBER",idn);
                map.put("PHONE_NUMBER",pn);
                map.put("LOCATION",loca);
                map.put("PASSWORD",pasw);

                pd.setTitle("DR."+name+" Being Added Please Wait..");
                pd.show();
                db.collection("Doctors")
                        .document(idn)
                        .set(map)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(Doctors.this,"Added SuccessFully",Toast.LENGTH_SHORT).show();
                                Docname.setText("");
                                Idno.setText("");
                                Location.setText("");
                                pass.setText("");
                                pconfirm.setText("");
                                MedId.setText("");
                                simple.setText("");
                                Pno.setText("");
                                pd.dismiss();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Doctors.this,e.getMessage(),Toast.LENGTH_LONG).show();
                                Docname.setText("");
                                Idno.setText("");
                                Location.setText("");
                                pass.setText("");
                                pconfirm.setText("");
                                MedId.setText("");
                                simple.setText("");
                                Pno.setText("");
                                pd.dismiss();
                            }
                        });
            }}
        });



    }
}
