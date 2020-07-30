package com.athings.photoeditor.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.renderscript.Allocation;
import android.support.v4.app.Fragment;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.athings.photoeditor.BuildConfig;
import com.athings.photoeditor.R;
import com.athings.photoeditor.canvastextview.FontCache;
import com.athings.photoeditor.canvastextview.WriteTextFontAdapter;
import com.athings.photoeditor.canvastextview.TextDataItem;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

public class WriteTextFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "WriteTextFragment";
    Activity activity;
    WriteTextFontAdapter customGridAdapter;
    EditText editText;
    FontChoosedListener fontChoosedListener;
    private String[] fontPathList;
    TextDataItem textData;
    TextView textView;

    public interface FontChoosedListener {
        void onOk(TextDataItem textData);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_write_text, container, false);
        this.activity = getActivity();
        Bundle extras = getArguments();
        if (extras != null) {
            this.textData = (TextDataItem) extras.getSerializable("text_data");
        }
        this.fontPathList = new String[]{"fonts/MfStillKindaRidiculous.ttf", "fonts/ahundredmiles.ttf", "fonts/Binz.ttf", "fonts/Blunt.ttf", "fonts/FreeUniversal-Bold.ttf", "fonts/gtw.ttf", "fonts/HandTest.ttf", "fonts/Jester.ttf", "fonts/Semplicita_Light.otf", "fonts/OldFolksShuffle.ttf", "fonts/vinque.ttf", "fonts/Primal _ream.otf", "fonts/Junction 02.otf", "fonts/Laine.ttf", "fonts/NotCourierSans.otf", "fonts/OSP-DIN.ttf", "fonts/otfpoc.otf", "fonts/Sofia-Regular.ttf", "fonts/Quicksand-Regular.otf", "fonts/Roboto-Thin.ttf", "fonts/RomanAntique.ttf", "fonts/SerreriaSobria.otf", "fonts/Strato-linked.ttf", "fonts/waltographUI.ttf", "fonts/CaviarDreams.ttf", "fonts/GoodDog.otf", "fonts/Pacifico.ttf", "fonts/Windsong.ttf", "fonts/digiclock.ttf"};
        this.textView = (TextView) fragmentView.findViewById(R.id.textview_font);
        this.textView.setPaintFlags(this.textView.getPaintFlags() | Allocation.USAGE_SHARED);
        this.textView.setOnClickListener(this);
        this.editText = (EditText) fragmentView.findViewById(R.id.edittext_font);
        this.editText.setInputType((this.editText.getInputType() | AccessibilityNodeInfoCompat.ACTION_COLLAPSE) | 176);
        this.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence message, int start, int before, int count) {
                if (message.toString().compareToIgnoreCase(BuildConfig.FLAVOR) != 0) {
                    textView.setText(message.toString());
                } else {
                    textView.setText(TextDataItem.defaultMessage);
                }
                editText.setSelection(editText.getText().length());
            }

            @Override
            public void afterTextChanged(Editable s) {
                editText.setSelection(editText.getText().length());
            }

        });
        this.editText.setFocusableInTouchMode(true);

        if (this.textData == null) {
            this.textData = new TextDataItem(this.activity.getResources().getDimension(R.dimen.myFontSize));
            float screenWidth = (float) getResources().getDisplayMetrics().widthPixels;
            float screenHeight = (float) getResources().getDisplayMetrics().heightPixels;
            Rect r = new Rect();
            this.textData.textPaint.getTextBounds(TextDataItem.defaultMessage, 0, TextDataItem.defaultMessage.length(), r);
            this.textData.xPos = (screenWidth / 2.0f) - ((float) (r.width() / 2));
            this.textData.yPos = (screenHeight / 2.0f) - ((float) (r.height() / 2));
            Log.e(TAG, "textData==null");
            this.editText.setText(BuildConfig.FLAVOR);
            this.textView.setText(getString(R.string.preview_text));
        } else {
            if (!this.textData.message.equals(TextDataItem.defaultMessage)) {
                this.editText.setText(this.textData.message, TextView.BufferType.EDITABLE);
            }
            Log.e(TAG, this.textData.message);
            this.textView.setTextColor(this.textData.textPaint.getColor());
            this.textView.setText(this.textData.message);
            if (this.textData.getFontPath() != null) {
                Typeface typeFace = FontCache.get(this.activity, this.textData.getFontPath());
                if (typeFace != null) {
                    this.textView.setTypeface(typeFace);
                }
            }
        }
        Log.e(TAG, this.textView.getText().toString());
        Log.e(TAG, this.textData.message);
        Log.e(TAG, this.editText.getText().toString());
        GridView gridView = (GridView) fragmentView.findViewById(R.id.gridview_font);
        this.customGridAdapter = new WriteTextFontAdapter(this.activity, R.layout.row_grid, this.fontPathList);
        gridView.setAdapter(this.customGridAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Typeface typeFace = FontCache.get(activity, fontPathList[position]);
                if (typeFace != null) {
                    textView.setTypeface(typeFace);
                }
                textData.setTextFont(fontPathList[position], activity);
            }
        });
        fragmentView.findViewById(R.id.button_text_color).setOnClickListener(this);
        fragmentView.findViewById(R.id.button_font_ok).setOnClickListener(this);
        return fragmentView;
    }

    public void setFontChoosedListener(FontChoosedListener l) {
        this.fontChoosedListener = l;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.activity = getActivity();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.textview_font) {
            editText.requestFocusFromTouch();
            ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(editText, 0);
            String message = textView.getText().toString();
            if (message.compareToIgnoreCase(TextDataItem.defaultMessage) != 0) {
                editText.setText(message);
                editText.setSelection(editText.getText().length());
            } else {
                editText.setText("");
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    editText.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 0, 0.0f, 0.0f, 0));
                    editText.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 1, 0.0f, 0.0f, 0));
                    editText.setSelection(editText.getText().length());
                }
            }, 200);
        } else if (id == R.id.button_font_ok) {
            String newMessage = textView.getText().toString();
            if (newMessage.compareToIgnoreCase(TextDataItem.defaultMessage) == 0 || newMessage.length() == 0) {
                if (activity == null) {
                    activity = getActivity();
                }
                Toast msg = Toast.makeText(activity, getString(R.string.canvas_text_enter_text), Toast.LENGTH_SHORT);
                msg.setGravity(17, msg.getXOffset() / 2, msg.getYOffset() / 2);
                msg.show();
                return;
            }
            if (newMessage.length() == 0) {
                textData.message = TextDataItem.defaultMessage;
            } else {
                textData.message = newMessage;
            }
            editText.setText("");
            textView.setText("");
            ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(editText.getWindowToken(), 0);
            if (fontChoosedListener != null) {
                fontChoosedListener.onOk(textData);
            } else {
                Toast.makeText(getActivity(), "Null", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.button_text_color) {
            try {

                ColorPickerDialogBuilder
                        .with(getActivity())
                        .setTitle("Choose color")
                        .initialColor(textView.getCurrentTextColor())
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(12)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {
                            }
                        })
                        .setPositiveButton("ok", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
//                                changeBackgroundColor(selectedColor);
                                textView.setTextColor(selectedColor);
                                textData.textPaint.setColor(selectedColor);
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .build()
                        .show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onDestroy() {
        if (this.customGridAdapter != null) {
            if (this.customGridAdapter.typeFaceArray != null) {
                int length = this.customGridAdapter.typeFaceArray.length;
                for (int i = 0; i < length; i++) {
                    this.customGridAdapter.typeFaceArray[i] = null;
                }
            }
            this.customGridAdapter.typeFaceArray = null;
        }
        super.onDestroy();
    }
}
