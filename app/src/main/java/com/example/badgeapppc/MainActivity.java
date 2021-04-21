package com.example.badgeapppc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button createAcc, login;
    EditText emailMain, passwordMain;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    public List<String> emails = new ArrayList<String>();
    public List<String> parole = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createAcc = (Button) findViewById(R.id.buttonCreateAccount);
        login = (Button) findViewById(R.id.buttonLogin);
        emailMain = (EditText) findViewById(R.id.editTextEmail);
        passwordMain = (EditText) findViewById(R.id.editTextPassword);

        extrageEmailuriSiParole();
        butoane();

    }

    private void butoane(){
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(verificareAutentificare()){
                    Intent intent = new Intent(MainActivity.this, FirstActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(MainActivity.this,"Your login credentials don't match an account in our system.",Toast.LENGTH_LONG).show();
                }
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

    private boolean verificareAutentificare(){
        String email = emailMain.getText().toString();
        String parola = passwordMain.getText().toString();

        for(int i=0;i<emails.size();i++){
            if(email.equals(emails.get(i)) && parola.equals(parole.get(i)))
                return true;
        }
        return false;
    }

    private void extrageEmailuriSiParole(){
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            private static final String TAG = "vlad";
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int lungime = (int) dataSnapshot.child("users").getChildrenCount();
                for(int i=1;i<=lungime;i++){
                    String email = String.valueOf(dataSnapshot.child("users").child(String.valueOf(i)).child("email").getValue());
                    String parola = String.valueOf(dataSnapshot.child("users").child(String.valueOf(i)).child("password").getValue());
                    emails.add(email);
                    parole.add(parola);
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }
}