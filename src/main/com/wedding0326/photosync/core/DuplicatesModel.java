package com.wedding0326.photosync.core;

import java.util.LinkedList;
import java.util.List;

public class DuplicatesModel {

    List<MediaModel> duplicates;

    public DuplicatesModel(MediaModel modle) {
        duplicates = new LinkedList<>();
        duplicates.add(modle);
    }

    public void addDuplicates(MediaModel model) {
        duplicates.add(model);
    }

    public List<MediaModel> getDuplicates() {
        return duplicates;
    }
}