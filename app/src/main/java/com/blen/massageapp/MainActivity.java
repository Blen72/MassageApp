package com.blen.massageapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG=MainActivity.class.getName();
    private static final String PACKAGE_NAME=MainActivity.class.getPackage().toString();

    private EditText emailET;
    private EditText passwordET;
    private SharedPreferences sharedPreferences;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences=getSharedPreferences(PACKAGE_NAME, MODE_PRIVATE);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });*/
        emailET=findViewById(R.id.emailAddress);
        passwordET=findViewById(R.id.password);
        firebaseAuth=FirebaseAuth.getInstance();
    }

    public void login(View view) {
        String email=emailET.getText().toString();
        String password=passwordET.getText().toString();
        if(email.isEmpty()||password.isEmpty())return;
        MainActivity dis=this;
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.i(LOG_TAG, "Bejelentkezve a "+email+" email címmel.");
                    Intent loginIntent=new Intent(dis, MassageActivity.class);
                    startActivity(loginIntent);
                } else {
                    Toast.makeText(dis.getApplicationContext(), "Sikertelen belépés: "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void register(View view) {
        Intent intent=new Intent(this, RegisterActivity.class);
        intent.putExtra("KEY", 642013729);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("email",emailET.getText().toString())
                .putString("password",passwordET.getText().toString());
        editor.apply();
    }
}