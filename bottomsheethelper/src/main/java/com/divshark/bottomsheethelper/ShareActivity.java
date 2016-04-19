package com.divshark.bottomsheethelper;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

/**
 * Base class to create the sharing capability in any activity
 * Created by kyle.jablonski on 4/18/16.
 */
public abstract class ShareActivity extends AppCompatActivity implements BottomSheetAdapter.Callback{

    // Tag for logging
    private static final String TAG = ShareActivity.class.getSimpleName();

    // using a large value here to not conflict with other Requests
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 7002;

    /**
     * RecyclerView for showing Applications to share to
     */
    private RecyclerView mRvBottomSheet;

    /**
     * The BottomSheetBehavior controller
     */
    private BottomSheetBehavior behavior;

    /**
     * The Selected Item from the RecyclerView of BottomSheetItem's
     */
    private BottomSheetItem mBottomSheetItem;

    /**
     * The RelativeLayout which wraps the RecyclerView
     */
    private RelativeLayout mRlBottomSheet;

    /**
     * The primary SheetHelper which controls the setup of the Sheet and
     * sharing capabilities
     */
    private SheetHelper mSheetHelper;

    /**
     * Implementations will need this method to create the file they need to share
     * @return - the Uri pointing to the file we want to share
     */
    protected abstract Uri createShareFile();

    /**
     * Implementations will need this method to setup an Intent using the information from {@link #createShareFile()}
     * @param bottomSheetItem - the bottom sheet item to share to
     * @param fileToShare - the Uri for the file to share
     * @return true/false if the share was a success
     */
    protected abstract boolean setupShareIntent(BottomSheetItem bottomSheetItem, Uri fileToShare);


    @Override
    public void setContentView(@LayoutRes int layoutResID) {

        View view = getLayoutInflater().inflate(R.layout.share_activity, null);
        FrameLayout content = (FrameLayout) view.findViewById(R.id.content);

        mRlBottomSheet  = (RelativeLayout) view.findViewById(R.id.rl_bottom_sheet);
        mRvBottomSheet = (RecyclerView) view.findViewById(R.id.rv_bottom_sheet);
        behavior = BottomSheetBehavior.from(mRlBottomSheet);

        getLayoutInflater().inflate(layoutResID, content, true);

        super.setContentView(view);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // file-related task you need to do.

                    // Save the bitmap to storage
                    Uri fileToShare = createShareFile();
                    setupShareIntent(mBottomSheetItem, fileToShare);

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    Toast.makeText(ShareActivity.this, getString(R.string.file_permission_toast), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    /**
     * Interface callback for handling the share
     * @param bottomSheetItem - the bottom sheet item to share to
     */
    @Override
    public void onShareTo(BottomSheetItem bottomSheetItem) {

        // Store off the item to share
        mBottomSheetItem = bottomSheetItem;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            requestPermission();
        }else{
            // Save the file to storage
            Uri contentUri = createShareFile();

            boolean success = setupShareIntent(mBottomSheetItem, contentUri);
            if(success) {
                behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        }
    }


    /**
     * Sets the SheetHelper to do all the heavy setup for the Bottom Sheet
     * @param sheetHelper - the {@link SheetHelper} instance
     */
    public final void setSheetHelper(SheetHelper sheetHelper){

        if(sheetHelper == null){
            throw new IllegalArgumentException("The ShareSheet helper cannot be null");
        }


        this.mSheetHelper = sheetHelper;

        // sets up the layout manager using the Sheet Helper
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, sheetHelper.getColumnCount());
        mRvBottomSheet.setLayoutManager(gridLayoutManager);
        mRvBottomSheet.setAdapter(sheetHelper.getAdapter());

        behavior.setBottomSheetCallback(sheetHelper.getBottomSheetCallback());
    }

    /**
     * Shows the share sheet
     */
    public final void showShareSheet(){

        if(mSheetHelper == null){
            throw new IllegalArgumentException("The ShareSheet helper must be defined!");
        }

        if(Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "called to showShareSheet....");
        }

        if(behavior.getState() == BottomSheetBehavior.STATE_HIDDEN || behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            final Animation animation = AnimationUtils.loadAnimation(ShareActivity.this, R.anim.bottom_slide_up);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    mRlBottomSheet.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            mRlBottomSheet.startAnimation(animation);
        }else{
            behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

    /**
     * Performs the work of checking for file permissions for READ_EXTERNAL_STORAGE, if
     * it was previously granted then the normal flow of storing the file continues
     */
    private void requestPermission(){

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                showPermissionDialog();

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }else{

            // Save the bitmap to storage
            Uri fileToShare = createShareFile();

            boolean success = setupShareIntent(mBottomSheetItem, fileToShare);
            if(success) {
                behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        }

    }

    /**
     * Presents the dialog to the user telling what the permission is
     */
    private void showPermissionDialog(){

        new MaterialDialog.Builder(ShareActivity.this)
                .title(getString(R.string.permission_read_and_write_storage_title))
                .content(R.string.permission_read_and_write_storage_message)
                .positiveText(getString(R.string.okay))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        ActivityCompat.requestPermissions(ShareActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                        dialog.dismiss();
                    }
                }).negativeText(getString(R.string.cancel))
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {

                        dialog.dismiss();
                    }
                }).build().show();
    }
}
