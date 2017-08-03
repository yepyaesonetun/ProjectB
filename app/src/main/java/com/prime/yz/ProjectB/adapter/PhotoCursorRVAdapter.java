package com.prime.yz.ProjectB.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.prime.yz.ProjectB.R;
import com.prime.yz.ProjectB.db.ProjectSqliteHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by KKT on 8/1/2017.
 **/

public class PhotoCursorRVAdapter extends BaseCursorRecyclerViewAdapter<PhotoCursorRVAdapter.PhotoViewHolder> {


    /**
     * BaseCursorRecyclerViewAdapter Constructor
     *
     * @param cursor    Cursor to be used as data set
     * @param idColName Name of Column which is used as PrimaryKey in the table
     */
    private  int  currentItemId;
    private int albumId;
    private Cursor currentCursor;

    public PhotoCursorRVAdapter(Cursor cursor, String idColName) {
        super(cursor, idColName);
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder viewHolder, Cursor cursor) {
        viewHolder.bindPhoto(cursor);
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_view,parent,false);
        return new PhotoViewHolder(view);
    }


    class PhotoViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.item_text)
        TextView itemText;

        @BindView(R.id.item_image)
        ImageView itemImage;

        private Context mContext;
        public PhotoViewHolder(View itemView) {

            super(itemView);
            mContext = itemView.getContext();
            ButterKnife.bind(this,itemView);

        }

        void bindPhoto(Cursor c){
            currentCursor = c;
            int photoIdIndex = c.getColumnIndex(ProjectSqliteHelper.COLUMN_PHOTO_ID);
            int photoAlbumIdIndex = c.getColumnIndex(ProjectSqliteHelper.COLUMN_PHOTO_ALBUM_ID);
            int photoTitleIndex = c.getColumnIndex(ProjectSqliteHelper.COLUMN_PHOTO_TITLE);
            int photoUrlIndex = c.getColumnIndex(ProjectSqliteHelper.COLUMN_PHOTO_URL);

            currentItemId = c.getInt(photoIdIndex);
            albumId = c.getInt(photoAlbumIdIndex);
            String photoTitle = c.getString(photoTitleIndex);
            String photoUrl = c.getString(photoUrlIndex);

            itemText.setText(photoTitle);
            Glide.with(mContext)
                    .load(photoUrl)
                    .placeholder(R.mipmap.ic_launcher)
                    .into(itemImage);

        }
    }
}
