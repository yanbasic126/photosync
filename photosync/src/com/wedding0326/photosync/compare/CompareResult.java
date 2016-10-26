package com.wedding0326.photosync.compare;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.wedding0326.photosync.core.DuplicatesModel;
import com.wedding0326.photosync.core.MediaModel;

/**
 * DOC yyi class global comment. Detailled comment <br/>
 *
 * $Id$
 *
 */
public class CompareResult {

    private List<DuplicatesModel> duplicateList;

    private List<MediaModel> identicalList;

    private Map<MediaModel, DuplicatesModel> result;

    public CompareResult() {
        identicalList = new ArrayList<>();
        duplicateList = new ArrayList<>();
    }

    public void addIdentical(MediaModel media) {
        getIdenticalList().add(media);
    }

    public void addDuplicate(DuplicatesModel media) {
        duplicateList.add(media);
    }

    public void addResult(Map<MediaModel, DuplicatesModel> result) {
        this.result = result;
        result.forEach((k, v) -> {

            if (v.getDuplicates().size() == 1) {
                identicalList.add(k);
            }else{
                duplicateList.add(v);
                
            }

        });
    }

    public Map<MediaModel, DuplicatesModel> getResult() {
        return result;
    }

    public List<MediaModel> getIdenticalList() {
        return identicalList;
    }

    public List<DuplicatesModel> getDuplicateList() {
        return duplicateList;
    }

}
