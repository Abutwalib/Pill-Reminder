package com.example.piremedtime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class ReminderAdd extends AppCompatActivity {
    private static  final int PICK_IMAGE_REQUEST=1;
    private Button mchooseImage;
    private Button mUpload;
    private TextView mtextshowuploads;
    private EditText mfileName;
    EditText tablets;
    EditText sessions;
    EditText Desc;
    private ImageView imageView;
    private ProgressDialog pd;
    FloatingActionButton addbutton;
    private Uri mImageUri;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private FirebaseFirestore db;
    private StorageTask mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_add);
        mchooseImage=findViewById(R.id.select_pill_image);
        mUpload=findViewById(R.id.add_reminder);
        mfileName=findViewById(R.id.pill_name);
        addbutton=findViewById(R.id.addButton);
        tablets=findViewById(R.id.number_of_pills);
        sessions=findViewById(R.id.number_of_sessions);
        Desc=findViewById(R.id.pill_desc);
        imageView=findViewById(R.id.imageview);
        db=FirebaseFirestore.getInstance();
        pd=new ProgressDialog(this);
        mStorageRef= FirebaseStorage.getInstance().getReference("Medicines");
        mDatabaseRef= FirebaseDatabase.getInstance().getReference("Medicines");
        addbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ReminderAdd.this,ReminderAdd.class);
                startActivity(intent);
                finish();
            }
        });
        mchooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
        mUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mUploadTask!=null && mUploadTask.isInProgress()){
                    Toast.makeText(ReminderAdd.this,"Upload is in Progress",Toast.LENGTH_LONG).show();
                }else {
                    UploadFile();
                }
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
                            Upload upload=new Upload(mfileName.getText().toString().trim(),
                                    taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());
                            String uploadId=mDatabaseRef.push().getKey();
                            mDatabaseRef.child(uploadId).setValue(upload);
                            pd.dismiss();
                            }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ReminderAdd.this,e.getMessage(),Toast.LENGTH_SHORT).show();

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
        String name=mfileName.getText().toString().trim();
        String descr=Desc.getText().toString().trim();
        int tabnumber=Integer.parseInt(tablets.getText().toString().trim());
        int session=Integer.parseInt(sessions.getText().toString().trim());
        Map<String,Object> reminder=new HashMap<>();
        reminder.put("NAME",name);
        reminder.put("DESCRIPTION",descr);
        reminder.put("NO_OF_TABLETS",tabnumber);
        reminder.put("SESSIONS",session);
        db.collection("REMINDERS")
                .document(name)
                .set(reminder)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(),"Updated Successfully",Toast.LENGTH_SHORT).show();
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



