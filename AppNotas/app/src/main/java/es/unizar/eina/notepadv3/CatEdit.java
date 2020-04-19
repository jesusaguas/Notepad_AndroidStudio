package es.unizar.eina.notepadv3;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CatEdit extends AppCompatActivity {

    private EditText mNameText;
    private Long mRowId;

    private NotesDbAdapter mDbHelper;

    // practica 4
    private EditText mIdText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();

        setContentView(R.layout.cat_edit);
        setTitle(R.string.edit_category);

        mNameText = (EditText) findViewById(R.id.title);
        mIdText = (EditText) findViewById(R.id.id);
        mIdText.setEnabled(false);

        Button confirmButton = (Button) findViewById(R.id.confirm);

        mRowId = (savedInstanceState == null) ? null :
                (Long) savedInstanceState.getSerializable(NotesDbAdapter.KEY_CAT_ROWID);
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = (extras != null) ? extras.getLong(NotesDbAdapter.KEY_CAT_ROWID)
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
            Cursor category = mDbHelper.fetchCategory(mRowId);
            startManagingCursor(category);
            mIdText.setText(category.getString(
                    category.getColumnIndexOrThrow(NotesDbAdapter.KEY_CAT_ROWID)));
            mNameText.setText(category.getString(
                    category.getColumnIndexOrThrow(NotesDbAdapter.KEY_CAT_NAME)));
        }
        else {
            mIdText.setText("---");
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(NotesDbAdapter.KEY_CAT_ROWID, mRowId);
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
        String name = mNameText.getText().toString();

        if (mRowId == null) {
            long id = mDbHelper.createCategory(name);
            if (id > 0) {
                mRowId = id;
            }
        } else {
            mDbHelper.updateCategory(mRowId, name);
        }
    }

}
