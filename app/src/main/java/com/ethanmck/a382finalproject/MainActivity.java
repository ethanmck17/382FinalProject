package com.ethanmck.a382finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText mCode;
    public static final String ESTABLISHED_CODE =
            "com.ethanmck.a382finalproject.extra.ESTABLISHED_CODE";
    public static final String IS_CODE_ESTABLISHED =
            "com.ethanmck.a382finalproject.extra.IS_CODE_ESTABLISHED";
    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: begun");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    /**
     * Begins the listNotecards activity using a provided code.
     * @param view
     */
    public void searchCodes(View view) {
        Log.d(TAG, "searchCodes: Starting...");

        // Get the code from the EditText.
        mCode = findViewById(R.id.code_edit);
        if (mCode.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter a code.",
                    Toast.LENGTH_SHORT).show();
        }
        else {
            int code = Integer.parseInt(mCode.getText().toString());

            Log.d(TAG, "searchCodes: " + code);

            // Add it to an intent.
            Intent intent = new Intent(this, ListNotecards.class);
            intent.putExtra(ESTABLISHED_CODE, code);

            // Begin the activity.
            startActivity(intent);
        }
    }

    /**
     * Method for creating a new code.
     * @param view
     */
    public void addCode(View view) {
        Log.d(TAG, "addCode: adding a new code...");

        // Start the AddNote activity.
        Intent intent = new Intent(this, AddNote.class);

        // Use -1 for the code to let the program know a new code must be created.
        intent.putExtra(ESTABLISHED_CODE, -1);
        startActivity(intent);
    }

    public void addNewCode(View view) {
    }
}
