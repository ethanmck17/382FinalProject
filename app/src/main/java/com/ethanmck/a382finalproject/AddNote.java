package com.ethanmck.a382finalproject;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Random;

public class AddNote extends AppCompatActivity {

    // Declare variables.
    private NotecardViewModel mNotecardViewModel;
    private final String TAG = "AddNote";
    int currentlyVisible = 0;
    int currentCode, brandNewCode;
    int  numNotes;
    TextView textTargetUri;
    ImageView targetImage;
    Bitmap currentBmp;
    public static final String ESTABLISHED_CODE =
            "com.ethanmck.a382finalproject.extra.ESTABLISHED_CODE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        currentlyVisible = R.id.text_layout;
        //textTargetUri = (TextView)findViewById(R.id.targeturi);
        targetImage = (ImageView)findViewById(R.id.targetimage);

        // Get a viewModel.
        mNotecardViewModel = ViewModelProviders.of(this).get(NotecardViewModel.class);

        //.of(this).get(NotecardViewModel.class);

        // Get the intent.
        Intent intent = getIntent();

        // Determine whether a new code needs generated.
        currentCode = intent.getIntExtra(MainActivity.ESTABLISHED_CODE, -1);

        // If the current code is -1, that means a new code is being created.
        if (currentCode == -1) {
            currentCode = genNewCode();
            numNotes = 0;
        }
        else {
            numNotes = intent.getIntExtra(ListNotecards.NUM_NOTES, 0);
        }

        TextView currentCodeText = findViewById(R.id.code_text);
        currentCodeText.setText("Current Code: " + currentCode);
    }

    /**
     * Method to create a text note.
     * @param view
     */
    public void submitText(View view) {

        // Get the title and content from the form.
        TextView title = findViewById(R.id.text_title_edit);
        TextView content = findViewById(R.id.text_content_edit);

        // Get the values of the inputs.
        String titleStr = String.valueOf(title.getText());
        String contentStr = String.valueOf(content.getText());

        // Check whether all forms are complete.
        if (titleStr.isEmpty() || contentStr.isEmpty()) {
            Toast.makeText(this, "Please complete all fields.",
                    Toast.LENGTH_SHORT).show();
        }
        else {
            // Create a new notecard based on the values.
            Notecard newCard = new Notecard('t', currentCode, titleStr, contentStr);


            // Set the order of the notecard
            newCard.setOrder(mNotecardViewModel.getAllNotecards().getValue().get(0).getOrder()+1);

            // Insert the card.
            mNotecardViewModel.insert(newCard);

            // Return to list notecards
            Intent intent = new Intent(this, ListNotecards.class);
            intent.putExtra(ESTABLISHED_CODE, currentCode);

            // Begin the activity.
            startActivity(intent);
        }
    }

    /**
     * Method to create a link note.
     * @param view
     */
    public void submitLink(View view) {

        Notecard newCard;

        // Get the title and content from the form.
        TextView title = findViewById(R.id.link_title_edit);
        TextView content = findViewById(R.id.link_content_edit);

        // Get the values of the inputs.
        String titleStr = String.valueOf(title.getText());
        String contentStr = String.valueOf(content.getText());

        // Check whether all forms are complete.
        if (titleStr.isEmpty() || contentStr.isEmpty()) {
            Toast.makeText(this, "Please complete all fields.",
                    Toast.LENGTH_SHORT).show();
        }
        else {
            // Check if user submitted a img url.
            // Create a new notecard based on the values.
            String fileExt = contentStr.substring(contentStr.length() - 3);
            fileExt = fileExt.toLowerCase();
            Log.d(TAG, "submitLink: " + fileExt);
            if (fileExt.equals("jpg") || fileExt.equals("png")) {
                newCard = new Notecard('w', currentCode, titleStr, contentStr);
            } else {
                newCard = new Notecard('l', currentCode, titleStr, contentStr);
            }

            // Set the order of the notecard
            newCard.setOrder(mNotecardViewModel.getAllNotecards().getValue().get(0).getOrder()+1);

            // Insert the card.
            mNotecardViewModel.insert(newCard);

            // Return to list notecards
            Intent intent = new Intent(this, ListNotecards.class);
            intent.putExtra(ESTABLISHED_CODE, currentCode);

            // Begin the activity.
            startActivity(intent);
        }
    }

    public void submitImage(View view) {

        // Get title from form.
        TextView title = findViewById(R.id.image_title_edit);

        // Get content from title.
        String titleStr = String.valueOf(title.getText());

        // Check whether all forms are complete.
        if (titleStr.isEmpty() || currentBmp == null) {
            Toast.makeText(this, "Please complete all forms.",
                    Toast.LENGTH_SHORT).show();
        }
        else {
            // Get byte array from image.
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            currentBmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] img = stream.toByteArray();
            currentBmp.recycle();

            // Create new notecard.
            Notecard n = new Notecard('i', currentCode, titleStr, img);

            // Set the order of the notecard
            n.setOrder(mNotecardViewModel.getNotecards(currentCode).getValue().get(0).getOrder()+1);

            // Insert the new card.
            mNotecardViewModel.insert(n);

            // Return to list notecards
            Intent intent = new Intent(this, ListNotecards.class);
            intent.putExtra(ESTABLISHED_CODE, currentCode);

            // Begin the activity.
            startActivity(intent);
        }
    }

    /**
     * Method  for updating the add note form based on input in the radio group.
     * @param view
     */
    public void updateNoteForm(View view) {

        // Make the currently visible layout invisible.
        LinearLayout currentlyVisibleLL = findViewById(currentlyVisible);
        currentlyVisibleLL.setVisibility(View.INVISIBLE);

        // Get the radio group.
        RadioGroup rg = findViewById(R.id.note_type_rg);

        // Find the button that is checked.
        RadioButton rb = findViewById(rg.getCheckedRadioButtonId());

        // Determine what the text of that button is.
        String name = String.valueOf(rb.getText());
        Log.d(TAG, "updateNoteForm: name char at 0: " + name.charAt(0));
        switch (name.charAt(0)) {

            // Text: make the text form visible.
            case 'T':
                findViewById(R.id.text_layout).setVisibility(View.VISIBLE);
                currentlyVisible = R.id.text_layout;
                break;
            // Link: make the link form visible.
            case 'L':
                findViewById(R.id.link_layout).setVisibility(View.VISIBLE);
                currentlyVisible = R.id.link_layout;
                break;
            // Image: make the image form visible.
            case 'I':
                findViewById(R.id.img_layout).setVisibility(View.VISIBLE);
                currentlyVisible = R.id.img_layout;
                break;
        }
    }

    /**
     * Method for generating a new notecard code that is not in use.
     * @return - a code that is not in use.
     */
    private int genNewCode() {

        // Generate 6 digit random integer.
        Random r = new Random();
        int newCode = (int) (Math.random()*900000) + 100000;

        // Determine if code has already been used.
        // If so, get a new code.
        // Otherwise, return the code generated.
        //if (mNotecardViewModel.codeExists(newCode))
        //    return genNewCode();
        //else
            return newCode;
        /*
        // Determine if that code has already been used
        final int length;
        mNotecardViewModel.getNotecards(newCode).observe(this, new Observer<List<Notecard>>() {
            @Override
            public void onChanged(@Nullable List<Notecard> notecards) {
                length = notecards.size();
            }
        });

        // If the code returns no rows, the code is not in use, return it.
        if (the_list.isEmpty())
            return newCode;
        // Otherwise, return a different code.
        else
            return genNewCode();
        /*mNotecardViewModel.getNotecards(brandNewCode).observe(this, new Observer<List<Notecard>>() {
            @Override
            public void onChanged(@Nullable List<Notecard> notecards) {
            }
        });*/
    }

    public void uploadImage(View view) {
        // TODO Auto-generated method stub
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Uri targetUri = data.getData();
            //textTargetUri.setText(targetUri.toString());
            try {
                currentBmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                targetImage.setImageBitmap(currentBmp);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                Toast.makeText(this, "File not found...",
                            Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
}
