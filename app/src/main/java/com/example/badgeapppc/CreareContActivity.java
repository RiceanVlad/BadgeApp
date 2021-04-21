package com.example.badgeapppc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.SoundEffectConstants;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CreareContActivity extends AppCompatActivity {

    EditText newName, newEmail, newPassword, newRePassword;
    Button buttonCreate;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<String> emails = new ArrayList<String>();
    private final String TAG = "vlad";
    private boolean existaEmail = false;
    private CollectionReference utilizatori = db.collection("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creare_cont);

        newName = (EditText) findViewById(R.id.editTextNewName);
        newEmail = (EditText) findViewById(R.id.editTextNewEmail);
        newPassword = (EditText) findViewById(R.id.editTextNewPassword);
        newRePassword = (EditText) findViewById(R.id.editTextNewRePassword);
        buttonCreate = (Button) findViewById(R.id.buttonCreate);

        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = newEmail.getText().toString();
                /////////////////////////////////////////////////////////////////////

                db.collection("users")
                        .whereEqualTo("email",email)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if(task.getResult().size()>0){
                                        Toast.makeText(CreareContActivity.this,"Email already exists.",Toast.LENGTH_LONG).show();
                                    }else{
                                        adaugaContInBazaDeDate();
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });

                /////////////////////////////////////////////////////////////////////

            }
        });
    }

    private void adaugaContInBazaDeDate(){
        String email = newEmail.getText().toString();
        String pass = newPassword.getText().toString();
        String rePass = newRePassword.getText().toString();
        String nume = newName.getText().toString();

        if(nume.length()<3){
            Toast.makeText(CreareContActivity.this,"Name must be at least 3 characters.",Toast.LENGTH_LONG).show();
        }else{
            if(!isAlpha(nume)){
                Toast.makeText(CreareContActivity.this,"Name must contain only letters.",Toast.LENGTH_LONG).show();
            }else{
                if(pass.length()<6){
                    Toast.makeText(CreareContActivity.this,"Password must be at least 6 characters.",Toast.LENGTH_LONG).show();
                    newPassword.setText("");
                    newRePassword.setText("");
                }else{
                    if(!isValidEmailAddress(email)){
                        Toast.makeText(CreareContActivity.this,"Invalid email address.",Toast.LENGTH_LONG).show();
                    }else{
                        if(pass.equals(rePass)){
                            Toast.makeText(CreareContActivity.this,"Account successfully created!",Toast.LENGTH_LONG).show();
                            HashMap<String, String> atribute = new HashMap<String, String>();
                            atribute.put("email",email);
                            atribute.put("nume",nume);
                            atribute.put("parola",pass);
                            utilizatori.document().set(atribute);
                        }else{
                            Toast.makeText(CreareContActivity.this,"Re-enter password, please.",Toast.LENGTH_LONG).show();
                            newPassword.setText("");
                            newRePassword.setText("");
                        }
                    }
                }
            }
        }
    }

    public boolean isAlpha(String name) {
        char[] chars = name.toCharArray();
        for (char c : chars) {
            if(!Character.isLetter(c)) {
                return false;
            }
        }
        return true;
    }

    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

}