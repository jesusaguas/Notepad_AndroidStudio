package es.unizar.eina.notepadv3;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.database.Cursor;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import es.unizar.eina.send.*;


public class Notepadv3 extends AppCompatActivity {

    private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;
    private static final int ACTIVITY_LIST=2;
    private static final int ACTIVITY_FILTER=3;

    // Menu options
    private static final int INSERT_ID = Menu.FIRST;
    private static final int ORDER_BY_TITLE_ID = Menu.FIRST + 1;
    private static final int ORDER_BY_CAT_ID = Menu.FIRST + 2;
    private static final int FILTER_BY_CAT_ID = Menu.FIRST + 3;
    private static final int GOTO_CATEGORIES_ID = Menu.FIRST + 4;
    private static final int TEST_ID = Menu.FIRST + 5;
    private static final int DELETEALL_ID = Menu.FIRST + 6;

    // Long press on a note
    private static final int DELETE_ID = Menu.FIRST + 7;
    private static final int EDIT_ID = Menu.FIRST + 8;
    private static final int SEND_ID = Menu.FIRST + 9;

    // To order notes
    private boolean orderByCategory = false;
    private Long filterCat = null;

    private NotesDbAdapter mDbHelper;
    private Cursor mNotesCursor;
    private ListView mList;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notepadv3);

        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
        mList = (ListView)findViewById(R.id.list);
        orderByCategory = false;
        filterCat = null;
        fillData();

        registerForContextMenu(mList);

    }

    private void fillData() {
        // Get all of the notes from the database and create the item list
        Cursor notesCursor = mDbHelper.fetchAllNotes();
        if(orderByCategory) {
            if (filterCat == null) {
                notesCursor = mDbHelper.fetchAllNotesByCategory();
            } else {
                notesCursor = mDbHelper.fetchAllNotesMatchingCategory(filterCat);
            }
        }
        startManagingCursor(notesCursor);

        // Create an array to specify the fields we want to display in the list (only TITLE)
        String[] from = new String[] { NotesDbAdapter.KEY_NOTES_TITLE };

        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[] { R.id.text1 };

        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter notes =
                new SimpleCursorAdapter(this, R.layout.notes_row, notesCursor, from, to);
        mList.setAdapter(notes);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, INSERT_ID, Menu.NONE, R.string.menu_new_note);
        menu.add(Menu.NONE, ORDER_BY_TITLE_ID, Menu.NONE, R.string.menu_order_by_title);
        menu.add(Menu.NONE, ORDER_BY_CAT_ID, Menu.NONE, R.string.menu_order_by_category);
        menu.add(Menu.NONE, FILTER_BY_CAT_ID, Menu.NONE, R.string.menu_filter_by_category);
        menu.add(Menu.NONE, GOTO_CATEGORIES_ID, Menu.NONE, R.string.menu_list_category);
        menu.add(Menu.NONE, TEST_ID, Menu.NONE, R.string.menu_test);
        menu.add(Menu.NONE, DELETEALL_ID, Menu.NONE, R.string.menu_delete_all);
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case INSERT_ID:
                createNote();
                return true;
            case ORDER_BY_TITLE_ID:
                orderByCategory = false;
                filterCat = null;
                fillData();
                return true;
            case ORDER_BY_CAT_ID:
                orderByCategory = true;
                filterCat = null;
                fillData();
                return true;
            case FILTER_BY_CAT_ID:
                orderByCategory = true;
                filterCat = null;
                filterByCategory();
                return true;
            case GOTO_CATEGORIES_ID:
                listcategory();
                return true;
            case TEST_ID:
                new Test(mDbHelper).runAllTests();
                fillData();
                return true;
            case DELETEALL_ID:
                mDbHelper.deleteAllNotes();
                fillData();
                Toast.makeText(this,"All notes deleted",Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(Menu.NONE, DELETE_ID, Menu.NONE, R.string.menu_delete);
        menu.add(Menu.NONE, EDIT_ID, Menu.NONE, R.string.menu_edit);
        menu.add(Menu.NONE, SEND_ID, Menu.NONE, R.string.menu_send);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case DELETE_ID:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                mDbHelper.deleteNote(info.id);
                fillData();
                Toast.makeText(this,"Note deleted",Toast.LENGTH_SHORT).show();
                return true;
            case EDIT_ID:
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                editNote(info.id);
                return true;
            case SEND_ID:
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                sendNote(info.id);
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void createNote() {
        Intent i = new Intent(this, NoteEdit.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }

    private void listcategory() {
        Intent i = new Intent(this, CatList.class);
        startActivityForResult(i, ACTIVITY_LIST);
    }

    private void filterByCategory() {
        Intent i = new Intent(this, CatFilter.class);
        startActivityForResult(i, ACTIVITY_FILTER);
    }

    private void editNote(long id) {
        Intent i = new Intent(this, NoteEdit.class);
        i.putExtra(NotesDbAdapter.KEY_NOTES_ROWID, id);
        startActivityForResult(i, ACTIVITY_EDIT);
    }

    private void sendNote(long id) {
        Cursor note = mDbHelper.fetchNote(id);
        startManagingCursor(note);

        // Title and body of the note
        String title = note.getString(note.getColumnIndexOrThrow(NotesDbAdapter.KEY_NOTES_TITLE));
        String body = note.getString(note.getColumnIndexOrThrow(NotesDbAdapter.KEY_NOTES_BODY));

        // Si el cuerpo de la nota tiene una longitud mayor de 50 caracteres se envía por email,
        // sino SMS
        String method;
        if(body.length()<50) {
            method = "SMS";
        }
        else{
            method = "EMAIL";
        }
        SendAbstraction sendAbstract = new SendAbstractionImpl(this, method);
        sendAbstract.send(title,body);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(intent != null){
            filterCat = intent.getLongExtra("CatId", -1);
        }
        fillData();
    }

}
