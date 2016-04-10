package figat.pl.mobilesql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class Model {

    DatabaseHandler handler;
    ArrayList<Table> tables;

    public Model(Context context)
    {
        handler = new DatabaseHandler(context);
        tables = new ArrayList<>();
    }

    /**
     * Tries to find table with given name
     * @param name Table name to find
     * @return Found table or null
     */
    public Table FindTable(String name) {
        Table result = null;
        for (int i = 0; i < tables.size(); i++) {
            if (tables.get(i).Name == name) {
                result = tables.get(i);
                break;
            }
        }
        return result;
    }

    /**
     * Creates new sql database table
     *
     * @param name New table name
     * @return New table
     */
    public Table CreateTable(String name) {

        // Create SQL table
        SQLiteDatabase db = handler.getWritableDatabase();
        db.execSQL("CREATE TABLE " + name + "( PersonID int );");
        db.close();

        // Create table entry
        Table table = new Table();
        table.Name = name;
        table.EntriesCount = 0;
        tables.add(table);

        return table;
    }
}
