package figat.pl.mobilesql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class Model {

    DatabaseHandler handler;
    ArrayList<Table> tables;

    /**
     * Gets amount of tables
     * @return Amount of tables in a database
     */
    public int GetTablesCount()
    {
        return tables.size();
    }

    /***
     * Gets table at given index
     * @param index Table index
     * @return Table
     */
    public Table GetTable(int index)
    {
        return tables.get(index);
    }

    public Model(Context context)
    {
        handler = new DatabaseHandler(context);
        tables = new ArrayList<>();

        handler.loadTables(tables);
    }

    /**
     * Tries to find table with given name
     * @param name Table name to find
     * @return Found table or null
     */
    public Table FindTable(String name) {
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
     * Creates new sql database table
     *
     * @param name New table name
     * @return New table
     */
    public Table CreateTable(String name) {

        // Create SQL table
       handler.createTable(name);

        // Create table entry
        Table table = new Table();
        table.Name = name;
        table.EntriesCount = 0;
        tables.add(table);

        return table;
    }
}
