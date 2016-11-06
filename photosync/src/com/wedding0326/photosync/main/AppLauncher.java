package com.wedding0326.photosync.main;

import org.eclipse.swt.widgets.Display;

import com.wedding0326.photosync.ui.PhotoSyncWindow;
import com.wedding0326.photosync.util.TestUtils;

/**
 * DOC yyi class global comment. Detailled comment <br/>
 *
 * $Id$
 *
 */
public class AppLauncher {

    public static void main(String[] args) {

        try {
            TestUtils.openSleak();
            PhotoSyncWindow window = new PhotoSyncWindow();
            window.setBlockOnOpen(true);
            window.open();
            Display.getCurrent().dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void test() {
        // MediaComparer comp = new MediaComparer();
        // comp.addPath("D:\\yyi.talendbj.esb\\dev\\comparetest\\src");
        // comp.addPath("D:\\yyi.talendbj.esb\\dev\\comparetest\\tar");
        // CompareResult result = comp.doCompare();
        // result.getIdenticalList().forEach(a -> System.out.println(a.getFullPath()));
        // result.getDuplicateList().forEach(a -> {
        // System.out.println("------>" + a.getDuplicates().get(0).getFullPath());
        // System.out.println(a.getDuplicates().size());
        // });
    }
}
