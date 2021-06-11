package com.example.badgeapppc;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ScanFragment extends Fragment {

    private ImageView imgViewScan;
    private String userId = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_scan,container,false);

        NavigationActivity activity = (NavigationActivity) getActivity();

        imgViewScan = view.findViewById(R.id.imageView_scan);
        userId = activity.getMyId();

        butoane();

        return view;

    }

    private void butoane() {
        imgViewScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),BluetoothActivity.class);
                intent.putExtra("id",userId);
                startActivity(intent);
            }
        });
    }
}
