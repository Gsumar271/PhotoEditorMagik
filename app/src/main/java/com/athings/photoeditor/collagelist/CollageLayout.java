package com.athings.photoeditor.collagelist;

import java.util.ArrayList;
import java.util.List;

public class CollageLayout {

    public int exceptionIndexForShapes[][];
    boolean isScalable;
    public List<MaskPair> maskPairList;
    int porterDuffClearBorderIntex;
    public List shapeList;
    public boolean useLine = true;

    public CollageLayout() {
        isScalable = false;
        porterDuffClearBorderIntex = -1;
        exceptionIndexForShapes = null;
        maskPairList = new ArrayList<>();
    }

    public CollageLayout(List list) {
        isScalable = false;
        porterDuffClearBorderIntex = -1;
        exceptionIndexForShapes = null;
        maskPairList = new ArrayList<>();
        shapeList = list;
    }

    public int getClearIndex() {
        return porterDuffClearBorderIntex;
    }

    public int[] getexceptionIndex(int i) {
        if (exceptionIndexForShapes == null || i >= exceptionIndexForShapes.length || i < 0) {
            return null;
        } else {
            return exceptionIndexForShapes[i];
        }
    }

    public void setClearIndex(int i) {
        if (i >= 0 && i < shapeList.size()) {
            porterDuffClearBorderIntex = i;
        }
    }
}
