package figat.pl.mobilesql;

import java.security.InvalidParameterException;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {

    public static final String TAG = "DB: ";

    // Database Info
    private static final String DATABASE_NAME = "database.db";
    private static final int DATABASE_VERSION = 11;

    // Root table
    private static final String TABLE_ROOT = "root";
    private static final String KEY_TABLE_ID = "id";
    private static final String KEY_TABLE_NAME = "name";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Loads all tables from the database
     * @param tables Result tables list
     */
    public void loadTables(ArrayList<Table> tables) {

        SQLiteDatabase db = getReadableDatabase();

        // Clear list
        tables.clear();

        // Ensure that root table exists
        if (isTableExists(db, TABLE_ROOT)) {

            // Gather all tables entries from the root table
            Cursor cursor = db.rawQuery("SELECT " + KEY_TABLE_NAME + " FROM " + TABLE_ROOT, null);

            try {
                if (cursor.moveToFirst()) {
                    do {

                        Table t = new Table();
                        t.name = cursor.getString(cursor.getColumnIndexOrThrow(KEY_TABLE_NAME));
                        tables.add(t);

                    } while (cursor.moveToNext());
                }
            } catch (Exception ex) {
                Log.d(TAG, "Error while trying to get data from database");
            } finally {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }
        }

        db.close();
    }

    /***
     * Create new table in a database
     * @param name New table name
     */
    public void createTable(String name) {
        // Validate name
        if (name.compareTo(TABLE_ROOT) == 0)
            throw new InvalidParameterException("Invalid table name!");

        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("DROP TABLE IF EXISTS " + name);
        db.execSQL("CREATE TABLE " + name + "( Dummy INTEGER PRIMARY KEY );");
        
        db.beginTransaction();
        try {

            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_TABLE_NAME, name);
            db.insertOrThrow(TABLE_ROOT, null, contentValues);
            db.setTransactionSuccessful();

        } catch (Exception ex) {
            Log.d(TAG, "Error while trying to add post to database");
        } finally {
            db.endTransaction();
        }

        db.close();
    }

    /**
     * Drops table and remove it's entry from the root table
     * @param name Table name
     */
    public void deleteTable(String name) {
        // Validate name
        if (name.compareTo(TABLE_ROOT) == 0)
            throw new InvalidParameterException("Invalid table name!");

        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("DROP TABLE IF EXISTS " + name);
        db.execSQL("DELETE FROM " + TABLE_ROOT + " WHERE " + KEY_TABLE_NAME + " = \'" + name + "\'");

        db.close();
    }

    private static boolean isTableExists(SQLiteDatabase db, String tableName) {
        if (tableName == null || db == null || !db.isOpen()) {
            return false;
        }
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?", new String[]{"table", tableName});
        if (!cursor.moveToFirst()) {
            return false;
    }
        int count = cursor.getInt(0);
        cursor.close();
        return count > 0;
    }

    // Called when the database connection is being configured.
    // Configure database settings for things like foreign key support, write-ahead logging, etc.
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // Called when the database is created for the FIRST time.
    // If a database already exists on disk with the same DATABASE_NAME, this method will NOT be called.
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ROOT_TABLE = "CREATE TABLE " + TABLE_ROOT +
                "(" +
                KEY_TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_TABLE_NAME + " TEXT NOT NULL" +
                ")";

        db.execSQL(CREATE_ROOT_TABLE);
    }

    // Called when the database needs to be upgraded.
    // This method will only be called if a database already exists on disk with the same DATABASE_NAME,
    // but the DATABASE_VERSION is different than the version of the database that exists on disk.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROOT);
            onCreate(db);
        }
    }
}

