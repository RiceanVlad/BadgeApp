package com.example.badgeapppc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button createAcc, login;
    EditText emailMain, passwordMain;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference utilizatori = db.collection("users");
    private final String TAG = "vlad";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createAcc = (Button) findViewById(R.id.buttonCreateAccount);
        login = (Button) findViewById(R.id.buttonLogin);
        emailMain = (EditText) findViewById(R.id.editTextEmail);
        passwordMain = (EditText) findViewById(R.id.editTextPassword);

        butoane();

    }

    private void butoane(){
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailMain.getText().toString();
                String parola = passwordMain.getText().toString();

                /////////////////////////////////////////////////////////////////////

                db.collection("users")
                        .whereEqualTo("email",email)
                        .whereEqualTo("parola", parola)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if(task.getResult().size()>0){
                                        Intent intent = new Intent(MainActivity.this, FirstActivity.class);
                                        startActivity(intent);
                                    }else{
                                        Toast.makeText(MainActivity.this,"Your login credentials don't match an account in our system.",Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });

                /////////////////////////////////////////////////////////////////////

            }
        });
        createAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreareContActivity.class);
                startActivity(intent);
            }
        });
    }

}