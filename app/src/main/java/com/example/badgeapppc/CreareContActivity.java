package com.example.badgeapppc;

import androidx.appcompat.app.AppCompatActivity;

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

public class CreareContActivity extends AppCompatActivity {

    EditText newName, newEmail, newPassword, newRePassword;
    Button buttonCreate;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    private List<String> emails = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creare_cont);

        newName = (EditText) findViewById(R.id.editTextNewName);
        newEmail = (EditText) findViewById(R.id.editTextNewEmail);
        newPassword = (EditText) findViewById(R.id.editTextNewPassword);
        newRePassword = (EditText) findViewById(R.id.editTextNewRePassword);
        buttonCreate = (Button) findViewById(R.id.buttonCreate);

        extrageEmailuri();

        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = newEmail.getText().toString();
                if(cautEmail(email)){
                    Toast.makeText(CreareContActivity.this,"Email already exists.",Toast.LENGTH_LONG).show();
                }
                else{
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
                                        String lung = String.valueOf(emails.size()+1);
                                        myRef.child("users").child(lung).child("email").setValue(email);
                                        myRef.child("users").child(lung).child("name").setValue(nume);
                                        myRef.child("users").child(lung).child("password").setValue(pass);
                                        String newKey = myRef.push().getKey();
                                        myRef.child("users").child(lung).child("id").setValue(newKey);
                                        Toast.makeText(CreareContActivity.this,"Account successfully created!",Toast.LENGTH_LONG).show();
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
            }
        });

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

    private void extrageEmailuri(){
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            private static final String TAG = "vlad";
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int lungime = (int) dataSnapshot.child("users").getChildrenCount();
                for(int i=1;i<=lungime;i++){
                    String email = String.valueOf(dataSnapshot.child("users").child(String.valueOf(i)).child("email").getValue());

                    emails.add(email);
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private boolean cautEmail(String email){
        for(String temp : emails){
            if(temp.equals(email))
                return true;
        }
        return false;
    }
}