package com.example.badgeapppc;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class NavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    String id="";

    ImageView imageViewHeaderProfil;
    TextView textViewHeaderNume,textViewHeaderEmail;
    View headerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ScanFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_scan);
        }

        id=getIntent().getStringExtra("id");


        headerView=navigationView.inflateHeaderView(R.layout.nav_header);

        textViewHeaderNume= headerView.findViewById(R.id.textViewHeaderNume);

        imageViewHeaderProfil=headerView.findViewById(R.id.imageViewHeaderPoza);

        textViewHeaderEmail=headerView.findViewById(R.id.textViewHeaderEmail);
        punePozaProfil();
        puneNumeEmail();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_scan:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ScanFragment()).commit();
                break;
            case R.id.nav_departamente:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new DepartamenteFragment()).commit();
                break;
            case R.id.nav_profile:

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfilFragment()).commit();
                break;
            case R.id.nav_logout:
                Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(NavigationActivity.this, MainActivity.class);
                startActivity(intent);
                break;
        }

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public String getMyId(){

        return id;
    }
    private void puneNumeEmail(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getId().equals(id)){
                                    String nume="",email="";
                                    nume= (String) document.get("nume");
                                    email= (String) document.get("email");

                                    Log.d("EMA",email);
                                    textViewHeaderEmail.setText(email);
                                    textViewHeaderNume.setText(nume);

                                }
                            }
                        } else {
                            Log.d("EMA", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private  void punePozaProfil(){

        Log.d("EMA",id);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imagesRef = storageRef.child("profile/"+id+".jpg");

        imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d("EMA","sunt in Glide");
                Glide.with(NavigationActivity.this)
                        .load(uri)
                        .into(imageViewHeaderProfil);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }
}