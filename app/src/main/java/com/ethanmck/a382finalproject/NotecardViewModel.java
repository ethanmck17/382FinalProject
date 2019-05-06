package com.ethanmck.a382finalproject;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.util.Log;

import java.util.List;

/**
 * A ViewModel class for safely providing data to the UI.
 */
public class NotecardViewModel extends AndroidViewModel {

    // Declare variables.
    private NotecardRepository mRepository;
    private LiveData<List<Notecard>> mAllNotecards;
    private final String TAG = "NotecardViewModel";

    /**
     * Constructor.
     * @param application
     */
    public NotecardViewModel(Application application) {
        super(application);
        Log.d(TAG, "NotecardViewModel: creating repository");
        mRepository = new NotecardRepository(application);
        Log.d(TAG, "NotecardViewModel: getting notecards");
        mAllNotecards = mRepository.getAllNotecards();
    }

    // DB queries.
    LiveData<List<Notecard>> getAllNotecards() { return mAllNotecards; }
    LiveData<List<Notecard>> getNotecards(int genId) { return mRepository.getNotecards(genId); }
    List<Notecard> getNotecardQ(int genId) { return mRepository.getNotecardQ(genId); }
    public void insert(Notecard notecard) { mRepository.insert(notecard); }
    public void deleteAll() { mRepository.deleteAll(); }
    public void deleteNotecard(Notecard notecard) { mRepository.deleteNotecard(notecard); }
    public void updateNotecard(Notecard notecard) { mRepository.updateNotecard(notecard); }
    public boolean codeExists(int genId) { return mRepository.codeExists(genId); }
}
