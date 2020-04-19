package es.unizar.eina.notepadv3;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class NoteEdit extends AppCompatActivity {

    private EditText mIdText;
    private EditText mTitleText;
    private EditText mBodyText;
    private TextView mCatText;
    private Spinner mCategory;


    private ArrayList<String> categories = new ArrayList<>();
    private ArrayList<Long> categoriesID = new ArrayList<>();

    private Long mRowId;

    private NotesDbAdapter mDbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();

        setContentView(R.layout.note_edit);
        setTitle(R.string.edit_note);

        mCategory = (Spinner) findViewById(R.id.category);

        categories.add("");
        categoriesID.add(null);

        Cursor categoriesCursor = mDbHelper.fetchAllCategories();

        for (categoriesCursor.moveToFirst(); !categoriesCursor.isAfterLast(); categoriesCursor.moveToNext()){
                Long id = categoriesCursor.getLong(categoriesCursor.getColumnIndex(NotesDbAdapter.KEY_CAT_ROWID));
                categoriesID.add(id);
                String catName = categoriesCursor.getString(categoriesCursor.getColumnIndex(NotesDbAdapter.KEY_CAT_NAME));
                categories.add(catName);
        }


        ArrayAdapter adp = new ArrayAdapter(NoteEdit.this, android.R.layout.simple_spinner_dropdown_item, categories);

        mCategory.setAdapter(adp);


        mTitleText = (EditText) findViewById(R.id.title);
        mBodyText = (EditText) findViewById(R.id.body);
        mCatText = (TextView) findViewById(R.id.CatText);

        mIdText = (EditText) findViewById(R.id.id);
        mIdText.setEnabled(false);

        Button confirmButton = (Button) findViewById(R.id.confirm);

        mRowId = (savedInstanceState == null) ? null :
                (Long) savedInstanceState.getSerializable(NotesDbAdapter.KEY_NOTES_ROWID);
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = (extras != null) ? extras.getLong(NotesDbAdapter.KEY_NOTES_ROWID)
                    : null;
        }

        confirmButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }

        });
    }

    private void populateFields() {
        if (mRowId != null) {
            Cursor note = mDbHelper.fetchNote(mRowId);
            startManagingCursor(note);
            mIdText.setText(note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_NOTES_ROWID)));
            mTitleText.setText(note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_NOTES_TITLE)));
            mBodyText.setText(note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_NOTES_BODY)));

            long oldCategory = note.getLong(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_NOTES_CATEGORY));
            if (oldCategory>0){
                Cursor category = mDbHelper.fetchCategory(oldCategory);
                mCatText.setText("Actual Category: " + category.getString(
                        category.getColumnIndexOrThrow(NotesDbAdapter.KEY_CAT_NAME)));
            }
            else{
                mCatText.setText("Actual Category: NONE");
            }
        }
        else {
            mIdText.setText("---");
            mCatText.setText("None");
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(NotesDbAdapter.KEY_NOTES_ROWID, mRowId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }

    private void saveState() {
        String title = mTitleText.getText().toString();
        String body = mBodyText.getText().toString();
        String selectedCat = mCategory.getSelectedItem().toString();
        Long catId;
        if (selectedCat==null || selectedCat.trim().equals("")) {
            catId = null;
        } else {
            catId = categoriesID.get(categories.indexOf(selectedCat));
        }

        if (mRowId == null) {
            long id = mDbHelper.createNote(title, body, catId);
            if (id > 0) {
                mRowId = id;
            }
        } else {
            mDbHelper.updateNote(mRowId, title, body, catId);
        }
    }



}
