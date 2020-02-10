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
    private static final boolean TODO = false;
    private ArrayAdapter<String> aa;
    private ArrayList<String> todoItems;
    private ContactsDbAdapter NDBA;
    private int mNoteNumber = 1;
    private int listView;

    private ListView listContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        NDBA = new ContactsDbAdapter(this);
        NDBA.open();
        fillData();

        listContacts = (android.widget.ListView)findViewById(R.id.listContacts);
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void fillData() {
        ListView mavariableListView = (ListView) findViewById(R.id.listContacts);
        // Get all of the notes from the database and create the item list
        Cursor c = NDBA.fetchAllContacts();
        startManagingCursor(c);
        Toast.makeText(getApplicationContext(), ContactsDbAdapter.KEY_PRENOM, Toast.LENGTH_SHORT).show();
        String[] from = new String[]{ContactsDbAdapter.KEY_NAME};
        int[] to = new int[]{R.id.text1};
        // TextView textView = (TextView) findViewById(R.id.text1);
        //textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ajouter_background, 0, 0, 0);
        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter Contacts =
                new SimpleCursorAdapter(this, R.layout.list_view, c, from, to);
        mavariableListView.setAdapter(Contacts);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, v.getId(), 0, "Profil");
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

        if (item.getTitle() == "Profil") {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        }
        else if(item.getTitle() == "Appeler")
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

        }
        return super.onContextItemSelected(item);
    }



}
