package edu.csce4623.rghosh.uncleroywalkthrough.data;
import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

//Room Database implementation
//Don't touch unless you know what you are doing.
@Database(entities = {PhotoEntry.class}, version = 1, exportSchema = false)
public abstract class PhotoEntryDatabase extends RoomDatabase {
    public static final String DATABASE_NAME = "photoentry_db";
    private static PhotoEntryDatabase INSTANCE;

    public static PhotoEntryDatabase getInstance(Context context){
        Log.d("IN DB:", context.toString());
        if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context,PhotoEntryDatabase.class,DATABASE_NAME).build();
        }
        return INSTANCE;
    }

    public abstract PhotoEntryDao getPhotoEntryDao();

}
