package com.athings.photoeditor.utils;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

public class Parameter implements Serializable, Parcelable {
    public static final Creator<Parameter> CREATOR;
    public static final int seek_bar_brightness = 0;
    public static final int seek_bar_saturation = 3;
    public static final int seek_bar_sharpen = 5;
    public static final int seek_bar_temperature = 2;
    public static final int seek_bar_tint = 4;
    private static final long serialVersionUID = -3588782317514910667L;
    public static AtomicInteger uniqueId;
    public int blur;
    public int brightness;
    public int contrast;
    public float highlight;
    public int id;
    public int saturation;
    public int seekBarMode;
    public int selectedBorderIndex;
    public int selectedFilterIndex;
    public int selectedOverlayIndex;
    public int selectedTextureIndex;
    public float shadow;
    public float sharpen;
    public int temperature;
    public int tint;


    public Parameter copy() {
        return new Parameter(this);
    }

    public int getId() {
        return this.id;
    }

    public void setId(int d) {
        this.id = d;
    }

    public boolean isParameterReallyChanged(Parameter p) {
        if (this.contrast == p.contrast && this.brightness == p.brightness && this.saturation == p.saturation && this.temperature == p.temperature && this.tint == p.tint && this.sharpen == p.sharpen && this.blur == p.blur && this.highlight == p.highlight && this.shadow == p.shadow && this.selectedBorderIndex == p.selectedBorderIndex && this.selectedFilterIndex == p.selectedFilterIndex && this.selectedOverlayIndex == p.selectedOverlayIndex && this.selectedTextureIndex == p.selectedTextureIndex) {
        }
        return true;
    }

    public Parameter(Parameter p) {
        this.sharpen = 0.0f;
        this.blur = seek_bar_brightness;
        this.highlight = 0.0f;
        this.shadow = 0.0f;
        this.seekBarMode = seek_bar_brightness;
        set(p);
    }

    public Parameter() {
        this.sharpen = 0.0f;
        this.blur = seek_bar_brightness;
        this.highlight = 0.0f;
        this.shadow = 0.0f;
        this.seekBarMode = seek_bar_brightness;
        reset();
        this.id = uniqueId.getAndIncrement();
        this.seekBarMode = seek_bar_brightness;
    }

    public void set(Parameter p) {
        this.brightness = p.brightness;
        this.temperature = p.temperature;
        this.contrast = p.contrast;
        this.saturation = p.saturation;
        this.tint = p.tint;
        this.selectedTextureIndex = p.selectedTextureIndex;
        this.selectedBorderIndex = p.selectedBorderIndex;
        this.selectedOverlayIndex = p.selectedOverlayIndex;
        this.selectedFilterIndex = p.selectedFilterIndex;
        this.sharpen = p.sharpen;
        this.blur = p.blur;
        this.highlight = p.highlight;
        this.shadow = p.shadow;
        this.seekBarMode = p.seekBarMode;
        this.id = p.id;
    }

    static {
        uniqueId = new AtomicInteger();
        CREATOR = new Creator<Parameter>() {
            @Override
            public Parameter createFromParcel(Parcel parcel) {
                return new Parameter(parcel);
            }

            @Override
            public Parameter[] newArray(int i) {
                return new Parameter[i];
            }
        };
    }

    public void reset() {
        this.brightness = seek_bar_brightness;
        this.contrast = seek_bar_brightness;
        this.temperature = seek_bar_brightness;
        this.saturation = 50;
        this.tint = seek_bar_brightness;
        this.selectedTextureIndex = seek_bar_brightness;
        this.selectedBorderIndex = seek_bar_brightness;
        this.selectedOverlayIndex = seek_bar_brightness;
        this.selectedFilterIndex = seek_bar_brightness;
        this.sharpen = 0.0f;
        this.blur = seek_bar_brightness;
        this.highlight = 0.0f;
        this.shadow = 0.0f;
    }

    public void setTemperature(int progress) {
        this.temperature = (progress - 50) * seek_bar_temperature;
    }

    public void setContrast(int progress) {
        this.contrast = (progress - 50) * seek_bar_temperature;
    }


    public void setBrightness(int progress) {
        int value = progress - 50;
        if (value < 0) {
            brightness = value * seek_bar_saturation;
        } else {
            brightness = value * seek_bar_sharpen;
        }
    }

    public int getContrastProgress() {
        return (this.contrast / seek_bar_temperature) + 50;
    }

    public int getBrightProgress() {
        if (this.brightness < 0) {
            return (this.brightness / seek_bar_saturation) + 50;
        }
        return (this.brightness / seek_bar_sharpen) + 50;
    }

    public int getTemperatureProgress() {
        return (this.temperature / seek_bar_temperature) + 50;
    }

    public void setSaturation(int progress) {
        this.saturation = progress;
    }

    public void setTint(int value) {
        this.tint = value - 50;
    }

    public int getTintProgressValue() {
        return this.tint + 50;
    }

    public void setSharpen(int value) {
        this.sharpen = ((float) value) / 100.0f;
    }

    public int getSharpenValue() {
        return (int) (this.sharpen * 100.0f);
    }

    public void setBlur(int value) {
        float radius = ((float) value) / 4.0f;
        if (radius > 25) {
            radius = 25;
        }
        this.blur = (int) radius;
    }

    public int getBlurValue() {
        return this.blur * seek_bar_tint;
    }

    public void setHighlight(int value) {
        this.highlight = ((float) (value - 50)) / 255.0f;
    }

    public int getHighlightValue() {
        return (int) ((this.highlight * 255.0f) + 50.0f);
    }

    public void setShadow(int value) {
        this.shadow = ((float) (value - 50)) / 255.0f;
    }

    public int getShadowValue() {
        return (int) ((this.shadow * 255.0f) + 50.0f);
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        oos.writeInt(brightness);
        oos.writeInt(contrast);
        oos.writeInt(temperature);
        oos.writeInt(saturation);
        oos.writeInt(tint);
        oos.writeInt(selectedTextureIndex);
        oos.writeInt(selectedBorderIndex);
        oos.writeInt(selectedOverlayIndex);
        oos.writeInt(selectedFilterIndex);
        oos.writeInt(seekBarMode);
        oos.writeFloat(sharpen);
        oos.writeInt(blur);
        oos.writeFloat(highlight);
        oos.writeFloat(shadow);
        oos.writeInt(id);
    }

    private void readObject(ObjectInputStream ois) throws Exception, ClassNotFoundException {
        ois.defaultReadObject();
        brightness = ois.readInt();
        this.contrast = ois.readInt();
        temperature = ois.readInt();
        saturation = ois.readInt();
        tint = ois.readInt();
        selectedTextureIndex = ois.readInt();
        this.selectedBorderIndex = ois.readInt();
        selectedOverlayIndex = ois.readInt();
        this.selectedFilterIndex = ois.readInt();
        seekBarMode = ois.readInt();
        try {
            sharpen = ois.readFloat();
            blur = ois.readInt();
            highlight = ois.readFloat();
            shadow = ois.readFloat();
            id = ois.readInt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Parameter(Parcel in) {
        sharpen = 0.0f;
        blur = seek_bar_brightness;
        highlight = 0.0f;
        shadow = 0.0f;
        seekBarMode = seek_bar_brightness;
        brightness = in.readInt();
        contrast = in.readInt();
        temperature = in.readInt();
        saturation = in.readInt();
        tint = in.readInt();
        selectedTextureIndex = in.readInt();
        selectedBorderIndex = in.readInt();
        selectedOverlayIndex = in.readInt();
        selectedFilterIndex = in.readInt();
        seekBarMode = in.readInt();
        sharpen = in.readFloat();
        blur = in.readInt();
        highlight = in.readFloat();
        shadow = in.readFloat();
        id = in.readInt();
    }

    public int describeContents() {
        return seek_bar_brightness;
    }

    public void writeToParcel(Parcel out, int arg1) {
        out.writeInt(brightness);
        out.writeInt(contrast);
        out.writeInt(temperature);
        out.writeInt(saturation);
        out.writeInt(tint);
        out.writeInt(selectedTextureIndex);
        out.writeInt(selectedBorderIndex);
        out.writeInt(selectedOverlayIndex);
        out.writeInt(selectedFilterIndex);
        out.writeInt(seekBarMode);
        out.writeFloat(sharpen);
        out.writeInt(blur);
        out.writeFloat(highlight);
        out.writeFloat(shadow);
        out.writeInt(id);
    }
}
