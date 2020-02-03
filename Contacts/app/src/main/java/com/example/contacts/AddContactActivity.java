package com.example.contacts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import java.util.ArrayList;

public class AddContactActivity extends AppCompatActivity {
    private ArrayAdapter<String> aa;
    private ArrayList<String> todoItems;
    private ContactsDbAdapter NDBA;
    private int mNoteNumber = 1;
    private int listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_contact);
        NDBA= new ContactsDbAdapter(this);
        NDBA.open();
    }
    public void createContact(View view) {
        EditText mavariableEditText;
        mavariableEditText= (EditText) findViewById(R.id.editText_prenom);
        String prenom = mavariableEditText.getText().toString();
        mavariableEditText= (EditText) findViewById(R.id.editText_Nom);
        String nom = mavariableEditText.getText().toString();
        mavariableEditText= (EditText) findViewById(R.id.edittext_email);
        String email = mavariableEditText.getText().toString();
        mavariableEditText= (EditText) findViewById(R.id.edittext_adresse);
        String adresse = mavariableEditText.getText().toString();
        mavariableEditText= (EditText) findViewById(R.id.edittext_Tel);
        String tel = mavariableEditText.getText().toString();
        NDBA.createContact(prenom,nom,email,tel,adresse,"0");
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }
    public void onBackPressed(){
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }

}
