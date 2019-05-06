package com.ethanmck.a382finalproject;


import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.util.Log;

@Database(entities = {Notecard.class}, version=5, exportSchema = false)
public abstract class NotecardRoomDatabase extends RoomDatabase {

    public abstract NotecardDao notecardDao();
    private final String TAG = "NotecardDB";
    private static NotecardRoomDatabase INSTANCE;

    public static NotecardRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (NotecardRoomDatabase.class) {
                if (INSTANCE == null)
                {
                    // Create database here.
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            NotecardRoomDatabase.class, "notecard_database")
                            // Wipes and rebuilds instead of migrating
                            // if no Migration object.
                            // Migration is not performed.
                            .fallbackToDestructiveMigration()
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback() {

                @Override
                public void onOpen(@NonNull SupportSQLiteDatabase db) {
                    super.onOpen(db);
                    new PopulateDbAsync(INSTANCE).execute();
                }
            };

    /**
     * Class for providing sample data to begin with.
     */
    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final NotecardDao mDao;

        Notecard[] notecards = {
                new Notecard('t',12345, "A notecard", "The content")
        };

        PopulateDbAsync(NotecardRoomDatabase instance) {
            mDao = instance.notecardDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // If there are no notecards, create initial list.
            if (mDao.getAnyNotecard().length < 1) {
                for (int i = 0; i <= notecards.length - 1; i++) {
                    mDao.insert(notecards[i]);
                }
            }
            return null;
        }
    }
}
