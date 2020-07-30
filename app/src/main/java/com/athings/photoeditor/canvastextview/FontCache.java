package com.athings.photoeditor.canvastextview;

import android.content.Context;
import android.graphics.Typeface;

import com.athings.photoeditor.BuildConfig;

import java.util.Hashtable;

public class FontCache {
    private static final Hashtable<String, Typeface> cache;

    static {
        cache = new Hashtable();
    }

    public static Typeface get(Context c, String name) {
        synchronized (cache) {
            if (name != null) {
                if (!(name.length() == 0 || name.compareTo(BuildConfig.FLAVOR) == 0)) {
                    if (!cache.containsKey(name)) {
                        cache.put(name, Typeface.createFromAsset(c.getAssets(), name));
                    }
                    Typeface typeface = (Typeface) cache.get(name);
                    return typeface;
                }
            }
            return null;
        }
    }
}
