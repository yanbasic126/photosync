package com.wedding0326.photosync.test;

import com.wedding0326.photosync.compare.CompareResult;
import com.wedding0326.photosync.compare.MediaComparer;

public class SimpleTest {

    public void testAddAndRemoveIU() {
        MediaComparer comp = new MediaComparer();
        comp.addPath("");
        comp.addPath("");
        CompareResult result = comp.doCompare();

        result.getIdenticalList().forEach(a -> System.out.println(a.getFullPath()));
        result.getDuplicateList().forEach(a -> {
            System.out.println("------>" + a.getDuplicates().get(0).getFullPath());
            System.out.println(a.getDuplicates().size());
        });
    }
}
