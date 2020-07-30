package com.athings.photoeditor.utils;

import java.util.ArrayList;
import java.util.List;

public class AlbumItem {
    public int ID;
    public List<GridViewItem> gridItems;
    public long imageIdForThumb;
    public List<Long> imageIdList;
    public String name;
    public List<Integer> orientationList;

    public AlbumItem() {
        this.imageIdList = new ArrayList<>();
        this.orientationList = new ArrayList<>();
    }
}
