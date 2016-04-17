package figat.pl.mobilesql;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.ViewDebug.CapturedViewProperty;
import android.view.ViewGroup.LayoutParams;
import android.widget.Adapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements IViewObject {

    private GoogleApiClient client;
    final Context context = this;

    // Switching views
    private View viewTablesList;
    private View viewTableView;
    private View currentView;
    private View previousView;
    private int mShortAnimationDuration;

    // Tables List
    private ListView tablesListView;
    private ArrayList<String> tablesList;
    private ArrayAdapter<String> tablesListAdapter;

    // Table View
    private TableLayout tableViewTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        // View changing
        viewTablesList = findViewById(R.id.viewTablesList);
        viewTableView = findViewById(R.id.viewTableView);
        viewTableView.setVisibility(View.GONE);
        currentView = viewTablesList;
        previousView = null;
        mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

        // Tables List
        tablesListView = (ListView) findViewById(R.id.viewTablesListTables);
        tablesList = new ArrayList<>();
        tablesListAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, tablesList);
        tablesListView.setAdapter(tablesListAdapter);
        tablesListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id)
            {
                String item = tablesListAdapter.getItem(position);
                Navigate(item);
            }
        });
        (findViewById(R.id.viewTablesListAdd)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCreateTableDialog();
            }
        });
        Toolbar tableListToolbar = (Toolbar)findViewById(R.id.viewTablesListToolbar);
        setSupportActionBar(tableListToolbar);

        // Table View
        tableViewTable = (TableLayout)findViewById(R.id.viewTableViewTable);
        (findViewById(R.id.viewTableViewAdd)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddEntry();
            }
        });
        Toolbar tableViewToolbar = (Toolbar)findViewById(R.id.viewTableViewToolbar);
        tableViewToolbar.inflateMenu(R.menu.menu_table);
        tableViewToolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        tableViewToolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showTablesList();
            }
        });

        // Register view
        Controller.getInstance().linkView(context, this);
    }

    private void showCreateTableDialog() {

        // Setup a dialog window
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.new_table, null);
        alertDialogBuilder.setView(promptView);
        final EditText input = (EditText) promptView.findViewById(R.id.userInput);
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Controller.getInstance().createTable(input.getText().toString());
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertD = alertDialogBuilder.create();

        // Show it
        alertD.show();
    }

    private void showAddEntry()
    {
        new AlertDialog.Builder(context).setTitle("TODO").setMessage("TODO: create new table entry").show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Table List
        if (id == R.id.action_exit) {
            finishAffinity();
            return true;
        }
        if(id == R.id.action_deleteTable) {
            new AlertDialog.Builder(context).setTitle("TODO").setMessage("TODO: delete current table").show();
            return true;
        }
        if(id == R.id.action_newEntry) {
            showAddEntry();
            return true;
        }
        if(id == R.id.action_querySql) {
            new AlertDialog.Builder(context).setTitle("TODO").setMessage("TODO: show new query SQL dialog").show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            // Check if is viewing a table
            if(currentView == viewTableView) {
                showTablesList();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://figat.pl.mobilesql/http/host/path"));
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://figat.pl.mobilesql/http/host/path"));
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    public void Navigate(String table)
    {
        Navigate(Controller.getInstance().getModel().findTable(table));
    }

    @Override
    public void Navigate(Table table) {
        // Check if show default page
        if (table == null) {
            showTablesList();
            return;
        }

        // Navigate to that view
        changeView(viewTableView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.viewTableViewToolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(table.Name);

        // Clear previous table
        tableViewTable.removeAllViews();

        try {

            // Get all table data
            Controller.getInstance().getModel().getTableData(table);

            // Add header row
            TableRow headerRow = new TableRow(this);
            headerRow.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            for (int i = 0; i < table.ColumnNames.length; i++) {
                TextView tv = new TextView(this);
                tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                //tv.setBackgroundResource(R.drawable.cell_shape);
                tv.setGravity(Gravity.CENTER);
                tv.setTextSize(20);
                tv.setPadding(0, 5, 0, 5);
                tv.setText(table.ColumnNames[i]);
                headerRow.addView(tv);
            }
            tableViewTable.addView(headerRow);

            // Fill table (row by row)
            for (int i = 0; i < table.Data.size(); i++) {

                TableRow row = new TableRow(this);
                row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                String[] rowData = table.Data.get(i);

                // Add all columns
                for (int j = 0; j < rowData.length; j++) {

                    TextView tv = new TextView(this);
                    tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                    //tv.setBackgroundResource(R.drawable.cell_shape);
                    tv.setGravity(Gravity.CENTER);
                    tv.setTextSize(18);
                    tv.setPadding(0, 5, 0, 5);
                    tv.setText(rowData[j]);

                    row.addView(tv);
                }

                tableViewTable.addView(row);
            }
        } catch (Exception ex) {
            OnException(ex, "Cannot show table " + table.Name);
        }
    }

    @Override
    public void OnTableAlreadyExists()
    {
        new AlertDialog.Builder(context).setTitle("Error").setMessage("Table with that name already exists!").show();
    }

    @Override
    public void OnTablesModified() {
        tablesList.clear();
        Model model = Controller.getInstance().getModel();
        for (int i = 0; i < model.getTablesCount(); i++)
            tablesList.add(model.getTable(i).Name);
        tablesListAdapter.notifyDataSetChanged();
    }

    @Override
    public void OnException(Exception ex, String info)
    {
        new AlertDialog.Builder(context).setTitle("Error").setMessage(info + "\n" + ex.getMessage()).show();
    }

    private void showTablesList()
    {
        changeView(viewTablesList);
        setSupportActionBar((Toolbar)findViewById(R.id.viewTablesListToolbar));
    }

    private void changeView(View targetView) {

        // Check if view won't change
        if(targetView == currentView)
            return;

        // Set new current view
        previousView = currentView;
        currentView = targetView;

        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        currentView.setAlpha(0f);
        currentView.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        currentView.animate()
                .alpha(1f)
                .setDuration(mShortAnimationDuration)
                .setListener(null);

        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)
        previousView.animate()
                .alpha(0f)
                .setDuration(mShortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        previousView.setVisibility(View.GONE);
                    }
                });
    }
}
