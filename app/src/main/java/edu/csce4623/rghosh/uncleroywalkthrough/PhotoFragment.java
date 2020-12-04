package edu.csce4623.rghosh.uncleroywalkthrough;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import edu.csce4623.rghosh.uncleroywalkthrough.data.PhotoEntry;

/**
 * A simple {@link Fragment} subclass.
 */
public class PhotoFragment extends Fragment {

    public PhotoFragment() {
        // Required empty public constructor
    }

    PhotoEntry photoLocation;

    ImageView myImageView;
    TextView address;
    TextView date;
    Date photoDate;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        photoLocation = (PhotoEntry) getArguments().getSerializable("PhotoLocation");

        View view = inflater.inflate(R.layout.fragment_photo, container, false);
        myImageView = (ImageView) view.findViewById(R.id.ivPhoto);
        address = (TextView) view.findViewById(R.id.tvAddress);
        date = (TextView) view.findViewById(R.id.tvDate);

        SimpleDateFormat sdf =  new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US);
        try {
            photoDate = sdf.parse(photoLocation.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.d("view", myImageView.toString());

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.post(new Runnable() {
            @Override
            public void run() {
                date.setText(photoDate.toString());
                List<Address> addresses;
                Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                try {
                    addresses  = geocoder.getFromLocation(photoLocation.getLatitude(),photoLocation.getLongitude(), 1);
                    Log.d("address", addresses.get(0).getAddressLine(0));
                    address.setText(addresses.get(0).getAddressLine(0));
                } catch (IOException e) {
                    e.printStackTrace();
                }


                try {
                    setPic(photoLocation.getPathname());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }




    private void setPic(String fileName) throws IOException {
        Log.d("view", myImageView.toString());

        // Get the dimensions of the View
        int targetW = myImageView.getWidth();
        int targetH = myImageView.getHeight();
        Log.d("Filename", fileName);
        Log.d("view width", Integer.toString(targetW));
        Log.d("view height", Integer.toString(targetH));

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(fileName, bmOptions);
        Log.d("bmOptions", bmOptions.toString());

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        Log.d("file width", Integer.toString(photoW));
        Log.d("file height", Integer.toString(photoH));

        // Determine how much to scale down the image
        int scaleFactor = Math.max(1, Math.min(photoW/targetW, photoH/targetH));

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(fileName, bmOptions);


        ExifInterface exif = new ExifInterface(fileName);
        int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int rotationInDegrees = exifToDegrees(rotation);
        Matrix matrix = new Matrix();
        if (rotation != 0) {matrix.preRotate(rotationInDegrees);}
        Bitmap adjustedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        myImageView.setImageBitmap(adjustedBitmap);
    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
    }
}