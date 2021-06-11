package com.example.badgeapppc;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {

    Button login;
    EditText emailMain, passwordMain;
    TextView createAcc;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final String TAG = "vlad";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createAcc = (TextView) findViewById(R.id.textViewCreateAcc);
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
                                        String id = "";
                                        for(QueryDocumentSnapshot document : task.getResult()){
                                            id = document.getId();
                                        }
                                        Intent intent = new Intent(MainActivity.this, NavigationActivity.class);
                                        intent.putExtra("id",id);
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