package com.blen.massageapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

public class AppointmentActivity extends AppCompatActivity {
    private TextView errorET;
    private EditText yearET;
    private EditText monthET;
    private EditText dayET;
    private EditText hourET;
    private Date aDate;
    private FirebaseAuth fbAuth;
    private FirebaseUser fbUser;
    private FirebaseFirestore fbFirestore;
    private CollectionReference collectionRef;


    private int editTextToInt(EditText et){
        return Integer.parseInt(et.getText().toString());
    }

    private boolean validateDate(){
        Date date = null;
        try {
            date=new Date(editTextToInt(yearET)-1900,editTextToInt(monthET)-1,editTextToInt(dayET),editTextToInt(hourET),0);
            errorET.setText("");
        } catch (NumberFormatException e){
            errorET.setText(R.string.date_format_error);
        }
        if(date==null)return false;
        return checkAvailable(date);
    }

    private boolean checkAvailable(Date date){
        AtomicBoolean isAvailable= new AtomicBoolean(true);
        collectionRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for(QueryDocumentSnapshot qs:queryDocumentSnapshots){
                Appointment a=qs.toObject(Appointment.class);
                if(a.getAppointmentDate().equals(date)){
                    isAvailable.set(false);
                    break;
                }
            }
        });
        if(!isAvailable.get()){
            errorET.setText(R.string.booked_appointment_error);
            return false;
        } else {
            errorET.setText("");
        }
        aDate=date;
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);
        Bundle bundle=getIntent().getExtras();
        if(bundle==null || bundle.getInt("KEY", 0)!=98869){
            finish();
        }
        fbAuth=FirebaseAuth.getInstance();
        fbUser=fbAuth.getCurrentUser();
        if(fbUser==null)finish();
        fbFirestore=FirebaseFirestore.getInstance();
        collectionRef=fbFirestore.collection("idopontok");

        yearET=findViewById(R.id.year);
        monthET=findViewById(R.id.month);
        dayET=findViewById(R.id.day);
        hourET=findViewById(R.id.hour);
        errorET=findViewById(R.id.errorText);

        TextWatcher tw=new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                validateDate();
            }
        };

        yearET.addTextChangedListener(tw);
        monthET.addTextChangedListener(tw);
        dayET.addTextChangedListener(tw);
        hourET.addTextChangedListener(tw);
    }

    public void cancel(View view) {
        finish();
    }

    public void addNewAppointment(View view) {
        if(!validateDate())return;
        collectionRef.add(new Appointment(aDate, fbUser.getEmail()));
        finish();
    }
}