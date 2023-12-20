package com.example.matedicine.ui.home;


import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.matedicine.AlarmReceiver;
import com.example.matedicine.R;
import com.example.matedicine.adapter.MedicinAdapter;
import com.example.matedicine.databinding.FragmentHomeBinding;
import com.example.matedicine.model.Medicine;
import com.example.matedicine.ui.login.Login;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;


public class Home extends Fragment {

    private FragmentHomeBinding binding;

    private MedicinAdapter adapter;

    private DatabaseReference mMedicineRef;

    private AlarmReceiver alarmReceiver;


    private ArrayList<Medicine>medicineArrayList =new ArrayList<>();

    private FirebaseAuth mAuth;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {

            });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mMedicineRef = database.getReference(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
        alarmReceiver = new AlarmReceiver();
        mAuth = FirebaseAuth.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().getDisplayName() != null){
            String name = mAuth.getCurrentUser().getDisplayName();
            binding.hello.setText("Hello,\n" + name);
        }
        if (Build.VERSION.SDK_INT >= 33) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }

        binding.fabBtn.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_home2_to_add2));

        setRecycleview();
        logOut();

        mMedicineRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                showLoading(true);
                medicineArrayList = new ArrayList<>();
                for (DataSnapshot data: snapshot.getChildren()){
                    Medicine medicine = data.getValue(Medicine.class);
                    if (medicine != null){
                        medicine.setId(data.getKey());
                        if (medicine.getTime() != null){
                            int requestCode = generateUniqueRequestCode(medicine.getId()); // Generate unique request code
                            alarmReceiver.setOneTimeAlarm(requireContext(), requestCode, AlarmReceiver.TYPE_ONE_TIME, medicine.getTime(), medicine.getTitle());
                        }

                        medicineArrayList.add(medicine);
                    }
                }
                adapter = new MedicinAdapter(medicineArrayList, getActivity(), id -> navigateToOtherFragment(id));
                binding.rvMedicine.setAdapter(adapter);
                showLoading(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private int generateUniqueRequestCode(String id) {
        return id.hashCode(); // Or any other way to generate a unique integer from the ID
    }

    private void logOut(){

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        binding.btLogout.setOnClickListener(v-> {
            showLoading(true);
            mAuth.signOut();
            showLoading(false);
            Intent intent = new Intent(getActivity(), Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

    }



    private void setRecycleview() {
        binding.rvMedicine.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvMedicine.setAdapter(adapter);
    }

    private void navigateToOtherFragment(String id) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("key", id); // Ensure MyDataItem is Serializable or Parcelable
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_home2_to_add2, bundle);
    }

    private void showLoading(boolean b){
        if (b) {
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.GONE);
        }
    }
}