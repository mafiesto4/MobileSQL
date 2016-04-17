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
        tablesListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, tablesList);
        tablesListView.setAdapter(tablesListAdapter);
        tablesListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id)
            {
                String item = tablesListAdapter.getItem(position);
                Navigate(item);
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.viewTablesListAdd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCreateTableDialog();
            }
        });
        Toolbar toolbar = (Toolbar)findViewById(R.id.viewTablesListToolbar);
        setSupportActionBar(toolbar);

        // Table View
        tableViewTable = (TableLayout)findViewById(R.id.viewTableViewTable);

        // Register view
        Controller.getInstance().LinkView(context, this);
    }

    protected void showCreateTableDialog() {

        // Setup a dialog window
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.new_table, null);
        alertDialogBuilder.setView(promptView);
        final EditText input = (EditText) promptView.findViewById(R.id.userInput);
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Controller.getInstance().CreateTable(input.getText().toString());
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_exit) {
            finishAffinity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            // Check if is viewing a table
            if(currentView == viewTableView) {
                crossfade(viewTablesList);
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
        Navigate(Controller.getInstance().GetModel().FindTable(table));
    }

    @Override
    public void Navigate(Table table)
    {
        crossfade(viewTableView);

        //new AlertDialog.Builder(context).setTitle("Navigate to...").setMessage(table.Name).show();
    }

    @Override
    public void OnTableAlreadyExists()
    {
        new AlertDialog.Builder(context).setTitle("Error").setMessage("Table with that name already exists!").show();
    }

    @Override
    public void OnTablesModified() {
        tablesList.clear();
        Model model = Controller.getInstance().GetModel();
        for (int i = 0; i < model.GetTablesCount(); i++)
            tablesList.add(model.GetTable(i).Name);
        tablesListAdapter.notifyDataSetChanged();
    }

    @Override
    public void OnException(Exception ex, String info)
    {
        new AlertDialog.Builder(context).setTitle("Error").setMessage(info + "\n" + ex.getMessage()).show();
    }

    private void crossfade(View targetView) {

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
