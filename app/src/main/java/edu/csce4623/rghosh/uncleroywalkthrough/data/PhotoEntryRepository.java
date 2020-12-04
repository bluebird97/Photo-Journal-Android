package edu.csce4623.rghosh.uncleroywalkthrough.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;
import util.AppExecutors;

/**
 * ToDoItemRepository class - implements the ToDoDataSource interface
 */

/* A3: do not need to call Content provider.
 * can instead directly call functions that ToDoProvider does
 * ex, just do cursor = toDOItemDao.findAll()
 * WILL STILL NEED TO CALL IN DIFF THREAD
 * combine Runnable threading portions with
 * direct access to functions
 */
public class PhotoEntryRepository implements PhotoEntryDataSource {

    //Memory leak here by including the context - Fix this at some point
    private static volatile PhotoEntryRepository INSTANCE;
    private PhotoEntryDao photoEntryDao;

    //Thread pool for execution on other threads
    private AppExecutors mAppExecutors;
    //Context for calling ToDoProvider
    private Context mContext;

    /**
     * private constructor - prevent direct instantiation
     * @param appExecutors - thread pool
     * @param context
     */
    private PhotoEntryRepository(@NonNull AppExecutors appExecutors, @NonNull Context context){
        mAppExecutors = appExecutors;
        mContext = context;
        photoEntryDao = PhotoEntryDatabase.getInstance(mContext).getPhotoEntryDao();
    }

    /**
     * public constructor - prevent creation of instance if one already exists
     * @param appExecutors
     * @param context
     * @return
     */
    public static PhotoEntryRepository getInstance(@NonNull AppExecutors appExecutors, @NonNull Context context){
        if(INSTANCE == null){
            synchronized (PhotoEntryRepository.class){
                if(INSTANCE == null){
                    INSTANCE = new PhotoEntryRepository(appExecutors, context);
                }
            }
        }
        return INSTANCE;
    }

    /**
     * getToDoItems runs query in a separate thread, and on success loads data from cursor into a list
     * @param callback
     */
    @Override
    public void getPhotoEntries(@NonNull final LoadPhotoEntriesCallback callback) {
        Log.d("REPOSITORY","Loading...");
        Runnable runnable = new Runnable(){
            @Override
            public void run() {
                String[] projection = {
                        PhotoEntry.PHOTOENTRY_ID,
                        PhotoEntry.PHOTOENTRY_PATHNAME,
                        PhotoEntry.PHOTOENTRY_LONGITUDE,
                        PhotoEntry.PHOTOENTRY_LATITUDE,
                        PhotoEntry.PHOTOENTRY_TIME};
                Log.d("REPOSITORY","Loading1...");
                final Cursor c = photoEntryDao.findAll();
                Log.d("REPOSITORY","Loading2...");
                final List<PhotoEntry> photoEntries = new ArrayList<PhotoEntry>(0);
                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if(c == null){
                            callback.onDataNotAvailable();
                        } else{
                            while(c.moveToNext()) {
                                PhotoEntry photo = new PhotoEntry();
                                photo.setId(c.getInt(c.getColumnIndex(PhotoEntry.PHOTOENTRY_ID)));
                                photo.setPathname(c.getString(c.getColumnIndex(PhotoEntry.PHOTOENTRY_PATHNAME)));
                                photo.setLongitude(c.getDouble(c.getColumnIndex(PhotoEntry.PHOTOENTRY_LONGITUDE)));
                                photo.setLatitude(c.getDouble(c.getColumnIndex(PhotoEntry.PHOTOENTRY_LATITUDE)));
                                photo.setTime(c.getString(c.getColumnIndex(PhotoEntry.PHOTOENTRY_TIME)));
                                photoEntries.add(photo);
                            }
                            c.close();
                            callback.onPhotoEntriesLoaded(photoEntries);
                        }
                    }
                });

            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    /**
     * Not implemented yet
     * @param photoEntryId
     * @param callback
     */
    @Override
    public void getPhotoEntry(@NonNull final String photoEntryId, @NonNull GetPhotoEntryCallback callback) {
        Log.d("REPOSITORY","getPhotoEntry");
    }

    /**
     * saveToDoItem runs contentProvider update in separate thread
     * @param photoEntry
     */
    @Override
    public void savePhotoEntry(@NonNull final PhotoEntry photoEntry) {
        Log.d("REPOSITORY","savePhotoEntry");
        Runnable runnable = new Runnable(){
            @Override
            public void run() {
                ContentValues myCV = new ContentValues();
                myCV.put(PhotoEntry.PHOTOENTRY_ID,photoEntry.getId());
                myCV.put(PhotoEntry.PHOTOENTRY_PATHNAME,photoEntry.getPathname());
                myCV.put(PhotoEntry.PHOTOENTRY_LONGITUDE,photoEntry.getLongitude());
                myCV.put(PhotoEntry.PHOTOENTRY_LATITUDE,photoEntry.getLatitude());
                myCV.put(PhotoEntry.PHOTOENTRY_TIME,photoEntry.getTime());
                final int numUpdated = 0;
                Log.d("REPOSITORY","Update ToDo updated " + String.valueOf(numUpdated) + " rows");
            }
        };
        mAppExecutors.diskIO().execute(runnable);

    }

    /**
     * createToDoItem runs contentProvider insert in separate thread
     * @param photoEntry
     */
    @Override
    public void createPhotoEntry(@NonNull final PhotoEntry photoEntry, @NonNull final CreatePhotoEntryCallback callback) {
        Log.d("REPOSITORY","CreateToDoItem");
        Log.d("REPOSITORY",photoEntry.getPathname());
        Runnable runnable = new Runnable(){
            @Override
            public void run() {
                ContentValues myCV = new ContentValues();
                myCV.put(PhotoEntry.PHOTOENTRY_PATHNAME,photoEntry.getPathname());
                myCV.put(PhotoEntry.PHOTOENTRY_LONGITUDE,photoEntry.getLongitude());
                myCV.put(PhotoEntry.PHOTOENTRY_LATITUDE,photoEntry.getLatitude());
                myCV.put(PhotoEntry.PHOTOENTRY_TIME,photoEntry.getTime());
                Log.d("REPOSITORY",PhotoEntry.fromContentValues(myCV).toString());
                final long id = photoEntryDao.insert(PhotoEntry.fromContentValues(myCV));
                Log.d("inCreate","ID is" + id);
                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.onCreatePhotoEntry(id, photoEntry);
                    }
                });
            }
        };
        mAppExecutors.diskIO().execute(runnable);

    }

    /**
     * deleteToDoItem runs contentProvider insert in separate thread
     * @param photoEntry
     */
    @Override
    public void deletePhotoEntry(@NonNull final PhotoEntry photoEntry) {
        Log.d("REPOSITORY","trying to delete");
        final long id = Long.valueOf(photoEntry.getId());
        Log.d("REPOSITORY",String.valueOf(id));
        Runnable runnable = new Runnable(){
            @Override
            public void run() {
                final int isDeleted = photoEntryDao.delete(id);
            }
        };

        mAppExecutors.diskIO().execute(runnable);
    }
}
