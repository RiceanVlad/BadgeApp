package com.example.badgeapppc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class FirstActivity extends AppCompatActivity {
    private static final String TAG = "vlad";
    private List<String> listaDep = new ArrayList<String>();
    private List<String> listaEchipa = new ArrayList<String>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference utilizatori = db.collection("departamente");
    Button alegeEchipaSiDep;
    private String depAles = "";
    private String echipaAleasa = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        alegeEchipaSiDep = findViewById(R.id.buttonAlegeEchipaSiDep);

        extragDepartamente();

        butoane();
    }

    private void butoane(){
        alegeEchipaSiDep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,depAles + " " + echipaAleasa);
            }
        });
    }

    private void extragEchipe(String departament){
        db.collection("departamente")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getId().equals(departament)){
                                    ArrayList<String> echipe = new ArrayList<String>();
                                    for(Map.Entry<String, Object> me : document.getData().entrySet()){
                                        echipe.add(me.getKey());
                                    }
                                    adaugaInSpinnerEchipa(echipe);
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void adaugaInSpinnerEchipa(ArrayList<String> echipe) {
        Spinner spinnerEchipa = findViewById(R.id.spinnerAlegeEchipa);
        ArrayAdapter<String> adapterEchipa = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, echipe);
        spinnerEchipa.setAdapter(adapterEchipa);

        spinnerEchipa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, echipe.get(position));
                echipaAleasa = echipe.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void extragDepartamente(){
        utilizatori
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                listaDep.add(document.getId());
                            }
                            adaugaInSpinnerDepartament();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void adaugaInSpinnerDepartament(){
        Spinner spinnerDepartament = findViewById(R.id.spinnerAlegeDepartament);
        ArrayAdapter<String> adapterDepartament = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, listaDep);
        spinnerDepartament.setAdapter(adapterDepartament);

        spinnerDepartament.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, listaDep.get(position));
                extragEchipe(listaDep.get(position));
                depAles = listaDep.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(FirstActivity.this,"You have not selected a department.",Toast.LENGTH_LONG).show();
            }
        });
    }


}