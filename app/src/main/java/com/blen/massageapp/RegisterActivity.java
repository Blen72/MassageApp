package com.blen.massageapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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

public class RegisterActivity extends AppCompatActivity {
    private static final String PACKAGE_NAME=MainActivity.class.getPackage().toString();
    private EditText emailET;
    private EditText passwordET;
    private EditText passwordAgainET;
    private SharedPreferences sharedPreferences;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Bundle bundle=getIntent().getExtras();
        if(bundle==null || bundle.getInt("KEY", 0)!=642013729){
            finish();
        }
        sharedPreferences=getSharedPreferences(PACKAGE_NAME, MODE_PRIVATE);
        emailET=findViewById(R.id.emailAddress);
        passwordET=findViewById(R.id.password);
        passwordAgainET=findViewById(R.id.passwordAgain);
        emailET.setText(sharedPreferences.getString("email",""));
        passwordET.setText(sharedPreferences.getString("password",""));
        firebaseAuth=FirebaseAuth.getInstance();
    }

    public void register(View view) {
        String password=passwordET.getText().toString();
        String passwordAgain=passwordAgainET.getText().toString();
        String email=emailET.getText().toString();
        if(password.isEmpty()||passwordAgain.isEmpty()||email.isEmpty())return;
        if(!password.equals(passwordAgain)){
            Toast.makeText(this.getApplicationContext(), "A két jelszó nem egyezik meg!",Toast.LENGTH_SHORT).show();
            return;
        }
        RegisterActivity dis=this;
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>(){
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent loginIntent=new Intent(dis, MainActivity.class);
                    startActivity(loginIntent);
                } else {
                    Toast.makeText(dis.getApplicationContext(), "Sikertelen regisztráció: "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}