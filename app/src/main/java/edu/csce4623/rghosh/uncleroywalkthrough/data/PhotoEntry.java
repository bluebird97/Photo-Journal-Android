package edu.csce4623.rghosh.uncleroywalkthrough.data;

import android.content.ContentValues;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;

/**
 * ToDoItem class
 * Implements serializable for easy pass through between intents
 * Includes Room annotations for five columns for each of five private members
 */

/* FOR ASSIGNMENT 3: can just change fields
 * replace ToDoItem with LocationImage
 * ID for each one
 * String URI (file location)
 * Double Lat
 * Double Longitutde
 * Long for time
*/

@Entity
public class PhotoEntry implements Serializable {

    // Static strings for the column names usable by other classes
    // Same 5 fields as what a To Do item holds
    public static final String PHOTOENTRY_ID = "id";
    public static final String PHOTOENTRY_PATHNAME = "pathname";
    public static final String PHOTOENTRY_LONGITUDE = "longitude";
    public static final String PHOTOENTRY_LATITUDE = "latitude";
    public static final String PHOTOENTRY_TIME = "time";


    // Primary Key should never be used by two same To Do items; prevent collision
    @PrimaryKey(autoGenerate = true)
    private Integer id;

    @ColumnInfo(name = "pathname")
    private String pathname;

    @ColumnInfo(name = "longitude")
    private Double longitude;

    @ColumnInfo(name = "latitude")
    private Double latitude;

    @ColumnInfo(name = "time")
    private String time;

    //Following are getters and setters for all five member variables
    public Integer getId(){
        return id;
    }

    public void setId(Integer id){
        this.id = id;
    }

    public String getPathname() { return pathname; }

    public void setPathname(String pathname) {
        this.pathname = pathname;
    }

    public Double getLongitude() { return longitude; }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() { return latitude; }

    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public String getTime() { return time; }

    public void setTime(String time) { this.time = time; }


    // Create a ToDoItem from a ContentValues object
    // Sets fields if they exist
    public static PhotoEntry fromContentValues(ContentValues contentValues){
        PhotoEntry photo = new PhotoEntry();
        if(contentValues.containsKey(PHOTOENTRY_ID)){
            photo.setId(contentValues.getAsInteger(PHOTOENTRY_ID));
        }
        if(contentValues.containsKey(PHOTOENTRY_PATHNAME)){
            photo.setPathname(contentValues.getAsString(PHOTOENTRY_PATHNAME));
        }
        if(contentValues.containsKey(PHOTOENTRY_LONGITUDE)){
            photo.setLongitude(contentValues.getAsDouble(PHOTOENTRY_LONGITUDE));
        }
        if(contentValues.containsKey(PHOTOENTRY_LATITUDE)){
            photo.setLatitude(contentValues.getAsDouble(PHOTOENTRY_LATITUDE));
        }
        if (contentValues.containsKey(PHOTOENTRY_TIME)){
            photo.setTime(contentValues.getAsString(PHOTOENTRY_TIME));
        }
        return photo;
    }


}
