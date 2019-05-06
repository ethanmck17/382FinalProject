package com.ethanmck.a382finalproject;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

/**
 * As noted by the Androids Fundamentals Codelab 10.1:
 *      "A Repository is a class that abstracts access to multiple data
 *      sources. The Repository is not part of the Architecture Components
 *      libraries, but is a suggested best practice for code separation and
 *      architecture. A Repository class handles data operations. It provides
 *      a clean API to the rest of the app for app data."
 * I use this class for handling operations on the notecard DB.
 */
public class NotecardRepository {

    // Declare variables.
    private NotecardDao mNotecardDao;
    private LiveData<List<Notecard>> mAllNotecards;

    NotecardRepository(Application application) {

        // Initialize variables.
        NotecardRoomDatabase db = NotecardRoomDatabase.getDatabase(application);
        mNotecardDao = db.notecardDao();

        // Get all notecards.
        mAllNotecards = mNotecardDao.getAllNotecards();
    }

    /**
     * @return all notecards in a LiveData list.
     */
    LiveData<List<Notecard>> getAllNotecards() {
        return mAllNotecards;
    }

    /**
     * @param genId - primary key used to retrieve a specific set of notecards.
     * @return - the noted set.
     */
    LiveData<List<Notecard>> getNotecards(int genId) {
        return mNotecardDao.getNotecard(genId);
    }

    boolean codeExists(int genId) {
        if (mNotecardDao.codeExists(genId).length > 0)
            return true;
        return false;
    }
    List<Notecard> getAllNotecardsQ() { return mNotecardDao.getAllNotecardsQ(); }
    List<Notecard> getNotecardQ(int genId) { return mNotecardDao.getNotecardQ(genId); }

    /**
     * Method for inserting a notecard into the db asynchronously.
     * @param notecard
     */
    public void insert(Notecard notecard) {
        new insertAsyncTask(mNotecardDao).execute(notecard);
    }

    /**
     * Method for deleting all notecards asynchronously.
     */
    public void deleteAll() {
        new deleteAllNotecardsAsyncTasks(mNotecardDao).execute();
    }

    /**
     * Method for updating a notecard asynchronously.
     */
    public void updateNotecard(Notecard notecard) {
        new updateNotecardAsyncTask(mNotecardDao).execute(notecard);
    }



    /**
     * Method for deleting a specific notecard asynchronously.
     * @param notecard
     */
    public void deleteNotecard(Notecard notecard) {
        new deleteNotecardAsyncTask(mNotecardDao).execute(notecard);
    }
    /**
     * Inner class for performing asynchronous insert tasks..
     */
    private static class insertAsyncTask extends AsyncTask<Notecard, Void, Void>
    {
        // Declare variables.
        private NotecardDao mAsyncDao;

        // Insert async task.
        insertAsyncTask(NotecardDao dao) {
            mAsyncDao = dao;
        }

        @Override
        protected Void doInBackground(final Notecard... params) {
            mAsyncDao.insert(params[0]);
            return null;
        }
    }

    /**
     * Inner class for performing asynchronous delete all task.
     */
    private static class deleteAllNotecardsAsyncTasks extends AsyncTask<Void, Void, Void> {

        // Declare variables.
        private NotecardDao mAsyncTaskDao;

        // Get the data access object.
        deleteAllNotecardsAsyncTasks(NotecardDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mAsyncTaskDao.deleteAll();
            return null;
        }
    }

    /**
     * Inner class for performing asynchronous task to delete a single notecard.
     */
    private static class deleteNotecardAsyncTask extends AsyncTask<Notecard, Void, Void> {
        private NotecardDao mAsyncTaskDao;

        deleteNotecardAsyncTask(NotecardDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Notecard... params) {
            mAsyncTaskDao.delete(params[0]);
            return null;
        }
    }

    /**
     * Inner class for performing asynchronous task to update a single notecard.
     */
    private static class updateNotecardAsyncTask extends AsyncTask<Notecard, Void, Void> {
        private NotecardDao mAsyncTaskDao;

        updateNotecardAsyncTask(NotecardDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Notecard... params) {
            mAsyncTaskDao.update(params[0]);
            return null;
        }
    }
}
