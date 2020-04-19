package es.unizar.eina.notepadv3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Simple notes database access helper class. Defines the basic CRUD operations
 * for the notepad example, and gives the ability to list all notes as well as
 * retrieve or modify a specific note.
 *
 * This has been improved from the first version of this tutorial through the
 * addition of better error handling and also using returning a Cursor instead
 * of using a collection of inner classes (which is less scalable and not
 * recommended).
 */
public class NotesDbAdapter {

    public static final String KEY_NOTES_ROWID = "_id";
    public static final String KEY_NOTES_TITLE = "title";
    public static final String KEY_NOTES_BODY = "body";
    public static final String KEY_NOTES_CATEGORY = "category";

    public static final String KEY_CAT_ROWID = "_id";
    public static final String KEY_CAT_NAME = "categoryName";

    private static final String TAG = "NotesDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    /**
     * Database creation sql statement
     */
    private static final String NOTES_CREATE =
            "create table notes (_id integer primary key autoincrement, "
                    + "title text not null, body text not null, category integer," +
            "foreign key (category) references categories(_id) ON DELETE SET NULL);";

    private static final String CATEGORIES_CREATE =
            "create table categories (_id integer primary key autoincrement, "
                    + "categoryName text not null);";

    private static final String DATABASE_NAME = "data";
    private static final String NOTES_TABLE = "notes";
    private static final String CATEGORIES_TABLE = "categories";
    private static final int DATABASE_VERSION = 2;

    private final Context mCtx;

    /**
     * Manages the notes app database.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CATEGORIES_CREATE);
            db.execSQL(NOTES_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + NOTES_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + CATEGORIES_TABLE);
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     *
     * @param ctx the Context within which to work
     */
    public NotesDbAdapter(Context ctx) {
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
    public NotesDbAdapter open() throws SQLException {
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
     * @param title the title of the note
     * @param body the body of the note
     * @param catId id of the category
     * @return rowId or -1 if failed
     */
    public long createNote(String title, String body, Long catId) {
        if (title==null || title.trim().equals("") || body==null){
            return -1;
        }
        else {
            ContentValues initialValues = new ContentValues();
            initialValues.put(KEY_NOTES_TITLE, title);
            initialValues.put(KEY_NOTES_BODY, body);
            initialValues.put(KEY_NOTES_CATEGORY, catId);
            return mDb.insert(NOTES_TABLE, null, initialValues);
        }
    }

    /**
     * Create a new category using the name provided. If the category is
     * successfully created return the new rowId for that category, otherwise return
     * a -1 to indicate failure.
     *
     * @param categoryName the name of the category
     * @return rowId or -1 if failed
     */
    public long createCategory(String categoryName) {
        if (categoryName==null || categoryName.trim().equals("")){
            return -1;
        }
        else {
            ContentValues initialValues = new ContentValues();
            initialValues.put(KEY_CAT_NAME, categoryName);
            return mDb.insert(CATEGORIES_TABLE, null, initialValues);
        }
    }

    /**
     * Delete the all the notes
     *
     * @return true if all notes have been deleted, false otherwise
     */
    public boolean deleteAllNotes() {

        return mDb.delete(NOTES_TABLE, null, null) > 0;
    }

    /**
     * Delete the all the categories
     *
     * @return true if all categories have been deleted, false otherwise
     */
    public boolean deleteAllCategories() {

        return mDb.delete(CATEGORIES_TABLE, null, null) > 0;
    }


    /**
     * Delete the note with the given rowId
     *
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteNote(long rowId) {

        return mDb.delete(NOTES_TABLE, KEY_NOTES_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Delete the category with the given rowId
     *
     * @param rowId id of category to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteCategory(long rowId) {

        return mDb.delete(CATEGORIES_TABLE, KEY_CAT_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all notes ordered by title in the database
     *
     * @return Cursor over all notes ordered by title
     */
    public Cursor fetchAllNotes() {

        return mDb.query(NOTES_TABLE, new String[] {KEY_NOTES_ROWID, KEY_NOTES_TITLE,
                KEY_NOTES_BODY, KEY_NOTES_CATEGORY}, null, null, null, null, KEY_NOTES_TITLE);

    }

    /**
     * Return a Cursor over the list of all notes ordered by Category id in the database
     *
     * @return Cursor over all notes ordered by Category id
     */
    public Cursor fetchAllNotesByCategory() {

        return mDb.query(NOTES_TABLE, new String[] {KEY_NOTES_ROWID, KEY_NOTES_TITLE,
                KEY_NOTES_BODY, KEY_NOTES_CATEGORY}, null, null, null, null, KEY_NOTES_CATEGORY);

    }

    /**
     * Return a Cursor over the list of all notes in the database that share the category
     * @param category category that all notes returned share
     * @return Cursor over all notes with the category
     */
    public Cursor fetchAllNotesMatchingCategory(long category) {

        return mDb.query(NOTES_TABLE, new String[] {KEY_NOTES_ROWID, KEY_NOTES_TITLE,
                KEY_NOTES_BODY, KEY_NOTES_CATEGORY}, KEY_NOTES_CATEGORY + " = " + category, null, null, null, KEY_NOTES_TITLE);
    }

    /**
     * Return a Cursor over the list of all categories in the database
     *
     * @return Cursor over all categories
     */
    public Cursor fetchAllCategories() {

        return mDb.query(CATEGORIES_TABLE, new String[] {KEY_CAT_ROWID, KEY_CAT_NAME},
                null, null, null, null, KEY_CAT_NAME);

    }

    /**
     * Return a Cursor positioned at the note that matches the given rowId
     *
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchNote(long rowId) throws SQLException {

        Cursor mCursor =

                mDb.query(true, NOTES_TABLE, new String[] {KEY_NOTES_ROWID,
                                KEY_NOTES_TITLE, KEY_NOTES_BODY, KEY_NOTES_CATEGORY}, KEY_NOTES_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    /**
     * Return a Cursor positioned at the category that matches the given rowId
     *
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching category, if found
     * @throws SQLException if category could not be found/retrieved
     */
    public Cursor fetchCategory(long rowId) throws SQLException {

        Cursor mCursor =

                mDb.query(true, CATEGORIES_TABLE, new String[] {KEY_CAT_ROWID,
                                KEY_CAT_NAME}, KEY_CAT_ROWID + "=" + rowId, null,
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
     * @param rowId id of note to update
     * @param title value to set note title to
     * @param body value to set note body to
     * @param catId id of the category
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateNote(long rowId, String title, String body, Long catId) {
        if(title==null || title.trim().equals("") || body==null || rowId < 1){
            return false;
        }
        ContentValues args = new ContentValues();
        args.put(KEY_NOTES_TITLE, title);
        args.put(KEY_NOTES_BODY, body);
        args.put(KEY_NOTES_CATEGORY, catId);
        return mDb.update(NOTES_TABLE, args, KEY_NOTES_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Update the category using the details provided. The category to be updated is
     * specified using the rowId, and it is altered to use the name value passed in
     *
     * @param rowId id of category to update
     * @param categoryName value to set category name to
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateCategory(long rowId, String categoryName) {
        if(categoryName==null || categoryName.trim().equals("") || rowId < 1){
            return false;
        }
        else{
            ContentValues args = new ContentValues();
            args.put(KEY_CAT_NAME, categoryName);
            return mDb.update(CATEGORIES_TABLE, args, KEY_CAT_ROWID + "=" + rowId, null) > 0;
        }
    }
}