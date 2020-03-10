/* Membres du groupes :
        Mohamed Takhchi - Noé Perez - Mohammed Lamtaoui
 */

package com.example.contacts;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import net.glxn.qrgen.android.QRCode;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.Date;


public class ProfileActivity extends AppCompatActivity {

    private long idContact;
    private String nom;
    private String prenom;
    private String tel;
    private String email;
    private String postal;
    private int isFavoris;
    private String QRCodeText;
    private ContactsDbAdapter NDBA;
    private TextView viewName;
    private TextView viewTel;
    private TextView viewEmail;
    private TextView viewPostal;
    private ImageView viewFavorits;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //Ouvrir la base de données
        NDBA= new ContactsDbAdapter(this);
        NDBA.open();

        //Initialiser le views de l'activity
        viewName = (TextView)findViewById(R.id.fullName);
        viewTel = (TextView)findViewById(R.id.tel);
        viewEmail = (TextView)findViewById(R.id.email);
        viewPostal = (TextView)findViewById(R.id.adresse);
        viewFavorits = (ImageView)findViewById(R.id.favorits);

        //Récupérer l'id du contact envoyé dans les extras de l'intent
        Intent intent = getIntent();
        if (intent.hasExtra("idContact")){
            idContact = intent.getLongExtra("idContact",0);
        }

        //Récupérer le contact de la base de données
        Cursor contact = NDBA.fetchContact(idContact);
        nom = contact.getString(2);
        prenom = contact.getString(1);
        tel = contact.getString(6);
        email = contact.getString(4);
        postal = contact.getString(5);
        isFavoris = contact.getInt(7);

        /**
         * Création du texte du QRCode
         * On le crée en mettant tous les champs du contact séparé par :
         * si un champs est vide en met NULL a sa place
         */
        QRCodeText = nom+":"+(prenom.equals("") ? "NULL" : prenom)+":"+tel+":"+(email.equals("") ? "NULL" : email)+":"+(postal.equals("") ? "NULL" : postal);


        //Mettre les données dans les TextView
        viewName.setText(nom+" "+prenom);
        viewTel.setText(tel);
        viewEmail.setText(email);
        viewPostal.setText(postal);
        if(isFavoris == 0)
            viewFavorits.setImageResource(R.mipmap.ic_starvide_foreground);
        else
            viewFavorits.setImageResource(R.mipmap.ic_star_foreground);


        /**
         * Création de l'image contenant le QRCode
         */
        Bitmap bitmap = QRCode.from(QRCodeText).bitmap();
        ImageView imagenCodigo = findViewById(R.id.codeQR);
        imagenCodigo.setImageBitmap(bitmap);
    }


    /**
     * Cette fonction permet d'ouvrir l'application pour appeler le contact
     */
    public void appeler(View view){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + tel));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    /**
     * Cette fonction permet a l'utilisateur d'ouvrir l'adresse du contact courant
     * On teste d'abord si l'adresse est renseigné
     * si oui on ouvre l'application maps dans l'adresse spécifié
     * sinon on informe l'utilisateur que le contact n'a pas d'adresse
     */
    public void mapscontact(View view)
    {
        if(postal.equals("")){
            new AlertDialog.Builder(ProfileActivity.this)
                    .setTitle("Attention")
                    .setMessage("Ce Contact n'a pas d'adresse veuillez l'ajouter afin d'effectuer cette operation")
                    .setPositiveButton("Ok",null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        else {
            Uri location = Uri.parse("geo:0,0?q=" + postal);
            Intent mapintent = new Intent(Intent.ACTION_VIEW, location);
            startActivity(mapintent);
        }
    }

    /**
     * Cette fonction permet d'ouvrir l'application pour envoyer un message au contact
     */
    public void message(View view)
    {
        Uri sms_uri = Uri.parse("smsto:" + tel);
        Intent sms_intent = new Intent(Intent.ACTION_SENDTO, sms_uri);
        startActivity(sms_intent);
    }

    /**
     * Cette fonction permet a l'utilisateur d'envoyer un email au contact courant
     * On teste d'abord si l'email est renseigné
     * si oui on ouvre l'application qui permet d'envoyer le mail
     * sinon on informe l'utilisateur que le contact n'a pas d'email
     */
    public void envemail(View view)
    {
        if(email.equals("")){
            new AlertDialog.Builder(ProfileActivity.this)
                    .setTitle("Attention")
                    .setMessage("Ce Contact n'a pas d'email veuillez l'ajouter afin d'effectuer cette operation")
                    .setPositiveButton("Ok",null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        else {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", email, null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Android APP - ");
            startActivity(emailIntent);
        }
    }

    /**
     * Cette fonction permet de mettre un contact soit dans les favoris ou non
     * Tout d'abord on teste si le contact courant est dans les favoris
     * si oui isFavoris = 1 on le supprime du favoris
     * si non isFavoris = 0 on l'ajoute aux favoris
     */
    public void setFavoris(View view){
        if(isFavoris == 0){
            NDBA.setFavoris(idContact);
            isFavoris=1;
            viewFavorits.setImageResource(R.mipmap.ic_star_foreground);
            Toast.makeText(getApplicationContext(), getString(R.string.confi_addfav), Toast.LENGTH_SHORT).show();
        }
        else{
            NDBA.setDefavoris(idContact);
            isFavoris=0;
            viewFavorits.setImageResource(R.mipmap.ic_starvide_foreground);
            Toast.makeText(getApplicationContext(), getString(R.string.conf_supfav), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Dans cette fonction on ouvre une nouvelle activity en passant l'id du contact en paramétre
     * l'id va nous aider a récupérer les données du contact dans l'autre activity pour qu'on puisse les modifier
     */
    public void modifier(View view){
        Intent intent = new Intent(ProfileActivity.this, AddContactActivity.class);
        intent.putExtra("idContact",idContact);
        startActivity(intent);
    }
}
