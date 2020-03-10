/* Membres du groupes :
        Mohamed Takhchi - Noé Perez - Mohammed Lamtaoui
 */
package com.example.contacts;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ContactsDbAdapter {
    public static final String KEY_ROWID = "_id";
    public static final String KEY_PRENOM = "prenom";
    public static final String KEY_NOM = "nom";
    public static final String KEY_TEL = "tel";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_POSTALE = "postale";
    public static final String KEY_FAVORIS = "isfavoris";
    public static final String KEY_NAME = "name";

    private static final String TAG = "ContactsDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    /**
     * Requête de la création de la base de données
     */
    private static final String DATABASE_CREATE =
            "create table contacts (_id integer primary key autoincrement, "
                    + "prenom text not null, nom text not null,name text not null, email text not null, tel text not null, postale text not null, isfavoris integer default 0);";

    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "contacts";
    private static final int DATABASE_VERSION = 2;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS contacts");
            onCreate(db);
        }
    }

    /**
     * Constructeur qui permet a la base de données de s'ouvrir/se créer
     * @param ctx the Context within which to work
     */
    public ContactsDbAdapter(Context ctx)
    {
        this.mCtx = ctx;
    }

    /**
     * Ouvrir la base de données Contacts, sinon créer une nouvelle
     * Puis l'instancier
     */
    public ContactsDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    /**
     * Fermer la base de données
     */
    public void close() {
        mDbHelper.close();
    }


    /**
     * Ajouter un nouveau contact
     */
    public long createContact(String prenom, String nom,String email, String tel,String postale,String isfavoris) {
        String name=nom+" "+prenom;
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_PRENOM,prenom);
        initialValues.put(KEY_NOM, nom);
        initialValues.put(KEY_TEL, tel);
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_EMAIL, email);
        initialValues.put(KEY_POSTALE,postale);
        initialValues.put(KEY_FAVORIS,isfavoris);

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    /**
     * Supprimer tous les Contacts
     */
    public boolean deleteAllContacts() {
        mDb.execSQL("delete from contacts");
        return true;
    }

    /**
     * Supprimer un Contact en passant son id en parametre
     */
    public boolean deleteContact(long rowId) {
        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Retourner un Curseur contenant tous les Contacts de la BD
     */
    public Cursor fetchAllContacts() {
        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_PRENOM,
                KEY_NOM,KEY_NAME,KEY_EMAIL,KEY_POSTALE,KEY_TEL,KEY_FAVORIS}, null, null, null, null, KEY_NOM+","+KEY_PRENOM);
    }

    /**
     * Retourner un Curseur contenant tous les Contacts Favoris de la BD
     */
    public Cursor fetchAllFavoritesContacts() {
        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_PRENOM,
                KEY_NOM,KEY_NAME,KEY_EMAIL,KEY_POSTALE,KEY_TEL,KEY_FAVORIS}, KEY_FAVORIS + "=" + 1, null, null, null, KEY_NOM+","+KEY_PRENOM);
    }

    /**
     * Retourner un Curseur correspondant au Contact dont l'id est passé en paramétres
     */
    public Cursor fetchContact(long rowId) throws SQLException {

        Cursor mCursor =

                mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_PRENOM,
                                KEY_NOM,KEY_NAME,KEY_EMAIL,KEY_POSTALE,KEY_TEL,KEY_FAVORIS}, KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    /**
     * Modifier les informations d'un Contact
     */
    public boolean updateContact(long rowId, String prenom, String nom, String tel, String email, String postal) {
        String name=nom+" "+prenom;
        ContentValues args = new ContentValues();
        args.put(KEY_PRENOM, prenom);
        args.put(KEY_NOM, nom);
        args.put(KEY_NAME,name);
        args.put(KEY_TEL, tel);
        args.put(KEY_EMAIL, email);
        args.put(KEY_POSTALE, postal);

        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Mettre un Conract dans les favoris en mettant le champ isfavoris = 1
     */
    public boolean setFavoris(long rowId) {
        ContentValues args = new ContentValues();
        args.put(KEY_FAVORIS, 1);

        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Supprimer un Conract du favoris en mettant le champ isfavoris = 0
     */
    public boolean setDefavoris(long rowId) {
        ContentValues args = new ContentValues();
        args.put(KEY_FAVORIS, 0);

        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
}
