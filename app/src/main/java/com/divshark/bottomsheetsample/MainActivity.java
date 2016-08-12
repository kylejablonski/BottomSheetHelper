package com.divshark.bottomsheetsample;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.divshark.bottomsheethelper.BottomSheetItem;
import com.divshark.bottomsheethelper.ShareActivity;
import com.divshark.bottomsheethelper.SheetHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Sample Activity to show the use of the {@link ShareActivity} base class
 */
public class MainActivity extends ShareActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Bitmap bitmap;

    private SheetHelper mSheetHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // This example uses a bitmap to share
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sample);

        // Use the SheetHelper.Builder to setup the settings for sharing
        mSheetHelper = new SheetHelper.Builder().with(this)
                .action(Intent.ACTION_SEND) /* Specify and Intent Action */
                .filePrefix("Image") /* configure a file prefix */
                .provider(".sampleprovider") /* configure the file provider name */
                .fileExtension(".png") /* configure the file extension */
                .columnCount(2) /* configure the column count for the grid adapter */
                .callback(ShareSheetBehaviorCallback) /* specify a BottomSheetBehavior.BottomSheetCallback */
                .dateFormat("_M-dd-yyyy_hhmmss") /* sets a Date format for saving the file */
                .directory("BottomSheet") /* specify the directory */
                .subject("Bottom Sheet Share") /* adds a subject to the share */
                .extraText("Look at this amazing photo I can share!") /* adds some extra text to share*/
                .mimeType("text/plain") /* sets the mime type of the file to share */
                .title("Bottom Sheet Sample") /* set the title */
                .create();

        // Tell the parent Activity here is my SheetHelper
        setSheetHelper(mSheetHelper);


        ImageButton button = (ImageButton) findViewById(R.id.btn_share);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Log.isLoggable(TAG, Log.DEBUG)) {
                    Log.d(TAG, "showing share sheet");
                }

                // Call to show share sheet sets up everything you need including
                // requesting file permissions
                showShareSheet();
            }
        });
    }


    /**
     * Overridden method to do app specific file sharing
     * @return - a Uri pointing to the file to share
     */
    @Override
    protected Uri createShareFile() {
        Uri contentUri;
        final File myDir = new File(getFilesDir(), mSheetHelper.getDirectory());
        boolean directoryCreated = myDir.mkdirs();

        if(directoryCreated || myDir.exists()) {
            final String fileName = mSheetHelper.getFilePrefix() + mSheetHelper.getDateFormat().format(new Date()) + mSheetHelper.getFileExtension();
            final File file = new File(myDir, fileName);
            FileOutputStream fileOutputStream;

            try {

                boolean fileCreated = file.createNewFile();

                if(fileCreated) {
                    fileOutputStream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                    fileOutputStream.close();

                    contentUri = FileProvider.getUriForFile(MainActivity.this, getPackageName() + mSheetHelper.getFileProvider(), file);

                    return contentUri;
                }

            } catch (IOException ex) {

                if(Log.isLoggable(TAG, Log.ERROR)){
                    Log.e(TAG, "Failed to create the file", ex);
                }
            }
        }

        // Return null if we can't create the directory and file
        return null;
    }

    /**
     * Override this method in order to explicitly handle your Intent
     * @param bottomSheetItem - the bottom sheet item to share to
     * @param fileToShare - the Uri for the file to share
     * @return - true/ false if the share was successful
     */
    @Override
    protected boolean setupShareIntent(BottomSheetItem bottomSheetItem, Uri fileToShare) {
        Intent shareIntent = new Intent(mSheetHelper.getAction());

        if(fileToShare != null && bottomSheetItem != null) {

            shareIntent.putExtra(Intent.EXTRA_STREAM, fileToShare);
            shareIntent.setType(mSheetHelper.getMimeType());
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);/* Add flag to let the apps read the uri we just added */
            shareIntent.setClassName(bottomSheetItem.packageName, bottomSheetItem.className);

            if(mSheetHelper.getExtraText() != null){
                shareIntent.putExtra(Intent.EXTRA_TEXT, mSheetHelper.getExtraText());
            }

            if(mSheetHelper.getSubject() != null){
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, mSheetHelper.getSubject());
            }

            if(mSheetHelper.getTitle() != null) {
                shareIntent.putExtra(Intent.EXTRA_TITLE, mSheetHelper.getTitle());
            }
            startActivity(shareIntent);

            return true;
        }

        return false;
    }

    private final BottomSheetBehavior.BottomSheetCallback ShareSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {

            switch(newState){

                case BottomSheetBehavior.STATE_COLLAPSED:
                    if(Log.isLoggable(TAG, Log.DEBUG)) {
                        Log.d(TAG, "state collapsed");
                    }
                    break;

                case BottomSheetBehavior.STATE_DRAGGING:
                    if(Log.isLoggable(TAG, Log.DEBUG)) {
                        Log.d(TAG, "state dragging");
                    }

                    break;

                case BottomSheetBehavior.STATE_EXPANDED:
                    if(Log.isLoggable(TAG, Log.DEBUG)) {
                        Log.d(TAG, "state expanded");
                    }

                    break;

                case BottomSheetBehavior.STATE_HIDDEN:
                    if(Log.isLoggable(TAG, Log.DEBUG)) {
                        Log.d(TAG, "state hidden");
                    }

                    break;

                case BottomSheetBehavior.STATE_SETTLING:
                    if(Log.isLoggable(TAG, Log.DEBUG)) {
                        Log.d(TAG, "state settling");
                    }

                    break;
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {}
    };
}
