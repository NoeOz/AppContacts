package com.example.contacts;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
    //0 pour afficher tous les contacts et 1 pour les favoris
    private int typeOfContent = 0;
    private ContactsDbAdapter NDBA;
    private int mNoteNumber = 1;
    private int listView;

    private ListView listContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        listContacts = (android.widget.ListView)findViewById(R.id.listContacts);
        setSupportActionBar(toolbar);
        NDBA = new ContactsDbAdapter(this);
        NDBA.open();
        fillData(typeOfContent);

        registerForContextMenu(listContacts);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                Intent intent = new Intent(MainActivity.this, AddContactActivity.class);
                startActivity(intent);
                finish();
            }
        });
        listContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int  position, long id) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                intent.putExtra("IdContact",id);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.showAllContacts) {
            typeOfContent = 0 ;
            fillData(typeOfContent);
        }
        if (id == R.id.showFavoritesContacts) {
            typeOfContent = 1 ;
            fillData(typeOfContent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void fillData(int typeOfContent) {
        //ListView mavariableListView = (ListView) findViewById(R.id.listContacts);
        // Get all of the notes from the database and create the item list
        Cursor c = null;
        if(typeOfContent == 0)
            c = NDBA.fetchAllContacts();
        else
            c = NDBA.fetchAllFavoritesContacts();

        startManagingCursor(c);
        //Toast.makeText(getApplicationContext(), ContactsDbAdapter.KEY_PRENOM, Toast.LENGTH_SHORT).show();
        String[] from = new String[]{ContactsDbAdapter.KEY_NAME};
        int[] to = new int[]{R.id.text1};
        // TextView textView = (TextView) findViewById(R.id.text1);
        //textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ajouter_background, 0, 0, 0);
        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter Contacts =
                new SimpleCursorAdapter(this, R.layout.list_view, c, from, to);
        listContacts.setAdapter(Contacts);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, v.getId(), 0, "Appeler");
        menu.add(0, v.getId(), 0, "Envoyer une message");
        menu.add(0, v.getId(), 0, "Suprimer");
        menu.add(0, v.getId(), 0, "Ajouter aux favorits");
    }

    @RequiresApi(api = Build.VERSION_CODES.M)

    public void dialPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        if(item.getTitle() == "Appeler")
        {
            dialPhoneNumber("0627999076");
        }
        else if(item.getTitle() == "Envoyer une message")
        {

        }
        else if(item.getTitle() == "Suprimer")
        {

        }
        else//favorits
        {
            NDBA.setFavoris(info.id);
            Toast.makeText(getApplicationContext(), "Contact ajouté aux Favoris", Toast.LENGTH_SHORT).show();
        }
        return super.onContextItemSelected(item);
    }



}
