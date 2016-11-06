package com.wedding0326.photosync.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import javax.activation.MimetypesFileTypeMap;

import com.wedding0326.photosync.core.MediaModel;

/**
 * DOC yyi class global comment. Detailled comment <br/>
 *
 * $Id$
 *
 */
public class MediaBrowser {

    public static void readMedias(String path, List<MediaModel> mediaList) throws IOException {
        if (mediaList == null) {
            return;
        }
        try (Stream<Path> paths = Files.walk(Paths.get(path))) {
            paths.forEach(filePath -> {

                if (Files.isRegularFile(filePath)) {
                    String mimetype = new MimetypesFileTypeMap().getContentType(filePath.toString());
//                     System.out.println("------>" + mimetype);
                    if (mimetype.startsWith("image/")) {
                        MediaModel model = new MediaModel();
                        File mediaFile = filePath.toFile();
                        model.setName(mediaFile.getName());
                        model.setFullPath(filePath.toString());
                        model.setSize(mediaFile.length());
                        mediaList.add(model);
                    }
                }
            });
        }
    }

}
