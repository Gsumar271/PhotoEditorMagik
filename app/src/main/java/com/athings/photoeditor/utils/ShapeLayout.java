package com.athings.photoeditor.utils;

public class ShapeLayout {
    boolean isScalable;
    int porterDuffClearBorderIntex;
    public Shape[] shapeArr;

    public ShapeLayout(Shape[] arr) {
        this.isScalable = false;
        this.porterDuffClearBorderIntex = -1;
        this.shapeArr = arr;
    }

    public void setClearIndex(int index) {
        if (index >= 0 && index < this.shapeArr.length) {
            this.porterDuffClearBorderIntex = index;
        }
    }

    public int getClearIndex() {
        return this.porterDuffClearBorderIntex;
    }

}
