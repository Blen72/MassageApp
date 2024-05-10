package com.blen.massageapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.ktx.Firebase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MassageActivity extends AppCompatActivity {
    private static final String LOG_TAG=MainActivity.class.getName();
    private ScrollView scrollAppointments;
    private ArrayList<Appointment> appointments=new ArrayList<>();
    private FirebaseAuth fbAuth;
    private FirebaseUser fbUser;
    private FirebaseFirestore fbFirestore;
    private CollectionReference collectionRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_massage);
        fbAuth=FirebaseAuth.getInstance();
        fbUser=fbAuth.getCurrentUser();
        if(fbUser==null)finish();
        scrollAppointments=findViewById(R.id.scrollAppointments);
        /*appointments.add(new Appointment(new Date(),"b@b.com","1"));
        appointments.add(new Appointment(new Date(2036-1900,7,12),"b@b.com","2"));
        appointments.add(new Appointment(new Date(2036-1900,7,12),"b@b.com",3));
        appointments.add(new Appointment(new Date(2036-1900,7,12),"b@b.com",4));
        appointments.add(new Appointment(new Date(2036-1900,7,12),"b@b.com",4));
        appointments.add(new Appointment(new Date(2036-1900,7,12),"b@b.com",4));
        appointments.add(new Appointment(new Date(2036-1900,7,12),"b@b.com",4));
        appointments.add(new Appointment(new Date(2036-1900,7,12),"b@b.com",4));
        appointments.add(new Appointment(new Date(2036-1900,7,12),"b@b.com",4));
        appointments.add(new Appointment(new Date(2036-1900,7,12),"b@b.com",4));
        appointments.add(new Appointment(new Date(2036-1900,7,12),"b@b.com",4));
        appointments.add(new Appointment(new Date(2036-1900,7,12),"b@b.com",4));
        appointments.add(new Appointment(new Date(2036-1900,7,12),"b@b.com",4));
        appointments.add(new Appointment(new Date(2036-1900,7,12),"b@b.com",4));
        appointments.add(new Appointment(new Date(2036-1900,7,12),"b@b.com",4));
        appointments.add(new Appointment(new Date(2036-1900,7,12),"b@b.com",4));
        appointments.add(new Appointment(new Date(2036-1900,7,12),"b@b.com",4));
        appointments.add(new Appointment(new Date(2036-1900,7,12),"b@b.com",4));
        appointments.add(new Appointment(new Date(2036-1900,7,12),"b@b.com",4));
        appointments.add(new Appointment(new Date(2036-1900,7,12),"b@b.com",4));*/
        fbFirestore=FirebaseFirestore.getInstance();
        collectionRef=fbFirestore.collection("idopontok");
    }

    public void getAppointments(){
        appointments.clear();
        //Log.i(LOG_TAG, "getAppointments: email="+fbUser.getEmail()+".");
        collectionRef.whereEqualTo("email",fbUser.getEmail()).orderBy("appointmentDate").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for(QueryDocumentSnapshot sq: queryDocumentSnapshots){
                Appointment appointment=sq.toObject(Appointment.class);
                appointment.setId(sq.getId());
                appointments.add(appointment);
                //Log.i(LOG_TAG, "getAppointments: appointments.length="+appointments.size()+". Added "+appointment._getAppointmentText()+".");
            }
            addAppointments();
        });
        //Log.i(LOG_TAG, "getAppointments: appointments.length="+appointments.size()+" after addition.");
    }

    private void addAppointments(){
        //Log.i(LOG_TAG, "addAppointments: starting with lenght of "+appointments.size()+".");
        long animOffset=0;
        for(Appointment a: appointments){
            View v=LayoutInflater.from(this.getApplicationContext()).inflate(R.layout.appointment_item,scrollAppointments,false);
            TextView et=v.findViewById(R.id.appointmentText);
            et.setText(et.getText().toString()+a._getAppointmentText()+":00");
            v.findViewById(R.id.delAppointment).setOnClickListener(v1 -> {
                onClickDelete(a);
            });
            v.findViewById(R.id.plush).setOnClickListener(v1 -> {
                onUpdateHour(a, 1);
            });
            v.findViewById(R.id.minush).setOnClickListener(v1 -> {
                onUpdateHour(a, -1);
            });
            ((LinearLayout)scrollAppointments.findViewById(R.id.lin)).addView(v);
            Animation animation=AnimationUtils.loadAnimation(this.getApplicationContext(),R.anim.slide);
            animation.setStartOffset(animOffset);
            animOffset+=100;
            v.startAnimation(animation);
        }
        //Log.i(LOG_TAG, "addAppointments: ended with lenght of "+appointments.size()+".");
    }

    private void onClickDelete(Appointment appointment){
        MassageActivity dis=this;
        collectionRef.document(appointment._getId()).delete().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                clearScrollView();
                getAppointments();
            } else {
                Toast.makeText(dis.getApplicationContext(), "Az időpont törlése sikertelen!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void onUpdateHour(Appointment appointment, int delteHour){
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(appointment.getAppointmentDate());
        calendar.add(Calendar.HOUR_OF_DAY, delteHour);
        Date newDate=calendar.getTime();
        Toast errmsg=Toast.makeText(this.getApplicationContext(), "Az időpont módosítása sikertelen!", Toast.LENGTH_LONG);
        collectionRef.get().addOnSuccessListener(queryDocumentSnapshots->{
            //Van-e már ilyen időpont
            for(QueryDocumentSnapshot qs: queryDocumentSnapshots){
                Appointment a=qs.toObject(Appointment.class);
                if(a.getAppointmentDate().equals(newDate)){
                    errmsg.show();
                    return;
                }
            }
            //Frissítés
            collectionRef.document(appointment._getId()).update("appointmentDate", newDate).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    clearScrollView();
                    getAppointments();
                } else {
                    errmsg.show();
                }
            });
        });

    }

    public void newAppointment(View view) {
        Intent intent=new Intent(this, AppointmentActivity.class);
        intent.putExtra("KEY", 98869);
        startActivity(intent);
    }

    private void clearScrollView(){
        ((LinearLayout)scrollAppointments.findViewById(R.id.lin)).removeAllViews();
    }

    @Override
    protected void onPause() {
        super.onPause();
        clearScrollView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAppointments();
    }
}