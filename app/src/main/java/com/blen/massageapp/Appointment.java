package com.blen.massageapp;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Appointment {
    private Date appointmentDate;
    private String email;
    private String id;

    public Appointment() {
    }

    public Appointment(Date appointmentDate, String email, String id) {
        this(appointmentDate, email);
        this.id = id;
    }

    public Appointment(Date appointmentDate, String email) {
        this.appointmentDate = appointmentDate;
        this.email=email;
    }

    public Date getAppointmentDate() {
        return appointmentDate;
    }

    public String getEmail() {
        return email;
    }

    public String _getAppointmentText(){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf=new SimpleDateFormat("yyyy. MM. dd. HH");
        return sdf.format(appointmentDate);
    }

    public void setId(String id) {
        this.id=id;
    }

    public String _getId() {
        return id;
    }
}
