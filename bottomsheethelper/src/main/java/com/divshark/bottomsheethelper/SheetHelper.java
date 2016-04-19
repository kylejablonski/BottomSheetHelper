package com.divshark.bottomsheethelper;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.support.design.widget.BottomSheetBehavior;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Helper class to implement Sharing in an app using a Bottom Sheet as a RecyclerView
 * Created by kyle.jablonski on 4/18/16.
 */
public class SheetHelper {

    /**
     * TAG for logging
     */
    private static final String TAG = SheetHelper.class.getSimpleName();

    /**
     * BottomSheetBehavior.BottomSheetCallback interface
     */
    private BottomSheetBehavior.BottomSheetCallback bottomSheetCallback;

    /**
     * Action for the Intent
     */
    private String action;

    /**
     * Adapter for the RecyclerView of {@link BottomSheetItem}'s
     */
    private BottomSheetAdapter adapter;

    /**
     * The List of {@link BottomSheetItem}'s
     */
    private List<BottomSheetItem> bottomSheetItems;

    /**
     * The current Context of the Application
     */
    private Context context;

    /**
     * Column count for the GridLayoutManager
     */
    private int columnCount;

    /**
     * The DateFormat for the file to save
     */
    private DateFormat dateFormat;

    /**
     * The directory to save the file to
     */
    private String directory;

    /**
     * Intent.EXTRA_TEXT String to put in the Intent
     */
    private String extraText;

    /**
     * The String name identifier for the FileProvider
     */
    private String fileProvider;

    /**
     * The File prefix for saving the file
     */
    private String filePrefix;

    /**
     * The extension for the saved file
     */
    private String fileExtension;

    /**
     * The MIME type for the file we are saving
     */
    private String mimeType;

    /**
     * Intent.EXTRA_SUBJECT to put in the Intent
     */
    private String subject;

    /**
     * Intent.EXTRA_TITLE to put in the Intent
     */
    private String title;

    /**
     * AsyncTask for getting applications for the {@link #action} for the Intent
     */
    private ResolveInfoTask mResolveInfoTask;


    /**
     * Private constructor called by the builder for Setup
     * @param builder - the Builder
     */
    private SheetHelper(Builder builder){

        action = builder.action;
        bottomSheetCallback = builder.bottomSheetCallback;
        context = builder.context;
        columnCount = builder.columnCount;
        dateFormat = builder.dateFormat;
        directory = builder.directory;
        extraText = builder.extraText;
        fileExtension = builder.fileExtension;
        filePrefix = builder.filePrefix;
        fileProvider = builder.fileProvider;
        mimeType = builder.mimeType;
        subject = builder.subject;
        title = builder.title;

        // We create the BottomSheetItem List and adapter separately from the Builder
        bottomSheetItems = new ArrayList<>();
        adapter = new BottomSheetAdapter(context, bottomSheetItems);

        // initialize the sheet
        initBottomSheet();
    }

    public String getAction() {
        return action;
    }

    public BottomSheetAdapter getAdapter() {
        return adapter;
    }

    public BottomSheetBehavior.BottomSheetCallback getBottomSheetCallback() {
        return bottomSheetCallback;
    }

    public List<BottomSheetItem> getBottomSheetItems() {
        return bottomSheetItems;
    }

    public Context getContext() {
        return context;
    }

    public int getColumnCount(){
        return columnCount;
    }

    public DateFormat getDateFormat(){

        if(dateFormat != null) {

            return dateFormat;
        }else{
            return new SimpleDateFormat("_M-dd-yyyy_hhmmss", Locale.getDefault());
        }
    }

    public String getDirectory() {
        return directory;
    }

    public String getExtraText() {
        return extraText;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public String getFilePrefix() {
        return filePrefix;
    }

    public String getFileProvider() {
        return fileProvider;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getSubject() {
        return subject;
    }

    public String getTitle() {
        return title;
    }

    /**
     * Builder class for the SheetHelper
     */
    public static class Builder{

        private String action;
        private  BottomSheetBehavior.BottomSheetCallback bottomSheetCallback;
        private Context context;
        private int columnCount;
        private DateFormat dateFormat;
        private String directory;
        private String extraText;
        private String fileExtension;
        private String filePrefix;
        private String fileProvider;
        private String mimeType;
        private String subject;
        private String title;

        public Builder with(Context context){
            this.context = context;
            return this;
        }


        public Builder action(String action){
            this.action = action;
            return this;
        }

        public Builder callback(BottomSheetBehavior.BottomSheetCallback bottomSheetCallback){
            this.bottomSheetCallback = bottomSheetCallback;
            return this;
        }

        public Builder columnCount(int columnCount){
            this.columnCount = columnCount;
            return this;
        }

        public Builder dateFormat(String format){
            dateFormat = new SimpleDateFormat(format, Locale.getDefault());
            return this;
        }

        public Builder directory(String directory){
            this.directory = directory;
            return this;
        }

        public Builder extraText(String extraText){
            this.extraText = extraText;
            return this;
        }

        public Builder filePrefix(String filePrefix){
            this.filePrefix = filePrefix;
            return this;
        }

        public Builder fileExtension(String fileExtension){
            this.fileExtension = fileExtension;
            return this;
        }

        public Builder mimeType(String mimeType){
            this.mimeType = mimeType;
            return this;
        }

        public Builder provider(String fileProvider){
            this.fileProvider = fileProvider;
            return this;
        }

        public Builder subject(String subject){
            this.subject = subject;
            return this;
        }

        public Builder title(String title){
            this.title = title;
            return this;
        }

        public SheetHelper create(){
            return new SheetHelper(this);
        }
    }

    /**
     * Initializes the bottom sheet
     */
    private void initBottomSheet(){

        /* Interrupt old task and set to null */
        if(mResolveInfoTask != null){
            mResolveInfoTask.cancel(true);
            mResolveInfoTask = null;
        }

        mResolveInfoTask = new ResolveInfoTask();
        mResolveInfoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    /**
     * AsyncTask to get the ResolveInfo for Sharing
     */
    public class ResolveInfoTask extends AsyncTask<Void, Void, List<BottomSheetItem>> {

        @Override
        protected List<BottomSheetItem> doInBackground(Void... params) {
            if(!isCancelled()) {

                Intent shareIntent = new Intent(Intent.ACTION_SEND, null);
                shareIntent.setType(mimeType);
                PackageManager packageManager = context.getPackageManager();
                List<ResolveInfo> activities = packageManager.queryIntentActivities(shareIntent, 0);
                if (activities != null) {
                    bottomSheetItems.clear();

                    for (ResolveInfo activity : activities) {

                        if (Log.isLoggable(TAG, Log.DEBUG)) {
                            Log.d(TAG, "can handle Intent.ACTION_SEND " + activity.loadLabel(packageManager).toString());
                        }

                        BottomSheetItem bottomSheetItem = new BottomSheetItem();
                        bottomSheetItem.title = activity.loadLabel(packageManager).toString();
                        bottomSheetItem.drawable = activity.loadIcon(packageManager);
                        bottomSheetItem.className = activity.activityInfo.name;
                        bottomSheetItem.packageName = activity.activityInfo.packageName;
                        bottomSheetItems.add(bottomSheetItem);
                    }

                    Collections.sort(bottomSheetItems, new Comparator<BottomSheetItem>() {
                        @Override
                        public int compare(BottomSheetItem lhs, BottomSheetItem rhs) {
                            return lhs.title.compareTo(rhs.title);
                        }
                    });
                }
                return bottomSheetItems;
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<BottomSheetItem> bottomSheetItems) {
            super.onPostExecute(bottomSheetItems);
            adapter.notifyDataSetChanged();
        }
    }

}
