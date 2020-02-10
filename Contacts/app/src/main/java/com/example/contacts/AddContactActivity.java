package com.example.contacts;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class AddContactActivity extends AppCompatActivity {

    private ContactsDbAdapter NDBA;
    EditText mavariableEditTextPrenom;
    EditText mavariableEditTextNom;
    EditText mavariableEditTextEmail;
    EditText mavariableEditTextAdresse;
    EditText mavariableEditTextTel;
    TextView errorTel;
    TextView errorEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_contact);
        NDBA= new ContactsDbAdapter(this);
        NDBA.open();
        mavariableEditTextPrenom = (EditText) findViewById(R.id.editText_prenom);
        mavariableEditTextNom= (EditText) findViewById(R.id.editText_Nom);
        mavariableEditTextEmail= (EditText) findViewById(R.id.edittext_email);
        mavariableEditTextAdresse = (EditText) findViewById(R.id.edittext_adresse);
        mavariableEditTextTel= (EditText) findViewById(R.id.edittext_Tel);
        errorTel = (TextView) findViewById(R.id.fTel);
        errorEmail = (TextView) findViewById(R.id.fEmail);
        mavariableEditTextTel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!(mavariableEditTextTel.getText().toString().matches("0[0-9]{9,9}")))
                    errorTel.setText("Vous devez respecter la forme du Numéro");
                else{
                    errorTel.setText("");
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mavariableEditTextEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!(mavariableEditTextEmail.getText().toString().matches("^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$")))
                    errorEmail.setText("Email incorrecte ! exemple : qlqchose@qlqchose.qlqchose");
                else{
                    errorEmail.setText("");
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    public void createContact(View view) {


        String prenom = mavariableEditTextPrenom.getText().toString();
        String nom = mavariableEditTextNom.getText().toString();
        String email = mavariableEditTextEmail.getText().toString();
        String adresse = mavariableEditTextAdresse.getText().toString();
        String tel = mavariableEditTextTel.getText().toString();
        if(nom.length()==0 || tel.length()==0){
            new AlertDialog.Builder(AddContactActivity.this)
                    .setTitle("Attention")
                    .setMessage("Vous devez au moins renseigner les champs Nom et Numéro de téléphone !")

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton("Ok",null)

                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        else {
            if(errorTel.getText().toString().length()!=0 || errorEmail.getText().toString().length()!=0){
                new AlertDialog.Builder(AddContactActivity.this)
                        .setTitle("Attention")
                        .setMessage("Il y'a des erreur que vous devez corriger !")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton("Ok",null)

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
            else {
                NDBA.createContact(prenom, nom, email, tel, adresse, "0");
                startActivity(new Intent(this, MainActivity.class));
                finish();
                Toast.makeText(getApplicationContext(), "Contact ajouté", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void onBackPressed(){
        startActivity(new Intent(this,MainActivity.class));
        finish();
        Toast.makeText(getApplicationContext(),"Rien ajouté",Toast.LENGTH_SHORT).show();
    }
    public void setImage(View view)
    {
        Toast.makeText(getApplicationContext(),"En developpement",Toast.LENGTH_SHORT).show();
    }

    public void cancelCreation(View view){
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }



}
