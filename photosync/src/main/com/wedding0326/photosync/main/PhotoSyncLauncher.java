package com.wedding0326.photosync.main;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import org.eclipse.swt.widgets.Display;

import com.wedding0326.photosync.ui.PhotoSyncWindow;
import com.wedding0326.photosync.ui.fileBrowser.FileBrowser;
import com.wedding0326.photosync.util.TestUtils;

/**
 * DOC yyi class global comment. Detailled comment <br/>
 *
 * $Id$
 *
 */
public class PhotoSyncLauncher {

    public static void main(String[] args) {

//        File swtjar = new File("lib/org.eclipse.swt.win32.win32.x86_64_3.103.1.v20140903-1947.jar");
//        if (swtjar.exists()) {
//            Class[] parameters = { URL.class };
//            URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
//            Class<URLClassLoader> sysclass = URLClassLoader.class;
//            try {
//                Method method = sysclass.getDeclaredMethod("addURL", parameters);
//                method.setAccessible(true);
//
//                method.invoke(sysloader, new Object[] { swtjar.toURL() });
//            } catch (Throwable t) {
//                t.printStackTrace();
//            }
//        }
        try {
            // FileViewer.main(null);
//            TestUtils.openSleak();
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
        // result.getIdenticalList().forEach(a ->
        // System.out.println(a.getFullPath()));
        // result.getDuplicateList().forEach(a -> {
        // System.out.println("------>" +
        // a.getDuplicates().get(0).getFullPath());
        // System.out.println(a.getDuplicates().size());
        // });
    }
}
