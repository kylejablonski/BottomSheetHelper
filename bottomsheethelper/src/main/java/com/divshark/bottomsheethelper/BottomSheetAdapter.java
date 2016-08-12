package com.divshark.bottomsheethelper;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.List;

/**
 * The Adapter for the bottom sheet items
 * Created by kyle.jablonski on 4/18/16.
 */
public class BottomSheetAdapter  extends RecyclerView.Adapter<BottomSheetAdapter.ViewHolder> {


    private static final String TAG = BottomSheetAdapter.class.getSimpleName();

    private Context mContext;
    private List<BottomSheetItem> mBottomSheetItems;
    private Callback mCallback;
    private int mItemTextColor;

    public interface Callback{

        void onShareTo(BottomSheetItem bottomSheetItem);
    }

    public BottomSheetAdapter(Context context, List<BottomSheetItem> bottomSheetItems){
        this.mContext = context;
        this.mBottomSheetItems = bottomSheetItems;

        mCallback = (Callback) mContext;
    }

    public void setItemTextColor(int itemTextColor){
        mItemTextColor = itemTextColor;
    }

    @Override
    public int getItemCount() {

        int count = 0;

        if(mBottomSheetItems != null && mBottomSheetItems.size() > 0){
            count = mBottomSheetItems.size();
        }

        return count;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bottom_sheet_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final BottomSheetItem bottomSheetItem = mBottomSheetItems.get(position);
        if(bottomSheetItem != null){

            holder.imageView.setImageDrawable(bottomSheetItem.drawable);
            holder.textView.setText(bottomSheetItem.title);
            holder.textView.setTextColor(mItemTextColor);

            holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onShareTo(bottomSheetItem);
                    if(Log.isLoggable(TAG, Log.DEBUG)){
                        Log.d(TAG, "bottom sheet callback invoked");
                    }
                }
            });
        }

    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout relativeLayout;
        ImageView imageView;
        AppCompatTextView textView;

        public ViewHolder(View itemView) {
            super(itemView);

            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.rl_root_bottom_sheet);
            imageView = (ImageView) itemView.findViewById(R.id.iv_app_icon);
            textView = (AppCompatTextView) itemView.findViewById(R.id.tv_app_name);
        }
    }
}
