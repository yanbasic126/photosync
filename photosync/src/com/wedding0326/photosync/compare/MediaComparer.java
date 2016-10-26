package com.wedding0326.photosync.compare;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.wedding0326.photosync.core.DuplicatesModel;
import com.wedding0326.photosync.core.MediaModel;
import com.wedding0326.photosync.util.MediaBrowser;

/**
 * DOC yyi class global comment. Detailled comment <br/>
 *
 * $Id$
 *
 */
public class MediaComparer {

    Set<String> pathList;

    private List<MediaModel> mediaList;

    public MediaComparer() {
        mediaList = new ArrayList<>();
        pathList = new HashSet<>();
    }

    public void addPath(String path) {
        this.pathList.add(path);
    }

    public Set<String> getPathList() {
        return this.pathList;
    }

    public CompareResult doCompare() {
        CompareResult result = null;
        if (readMedias()) {

            Map<MediaModel, DuplicatesModel> duplicates = new HashMap<>();

            mediaList.forEach(m -> {
                if (duplicates.get(m) != null) {
                    duplicates.get(m).addDuplicates(m);
                } else {
                    duplicates.put(m, new DuplicatesModel(m));
                }
            });
            result = new CompareResult();
            result.addResult(duplicates);
        } else {
            System.out.println("do compare fails");
        }

        return result;

    }

    private boolean readMedias() {

        boolean read = true;
        this.pathList.forEach(path -> {
            try {
                MediaBrowser.readMedias(path, mediaList);
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("read medias fails");
            }

        });

        return read;
    }
}
