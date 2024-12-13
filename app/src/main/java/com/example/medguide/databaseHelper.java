package com.example.medguide;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class databaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "med_guide.db";
    private static final int DATABASE_VERSION = 1;
    private final Context context;

    public databaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        initializeDatabase();  // Initialize the database once when the helper is created
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create tables if they don't already exist
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades
    }

    private String getDatabasePath() {
        return context.getDatabasePath(DATABASE_NAME).getAbsolutePath();
    }

    // Method to copy the database from assets to internal storage
    private void copyDatabase() throws IOException {
        String destPath = getDatabasePath();
        File dbFile = new File(destPath);

        if (!dbFile.exists()) {
            InputStream inputStream = context.getAssets().open(DATABASE_NAME);
            OutputStream outputStream = new FileOutputStream(destPath);

            byte[] buffer = new byte[1024];
            int length;

            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();
            Log.d("Database", "Database copied to: " + destPath);
        }
    }

    // Method to check for the tables in the database
    private void checkTablesExist() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table';", null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String tableName = cursor.getString(0);
                Log.d("Database", "Table name: " + tableName);  // Log all table names
            }
            cursor.close();
        }
    }

    // Initialize the database once
    public void initializeDatabase() {
        try {
            File databaseFile = new File(getDatabasePath());

            // Log the current database path
            Log.d("Database", "Database path: " + getDatabasePath());

            // If the database file exists, delete it to refresh it
            if (databaseFile.exists()) {
                databaseFile.delete();  // Delete the old database
                Log.d("Database", "Old database deleted.");
            }

            // Copy the correct database from assets to the app's database folder
            copyDatabase();

            // Log column names from the medicaments table
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("PRAGMA table_info(medicaments);", null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String columnName = cursor.getString(cursor.getColumnIndex("name"));
                    Log.d("Database", "Column name: " + columnName);
                }
                cursor.close();
            }
            // doctors
            // Log column names from the doctors table

            Cursor cursor1 = db.rawQuery("PRAGMA table_info(doctors);", null);

            if (cursor1 != null) {
                while (cursor1.moveToNext()) {
                    String columnName = cursor1.getString(cursor1.getColumnIndex("name"));
                    Log.d("Database", "Column name: " + columnName);
                }
                cursor1.close();
            }

            // Verify if the database was copied successfully
            if (!databaseFile.exists()) {
                Log.e("Database", "Database not found after copy");
            } else {
                Log.d("Database", "Database successfully copied to: " + databaseFile.getAbsolutePath());
            }

            // Check if the required tables exist (optional)
            checkTablesExist();

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Database", "Error initializing database", e);
        }
    }


    // Access the database in read-only mode
    @Override
    public SQLiteDatabase getReadableDatabase() {
        return SQLiteDatabase.openDatabase(getDatabasePath(), null, SQLiteDatabase.OPEN_READONLY);
    }

    // Access the database in read-write mode
    @Override
    public SQLiteDatabase getWritableDatabase() {
        return SQLiteDatabase.openDatabase(getDatabasePath(), null, SQLiteDatabase.OPEN_READWRITE);
    }

    // Fetch data for displaying Medicaments
    public Cursor getMedicamentsForDisplay() {
        SQLiteDatabase db = this.getReadableDatabase();

        // Query to select all rows from the medicaments table
        String queryDisplayMedicaments = "SELECT * FROM medicaments";
        Log.d("Database", "Query: " + queryDisplayMedicaments);

        // Execute the query and return the cursor
        return db.rawQuery(queryDisplayMedicaments, null);
    }
    // Fetch data for displaying Doctors
    public Cursor getDoctorsForDisplay() {
        SQLiteDatabase db = this.getReadableDatabase();

        // Query to select all rows from the medicaments table
        String queryDisplayDoctors = "SELECT * FROM doctors";
        Log.d("Database", "Query: " + queryDisplayDoctors);

        // Execute the query and return the cursor
        return db.rawQuery(queryDisplayDoctors, null);
    }


    // Search Medicaments by keyword
    public Cursor searchMedicamentByName(String keyword) {
        SQLiteDatabase db = this.getReadableDatabase();
        String querySearchMedicaments = "SELECT * FROM medicaments WHERE " +
                "NOM LIKE ? OR " +
                "FORME LIKE ? OR " +
                "PRESENTATION LIKE ? OR " +
                "PRIX_BR LIKE ? OR " +
                "TAUX_REMBOURSEMENT LIKE ?";
        String wildcardKeyword = "%" + keyword + "%";
        return db.rawQuery(querySearchMedicaments, new String[]{
                wildcardKeyword, wildcardKeyword, wildcardKeyword, wildcardKeyword, wildcardKeyword
        });
    }
    // Search Doctors by keyword
    public Cursor searchDoctorsByName(String keyword) {
        SQLiteDatabase db = this.getReadableDatabase();
        String querySearchDoctors= "SELECT * FROM doctors WHERE " +
                "doctor_name LIKE ? OR " +
                "adresse LIKE ? OR " +
                "phoneNumber LIKE ? OR " +
                "specialite LIKE ?";
        String wildcardKeyword = "%" + keyword + "%";
        return db.rawQuery(querySearchDoctors, new String[]{
                wildcardKeyword, wildcardKeyword, wildcardKeyword, wildcardKeyword
        });
    }
}
