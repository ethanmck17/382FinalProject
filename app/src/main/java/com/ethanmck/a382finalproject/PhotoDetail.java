package com.ethanmck.a382finalproject;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.InputStream;
import java.net.URL;

public class PhotoDetail extends AppCompatActivity {

    EditText noteTitle;
    EditText noteContent;
    TextView noteType;
    ImageView noteImage;
    Context mContext;
    int genId, unId, order;
    String title, content;
    char type;
    byte[] img;
    private NotecardViewModel mNotecardViewModel;
    public static final String ESTABLISHED_CODE =
            "com.ethanmck.a382finalproject.extra.ESTABLISHED_CODE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        mContext = PhotoDetail.this;

        // Get the ViewModel.
        mNotecardViewModel = ViewModelProviders.of(this).get(NotecardViewModel.class);


        // Initialize layout element variables.
        noteImage = findViewById(R.id.photo_detail_img);
        noteTitle = findViewById(R.id.photo_detail_title);

        // Get info  from intents.
        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        genId = getIntent().getIntExtra("genid", 0);
        unId = getIntent().getIntExtra("unid", 0);
        type = getIntent().getStringExtra("type").charAt(0);
        order = getIntent().getIntExtra("order",0);

        // Set title editText.
        noteTitle.setText(title);

        // Update the image based on whether it is a user image or a  web image.
        if (type == 'I') {
            //img = getIntent().getByteArrayExtra("img");
            //noteTitle.setText(getIntent().getStringExtra("title"));
            //noteType.setText(getIntent().getStringExtra("type") + " Note");
            //Glide.with(this).load(imageResources.getResourceId(0,1)).into(noteImage);
            Bitmap bmp = BitmapFactory.decodeByteArray(NotecardAdapter.currentImg, 0, NotecardAdapter.currentImg.length);

            noteImage.setImageBitmap(Bitmap.createScaledBitmap(bmp, 500,
                    200, false));
            bmp.recycle();
        }
        else if (type == 'W') {
            PhotoDetail.RetrieveWebPhotoTask r = new PhotoDetail.RetrieveWebPhotoTask();
            Drawable d;
            try {
                d = r.execute(content).get();
            } catch (Exception e) {
                d = null;
                e.printStackTrace();
            }
            if (d != null) {
                noteImage.setImageDrawable(d);
            }
            else {
                // Create intent to return to the list.
                Intent intent = new Intent(mContext, ListNotecards.class);
                intent.putExtra(ESTABLISHED_CODE, genId);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onBackPressed() {

        Notecard newNotecard;
        title = String.valueOf(noteTitle.getText());

        // Update the notecard.
        switch (type) {
            case 'W':
                newNotecard = new Notecard('w',genId,unId,title,content);
                newNotecard.setOrder(order);
                break;
            case 'I':
                newNotecard = new Notecard('i',genId,unId,title,NotecardAdapter.currentImg);
                newNotecard.setOrder(order);
                break;
            default:
                newNotecard = new Notecard('l',genId,unId, title,content);
                newNotecard.setOrder(order);
        }
        mNotecardViewModel.updateNotecard(newNotecard);

        // Create intent to return to the list.
        Intent intent = new Intent(mContext, ListNotecards.class);
        intent.putExtra(ESTABLISHED_CODE, genId);
        startActivity(intent);
    }

    class RetrieveWebPhotoTask extends AsyncTask<String, Void, Drawable> {
        private Exception exception;

        protected Drawable doInBackground(String... urls) {
            try {
                InputStream is = (InputStream) new URL(urls[0]).getContent();
                Drawable d = Drawable.createFromStream(is, "src name");
                return d;
            }
            catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
