package es.unizar.eina.notepadv3;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

/**
 * Shows a list with all the categories in the database and allows the user
 * to choose one to return with an intent to the parent activity.
 */
public class CatFilter extends AppCompatActivity {


    public static final int SELECT_CATEGORY = Menu.FIRST;
    private NotesDbAdapter mDbHelper;
    private ListView mList;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catlist);

        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
        mList = (ListView)findViewById(R.id.list);
        fillData();

        registerForContextMenu(mList);

    }

    /**
     * Fills the layout with all the categories stored in the database
     */
    private void fillData() {
        // Get all of the categories from the database and create the item list
        Cursor categoriesCursor = mDbHelper.fetchAllCategories();
        startManagingCursor(categoriesCursor);

        // Create an array to specify the fields we want to display in the list (only NAME)
        String[] from = new String[] { NotesDbAdapter.KEY_CAT_NAME};

        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[] { R.id.text1 };

        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter categories =
                new SimpleCursorAdapter(this, R.layout.notes_row, categoriesCursor, from, to);
        mList.setAdapter(categories);
    }

    /**
     * Creates the context menu for a category selection
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(Menu.NONE, SELECT_CATEGORY, Menu.NONE, R.string.menu_select_category);
    }
    /**
     * Executes the action that corresponds with the chosen menu option
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case SELECT_CATEGORY:
                // Return the chosen element
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                Intent result = new Intent();
                result.putExtra("CatId", info.id);
                setResult(Activity.RESULT_OK, result);
                finish();
                return true;
        }
        return super.onContextItemSelected(item);
    }

}