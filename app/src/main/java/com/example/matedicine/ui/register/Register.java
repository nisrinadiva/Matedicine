package com.example.matedicine.ui.register;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.matedicine.MainActivity;
import com.example.matedicine.R;
import com.example.matedicine.ui.login.Login;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

public class Register extends AppCompatActivity {
    EditText editTextEmail, editTextPassword, editTextUsername; // Mengubah TextInputEditText ke EditText
    Button buttonReg;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();

        // Inisialisasi elemen UI yang belum diinisialisasi
        editTextEmail = findViewById(R.id.Email);
        editTextPassword = findViewById(R.id.Pass);
        editTextUsername = findViewById(R.id.Username);
        buttonReg = findViewById(R.id.register);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.login);

        // kalo udah punya akun
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                String email = editTextEmail.getText().toString(); // Mengambil teks dari EditText
                String password = editTextPassword.getText().toString();
                String username = editTextUsername.getText().toString();

                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(Register.this, "Enter value in a field", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);

                }else {

                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBar.setVisibility(View.GONE);
                                    if (task.isSuccessful()) {
                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(username).build();
                                        if (mAuth.getCurrentUser() != null && profileUpdates.getDisplayName() != null) {
                                            mAuth.getCurrentUser().updateProfile(profileUpdates).addOnCompleteListener(job -> {
                                                if (job.isSuccessful()) {
                                                    // Update was successful
                                                    Log.d("user", "User profile updated.");
                                                    String updatedName = mAuth.getCurrentUser().getDisplayName();
                                                    Log.d("user", updatedName != null ? updatedName : "null");
                                                } else {
                                                    // Handle the failure
                                                    Log.d("user", "Failed to update user profile.");
                                                }
                                            });
                                        }

                                        Toast.makeText(Register.this, "Account created.", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Register.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(Register.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }
}
