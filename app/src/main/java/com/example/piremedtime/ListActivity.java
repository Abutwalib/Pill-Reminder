package com.example.piremedtime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    ArrayList<Model> modelList=new ArrayList<>();
    RecyclerView mrecycler;
    //Layout manager from recyclerview
    RecyclerView.LayoutManager layoutManager;
    //Firestore instance
    FirebaseFirestore db;
    public CustomerAdapter adapter;
    FloatingActionButton mAddbtn;
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Emergency Cases");
        //initialize firestore
        db=FirebaseFirestore.getInstance();
        // initialize Views
        mrecycler=findViewById(R.id.recycler_view);
        //set recycler view properties
        mrecycler.hasFixedSize();
        layoutManager=new LinearLayoutManager(this);
        mrecycler.setLayoutManager(layoutManager);
        pd=new ProgressDialog(this);
        mAddbtn=findViewById(R.id.addButton);
        //show data in recycler in recycler view

        showData();
        mAddbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ListActivity.this,DoctorsOptions.class));
                finish();
            }
        });

    }

    private void showData() {

        // SET title on progress dialog

        pd.setTitle("Loading Emergency Cases...");
        //Show progress Dialog
        pd.show();
        db.collection("EMERGENCIES")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        //called when data is retrieved
                        modelList.clear();
                        pd.dismiss();
                        //show data
                        for (DocumentSnapshot doc:task.getResult()){
                            Model model=new Model(
                                    doc.getString("PHONE"),
                                    doc.getString("CONDITION"),
                                    doc.getString("MESSAGE"));

                            modelList.add(model);
                        }
                        //adapter
                        adapter=new CustomerAdapter(ListActivity.this,modelList);
                        //set adapter to recycler view
                        mrecycler.setAdapter(adapter);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //called when there is any error while retrieving data
                        pd.dismiss();

                        Toast.makeText(ListActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void searchData(String s) {
        //seeing title for the progress dialog
        pd.setTitle("Searching");
        //show progress bar when user clicks a button
        pd.show();
        db.collection("Market").whereEqualTo("search",s.toLowerCase())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        //called when searching in succeeded
                        modelList.clear();
                        pd.dismiss();
                        for (DocumentSnapshot doc:task.getResult()){
                            Model model=new Model(
                                    doc.getString("PHONE"),
                                    doc.getString("CONDITION"),
                                    doc.getString("MESSAGE"));

                            modelList.add(model);
                        }
                        //adapter
                        adapter=new CustomerAdapter(ListActivity.this,modelList);
                        //set adapter to recycler view
                        mrecycler.setAdapter(adapter);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //called when there is any error in accessing
                        pd.dismiss();
                        //get and show error messages
                        Toast.makeText(ListActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();

                    }
                });
    }
    public void deleteData(int index){
        pd.setTitle("Deleting selected datum...");
        //Show progress Dialog
        pd.show();
        db.collection("Market").document(modelList.get(index).getPhone())
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Called when delete operation is success
                        Toast.makeText(getApplicationContext(),"Deleted Successfully..",Toast.LENGTH_SHORT).show();
                        //update data
                        showData();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //called when there is any kind of error
                        //get and display error message
                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });
    }
    //menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflating menu_main.xml
        getMenuInflater().inflate(R.menu.main,menu);
        //SearchView
        MenuItem item=menu.findItem(R.id.item_search);
        SearchView searchView=(SearchView)MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //called when we press search button
                searchData(s);// function call with string entered in  searchview as parameter
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //called as and when we press even a single letter
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //handle other Menu item Clicks here
        if(item.getItemId()==R.id.action_settings){
            Toast.makeText(ListActivity.this,"Settings",Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
