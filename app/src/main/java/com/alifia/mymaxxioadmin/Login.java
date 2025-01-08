package com.alifia.mymaxxioadmin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;

public class Login extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(Login.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Silakan tunggu...");
        progressDialog.setCancelable(false);

        etEmail = findViewById(R.id.email);
        etPassword = findViewById(R.id.password);

        btnLogin = findViewById(R.id.button_masuk);
        btnLogin.setOnClickListener(v -> {
            if (etEmail.getText().length()>0 && etPassword.getText().length()>0){
                login(etEmail.getText().toString(), etPassword.getText().toString());
            } else if (etEmail.getText().length()==0) {
                etEmail.setError("Email tidak boleh kosong!");
            } else if (etPassword.getText().length()==0) {
                etPassword.setError("Password tidak boleh kosong!");
            }
        });
    }

    private void login(String email, String password) {
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful() && task.getResult()!=null){
                    if(task.getResult().getUser()!=null){
                        reload();
                    }else{
                        Toast.makeText(getApplicationContext(), "Email atau password salah!", Toast.LENGTH_SHORT).show();
                        progressDialog.hide();
                    }
                }else{
                    // mengecek alasan login gagal
                    Exception e = task.getException();
                    if (e instanceof FirebaseAuthInvalidCredentialsException){
                        // untuk password salah
                        Toast.makeText(getApplicationContext(), "Password Anda salah", Toast.LENGTH_SHORT).show();
                        progressDialog.hide();
                    }
                }
            }
        });
    }

    private void reload() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}