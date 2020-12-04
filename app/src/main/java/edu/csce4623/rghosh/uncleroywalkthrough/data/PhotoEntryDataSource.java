package edu.csce4623.rghosh.uncleroywalkthrough.data;

import androidx.annotation.NonNull;
import java.util.List;

/**
 * Interface for any implementation of a ToDoListDataSource
 * (Currently only have one - a local ContentProvider based implementation (@ToDoItemRepository)
 */
public interface PhotoEntryDataSource {

    /**
     * LoadToDoItemsCallback interface
     * Example of how to implement callback functions depending on the result of functions in interfaces
     * Currently, onDataNotAvailable is not implemented
     */
    interface LoadPhotoEntriesCallback {

        void onPhotoEntriesLoaded(List<PhotoEntry> photoEntries);

        void onDataNotAvailable();
    }

    interface CreatePhotoEntryCallback {

        void onCreatePhotoEntry(long id, PhotoEntry photo);

        void onDataNotAvailable();
    }

    /**
     * GetToDoItemsCallback interface
     * Not currently implementd
     */
    interface GetPhotoEntryCallback {

        void onPhotoEntryLoaded(PhotoEntry entry);

        void onDataNotAvailable();
    }

    /**
     * getToDoItems loads all ToDoItems, calls either success or failure fuction above
     * @param callback - Callback function
     */
    void getPhotoEntries(@NonNull LoadPhotoEntriesCallback callback);

    /**
     * getToDoItem - Get a single ToDoItem - currently not implemented
     * @param photoEntryId - String of the current ItemID to be retrieved
     * @param callback - Callback function
     */
    void getPhotoEntry(@NonNull String photoEntryId, @NonNull GetPhotoEntryCallback callback);

    /**
     * SaveToDoItem saves a toDoItem to the database - No callback (should be implemented for
     * remote databases)
     * @param photoEntry
     */
    void savePhotoEntry(@NonNull final PhotoEntry photoEntry);

    /**
     * CreateToDoItem adds a toDoItem to the database - No callback (should be implemented for
     * remote databases)
     * @param photoEntry
     */
    void createPhotoEntry(@NonNull PhotoEntry photoEntry, @NonNull final CreatePhotoEntryCallback callback);

    /**
     * DeleteToDoItem deletes a toDoItem from the database - No callback (should be implemented for
     * remote databases)
     */
    void deletePhotoEntry(@NonNull PhotoEntry photoEntry);

}
