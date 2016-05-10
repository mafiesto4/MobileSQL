package figat.pl.mobilesql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

public class Model {

    DatabaseHandler handler;
    ArrayList<Table> tables;

    /**
     * Gets amount of tables
     *
     * @return Amount of tables in a database
     */
    public int getTablesCount() {
        return tables.size();
    }

    /***
     * Gets table at given index
     *
     * @param index Table index
     * @return Table
     */
    public Table getTable(int index) {
        return tables.get(index);
    }

    /**
     * Init
     *
     * @param context Application context
     */
    public Model(Context context) {
        handler = new DatabaseHandler(context);
        tables = new ArrayList<>();

        handler.loadTables(tables);
    }

    /**
     * Tries to find table with given name
     *
     * @param name Table name to find
     * @return Found table or null
     */
    public Table findTable(String name) {
        Table result = null;
        for (int i = 0; i < tables.size(); i++) {
            if (tables.get(i).name.compareTo(name) == 0) {
                result = tables.get(i);
                break;
            }
        }
        return result;
    }

    /**
     * Creates new SQL database table
     *
     * @param name New table name
     * @return New table
     */
    public Table createTable(String name) {

        // Create SQL table
        handler.createTable(name);

        // Create table entry
        Table table = new Table();
        table.name = name;
        tables.add(table);

        return table;
    }

    /**
     * Deletes SQL table
     *
     * @param table Table to remove
     */
    public void deleteTable(Table table) {

        // Delete SQL table
        handler.deleteTable(table.name);

        // Remove table entry
        tables.remove(table);
    }

    /**
     * Perform query to the database to gather table data to teh cache
     *
     * @param table Table to update
     */
    public void getTableData(Table table) {
        table.cache = performQuery("SELECT * FROM " + table.name);
    }

    /***
     * Perform raw SQL query to teh database
     * @param sql SQL query text
     */
    public void rawQuery(String sql)
    {
        SQLiteDatabase db = handler.getWritableDatabase();
        db.execSQL(sql);
        db.close();
    }

    /**
     * Performs SQL query to the database
     *
     * @param sql SQL query text
     * @return Query result
     */
    public SqlQueryResult performQuery(String sql) {
        // Prepare
        SqlQueryResult result = new SqlQueryResult();
        SQLiteDatabase db = handler.getWritableDatabase();

        // Gather all tables entries
        Cursor cursor = db.rawQuery(sql, null);
        result.ColumnNames = cursor.getColumnNames();
        result.EntriesCount = cursor.getCount();

        try {
            if (cursor.moveToFirst()) {

                int rows = cursor.getCount();
                int columns = cursor.getColumnCount();

                for (int row = 0; row < rows; row++) {

                    String[] rowData = new String[columns];

                    for (int col = 0; col < columns; col++)
                        rowData[col] = cursor.getString(col);

                    result.Data.add(rowData);

                    if (!cursor.moveToNext())
                        break;
                }
            }
        } catch (Exception ex) {
            Log.d(DatabaseHandler.TAG, "Error while trying to get table data from database");
            throw ex;
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        db.close();
        return result;
    }

    /***
     * Add new column to the table
     * @param table        Table
     * @param columnName   Column name
     * @param columnType   Column type
     * @param defaultValue Default value
     */
    public void addColumn(Table table, String columnName, String columnType, String defaultValue) {

        SQLiteDatabase db = handler.getWritableDatabase();

        db.execSQL("ALTER TABLE " + table.name + " ADD COLUMN " + columnName + " " + columnType + " DEFAULT " + defaultValue);

        db.close();
    }

    /***
     * Remove column to the table
     * @param table      Table
     * @param columnName Column name
     */
    public void removeColumn(Table table, String columnName) {

        getTableData(table);

        // Create string from column names to keep in table
        String keepColumns = "";
        for(String column : table.cache.ColumnNames)
        {
            if (column.compareTo(columnName) != 0) {
                if(keepColumns.length() != 0)
                    keepColumns += ",";
                keepColumns += column;
            }
        }

        table.clearCache();

        SQLiteDatabase db = handler.getWritableDatabase();

        //db.beginTransaction();

        db.execSQL(
                /*"CREATE TABLE _backup AS (SELECT " + keepColumns + " FROM " + table.name + ");\n" +
                "DROP " + table.name + ";\n"+
                "ALTER TABLE _backup RENAME TO " + table.name*/

                "ALTER TABLE " + table.name + " DROP COLUMN " + columnName

                /*"CREATE TEMPORARY TABLE _backup(" + keepColumns + ");\n" +
                "INSERT INTO _backup SELECT " + keepColumns + " FROM " + table.name + ";\n" +
                "DROP TABLE " + table.name + ";\n" +
                "CREATE TABLE " + table.name + "(" + keepColumns + ");\n" +
                "INSERT INTO " + table.name + " SELECT " + keepColumns + " FROM _backup;\n" +
                "DROP TABLE _backup;\n"*/

                );

        //db.setTransactionSuccessful();
        //db.endTransaction();

        db.close();
    }

    /**
     * Add row to teh table with default values
     * @param table Table to modify
     */
    public void addRow(Table table)
    {
        SQLiteDatabase db = handler.getWritableDatabase();

        db.execSQL("INSERT INTO " + table.name + " DEFAULT VALUES");

        db.close();
    }
}
