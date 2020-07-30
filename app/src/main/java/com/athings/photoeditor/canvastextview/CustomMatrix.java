package com.athings.photoeditor.canvastextview;

import android.graphics.Matrix;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class CustomMatrix extends Matrix implements Serializable {
    private static final long serialVersionUID = 6346371585195628612L;

    public CustomMatrix(Matrix src) {
        super(src);
    }

    public CustomMatrix() {
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        float[] values = new float[9];
        super.getValues(values);
        oos.writeObject(values);
    }

    private void readObject(ObjectInputStream ois) throws Exception, ClassNotFoundException {
        ois.defaultReadObject();
        float[] fArr = new float[9];
        super.setValues((float[]) ois.readObject());
    }
}
