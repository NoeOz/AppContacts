/* Membres du groupes :
        Mohamed Takhchi - Noé Perez - Mohammed Lamtaoui
 */

package com.example.contacts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.webkit.URLUtil;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

import static java.security.AccessController.getContext;

public class addContactQRActivity extends AppCompatActivity {

    private CameraSource cameraSource;
    private SurfaceView cameraView;
    private final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private String token = "";
    private String tokenanterior = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact_qr);

        /**
         * Tester si l'utilisateur a donné son permission pour utiliser la Caméra sinon on le redemande
         */
        if (ContextCompat.checkSelfPermission(addContactQRActivity.this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(addContactQRActivity.this, new String[] {Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
        }

        cameraView = (SurfaceView) findViewById(R.id.camera_view);

        /**
         * la fonction qui permet la lecture du QRCode
         */
        initQR();
    }

    public void initQR() {

        // creation detecteur QR
        BarcodeDetector barcodeDetector =
                new BarcodeDetector.Builder(this)
                        .setBarcodeFormats(Barcode.ALL_FORMATS)
                        .build();

        // creation de l'apareil photo
        cameraSource = new CameraSource
                .Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1600, 1024)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();


        // listener de l'apareil photo
        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

                // verifier les permisions d'user pour utiliser l'apareil photo si oui on crée notre caméra
                if (ContextCompat.checkSelfPermission(addContactQRActivity.this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_DENIED){
                    ActivityCompat.requestPermissions(addContactQRActivity.this, new String[] {Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
                } else {
                    try {
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException ie) {
                        Log.e("CAMERA SOURCE", ie.getMessage());
                    }
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        // Preparation de detecteur QR
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }


            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() > 0) {

                    // obtnir le token
                    token = barcodes.valueAt(0).displayValue.toString();

                    // verification de token
                    if (!token.equals(tokenanterior)) {

                        // eregistrer le dernier token
                        tokenanterior = token;
                        Log.i("token", token);

                        /**
                         * On a enregistrer les données dans le QRCode en séparant entre les champs par :
                         * Donc pour avoir nos information on fait un split a la chaine obtenu pour avoir tous les champs
                         * On passe ces champs en paramétre pour ouvrir une nouvelle activity qui nous permet d'ajouter
                         * un nouveau contact a partir de ces champs
                         */
                        String []valeurs = token.split(":");
                        Intent intent = new Intent(addContactQRActivity.this, AddContactActivity.class);
                        intent.putExtra("nomContact",valeurs[0]);
                        intent.putExtra("prenomContact",valeurs[1]);
                        intent.putExtra("telContact",valeurs[2]);
                        intent.putExtra("emailContact",valeurs[3]);
                        intent.putExtra("adresseContact",valeurs[4]);
                        startActivity(intent);

                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    synchronized (this) {
                                        wait(5000);
                                        // limpiamos el token
                                        tokenanterior = "";
                                    }
                                } catch (InterruptedException e) {
                                    // TODO Auto-generated catch block
                                    Log.e("Error", "Waiting didnt work!!");
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    }
                }
            }
        });

    }
}
