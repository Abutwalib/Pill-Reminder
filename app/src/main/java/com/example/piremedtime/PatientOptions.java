package com.example.piremedtime;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.Toast;

public class PatientOptions extends AppCompatActivity {
    GridLayout mainGrid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_options);
        mainGrid=(GridLayout)findViewById(R.id.mainGrid);
        setSingleEvent(mainGrid);
    }

    private void setSingleEvent(GridLayout mainGrid) {
        for (int i=0;i<mainGrid.getChildCount();i++){
            CardView cardView=(CardView) mainGrid.getChildAt(i);

            final int finali=i;
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(finali==0){
                        Intent intent= new Intent(PatientOptions.this,ReminderAdd.class);
                        startActivity(intent);
                    }else if(finali==1){
                        Toast.makeText(getApplicationContext(),"WORKING IN PROGRESS",Toast.LENGTH_SHORT).show();
                    }
                    else if(finali==2){
                       Intent intent=new Intent(PatientOptions.this,Emergency.class);
                       startActivity(intent);

                    }
                    else if(finali==3){
                        Intent intent=new Intent(PatientOptions.this,Profile.class);
                        startActivity(intent);

                    }


                }
            });
        }
    }
}
