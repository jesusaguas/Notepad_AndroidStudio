package es.unizar.eina.notepadv3;

import android.database.Cursor;
import android.util.Log;

/**
 * Tests for note and category modifying methods
 *
 * @autor Jesus Aguas Acin (736935)
 */

public class Test {
    private NotesDbAdapter myNotes;

    public Test(NotesDbAdapter notes) {
        myNotes = notes;
    }
    public void runAllTests(){

        createCorrectNote();
        createNoteTitleNull();
        createNoteTitleEmpty();
        createNoteBodyNull();
        deleteNoteIdPositive();
        deleteNoteIdNegativeOrZero();
        updateNoteCorrect();
        updateNoteIdNegativeOrZero();
        updateNoteTitleNull();
        updateNoteTitleEmpty();
        try {
            updateNoteBodyNull();
            createMoreThanThousandNotes();
            createLessOrEqThanThousandNotes();
            //overflowDatabase(); // Se evita su ejecucion en dispositivo real
            createCorrectCategory();
            createNullNameCategory();
            createCategoryMoreThanOneSpace();
            updateNoteCategory();
            createNoteWrongCategory();
            deleteCategory();
        }catch(Throwable t){
            Log.d("Exception thrown", t.getMessage());
        }
        Log.d("Tests end", "Deleting all notes and categories");
        myNotes.deleteAllNotes();
        myNotes.deleteAllCategories();
    }




    public void createCorrectNote() {
        long id = myNotes.createNote("CorrectTitle", "CorrectBody", null);
        // La nota creada es correcta.
        if (id > 0) {
            Log.d("Case 1", "Creating a correct note. OK.");
        } else {
            Log.d("Case 1", "Creating a correct note. FAILURE.");
        }
    }

    public void createNoteTitleNull() {
        long id = myNotes.createNote(null, "CorrectBody", null);
        if (id > 0) {
            Log.d("Case 2", "Creating a note with null title. FAILURE.");
        } else {
            Log.d("Case 2", "Creating a note with null title. OK.");
        }
    }

    public void createNoteTitleEmpty() {
        long id = myNotes.createNote("", "CorrectBody", null);
        if (id > 0) {
            Log.d("Case 3", "Creating a note with empty title. FAILURE.");
        } else {
            Log.d("Case 3", "Creating a note with empty title. OK.");
        }
    }

    public void createNoteBodyNull() {
        long id = myNotes.createNote("CorrectTitle", null, null);
        if (id > 0) {
            Log.d("Case 4", "Creating de note with null body. FAILURE.");
        } else {
            Log.d("Case 4", "Creating de note with null body. OK.");
        }
    }

    public void deleteNoteIdPositive() {
        long rowId = myNotes.createNote("ERASABLE", "ERASABLE", null);
        boolean success = myNotes.deleteNote(rowId);
        if (success) {
            Log.d("Case 5", "Erasing note. OK.");
        } else {
            Log.d("Case 5", "Erasing note. FAILURE.");
        }
    }

    public void deleteNoteIdNegativeOrZero() {
        boolean success = myNotes.deleteNote(-1);
        if (success) {
            Log.d("Case 6", "Erasing negative id note. FAILURE.");
        } else {
            Log.d("Case 6", "Erasing negative id note. OK.");
        }
    }

    public void updateNoteCorrect() {
        long myNote = myNotes.createNote("CorrectTitle", "CorrectBody", null);
        boolean success = myNotes.updateNote(myNote, "NewTitle", "NewBody", null);
        if (success) {
            Log.d("Case 7", "Updating a note. OK.");
        } else {
            Log.d("Case 7", "Updating a note. FAILURE.");
        }
    }

    public void updateNoteIdNegativeOrZero() {
        boolean success = myNotes.updateNote(-1, "NewTitle", "NewBody", null);
        if (success) {
            Log.d("Case 8", "Updating a note with negative or zero id. FAILURE.");
        } else {
            Log.d("Case 8", "Updating a note with negative or zero id. OK.");
        }
    }

    public void updateNoteTitleNull() {
        long myNote = myNotes.createNote("CorrectTitle", "CorrectBody", null);
        boolean success = myNotes.updateNote(myNote, null, "NewBody", null);
        if (success) {
            Log.d("Case 9", "Updating a note with null title. FAILURE.");
        } else {
            Log.d("Case 9", "Updating a note with null title. OK.");
        }
    }

    public void updateNoteTitleEmpty() {
        long myNote = myNotes.createNote("CorrectTitle", "CorrectBody", null);
        boolean success = myNotes.updateNote(myNote, "", "NewBody", null);
        if (success) {
            Log.d("Case 10", "Updating note with empty title. FAILURE.");
        } else {
            Log.d("Case 10", "Updating note with empty title. OK.");
        }
    }

    public void updateNoteBodyNull() {
        long myNote = myNotes.createNote("CorrectTitle", "CorrectBody", null);
        boolean success = myNotes.updateNote(myNote, "CorrectTitle", null, null);
        if (success) {
            Log.d("Case 11", "Updating note with null body. FAILURE.");
        } else {
            Log.d("Case 11", "Updating note with null body. OK.");
        }
    }


    public void createLessOrEqThanThousandNotes() {
        long id = myNotes.createNote("Test0", "The body of this note doesn't matter", null);
        if (id > 0) {
            Log.d("Case 12", "Creating less than or a 1000 notes. OK.");
        } else {
            Log.d("Case 12", "Creating less than or a 1000 notes. FAILURE.");
        }
    }

    public void createMoreThanThousandNotes() {
        boolean success = true;
        for (int i = 1; i <= 1001 && success; i++) {
            long id = myNotes.createNote("Test" + i, "The body of this note doesn't matter", null);
            success = id > 0;
        }
        if (success) {
            Log.d("Case 13", "Creating more than 1000 notes. OK.");
        } else {
            Log.d("Case 13", "Creating more than 1000 notes. FAILURE.");
        }

    }


    public void overflowDatabase() {
        try {
            String content = "A";
            String title = "Overflow";
            int i = 1;
            while (true) {
                long id = myNotes.createNote(title + i, content, null);
                if (id < 1){
                    Log.d("Case 14", "Overflow con body de tamano " + content.length());
                }
                Log.d("Introduced: " , content);
                content = content + "A";

            }
        }
        catch(Exception e){
            Log.d("Case 14", e.getMessage());
        }
    }

    public void createCorrectCategory(){
        long cId = myNotes.createCategory("Correct Category");
        if(cId > 0){
            Log.d("Case 15", "Creating category. OK.");
        } else{
            Log.d("Case 15", "Creating category. FAILURE.");
        }
    }

    public void createNullNameCategory(){
        long cId = myNotes.createCategory(null);
        if(cId <= 0){
            Log.d("Case 16", "Creating category with null name. OK.");
        } else{
            Log.d("Case 16", "Creating category with null name. FAILURE.");
        }
    }

    public void createCategoryMoreThanOneSpace(){
        long cId = myNotes.createCategory("A    CATEGORY   OF  SORTS");
        if(cId > 0){
            Log.d("Case 17", "Creating category with multiple spaces. OK.");
        } else{
            Log.d("Case 17", "Creating category with multiple spaces. FAILURE.");
        }
    }

    public void updateNoteCategory(){
        long nId = myNotes.createNote("Correct Note", "", null);
        long cId = myNotes.createCategory("Category 1");
        boolean success = myNotes.updateNote(nId, "Correct Note", "", cId);
        if(success){
            Log.d("Case 18", "Updating a note's category. OK.");
        } else{
            Log.d("Case 18", "Updating a note's category. FAILURE.");
        }
    }

    public void createNoteWrongCategory(){
        long nId = myNotes.createNote("Correct Note", "", -1l);
        if(nId <= 0){
            Log.d("Case 19", "Creating a note with negative category Id. OK.");
        } else{
            Log.d("Case 19", "Creating a note with negative category Id. FAILURE.");
        }
    }

    public void deleteCategory(){
        long cId = myNotes.createCategory("A category");
        long nId = myNotes.createNote("A note", "", cId);
        myNotes.deleteCategory(cId);
        Cursor note = myNotes.fetchNote(nId);
        Long backCID = note.getLong(note.getColumnIndexOrThrow(NotesDbAdapter.KEY_NOTES_CATEGORY));
        if(backCID == null){
            Log.d("Case 20", "Deleting category deletes from note. OK.");
        } else{
            Log.d("Case 20", "Deleting category deletes from note. FAILURE.");
        }
    }
}