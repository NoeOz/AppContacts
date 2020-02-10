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
     * Database creation sql statement
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
     * Constructor - takes the context to allow the database to be
     * opened/created
     *
     * @param ctx the Context within which to work
     */
    public ContactsDbAdapter(Context ctx)
    {
        this.mCtx = ctx;
    }

    /**
     * Open the notes database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     *
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public ContactsDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }


    /**
     * Create a new note using the title and body provided. If the note is
     * successfully created return the new rowId for that note, otherwise return
     * a -1 to indicate failure.
     *
     * param title the title of the note
     * param body the body of the note
     * return rowId or -1 if failed
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
     * Delete the note with the given rowId
     *
     * param rowId id of note to delete
     * return true if deleted, false otherwise
     */
    public boolean deleteAllContacts() {
        mDb.execSQL("delete from contacts");
        return true;
    }

    public boolean deleteContact(long rowId) {
        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all notes in the database
     *
     * @return Cursor over all notes
     */
    public Cursor fetchAllContacts() {
        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_PRENOM,
                KEY_NOM,KEY_NAME,KEY_EMAIL,KEY_POSTALE,KEY_TEL,KEY_FAVORIS}, null, null, null, null, KEY_NOM+","+KEY_PRENOM);
    }

    public Cursor fetchAllFavoritesContacts() {
        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_PRENOM,
                KEY_NOM,KEY_NAME,KEY_EMAIL,KEY_POSTALE,KEY_TEL,KEY_FAVORIS}, KEY_FAVORIS + "=" + 1, null, null, null, KEY_NOM+","+KEY_PRENOM);
    }

    /**
     * Return a Cursor positioned at the note that matches the given rowId
     *
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
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
     * Update the note using the details provided. The note to be updated is
     * specified using the rowId, and it is altered to use the title and body
     * values passed in
     *
     * param rowId id of note to update
     * param title value to set note title to
     * param body value to set note body to
     * @return true if the note was successfully updated, false otherwise
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

    public boolean setFavoris(long rowId) {
        ContentValues args = new ContentValues();
        args.put(KEY_FAVORIS, 1);

        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public boolean setDefavoris(long rowId) {
        ContentValues args = new ContentValues();
        args.put(KEY_FAVORIS, 0);

        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
}
