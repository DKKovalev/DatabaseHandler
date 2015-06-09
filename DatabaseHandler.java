package com.example.firstprogramm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;



public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "db_ver_" + DATABASE_VERSION + ".db";

    private static final String NOTES = "notes";

    private static final String TITLE = "title";
    private static final String DATE = "date";
    private static final String COORDSLAT = "coordLat";
    private static final String COORDSLNG = "coordLng";

    Context context;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_EVENTS = "CREATE TABLE "
                + NOTES + "("
                + TITLE + " STRING,"
                + DATE + " STRING,"
                + COORDSLAT + " STRING,"
                + COORDSLNG + " STRING)" ;
        db.execSQL(CREATE_TABLE_EVENTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + NOTES);
        onCreate(db);
    }

    public void addNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL("PRAGMA synchronous=OFF");
            db.setLockingEnabled(false);
            db.beginTransaction();
            int newId = db.update(NOTES, getContentValues(note), "title='" + note.getTitle() + "'", null);
            if (newId == 0) {
                db.insert(NOTES, null, getContentValues(note));
            }
            db.setTransactionSuccessful();

        } catch (Exception e) {

        } finally {
            db.endTransaction();
            db.setLockingEnabled(true);
            db.execSQL("PRAGMA synchronous=NORMAL");
        }

        db.close();
    }

    private ContentValues getContentValues(Note note) {
        ContentValues values = new ContentValues();
        values.put(TITLE, note.getTitle());
        values.put(DATE, note.date);
        values.put(COORDSLAT, note.coordLat);
        values.put(COORDSLNG, note.coordLong);
        return values;
    }

    public ArrayList<Note> getNotes() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Note> notes = new ArrayList<Note>();
        Cursor notesCursor = db.rawQuery("SELECT * FROM " + NOTES, null);
        if (notesCursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.setTitle(notesCursor.getString(0));
                note.setDate(notesCursor.getString(1));
                note.setCoordLat(notesCursor.getDouble(2));
                note.setCoordLong(notesCursor.getDouble(3));
                notes.add(note);
            } while (notesCursor.moveToNext());
        }
        notesCursor.close();
        db.close();
        return notes;
    }

    public void formatDB() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(NOTES, null, null);
        db.close();
    }

    public Note getNote(String title) throws Exception {
        ArrayList<Note> notes =getNotes();
        for (Note note : notes)
            if (note.getTitle().equals(title)) {
                return note;
            }
        throw new Exception("Event  with title=" + title + " not found");
    }
}