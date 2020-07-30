package com.athings.photoeditor.canvastextview;

import android.graphics.Paint;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class CustomPaint extends Paint implements Serializable {
    private static final long serialVersionUID = -2455397208601380474L;
    int color;
    float textSize;

    public CustomPaint() {
        super.setAntiAlias(true);
    }

    public CustomPaint(CustomPaint src) {
        super(src);
        this.color = src.color;
        this.textSize = src.textSize;
        super.setAntiAlias(true);
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        this.color = super.getColor();
        this.textSize = super.getTextSize();
        oos.writeInt(this.color);
        oos.writeFloat(this.textSize);
    }

    private void readObject(ObjectInputStream ois) throws Exception, ClassNotFoundException {
        ois.defaultReadObject();
        this.color = ois.readInt();
        this.textSize = ois.readFloat();
        super.setColor(this.color);
        super.setTextSize(this.textSize);
        super.setAntiAlias(true);
    }
}
