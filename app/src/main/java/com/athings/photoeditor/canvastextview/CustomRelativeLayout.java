package com.athings.photoeditor.canvastextview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.athings.photoeditor.R;
import com.athings.photoeditor.utils.OnItemSelected;

import java.util.ArrayList;

public class CustomRelativeLayout extends RelativeLayout implements OnItemSelected, View.OnClickListener {
    private static final String TAG = "CustomRelativeLayout";
    ApplyTextInterface applyListener;
    ArrayList<CanvasTextView> canvasTextViewList;
    Context context;
    int currentCanvasTextIndex;
    LayoutParams levelParams;
    RelativeLayout mainLayout;
    public OnClickListener onClickListener;
    Bitmap removeBitmap;
    RemoveTextListener removeTextListener;
    Bitmap scaleBitmap;
    SingleTapInterface singleTapListener;
    ArrayList<TextDataItem> textDataList;
    ArrayList<TextDataItem> textDataListOriginal;
    ViewSelectedListener viewSelectedListner;

    @Override
    public void itemSelected(int color) {
        if (!canvasTextViewList.isEmpty()) {
            canvasTextViewList.get(currentCanvasTextIndex).setTextColor(color);
        }
    }

    public void onClick(View v) {
        hideSoftKeyboard((Activity) context);
        int id = v.getId();
        if (id == R.id.button_text_color) {
        } else if (id == R.id.button_apply_action) {
            int i = 0;
            while (i < textDataList.size()) {
                if (textDataList.get(i).message.compareTo(TextDataItem.defaultMessage) == 0) {
                    textDataList.remove(i);
                    i--;
                }
                i++;
            }
            applyListener.onOk(textDataList);
        } else if (id == R.id.button_cancel_action) {
            textDataList.clear();
            for (int i = 0; i < textDataListOriginal.size(); i++) {
                textDataList.add(textDataListOriginal.get(i));
            }
            applyListener.onCancel();
        }
    }

    public interface RemoveTextListener {
        void onRemove();
    }

    /* renamed from: com.lyrebirdstudio.canvastext.CustomRelativeLayout.1 */
    class RemoveText implements RemoveTextListener {
        RemoveText() {
        }

        public void onRemove() {
            if (!canvasTextViewList.isEmpty()) {
                CanvasTextView canvasTextView = canvasTextViewList.get(currentCanvasTextIndex);
                mainLayout.removeView(canvasTextView);
                canvasTextViewList.remove(canvasTextView);
                textDataList.remove(canvasTextView.textData);
                currentCanvasTextIndex = canvasTextViewList.size() - 1;
                if (!canvasTextViewList.isEmpty()) {
                    canvasTextViewList.get(currentCanvasTextIndex).setTextSelected(true);
                }
            }
        }
    }

    /* renamed from: com.lyrebirdstudio.canvastext.CustomRelativeLayout.2 */
    class ViewSelector implements ViewSelectedListener {
        ViewSelector() {
        }

        public void setSelectedView(CanvasTextView cvt) {
            currentCanvasTextIndex = canvasTextViewList.indexOf(cvt);
            for (int i = 0; i < canvasTextViewList.size(); i++) {
                if (currentCanvasTextIndex != i) {
                    canvasTextViewList.get(i).setTextSelected(false);
                }
            }
        }
    }

    void loadBitmaps() {
        if (removeBitmap == null || removeBitmap.isRecycled()) {
            removeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.close);
        }
        if (scaleBitmap == null || scaleBitmap.isRecycled()) {
            scaleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_accept);
        }
    }

    public CustomRelativeLayout(Context c, ArrayList<TextDataItem> textDataListParam, Matrix canvasMatrix, SingleTapInterface l) {
        super(c);
        int i;
        this.currentCanvasTextIndex = 0;
        this.removeTextListener = new RemoveText();
        this.viewSelectedListner = new ViewSelector();
        this.context = c;
        this.singleTapListener = l;
        loadBitmaps();
        ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.activity_canvas, this);
        this.mainLayout = (RelativeLayout) findViewById(R.id.canvas_relative_layout);
        this.levelParams = new LayoutParams( LayoutParams.MATCH_PARENT,  LayoutParams.MATCH_PARENT);
        this.levelParams.addRule(15,-1);
        this.levelParams.addRule(14, -1);
        this.canvasTextViewList = new ArrayList();
        this.textDataList = new ArrayList();
        this.textDataListOriginal = new ArrayList();
        for (i = 0; i < textDataListParam.size(); i++) {
            this.textDataListOriginal.add(new TextDataItem(textDataListParam.get(i)));
            this.textDataList.add(new TextDataItem(textDataListParam.get(i)));
        }
        for (i = 0; i < this.textDataList.size(); i++) {
            CanvasTextView canvasTextView = new CanvasTextView(this.context, this.textDataList.get(i), this.removeBitmap, this.scaleBitmap);
            canvasTextView.setSingleTapListener(this.singleTapListener);
            canvasTextView.setViewSelectedListener(this.viewSelectedListner);
            canvasTextView.setRemoveTextListener(new RemoveText());
            this.mainLayout.addView(canvasTextView, this.levelParams);
            this.canvasTextViewList.add(canvasTextView);
            CustomMatrix textMatrix = new CustomMatrix();
            textMatrix.set((textDataList.get(i)).imageSaveMatrix);
            textMatrix.postConcat(canvasMatrix);
            canvasTextView.setMatrix(textMatrix);
        }
        if (!this.canvasTextViewList.isEmpty()) {
            canvasTextViewList.get(this.canvasTextViewList.size() - 1).setTextSelected(true);
            this.currentCanvasTextIndex = this.canvasTextViewList.size() - 1;
        }
        TextView okButton = (TextView)findViewById(R.id.button_apply_action);
        TextView cancelButton = (TextView)findViewById(R.id.button_cancel_action);
        okButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
    }

    public void setSingleTapListener(SingleTapInterface l) {
        this.singleTapListener = l;
    }

    public void setApplyTextListener(ApplyTextInterface l) {
        this.applyListener = l;
    }

    public void addTextView(TextDataItem textData) {
        if (this.textDataList.contains(textData)) {
            Log.e(TAG, "textDataList.contains(textData)");
            canvasTextViewList.get(this.currentCanvasTextIndex).setNewTextData(textData);
            return;
        }
        for (int i = 0; i < this.canvasTextViewList.size(); i++) {
            canvasTextViewList.get(i).setTextSelected(false);
        }
        this.currentCanvasTextIndex = this.canvasTextViewList.size();
        loadBitmaps();
        CanvasTextView canvasTextView = new CanvasTextView(this.context, textData, this.removeBitmap, this.scaleBitmap);
        canvasTextView.setSingleTapListener(this.singleTapListener);
        canvasTextView.setViewSelectedListener(this.viewSelectedListner);
        canvasTextView.setRemoveTextListener(new RemoveText());
        this.canvasTextViewList.add(canvasTextView);
        this.mainLayout.addView(canvasTextView);
        this.textDataList.add(canvasTextView.textData);
        canvasTextView.setTextSelected(true);
        canvasTextView.singleTapped();
    }

    public void addTextDataEx(TextDataItem textData) {
        if (this.textDataList.contains(textData)) {
            Log.e(TAG, "textDataList.contains(textData)");
            ((CanvasTextView) this.canvasTextViewList.get(this.currentCanvasTextIndex)).setNewTextData(textData);
            return;
        }
        for (int i = 0; i < this.canvasTextViewList.size(); i++) {
            ((CanvasTextView) this.canvasTextViewList.get(i)).setTextSelected(false);
        }
        this.currentCanvasTextIndex = this.canvasTextViewList.size();
        CanvasTextView canvasTextView = new CanvasTextView(this.context, textData, this.removeBitmap, this.scaleBitmap);
        canvasTextView.setSingleTapListener(this.singleTapListener);
        canvasTextView.setViewSelectedListener(this.viewSelectedListner);
        canvasTextView.setRemoveTextListener(new RemoveText());
    }

    public boolean onTouchEvent(MotionEvent event) {
        hideSoftKeyboard((Activity) this.context);
        return true;
    }

    public void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null && activity.getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }
}
