package com.ethanmck.a382finalproject;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListNotecards extends AppCompatActivity {

    // Member variables.
    private RecyclerView mRecyclerView;
    private List<Notecard> mNotecardData;
    private NotecardAdapter mAdapter;
    private NotecardViewModel mNotecardViewModel;
    int code;
    byte[] currentImg;
    int numNotes;
    Context mContext;
    private String TAG = "ListNotecardsActivity";
    int orderNum;


    public static final String ESTABLISHED_CODE =
            "com.ethanmck.a382finalproject.extra.ESTABLISHED_CODE";
    public static final String NUM_NOTES =
            "com.ethanmck.a382finalproject.extra.NUM_NOTES";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: beginning");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_notecards);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContext = this;

        // Get intent.
        Intent intent = getIntent();

        // Get code from mainActivity.
        code = intent.getIntExtra(MainActivity.ESTABLISHED_CODE, 12345);
        Log.d(TAG, "onCreate: code received: " + code);

                // Initialize the RecyclerView.
        mRecyclerView = findViewById(R.id.recyclerView);

        // Set the Layout Manager.
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter and set it to the RecyclerView.
        mAdapter = new NotecardAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        // Get a viewModel.
        mNotecardViewModel = ViewModelProviders.of(this).get(NotecardViewModel.class);

        // Get the notecards and add them to the adapter.
        mNotecardViewModel.getNotecards(code).observe(this, new Observer<List<Notecard>>() {
            @Override
            public void onChanged(@Nullable List<Notecard> notecards) {
                numNotes = notecards.size();
                mAdapter.setNotecards(notecards);
                mNotecardData = notecards;
                Log.d(TAG, "onChanged: length: " +  notecards.size());
            }
        });

        // Allow items in the recycler view to be arranged and swiped.
        // Drag and drop to rearrange notes.
        // A swipe deletes the notecard.
        ItemTouchHelper helper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN |
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView,
                                          @NonNull RecyclerView.ViewHolder viewHolder,
                                          @NonNull RecyclerView.ViewHolder target) {
                        // Swap the items in the recycler view.
                        int from = viewHolder.getAdapterPosition();
                        int to = target.getAdapterPosition();
                        Collections.swap(mAdapter.getNotecards(), from, to);

                        // Swap the items unique ids, so that the order stays.
                        int temp = mAdapter.getNotecardAtPosition(from).getOrder();
                        int temp2 = mAdapter.getNotecardAtPosition(to).getOrder();
                        mAdapter.getNotecardAtPosition(from).setOrder(temp2);
                        mAdapter.getNotecardAtPosition(to).setOrder(temp);
                        mAdapter.notifyItemMoved(from,to);
                        return true;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder,
                                         int i) {
                        // Get the position of the swipe.
                        int position = viewHolder.getAdapterPosition();

                        // Get notecard at position.
                        Notecard myNotecard = mAdapter.getNotecardAtPosition(position);

                        // Inform the user.
                        Toast.makeText(ListNotecards.this, "Deleting Notecard...",
                                Toast.LENGTH_LONG).show();

                        // Delete the notecard.
                        mNotecardViewModel.deleteNotecard(myNotecard);
                    }
                });

        // Attach it to the recycler view.
        helper.attachToRecyclerView(mRecyclerView);

        /*mNotecardViewModel.getNotecards(code).observe(this, new Observer<List<Notecard>>() {
            @Override
            public void onChanged(@Nullable List<Notecard> notecards) {
                mAdapter.setNotecards(notecards);
            }
        });*/

        // Get the data.
        //initializeData();
    }

    void setNotecards(int code) {


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list_menu, menu);
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
        else if (id == R.id.clear_data) {

            // If the user wants to clear all data, alert them first.
            // Create the alert dialog.
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

            // Add the text.
            builder.setMessage("Delete all data?");

            // Add the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    Toast.makeText(mContext, "Clearing all data...",
                            Toast.LENGTH_SHORT).show();
                    mNotecardViewModel.deleteAll();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            });
            AlertDialog dialog = builder.create();
            return true;
        }
        else if (id == R.id.action_home) {

            // Start the Main activity.
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void searchCodes(View view) {
    }

    public void addNote(View view) {

        // Start the AddNote activity.
        Intent intent = new Intent(this, AddNote.class);

        // Use -1 for the code to let the program know a new code must be created.
        intent.putExtra(ESTABLISHED_CODE, code);

        // Include the current number of notes.
        intent.putExtra(NUM_NOTES, numNotes);

        // Start the activity.
        startActivity(intent);

    }

    @Override
    public void onBackPressed() {

        // Create intent to return to the list.
        Intent intent = new Intent(mContext, MainActivity.class);
        startActivity(intent);
    }
}
