package com.ethanmck.a382finalproject;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;



@Entity(tableName = "notecard_table")
public class Notecard {

    // Title of the note.
    @NonNull
    @ColumnInfo(name = "title")
    private String title;

    /* Type of note. Can be:
     * 't' = text
     * 'i' = image
     * 'l' = link
     */
    @NonNull
    @ColumnInfo(name = "type")
    private char type;

    // genId = the basic code
    // unId = the code specific to this notecard.
    @NonNull
    @ColumnInfo(name="gen_id")
    private int genId;

    @NonNull
    @PrimaryKey(autoGenerate=true)
    @ColumnInfo(name="un_id")
    private int unId;

    // If it is a photo note, it will be stored here.
    //private Bitmap img;

    // Text content, if it is a text note.
    @ColumnInfo(name = "content")
    private String content;

    // Link, if it is a link note.
    @ColumnInfo(name = "link")
    private String link;

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    private byte[] image;

    // Stores the order that the note is to be stored in.
    @ColumnInfo(name="order")
    private int order;

    // For testing purposes.
    @Ignore
    private int imageResource;

    public void setImage(byte[] image) {
        this.image = image;
    }

    /**
     * Constructor for photo notes.
     * @param type
     * @param genId
     * @param title
     * @param image
     */
    public Notecard(char type, int genId, @NonNull String title, @NonNull byte[] image) {
        this.type = type;
        this.genId = genId;
        this.image = image;
        this.title = title;
        this.order = 0;
    }

    /**
     * Constructor for photo notes, when the unId is provided.
     * @param type
     * @param genId
     * @param unId
     * @param image
     */
    @Ignore
    public Notecard(char type, int genId, int unId, @NonNull String title, @NonNull byte[] image) {
        this.type = type;
        this.genId = genId;
        this.unId = unId;
        this.image = image;
        this.title = title;
        this.order = 0;
    }

    /**
     * Constructor for link or text notes.
     * @param type
     * @param genId
     * @param title
     * @param content
     */
    public Notecard(char type, int genId, @NonNull String title, @NonNull String content) {
        this.title = title;
        this.type = type;
        this.genId = genId;
        this.content = content;
        this.order = 0;
    }

    /**
     * Constructor for link or text notes, when the unId is provided.
     * @param type
     * @param genId
     * @param unId
     * @param content
     */
    @Ignore
    public Notecard(char type, int genId, int unId, String title, String content) {
        this.title = title;
        this.type = type;
        this.genId = genId;
        this.unId = unId;
        this.content = content;
        this.order = 0;
    }

    // All getters follow.

    public String getTitle() {
        return title;
    }

    public char getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public String getLink() {
        return link;
    }

    public int getGenId() {
        return genId;
    }

    public int getUnId() {
        return unId;
    }

    public int getImageResource() { return imageResource; }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    public void setType(char type) {
        this.type = type;
    }

    public void setGenId(int genId) {
        this.genId = genId;
    }

    public void setUnId(int unId) {
        this.unId = unId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public byte[] getImage() {
        return image;
    }

    public int getOrder() { return order; }

    public void setOrder(int order) { this.order = order; }
}
