/* Membres du groupes :
        Mohamed Takhchi - Noé Perez - Mohammed Lamtaoui
 */

package com.example.contacts;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static java.net.Proxy.Type.HTTP;

public class MainActivity extends AppCompatActivity {
    /**
     * Cette variable existe pour savoir le type de contenu qu'on va afficher
     * typeOfContent = 0 (par defaut) pour afficher tous les contacts et typeOfContent = 1 pour les favoris
     */
    private int typeOfContent = 0;
    private ContactsDbAdapter NDBA;
    private ListView listContacts;
    private final int MY_PERMISSIONS_REQUEST_CAMERA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);

        //Initialiser la ListView
        listContacts = (android.widget.ListView)findViewById(R.id.listContacts);
        setSupportActionBar(toolbar);

        //Ouvrire la base de données
        NDBA = new ContactsDbAdapter(this);
        NDBA.open();

        //Appeler la fonction qui permet de récupérer les données de la base et les affichés
        fillData(typeOfContent);


        registerForContextMenu(listContacts);
        FloatingActionButton fab = findViewById(R.id.fab);

        /**
         * Action qui permet d'ouvrir une nouvelle activity pour ajouter un nouveau contact
         */
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddContactActivity.class);
                startActivity(intent);
                finish();
            }
        });

        /**
         * C'est l'action d'un simple clique sur un contact
         * on affiche les information de ce contact dans une nouvelle activity en passant l'id en extras
         */
        listContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int  position, long id) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                intent.putExtra("idContact",id);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Cette fonction permet de réaliser quelques actions dans le menu en haut droit de l'activity
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //Afficher tous les contacts
        if (id == R.id.showAllContacts) {
            typeOfContent = 0 ;
            fillData(typeOfContent);
        }
        //Afficher juste les favoris
        if (id == R.id.showFavoritesContacts) {
            typeOfContent = 1 ;
            fillData(typeOfContent);
        }
        //Commencer une nouvelle activity qui permet d'ajouter un contact par QRCode
        if (id == R.id.addContactQR) {
            while (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
            }
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(MainActivity.this, addContactQRActivity.class);
                startActivity(intent);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Cette fonction permet de récupérer les données de la base
     * Tout d'abord on teste si on a besoin de tous les contacts ou juste les favoris
     * on appelle la fonction correspondante
     * on rempli la listView
     */
    private void fillData(int typeOfContent) {
        Cursor c = null;
        if(typeOfContent == 0)
            c = NDBA.fetchAllContacts();
        else
            c = NDBA.fetchAllFavoritesContacts();

        startManagingCursor(c);
        String[] from = new String[]{ContactsDbAdapter.KEY_NAME};
        int[] to = new int[]{R.id.text1};
        SimpleCursorAdapter Contacts =
                new SimpleCursorAdapter(this, R.layout.list_view, c, from, to);
        listContacts.setAdapter(Contacts);
    }

    /**
     * C'est le menu qui s'affiche lorsqu'on réalise un appuie long sur un contact
     */
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        Cursor contact = NDBA.fetchContact(info.id);
        menu.add(0, v.getId(), 0, getString(R.string.text_appel));
        menu.add(0, v.getId(), 0, getString(R.string.text_envmsg));
        //on teste si ce contact est dans les favoris pour l'y ajouter sinon l'y supprimer
        if (contact.getInt(7) == 0)
            menu.add(0, v.getId(), 0, getString(R.string.add_fav));
        else
            menu.add(0, v.getId(), 0, getString(R.string.sup_fav));
        menu.add(0, v.getId(), 0, getString(R.string.text_mod));
        menu.add(0, v.getId(), 0, getString(R.string.text_sup));

    }

    /**
     * Les actions du long appuie sur un contact
     */
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        /**
         * Cette fonction permet d'ouvrir l'application pour appeler le contact
         */
        if(item.getTitle() == getString(R.string.text_appel))
        {
            Cursor contact = NDBA.fetchContact(info.id);
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + contact.getString(6)));
            startActivity(intent);
        }
        /**
         * Cette fonction permet d'ouvrir l'application pour envoyer un message au contact
         */
        else if(item.getTitle() == getString(R.string.text_envmsg))
        {
            Cursor contact = NDBA.fetchContact(info.id);
            Uri sms_uri = Uri.parse("smsto:"+contact.getString(6));
            Intent sms_intent = new Intent(Intent.ACTION_SENDTO, sms_uri);
            startActivity(sms_intent);
        }
        /**
         * Cette fonction permet de supprimer un contact
         * on demande la confirmation avant de supprimer
         */
        else if(item.getTitle() == getString(R.string.text_sup))
        {
            final long contactToDelete = info.id;
            new AlertDialog.Builder(MainActivity.this)
                    .setMessage(getString(R.string.confir_supp))
                    .setTitle("Confirmation")
                    .setPositiveButton(getString(R.string.rep_oui), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            NDBA.deleteContact(contactToDelete);
                            Toast.makeText(getApplicationContext(), getString(R.string.text_suppcont), Toast.LENGTH_SHORT).show();
                            fillData(typeOfContent);
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
        /**
         * Cette fonction permet d'ajouter le contact aux favoris
         */
        else if(item.getTitle() == getString(R.string.add_fav))
        {
            NDBA.setFavoris(info.id);
            Toast.makeText(getApplicationContext(), getString(R.string.contact_add), Toast.LENGTH_SHORT).show();
            fillData(typeOfContent);
        }
        /**
         * Cette fonction permet de supprimer le contact du favoris
         */
        else if(item.getTitle() == getString(R.string.sup_fav))
        {
            NDBA.setDefavoris(info.id);
            Toast.makeText(getApplicationContext(), getString(R.string.conf_supfav), Toast.LENGTH_SHORT).show();
            fillData(typeOfContent);
        }
        /**
         * Cette fonction permet d'ouvrir une nouvelle activity pour modifier le contact en passant l'id en extras
         */
        else if(item.getTitle() == getString(R.string.text_mod))
        {
            Intent intent = new Intent(MainActivity.this, AddContactActivity.class);
            intent.putExtra("idContact",info.id);
            startActivity(intent);
        }
        return super.onContextItemSelected(item);
    }



}
