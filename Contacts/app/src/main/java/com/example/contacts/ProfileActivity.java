package com.example.contacts;

import androidx.appcompat.app.AppCompatActivity;

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
        NDBA= new ContactsDbAdapter(this);
        NDBA.open();

        viewName = (TextView)findViewById(R.id.fullName);
        viewTel = (TextView)findViewById(R.id.tel);
        viewEmail = (TextView)findViewById(R.id.email);
        viewPostal = (TextView)findViewById(R.id.adresse);

        viewFavorits = (ImageView)findViewById(R.id.favorits);

        Intent intent = getIntent();
        if (intent.hasExtra("idContact")){
            idContact = intent.getLongExtra("idContact",0);
        }
        Cursor contact = NDBA.fetchContact(idContact);
        nom = contact.getString(2);
        prenom = contact.getString(1);
        tel = contact.getString(6);
        email = contact.getString(4);
        postal = contact.getString(5);
        isFavoris = contact.getInt(7);

        QRCodeText = nom+":"+(prenom.equals("") ? "NULL" : prenom)+":"+tel+":"+(email.equals("") ? "NULL" : email)+":"+(postal.equals("") ? "NULL" : postal);


        viewName.setText(nom+" "+prenom);
        viewTel.setText(tel);
        viewEmail.setText(email);
        viewPostal.setText(postal);
        if(isFavoris == 0)
            viewFavorits.setImageResource(R.mipmap.ic_starvide_foreground);
        else
            viewFavorits.setImageResource(R.mipmap.ic_star_foreground);


        Bitmap bitmap = QRCode.from(QRCodeText).bitmap();
        ImageView imagenCodigo = findViewById(R.id.codeQR);
        imagenCodigo.setImageBitmap(bitmap);
    }

    
    public void dialPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
    public void appeler(View view){
        dialPhoneNumber(tel);
    }

    public void mapscontact(View view)
    {
        Uri location = Uri.parse("geo:0,0?q=" + postal);
        Intent mapintent = new Intent(Intent.ACTION_VIEW,location);
        startActivity(mapintent);
    }

    public void message(View view)
    {
        Uri sms_uri = Uri.parse("smsto:" + tel);
        Intent sms_intent = new Intent(Intent.ACTION_SENDTO, sms_uri);
        startActivity(sms_intent);
    }

    public void envemail(View view)
    {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",email, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Android APP - ");
        startActivity(emailIntent);
    }

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

    public void modifier(View view){
        Intent intent = new Intent(ProfileActivity.this, AddContactActivity.class);
        intent.putExtra("idContact",idContact);
        startActivity(intent);
    }
}
