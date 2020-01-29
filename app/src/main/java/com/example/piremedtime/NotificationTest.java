package com.example.piremedtime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class NotificationTest extends AppCompatActivity  implements View.OnClickListener {
    FirebaseFirestore db;
    public EditText pillname,no_pills,sess,desc;
    Button selimage;
    ProgressDialog pd;
    TimePicker timePicker;
    private Uri mImageUri;
    private ImageView imageView;
    private  int notificationId=1;
    private static  final int PICK_IMAGE_REQUEST=1;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_test);
        db=FirebaseFirestore.getInstance();
        pd=new ProgressDialog(this);
        //set onclickListeners
        findViewById(R.id.savebtn).setOnClickListener(this);
        findViewById(R.id.cancelbtn).setOnClickListener(this);
        mStorageRef= FirebaseStorage.getInstance().getReference("Medicines");
        mDatabaseRef= FirebaseDatabase.getInstance().getReference("Medicines");
        pillname=findViewById(R.id.task);
        no_pills=findViewById(R.id.nopills);
        sess=findViewById(R.id.sessions);
        desc=findViewById(R.id.description);
        imageView=findViewById(R.id.imageview);
        selimage=findViewById(R.id.select_pill_image);
        InputFilterIntRange rangeFilter = new InputFilterIntRange(1, 4);
        sess.setFilters(new InputFilter[]{rangeFilter});
        no_pills.setFilters(new InputFilter[]{rangeFilter});
        timePicker=findViewById(R.id.timePicker);
        selimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
    }




    @Override
    public void onClick(View view){

        //Set NOtification and Text
        Intent intent=new Intent(NotificationTest.this,AlarmReceiver.class);
        intent.putExtra("notificationId",notificationId);
        intent.putExtra("PillName",pillname.getText().toString());
        intent.putExtra("NoPills",no_pills.getText().toString());
        intent.putExtra("Sessions",Integer.parseInt(sess.getText().toString()));
        intent.putExtra("Description",desc.getText().toString());
        //get broadcast(context,request code,intent,flags)
        PendingIntent alarmintent= PendingIntent.getBroadcast(NotificationTest.this,0,
                intent,PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarm=(AlarmManager)getSystemService(ALARM_SERVICE);

        switch (view.getId()){
            case R.id.savebtn:
                int hour=timePicker.getCurrentHour();
                int minute=timePicker.getCurrentMinute();
                //create time
                Calendar startTime= Calendar.getInstance();
                startTime.set(Calendar.HOUR_OF_DAY,hour);
                startTime.set(Calendar.MINUTE,minute);
                startTime.set(Calendar.SECOND,0);
                long alarmstart=startTime.getTimeInMillis();
                final int id= (int) System.currentTimeMillis();
                PendingIntent alarminten= PendingIntent.getBroadcast(NotificationTest.this,id,
                        intent,PendingIntent.FLAG_CANCEL_CURRENT);
                //set Alarm
                AlarmManager alarmManager1 = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager1.set(AlarmManager.RTC_WAKEUP, alarmstart,alarminten);
                Toast.makeText(this,"Done",Toast.LENGTH_LONG).show();
                UploadFile();
                addReminderInfo();
                break;
            case R.id.cancelbtn:
                alarm.cancel(alarmintent);
                Toast.makeText(this,"Cancelled",Toast.LENGTH_SHORT).show();

        }
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
    private String getFileExtension(Uri uri){
        ContentResolver cR=getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));

    }
    private void UploadFile(){
        if(mImageUri!=null){
            pd.setTitle("Adding Reminder Information");
            pd.show();
            StorageReference fileReference=mStorageRef.child(System.currentTimeMillis()
                    +"."+getFileExtension(mImageUri));
            mUploadTask=fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler=new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    pd.setProgress(0);
                                }
                            },5000);
                            addReminderInfo();
                            Upload upload=new Upload(pillname.getText().toString().trim(),
                                    taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());
                            String uploadId=mDatabaseRef.push().getKey();
                            mDatabaseRef.child(uploadId).setValue(upload);
                            pd.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(NotificationTest.this,e.getMessage(),Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress=(100* taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            pd.setProgress((int) progress);

                        }
                    });
        }else{
            Toast.makeText(getApplicationContext(),"No File Selected",Toast.LENGTH_SHORT).show();
        }
    }
    private void addReminderInfo() {
        pd.show();
        String name=pillname.getText().toString().trim();
        String descr=desc.getText().toString().trim();
        int tabnumber=Integer.parseInt(no_pills.getText().toString().trim());
        Map<String,Object> reminder=new HashMap<>();
        reminder.put("NAME",name);
        reminder.put("DESCRIPTION",descr);
        reminder.put("NO_OF_TABLETS",tabnumber);
        db.collection("REMINDERS")
                .document(name)
                .set(reminder)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(),"Updated Successfully",Toast.LENGTH_SHORT).show();
                        //onClick(null);
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

}
