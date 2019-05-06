/*
 * Copyright (C) 2018 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ethanmck.a382finalproject;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ethanmck.a382finalproject.Notecard;
import com.ethanmck.a382finalproject.R;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.support.constraint.Constraints.TAG;

/***
 * The adapter class for the RecyclerView, contains the notecard data.
 */
class NotecardAdapter extends RecyclerView.Adapter<NotecardAdapter.ViewHolder>  {

    // Member variables.
    private List<Notecard> mNotecardData;
    private Context mContext;
    private ImageView mNotecardImage;
    private final LayoutInflater mInflater;
    public static byte[] currentImg;

    /**
     * Constructor that passes in the notecard data and the context.
     *
     * @param context Context of the application.
     */
    NotecardAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
    }


    /**
     * Required method for creating the viewholder objects.
     *
     * @param parent The ViewGroup into which the new View will be added
     *               after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return The newly created ViewHolder.
     */
    @Override
    public NotecardAdapter.ViewHolder onCreateViewHolder(
            ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.notecard, parent, false);
        return new ViewHolder(itemView);
    }

    /**
     * Required method that binds the data to the viewholder.
     *
     * @param holder The viewholder into which the data should be put.
     * @param position The adapter position.
     */
    @Override
    public void onBindViewHolder(NotecardAdapter.ViewHolder holder,
                                 int position) {
        TypedArray imageResources = mContext.getResources().obtainTypedArray(R.array.notecard_imgs);
        if (mNotecardData != null) {
            // Get current notecard.
            Notecard currentNotecard = mNotecardData.get(position);

            // Add notecard title.
            holder.mTitleText.setText(currentNotecard.getTitle());

            // Switch note type based on type.
            switch (currentNotecard.getType()){
                case 't':

                    // If the text is of appropriate size, add it as is.
                    if (currentNotecard.getContent().length() < 250)
                        holder.mInfoText.setText(currentNotecard.getContent());
                    // Otherwise, cut it down.
                    else
                        holder.mInfoText.setText(currentNotecard.getContent().substring(0,250) + "...");
                    // Add the type.
                    holder.mType.setText("Text");
                    break;
                case 'i':
                    holder.mType.setText("Photo");
                    break;
                case 'l':
                    holder.mType.setText("Link");
                    // Populate the data.
                    holder.mTitleText.setText(currentNotecard.getTitle());
                    holder.mInfoText.setText(currentNotecard.getContent());
                    holder.mInfoText.setMovementMethod(LinkMovementMethod.getInstance());
                    break;
                case 'w':
                    holder.mType.setText("Web Photo");
                    break;
                default:
                    holder.mType.setText("Unknown");
                    break;
            }

            // If the notecard is an image (i) notecard, load it into the imageview.
            if (currentNotecard.getType()=='i') {

                // Decode the byte array that it is stored in.
                Bitmap bmp = BitmapFactory.decodeByteArray(currentNotecard.getImage(),0,currentNotecard.getImage().length);

                // Load it into the imageview.
                holder.mImage.setImageBitmap(Bitmap.createScaledBitmap(bmp, 500,
                        200, false));

                // Clean it up.
                bmp.recycle();
            }

            // If the notecard is a web photo type (w) load it from the web
            // and into the imageview.
            else if (currentNotecard.getType() == 'w') {

                // Create ann async task to do it off the main thread.
                RetrieveWebPhotoTask r = new RetrieveWebPhotoTask();
                Drawable d;

                // Try executing the async task.
                try {
                    d = r.execute(currentNotecard.getContent()).get();
                }
                // if it fails, inform the user.
                catch (Exception e) {
                    d = null;
                    e.printStackTrace();
                }

                // If the drawable does get filled, add it to the imageview.
                if (d != null) {
                    holder.mImage.setImageDrawable(d);
                    holder.mInfoText.setText("");
                }

                // Otherwise, glide the default image in.
                else
                    Glide.with(mContext).load(imageResources.getResourceId(0,1)).into(holder.mImage);

            }

            // If all else fails, glide the default image in.
            else {
                Glide.with(mContext).load(imageResources.getResourceId(0,1)).into(holder.mImage);
            }
        }

        // If nothing else, insert default content
        else {
            holder.mTitleText.setText("Title");
            holder.mInfoText.setText("Content");
            holder.mType.setText("Type");
        }
    }

    /**
     * Method for setting a new notecard data source.
     * @param notecards
     */
    void setNotecards (List<Notecard> notecards)
    {
        mNotecardData = notecards;
        notifyDataSetChanged();
    }

    /**
     * Required method for determining the size of the data set.
     *
     * @return Size of the data set.
     */
    @Override
    public int getItemCount() {
        if (mNotecardData != null)
            return mNotecardData.size();
        else return 0;
    }


    /**
     * ViewHolder class that represents each row of data in the RecyclerView.
     */
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Member Variables for the TextViews
        private TextView mTitleText;
        private TextView mInfoText;
        private TextView mType;
        private ImageView mImage;

        /**
         * Constructor for the ViewHolder, used in onCreateViewHolder().
         *
         * @param itemView The rootview of the list_item.xml layout file.
         */
        ViewHolder(View itemView) {
            super(itemView);
            // Initialize the views.
            mTitleText = itemView.findViewById(R.id.title);
            mInfoText = itemView.findViewById(R.id.subTitle);
            mType = itemView.findViewById(R.id.noteType);
            mImage = itemView.findViewById(R.id.mainImg);

            // Set on click listener to  the entire view.
            itemView.setOnClickListener(this);
        }

        /**
         * Allows the cards clickable and, thus, expandable.
         * @param v
         */
        @Override
        public void onClick(View v) {

            // Get the current notecard.
            Notecard currentNotecard = mNotecardData.get(getAdapterPosition());

            // If it is a text notecard, it can be expanded.
            if (currentNotecard.getType() == 't') {

                // Create an intent to go to  the detail activity.
                Intent detailIntent = new Intent(mContext, DetailActivity.class);
                detailIntent.putExtra("title", currentNotecard.getTitle());
                detailIntent.putExtra("content", currentNotecard.getContent());
                detailIntent.putExtra("type", "Text");
                detailIntent.putExtra("genid", currentNotecard.getGenId());
                detailIntent.putExtra("unid", currentNotecard.getUnId());
                mContext.startActivity(detailIntent);
            }
            else if (currentNotecard.getType() == 'l') {

                // Create an intent to go to  the detail activity.
                Intent detailIntent = new Intent(mContext, DetailActivity.class);
                detailIntent.putExtra("title", currentNotecard.getTitle());
                detailIntent.putExtra("content", currentNotecard.getContent());
                detailIntent.putExtra("type", "Link");
                detailIntent.putExtra("genid", currentNotecard.getGenId());
                detailIntent.putExtra("unid", currentNotecard.getUnId());
                detailIntent.putExtra("order", currentNotecard.getOrder());
                mContext.startActivity(detailIntent);
            }
            else if (currentNotecard.getType() == 'i') {

                // Create an intent to go to the photo detail activity.
                Intent detailIntent = new Intent(mContext, PhotoDetail.class);
                detailIntent.putExtra("title", currentNotecard.getTitle());
                detailIntent.putExtra("content", currentNotecard.getContent());
                detailIntent.putExtra("type", "Image");
                detailIntent.putExtra("genid", currentNotecard.getGenId());
                detailIntent.putExtra("unid", currentNotecard.getUnId());
                detailIntent.putExtra("order", currentNotecard.getOrder());

                // Sending a massive byte array in an intent will crash the program,
                // so I load the img into a public static variable to be accessed in
                // the next activity.
                currentImg=currentNotecard.getImage();
                mContext.startActivity(detailIntent);
            }
            else if (currentNotecard.getType() == 'w') {

                // Create an intent to go to the photo detail activity.
                Intent detailIntent = new Intent(mContext, PhotoDetail.class);
                detailIntent.putExtra("title", currentNotecard.getTitle());
                detailIntent.putExtra("content", currentNotecard.getContent());
                detailIntent.putExtra("type", "Web Photo");
                detailIntent.putExtra("genid", currentNotecard.getGenId());
                detailIntent.putExtra("unid", currentNotecard.getUnId());
                detailIntent.putExtra("order", currentNotecard.getOrder());
                mContext.startActivity(detailIntent);
            }

        }

        /*
        void bindTo(Notecard currentNotecard){
            // Populate the textviews with data.
            mTitleText.setText(currentNotecard.getTitle());
            mInfoText.setText(currentNotecard.getContent());
            Glide.with(mContext).load(currentNotecard.getImageResource()).into(mNotecardImage);

        }
        */
    }

    /**
     * Method for returning a notecard at a given position.
     *      * Used when deleting a specific notecard. *
     * @param position - requested position.
     * @return
     */
    public Notecard getNotecardAtPosition (int position) {
        return mNotecardData.get(position);
    }

    /**
     * A method for returning the internal notecard list.
     * @return
     */
    List<Notecard> getNotecards() { return this.mNotecardData; }

    /**
     * A class for retrieving a web photo asynchronously.
     */
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
