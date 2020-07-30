package com.athings.photoeditor.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.athings.photoeditor.R;
import com.athings.photoeditor.utils.GridViewItem;

import java.lang.ref.WeakReference;
import java.util.List;

public class MyGridAdapter extends BaseAdapter {
    private static final String TAG = "MyGridAdapter";
    Context context;
    GridView gridView;
    LayoutInflater inflater;
    public List<GridViewItem> items;
    Bitmap placeHolder;

    public MyGridAdapter(Context context, List<GridViewItem> items, GridView gridView) {
        this.items = items;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.gridView = gridView;
        this.placeHolder = BitmapFactory.decodeResource(context.getResources(), R.drawable.no_pattern);
        this.context = context;
    }

    public int getCount() {
        return this.items.size();
    }

    public Object getItem(int position) {
        return this.items.get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    @SuppressLint({"NewApi"})
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = this.inflater.inflate(R.layout.grid_item, null);
            viewHolder = new ViewHolder();
            viewHolder.textPath = (TextView) convertView.findViewById(R.id.textView_path);
            viewHolder.textCount = (TextView) convertView.findViewById(R.id.textViewCount);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
            viewHolder.textContainer = convertView.findViewById(R.id.grid_item_text_container);
            viewHolder.selectedCount = (TextView) convertView.findViewById(R.id.textViewSelectedItemCount);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String folderName = items.get(position).getFolderName();
        if (folderName == null || folderName.length() == 0) {
            if (viewHolder.textContainer.getVisibility() == View.VISIBLE) {
                viewHolder.textContainer.setVisibility(View.GONE);
            }
            if (items.get(position).selectedItemCount > 0) {
                viewHolder.selectedCount.setText("" + items.get(position).selectedItemCount);
                if (viewHolder.selectedCount.getVisibility() == View.INVISIBLE) {
                    viewHolder.selectedCount.setVisibility(View.VISIBLE);
                }
            } else if (viewHolder.selectedCount.getVisibility() == View.VISIBLE) {
                viewHolder.selectedCount.setVisibility(View.INVISIBLE);
            }
        } else {
            if (viewHolder.textContainer.getVisibility() == View.GONE) {
                viewHolder.textContainer.setVisibility(View.VISIBLE);
            }
            viewHolder.textPath.setText(this.items.get(position).getFolderName());
            viewHolder.textCount.setText(this.items.get(position).count);
            if (viewHolder.selectedCount.getVisibility() == View.VISIBLE) {
                viewHolder.selectedCount.setVisibility(View.INVISIBLE);
            }
        }
        loadBitmap((long) position, viewHolder.imageView, this.items.get(position));
        return convertView;
    }


    static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            this.bitmapWorkerTaskReference = new WeakReference(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return this.bitmapWorkerTaskReference.get();
        }
    }

    static class ViewHolder {
        ImageView imageView;
        TextView selectedCount;
        View textContainer;
        TextView textCount;
        TextView textPath;

        ViewHolder() {
        }
    }

    class BitmapWorkerTask extends AsyncTask<Long, Void, Bitmap> {
        private long data;
        private final WeakReference<ImageView> imageViewReference;
        private GridViewItem item;

        public BitmapWorkerTask(ImageView imageView, GridViewItem item) {
            this.data = 0;
            this.imageViewReference = new WeakReference(imageView);
            this.item = item;
        }

        protected Bitmap doInBackground(Long... params) {
            this.data = params[0];
            return this.item.getImage();
        }

        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }
            if (bitmap != null) {
                ImageView imageView = this.imageViewReference.get();
                if (this == MyGridAdapter.getBitmapWorkerTask(imageView)) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }


    public void loadBitmap(long resId, ImageView imageView, GridViewItem item) {
        if (cancelPotentialWork(resId, imageView)) {
            BitmapWorkerTask task = new BitmapWorkerTask(imageView, item);
            imageView.setImageDrawable(new AsyncDrawable(this.context.getResources(), this.placeHolder, task));
            task.execute(resId);
        }
    }

    public static boolean cancelPotentialWork(long data, ImageView imageView) {
        BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
        if (bitmapWorkerTask == null) {
            return true;
        }
        long bitmapData = bitmapWorkerTask.data;
        if (bitmapData != 0 && bitmapData == data) {
            return false;
        }
        bitmapWorkerTask.cancel(true);
        return true;
    }

    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                return ((AsyncDrawable) drawable).getBitmapWorkerTask();
            }
        }
        return null;
    }
}
