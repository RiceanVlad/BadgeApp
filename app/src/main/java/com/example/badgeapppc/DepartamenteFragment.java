package com.example.badgeapppc;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DepartamenteFragment extends Fragment {
    
    private static final String TAG = "vlad";
    private List<String> listaDep = new ArrayList<String>();
    private List<String> listaEchipa = new ArrayList<String>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference utilizatori = db.collection("departamente");

    private String depAles = "";
    private String echipaAleasa = "";
    private Button alegeEchipaSiDep;
    private String userId = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        
        View view = inflater.inflate(R.layout.fragment_departamente,container,false);

        NavigationActivity activity = (NavigationActivity) getActivity();
        userId = activity.getMyData();

        Log.d(TAG,"dep fragment: " + userId);

        alegeEchipaSiDep = view.findViewById(R.id.buttonAlegeEchipaSiDep2);

        extragDepartamente();

        butoane();

        return view;


    }

    private void adaugaUtilizatoriInDepartamenteSiEchipe(){
        DocumentReference ref = db.collection("departamente").document(depAles);

        ref.update(echipaAleasa, FieldValue.arrayUnion(userId));

    }

    private void butoane(){
        alegeEchipaSiDep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,depAles + " " + echipaAleasa);
                adaugaUtilizatoriInDepartamenteSiEchipe();
                Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
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
        Spinner spinnerEchipa = getView().findViewById(R.id.spinnerAlegeEchipa2);
        ArrayAdapter<String> adapterEchipa = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, echipe);
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
        Spinner spinnerDepartament = getView().findViewById(R.id.spinnerAlegeDepartament2);
        ArrayAdapter<String> adapterDepartament = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, listaDep);
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
                Toast.makeText(getActivity(), "clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
