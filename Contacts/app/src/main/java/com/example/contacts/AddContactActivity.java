/* Membres du groupes :
        Mohamed Takhchi - Noé Perez - Mohammed Lamtaoui
 */

package com.example.contacts;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
    /**
     * cette variable est fait pour savoir si on ouvre cette activity pour ajouter ou pour modifier un contact
     * si ifModifier = 0 (le cas par defaut) c'est pour ajouter un nouveau contact
     * si ifModifier = 1 c'est pour modifier un contact
     */
    private int ifModifier = 0;
    Cursor contact = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_contact);
        //Ouverture de la base de données
        NDBA= new ContactsDbAdapter(this);
        NDBA.open();

        //Initialisation des views
        mavariableEditTextPrenom = (EditText) findViewById(R.id.editText_prenom);
        mavariableEditTextNom= (EditText) findViewById(R.id.editText_Nom);
        mavariableEditTextEmail= (EditText) findViewById(R.id.edittext_email);
        mavariableEditTextAdresse = (EditText) findViewById(R.id.edittext_adresse);
        mavariableEditTextTel= (EditText) findViewById(R.id.edittext_Tel);
        errorTel = (TextView) findViewById(R.id.fTel);
        errorEmail = (TextView) findViewById(R.id.fEmail);

        /**
         * dans le cas du modification on a passé l'id du contact en extras donc on essai de le récupére
         * on met ifModifier = 1 car on va modifier le contact dans ce cas
         * On le récupére aussi de la base de données en utilisant l'id et on rempli les views
         */
        Intent intent = getIntent();
        if (intent.hasExtra("idContact")){
            ifModifier = 1;
            contact = NDBA.fetchContact(intent.getLongExtra("idContact",0));
            mavariableEditTextPrenom.setText(contact.getString(1));
            mavariableEditTextNom.setText(contact.getString(2));
            mavariableEditTextEmail.setText(contact.getString(4));
            mavariableEditTextAdresse.setText(contact.getString(5));
            mavariableEditTextTel.setText(contact.getString(6));
        }

        /**
         * dans le cas d'ajout d'un nouveau contact par QRCode c'est le nomContact,prenomContact,emailContact,adresseContact et telContact qui sont passés en extras
         * on les récupére et les mettre dans les view correspondantes
         */
        if (intent.hasExtra("nomContact")){
            mavariableEditTextPrenom.setText(intent.getStringExtra("prenomContact").equals("NULL") ? "" : intent.getStringExtra("prenomContact"));
            mavariableEditTextNom.setText(intent.getStringExtra("nomContact"));
            mavariableEditTextEmail.setText(intent.getStringExtra("emailContact").equals("NULL") ? "" : intent.getStringExtra("emailContact"));
            mavariableEditTextAdresse.setText(intent.getStringExtra("adresseContact").equals("NULL") ? "" : intent.getStringExtra("adresseContact"));
            mavariableEditTextTel.setText(intent.getStringExtra("telContact"));
        }

        /**
         * cette fonction est fait pour tester si la format du numéro entré est correcte ou non il s'execute quand l'utilisateur est entrain d'entrer le numéro
         * la format est la suivante ; 10 chiffres commencant par 0 (0 inclus)
         * si la format est incorrecte un message en rouge va s'afficher au dessous du champ Numéro de Téléphone
         */
        mavariableEditTextTel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if((!(mavariableEditTextTel.getText().toString().matches("0[0-9]{9,9}"))) && !mavariableEditTextTel.getText().toString().equals(""))
                    errorTel.setText(getString(R.string.error_num));
                else{
                    errorTel.setText("");
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        /**
         * cette fonction est fait pour tester si la format de l'Email entré est correcte ou non il s'execute quand l'utilisateur est entrain d'entrer l'email
         * si la format est incorrecte un message en rouge va s'afficher au dessous du champ Email
         */
        mavariableEditTextEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if((!(mavariableEditTextEmail.getText().toString().matches("^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$")))  && !mavariableEditTextEmail.getText().toString().equals(""))
                    errorEmail.setText(getString(R.string.email_error));
                else{
                    errorEmail.setText("");
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    /**
     * Cette fonction permet d'ajouter ou modifier un contact
     * D'abord on teste si les champs Téléphone et Nom sont renseignés car ils sont obligatoires sinon on affiche un avertissement
     * Aprés on teste s'il y a une erreur dans l'email ou le numéro entré sinon on affiche un avertissement
     * Aprés on teste si pour modifier ou pour ajouter et on affectu l'opération correspondante
     */
    public void createContact(View view) {
        String prenom = mavariableEditTextPrenom.getText().toString();
        String nom = mavariableEditTextNom.getText().toString();
        String email = mavariableEditTextEmail.getText().toString();
        String adresse = mavariableEditTextAdresse.getText().toString();
        String tel = mavariableEditTextTel.getText().toString();
        if(nom.length()==0 || tel.length()==0){
            new AlertDialog.Builder(AddContactActivity.this)
                    .setTitle("Attention")
                    .setMessage(getString(R.string.error_champs))
                    .setPositiveButton("Ok",null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        else {
            if(errorTel.getText().toString().length()!=0 || errorEmail.getText().toString().length()!=0){
                new AlertDialog.Builder(AddContactActivity.this)
                        .setTitle("Attention")
                        .setMessage(getString(R.string.error_corr))
                        .setPositiveButton("Ok",null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
            else {
                if(ifModifier==0){
                    NDBA.createContact(prenom, nom, email, tel, adresse, "0");
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                    Toast.makeText(getApplicationContext(), getString(R.string.contact_add), Toast.LENGTH_SHORT).show();
                }
                else {
                    NDBA.updateContact(contact.getLong(0),prenom, nom, tel, email, adresse);
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                    Toast.makeText(getApplicationContext(), getString(R.string.contact_edit), Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    /**
     * cette fonction est faite juste pour fermer cette activity lorsque l'utilisateur clique sur le bouton de retour
     */
    public void onBackPressed(){
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }

    /**
     * cette fonction permet d'annuler l'ajout ou la modification du contact
     */
    public void cancelCreation(View view){
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }



}
