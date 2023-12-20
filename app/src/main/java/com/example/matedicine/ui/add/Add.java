package com.example.matedicine.ui.add;


import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.matedicine.R;
import com.example.matedicine.TimePickerFragment;
import com.example.matedicine.dao.MedicineDao;
import com.example.matedicine.databinding.FragmentAddBinding;
import com.example.matedicine.model.Medicine;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class Add extends Fragment implements TimePickerFragment.DialogTimeListener {

    private final MedicineDao medicineDao = new MedicineDao(); //interaksi realtime firebase untuk CRUD

    private ActivityResultLauncher<String> mGetContent; //memilih gambar dari penyimpanan device

    private String id, time, pills; //id tiap item recycle yg akan keluar

    private StorageReference storage;

    private Uri uriContainer , uriOld;

    private final StorageReference reference = FirebaseStorage.getInstance().getReference();

    private FirebaseStorage storageForDelete = FirebaseStorage.getInstance();
    private StorageReference fileRef;

    private final static String TIME_PICKER_ONCE_TAG = "TimePickerOnce";


    private FragmentAddBinding binding;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // mengubah XML layout ke dalam kode java
        binding = FragmentAddBinding.inflate(inflater, container, false);

        View view = binding.getRoot();

        storage = reference.child("img"+ new Date().getTime() + ".jpg"); //menetapkan lokasi dan format storage

        if (getArguments() != null){
            id = String.valueOf(getArguments().getSerializable("key"));
            Log.d("id", id);
            setupUpdateUI();
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setRequired();

        binding.radioGroup.setOnCheckedChangeListener((group, checkedId) -> {

            if (checkedId == R.id.op3){
                setupRadioButton("Before eat");
            }else if(checkedId == R.id.op2){
                setupRadioButton("While eat");
            }else if(checkedId == R.id.op5){
                setupRadioButton("After eat");
            }
        });

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    // Handle the returned Uri
                    if (uri != null) {
                        binding.dropFilesOrClickToUpload.setText(R.string.image_uploaded);
                        uriContainer = uri;
                    }
                });

        binding.btnBrowse.setOnClickListener(v -> importImage());

        if (id == null){
            binding.deleteButton.setVisibility(View.GONE);
        }else {
            binding.deleteButton.setVisibility(View.VISIBLE);
            binding.deleteButton.setOnClickListener(v -> showDeleteConfirmationDialog());
        }

        binding.btnBack.setOnClickListener(v -> navigatoToHome());
        binding.saveButton.setOnClickListener(v ->uploadFirebase());

        binding.cardView.setOnClickListener(v -> {
            TimePickerFragment timePickerFragmentOne = new TimePickerFragment();
            timePickerFragmentOne.setDialogTimeListener(this);
            timePickerFragmentOne.show(getChildFragmentManager(), TIME_PICKER_ONCE_TAG);
        });
    }

    private boolean setRequired(){
        String medicineName = binding.medicinename.getText().toString();
        String amount = binding.amount.getText().toString();
        String image;
        if (uriContainer == null){
            if (uriOld == null){
                image = "";
            }else{
                image = uriOld.toString();
            }
        }else {
            image = uriContainer.toString();
        }

        if (medicineName.isEmpty() || amount.isEmpty() || image.equals("") || time.isEmpty() || pills.isEmpty()){
            return false;
        }
        else {
            return true;
        }
    }

    private void deleteFromFirebase() {
        showLoading(true);
        medicineDao.delete(id).addOnSuccessListener(unused -> {
            fileRef = storageForDelete.getReferenceFromUrl(uriOld.toString());
            fileRef.delete();
            showLoading(false);
            navigatoToHome();
        });
    }

    private void importImage(){
        mGetContent.launch("image/*");
    }

    private void uploadFirebase(){
            if (setRequired()){
                showLoading(true);
                if (id != null){
                    if (uriContainer == null){
                        updateData();
                    }else {
                        fileRef = storageForDelete.getReferenceFromUrl(uriOld.toString());
                        fileRef.delete().addOnSuccessListener(unused -> {
                            showLoading(false);
                            deleteFromFirebase();
                            uploadImageToStorage();
                        });

                    }

                }else {
                    uploadImageToStorage();
                    navigatoToHome();
                }
            }else {
                Toast.makeText(requireContext(), "Make sure all sections are filled", Toast.LENGTH_SHORT).show();
            }


    }


    private void updateData(){
        Medicine medicine = new Medicine(uriOld.toString(), binding.medicinename.getText().toString(), time, pills, binding.amount.getText().toString());

        medicineDao.update(id, medicine).addOnSuccessListener(unused -> navigatoToHome()); //pembaharuan berdasarkan id
    }

    private void uploadImageToStorage() {
        storage.putFile(uriContainer).addOnSuccessListener(taskSnapshot ->
                storage.getDownloadUrl().addOnSuccessListener(uri -> {
                showLoading(false);
                Medicine medicine = new Medicine(uri.toString(), binding.medicinename.getText().toString(), time, pills, binding.amount.getText().toString());
                medicineDao.insert(medicine);
                Log.d("upload", "upload successful");
        }));
    }

    private void setupUpdateUI(){
        medicineDao.getall(id).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Medicine medicine = task.getResult().getValue(Medicine.class);
                if (medicine != null){
                    binding.medicinename.setText(medicine.getTitle());
                    binding.amount.setText(medicine.getAmount());
                    setupRadioButton(medicine.getPills());
                    binding.tvTime.setText(medicine.getTime());
                    time = medicine.getTime();
                    uriOld = Uri.parse(medicine.getImage());
                    binding.dropFilesOrClickToUpload.setText(R.string.change_photo);
                    binding.saveButton.setText(R.string.update);
                }
            }else{
                binding.deleteButton.setVisibility(View.GONE);
            }
        });
    }

    private void navigatoToHome() {
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_add2_to_home2);
    }

    private void setupRadioButton(String name){
        switch (name) {
            case "Before eat":
                binding.op3.setChecked(true);
                pills = name;
                break;
            case "While eat":
                binding.op2.setChecked(true);
                pills = name;
                break;
            case "After eat":
                binding.op5.setChecked(true);
                pills = name;
                break;
        }
    }

    private void showLoading(boolean b) {
        if (b){
            binding.progressBar.setVisibility(View.VISIBLE);
        }else{
            binding.progressBar.setVisibility(View.GONE);
        }
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Delete Confirmation");
        builder.setMessage("Are you sure want to delete the data?");

        // Tombol Ya
        builder.setPositiveButton("Yes", (dialog, id) -> {
            // Aksi untuk menghapus data
            deleteFromFirebase();
        });

        // Tombol Tidak
        builder.setNegativeButton("No", (dialog, id) -> {
            // Pengguna membatalkan dialog, tidak melakukan apa-apa
            dialog.dismiss();
        });

        // Membuat dan menampilkan AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    public void onDialogTimeSet(String tag, int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        time = dateFormat.format(calendar.getTime());
        binding.tvTime.setText(dateFormat.format(calendar.getTime()));
    }
}