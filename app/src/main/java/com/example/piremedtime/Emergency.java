package com.example.piremedtime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
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

public class Emergency extends AppCompatActivity {
    AutoCompleteTextView simple;
    EditText message,phone;
    Button send;
    FirebaseFirestore db;
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);
        simple=findViewById(R.id.condition);
        message=findViewById(R.id.con_desc);
        phone=findViewById(R.id.pat_phone);
        send=findViewById(R.id.emergency);
        db=FirebaseFirestore.getInstance();
        pd=new ProgressDialog(this);
        String[] specialisations={"Asthma","Heart Failure","Stroke","Bronchitis","Acute Malaria","Headache","Blocked Nose","Acute Stomachache","Haemorrage","Paresthesia(Paralysis)"};
        ArrayAdapter adapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,specialisations);
        simple.setAdapter(adapter);
        simple.setThreshold(1);//start searching from character one
        simple.setAdapter(adapter);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.setTitle("RECORDING EMERGENCY ");
                pd.show();
                String pno=phone.getText().toString().trim();
                String cond=simple.getText().toString().trim();
                String mess=message.getText().toString().trim();
                Map<String,Object> emerge=new HashMap<>();
                emerge.put("PHONE",pno);
                emerge.put("CONDITION",cond);
                emerge.put("MESSAGE",mess);
                db.collection("EMERGENCIES")
                        .document(pno)
                        .set(emerge)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getApplicationContext(),"DOCTOR WILL ARRIVE SOON",Toast.LENGTH_SHORT).show();
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
