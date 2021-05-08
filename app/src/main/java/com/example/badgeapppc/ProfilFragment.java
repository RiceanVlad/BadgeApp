package com.example.badgeapppc;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class ProfilFragment extends Fragment {

    EditText newNume, newEmail, newParola;
    ImageView imageViewProfil;
    Button buttonModific;

    String nume="",parola="",email="",TAG="EMA",id="";

    Uri imageUri;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference reference= FirebaseStorage.getInstance().getReference();
    private CollectionReference utilizatori = db.collection("users");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root=inflater.inflate(R.layout.fragment_profil,container,false);

        NavigationActivity activity=(NavigationActivity)getActivity();
        id=activity.getMyId();

        newNume = (EditText) root.findViewById(R.id.editTextProfilName);
        newEmail= (EditText) root.findViewById(R.id.editTextProfilEmail);
        newParola = (EditText) root.findViewById(R.id.editTextProfilParola);
        imageViewProfil=root.findViewById(R.id.imageViewProfil);
        buttonModific =root.findViewById(R.id.buttonModific);

        puneDateInEditTextDinBD();
        punePozaProfil();
        butoane();
        return root;
    }

    private  void punePozaProfil(){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imagesRef = storageRef.child("profile/"+id+".jpg");

        imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d("EMA","sunt in Glide");
                Glide.with(ProfilFragment.this)
                        .load(uri)
                        .into(imageViewProfil);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                 Toast.makeText(getActivity(), "It didnt download", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void puneDateInEditTextDinBD(){

        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getId().equals(id)){
                                    String nume="",parola="",email="";
                                    nume= (String) document.get("nume");
                                    parola= (String) document.get("parola");
                                    email= (String) document.get("email");
                                    newNume.setText(nume);
                                    newParola.setText(parola);
                                    newEmail.setText(email);

                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void butoane(){

        imageViewProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent=new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,2);
            }
        });

        buttonModific.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*nume=newNume.getText().toString();
                parola=newParola.getText().toString();
                email=newEmail.getText().toString();*/

                Log.d("EMA","sunt in buton");
                extrageDateNoiDinEditText();

            }
        });
    }


    public void extrageDateNoiDinEditText(){

        String email = newEmail.getText().toString();

        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult())
                                if(!document.getId().equals(id) && document.get("email").equals(email)){
                                    Toast.makeText(getActivity(),"Email already exists.",Toast.LENGTH_LONG).show();
                                }
                            else{
                                modificaContInBD();
                                if(imageUri!=null){
                                    uploadToFirebase(imageUri);
                                }
                                else{
                                    Toast.makeText(getActivity(), "Please select image",Toast.LENGTH_LONG).show();
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });




    }

    private void modificaContInBD(){
        String email = newEmail.getText().toString();
        String pass = newParola.getText().toString();
        String nume = newNume.getText().toString();

        if(nume.length()<3){
            Toast.makeText(getActivity(),"Name must be at least 3 characters.",Toast.LENGTH_LONG).show();
        }else{
            if(!isAlpha(nume)){
                Toast.makeText(getActivity(),"Name must contain only letters.",Toast.LENGTH_LONG).show();
            }else{
                if(pass.length()<6){
                    Toast.makeText(getActivity(),"Password must be at least 6 characters.",Toast.LENGTH_LONG).show();
                    newParola.setText("");
                }else{
                    if(!isValidEmailAddress(email)){
                        Toast.makeText(getActivity(),"Invalid email address.",Toast.LENGTH_LONG).show();
                    }else{

                        DocumentReference ref= db.collection("users").document(id);

                        ref
                                .update("email", email,
                                        "parola",pass,
                                        "nume",nume)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "Successfully updated!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error updating", e);
                                    }
                                });

                    }
                }
            }
        }
    }

    public boolean isAlpha(String name) {
        char[] chars = name.toCharArray();
        for (char c : chars) {
            if(!Character.isLetter(c) && !Character.isSpaceChar(c)) {
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



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==2 &&  resultCode==RESULT_OK && data!=null){
            imageUri=data.getData();
            imageViewProfil.setImageURI(imageUri);

        }
    }

    private void uploadToFirebase(Uri uri){
        StorageReference fileRef=reference.child("profile/"+id+".jpg");
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Toast.makeText(getActivity(),"Successfully",Toast.LENGTH_LONG).show();
                        Log.d("EMA","adaugare in storage");
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(),"Uploading fail",Toast.LENGTH_LONG).show();
            }
        });
    }

}
