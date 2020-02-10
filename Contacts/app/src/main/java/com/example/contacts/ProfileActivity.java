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

import java.util.Date;


public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        String texto = "El contenido del código QR";
        Bitmap bitmap = QRCode.from(texto).bitmap();
        // Suponiendo que tienes un ImageView con el id ivCodigoGenerado
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
        dialPhoneNumber("01212121212");
    }

    public void mapscontact(View view)
    {
        Uri location = Uri.parse("geo:0,0?q=" + "datos");
        Intent mapintent = new Intent(Intent.ACTION_VIEW,location);
        startActivity(mapintent);
    }

    public void message(View view)
    {
        Uri sms_uri = Uri.parse("smsto:"+"08555555555");
        Intent sms_intent = new Intent(Intent.ACTION_SENDTO, sms_uri);
        startActivity(sms_intent);
    }

    public void envemail(View view)
    {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto","correo@gmail.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Android APP - ");
        startActivity(emailIntent);
    }
}
