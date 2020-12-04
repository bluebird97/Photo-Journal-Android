package edu.csce4623.rghosh.uncleroywalkthrough.data;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

// Interface used by Room Library to understand data access & avaliable queries

/* UPDATE for A3: for Location Image
 * findAll, delete would still work
 * would not need update most likely
 */

@Dao
public interface PhotoEntryDao {
    /**
     * Insert a photoentry into the table
     * @return row ID for newly inserted data
     */
    @Insert
    long insert(PhotoEntry photo);    /**
     * select all todoitems
     * @return A {@link Cursor} of all todoitems in the table
     */
    @Query("SELECT * FROM PhotoEntry")
    Cursor findAll();      /**
     * Delete a todoitem by ID
     * @return A number of todoitems deleted
     */
    @Query("DELETE FROM PhotoEntry WHERE id = :id ")
    int delete(long id);    /**
     * Update the todoitem
     * @return A number of todoitems updated
     */

}