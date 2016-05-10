package figat.pl.mobilesql;

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
     * @return Amount of tables in a database
     */
    public int getTablesCount() {
        return tables.size();
    }

    /***
     * Gets table at given index
     * @param index Table index
     * @return Table
     */
    public Table getTable(int index) {
        return tables.get(index);
    }

    /**
     * Init
     * @param context Application context
     */
    public Model(Context context) {
        handler = new DatabaseHandler(context);
        tables = new ArrayList<>();

        handler.loadTables(tables);
    }

    /**
     * Tries to find table with given name
     * @param name Table name to find
     * @return Found table or null
     */
    public Table findTable(String name) {
        Table result = null;
        for (int i = 0; i < tables.size(); i++) {
            if (tables.get(i).Name.compareTo(name) == 0) {
                result = tables.get(i);
                break;
            }
        }
        return result;
    }

    /**
     * Creates new SQL database table
     * @param name New table name
     * @return New table
     */
    public Table createTable(String name) {

        // Create SQL table
        handler.createTable(name);

        // Create table entry
        Table table = new Table();
        table.Name = name;
        tables.add(table);

        return table;
    }

    /**
     * Deletes SQL table
     * @param table Table to remove
     */
    public void deleteTable(Table table) {

        // Delete SQL table
        handler.deleteTable(table.Name);

        // Remove table entry
        tables.remove(table);
    }

    /**
     * Perform query to the database to gather table data to teh cache
     * @param table Tabe to update
     */
    public void getTableData(Table table) {
        table.Cache = performQuery("SELECT * FROM " + table.Name);
    }

    /**
     * Performs SQL query to the database
     * @param sql SQL query text
     * @return Query result
     */
    public SqlQueryResult performQuery(String sql)
    {
        // Prepare
        SqlQueryResult result = new SqlQueryResult();
        SQLiteDatabase db = handler.getReadableDatabase();

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
}
