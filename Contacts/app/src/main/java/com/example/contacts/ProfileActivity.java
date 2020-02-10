package com.example.contacts;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import net.glxn.qrgen.android.QRCode;

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
}
