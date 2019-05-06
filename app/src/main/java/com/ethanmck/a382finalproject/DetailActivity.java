package com.ethanmck.a382finalproject;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class DetailActivity extends AppCompatActivity {

    EditText noteTitle;
    EditText noteContent;
    TextView noteType;
    ImageView noteImage;
    Context mContext;
    int genId, unId, order;
    String title, content;
    char type;
    private NotecardViewModel mNotecardViewModel;
    public static final String ESTABLISHED_CODE =
            "com.ethanmck.a382finalproject.extra.ESTABLISHED_CODE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        TypedArray imageResources = this.getResources().obtainTypedArray(R.array.notecard_imgs);

        mContext = DetailActivity.this;

        // Get the ViewModel.
        mNotecardViewModel = ViewModelProviders.of(this).get(NotecardViewModel.class);


        // Initialize layout element variables.
        noteTitle = findViewById(R.id.titleDetail);
        noteContent = findViewById(R.id.subTitleDetail);
        noteType = findViewById(R.id.noteTypeDetail);
        noteImage = findViewById(R.id.mainImgDetail);

        // Get info  from intents.
        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        genId = getIntent().getIntExtra("genid", 0);
        unId = getIntent().getIntExtra("unid", 0);
        type = getIntent().getStringExtra("type").charAt(0);
        order = getIntent().getIntExtra("order",0);

        // Set layout items.
        noteTitle.setText(getIntent().getStringExtra("title"));
        noteContent.setText(getIntent().getStringExtra("content"));
        noteType.setText(getIntent().getStringExtra("type") + " Note");
        Glide.with(this).load(imageResources.getResourceId(0,1)).into(noteImage);
    }

    @Override
    public void onBackPressed() {

        Notecard newNotecard;
        title = String.valueOf(noteTitle.getText());
        content = String.valueOf(noteContent.getText());

        // Update the notecard.
        switch (type) {
            case 'T':
                newNotecard = new Notecard('t',genId, unId, title,content);
                newNotecard.setOrder(order);
                break;
            case 'W':
                newNotecard = new Notecard('w',genId,unId,title,content);
                newNotecard.setOrder(order);
                break;
            case 'L':
                newNotecard = new Notecard('l',genId,unId,title,content);
                newNotecard.setOrder(order);
                break;
            default:
                newNotecard = new Notecard('t',genId,unId, title,content);
                newNotecard.setOrder(order);
        }
        mNotecardViewModel.updateNotecard(newNotecard);

        // Create intent to return to the list.
        Intent intent = new Intent(mContext, ListNotecards.class);
        intent.putExtra(ESTABLISHED_CODE, genId);
        startActivity(intent);
    }

    void updateNotecard() {

    }
}
