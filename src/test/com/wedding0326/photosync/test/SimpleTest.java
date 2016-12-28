package com.wedding0326.photosync.test;

import com.wedding0326.photosync.compare.CompareResult;
import com.wedding0326.photosync.compare.MediaComparer;

public class SimpleTest {

    public void testAddAndRemoveIU() {
        MediaComparer comp = new MediaComparer();
        comp.addPath("D:\\yyi.talendbj.esb\\dev\\comparetest\\src");
        comp.addPath("D:\\yyi.talendbj.esb\\dev\\comparetest\\tar");
        CompareResult result = comp.doCompare();
        String b;
        System.out.println(b);
        result.getIdenticalList().forEach(a -> System.out.println(a.getFullPath()));
        result.getDuplicateList().forEach(a -> {
            System.out.println("------>" + a.getDuplicates().get(0).getFullPath());
            System.out.println(a.getDuplicates().size());
        });
    }
}
