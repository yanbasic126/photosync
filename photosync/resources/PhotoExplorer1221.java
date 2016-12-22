package com.wedding0326.photosync.ui.photoExplorer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Stream;

import javax.activation.MimetypesFileTypeMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.nebula.widgets.gallery.DefaultGalleryItemRenderer;
import org.eclipse.nebula.widgets.gallery.Gallery;
import org.eclipse.nebula.widgets.gallery.GalleryItem;
import org.eclipse.nebula.widgets.gallery.NoGroupRenderer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.internal.gdip.Gdip;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.exif.ExifThumbnailDirectory;
import com.wedding0326.photosync.compare.CompareResult;
import com.wedding0326.photosync.core.MediaModel;
import com.wedding0326.photosync.util.TestUtils;

public class PhotoExplorer {

    /**
     * 
     */
    private static final String IMAGE_MEDIA = "image";

    private static final int PREVIEW_SIZE = 102;

    private Gallery gallery;

    private GalleryItem defaultGroup;

    private boolean[] generated;

    private String[] paths;

    private Stack<Integer> displayItemStack = new Stack<>();

    private Stack<String> imagePathStack = new Stack<>();

    private ArrayList<byte[]> imageDataList = new ArrayList<>();

    private int exists;

    class ItemImage {

        public ItemImage(int index, byte[] data) {
            this.index = index;
            this.data = data;
        }

        public int index;

        public byte[] data;

    }

    public PhotoExplorer() {
    }

    public void createExplorer(Composite parent) {
        gallery = new Gallery(parent, SWT.V_SCROLL | SWT.MULTI | SWT.VIRTUAL);

        gallery.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        gallery.setLayout(new GridLayout(1, false));

        NoGroupRenderer gr = new NoGroupRenderer();
        // gr.setMinMargin(2);
        gr.setItemSize(PREVIEW_SIZE, PREVIEW_SIZE);
        gr.setMinMargin(0);
        // gr.setItemHeight(56 * 2);
        // gr.setItemWidth(56 * 2);
        gr.setAutoMargin(false);
        gallery.setGroupRenderer(gr);

        DefaultGalleryItemRenderer ir = new DefaultGalleryItemRenderer();
        ir.setShowRoundedSelectionCorners(false);
        ir.setDropShadows(false);
        ir.setShowLabels(false);

        gallery.setItemRenderer(ir);
        gallery.setVirtualGroups(true);

        galleryDragListener(gallery);
        defaultGroup = new GalleryItem(gallery, SWT.NONE);

    }

    private void galleryDragListener(Gallery gallery) {
        gallery.addListener(SWT.SetData, new Listener() {

            public void handleEvent(Event event) {
                GalleryItem item = (GalleryItem) event.item;
                int index = -1;
                if (item.getParentItem() != null) {
                    index = item.getParentItem().indexOf(item);
                } else {
                    index = gallery.indexOf(item);
                }
                if (index != -1) {
                    displayItemStack.push(index - exists);
                }
            }
        });
    }

    public void addMedias(List<MediaModel> mediaList) {
        exists = defaultGroup.getItemCount();
        defaultGroup.setItemCount(exists + mediaList.size());
        defaultGroup.setExpanded(true);
        gallery.setFocus();
        generated = new boolean[mediaList.size()];
        paths = new String[mediaList.size()];
        displayItemStack.clear();

        if (mediaList != null) {
            // for (int i = 0; i < mediaList.size() - 1; i++) {
            TestUtils.printTime("read file1");
            for (int i = mediaList.size() - 1; i >= 0; i--) {
                displayItemStack.push(i);
                paths[i] = mediaList.get(i).getFullPath();
                // try {
                // imageDataList.add(Files.readAllBytes(Paths.get(paths[i])));
                // } catch (IOException e) {
                // e.printStackTrace();
                // }
            }
            TestUtils.printTime("read file done");

            new Thread(new PreviewGenerator()).start();
            new Thread(new PreviewGenerator()).start();
            new Thread(new PreviewGenerator()).start();
            // new Thread(new PreviewGenerator()).start();
            // new Thread(new PreviewGenerator()).start();
            // new Thread(new PreviewGenerator()).start();
        }
    }

    public void setCompareResult(CompareResult result, IProgressMonitor monitor) {
        addMedias(result.getIdenticalList());
    }

    private Image generatePreview(Image image) {
        Image scaled = new Image(Display.getDefault(), PREVIEW_SIZE, PREVIEW_SIZE);
        GC gc = new GC(scaled, Gdip.SmoothingModeNone);
        // gc.setAntialias(SWT.ON);//smooth
        // gc.setInterpolation(SWT.LOW);

        int size = Math.min(image.getBounds().width, image.getBounds().height);
        int x = image.getBounds().width > image.getBounds().height ? (image.getBounds().width - image.getBounds().height) / 2 : 0;
        int y = image.getBounds().height > image.getBounds().width ? (image.getBounds().height - image.getBounds().width) / 2 : 0;
        gc.drawImage(image, x, y, size, size, 0, 0, PREVIEW_SIZE, PREVIEW_SIZE);
        gc.dispose();
        return scaled;
    }

    class PreviewGenerator implements Runnable {

        @Override
        public void run() {
            Image src;
            Image preview;
            ByteArrayInputStream is;
            ByteArrayInputStream is2;
            Display display = Display.getDefault();
            ImageLoader loader = new ImageLoader();
            while (!displayItemStack.isEmpty()) {
                int i = displayItemStack.pop();
                if (!generated[i]) {
                    generated[i] = true;
                    try {
                        TestUtils.printTime("--read 0---");
                        is = new ByteArrayInputStream(Files.readAllBytes(Paths.get(paths[i])));
                        TestUtils.printTime("--read 1(I/O)---");
                        ExifThumbnailDirectory thumb = ImageMetadataReader.readMetadata(is).getFirstDirectoryOfType(
                                ExifThumbnailDirectory.class);

                        is2 = new ByteArrayInputStream(thumb.getThumbnailData());
                        src = new Image(display, loader.load(is2)[0]);
                        preview = generatePreview(src);
                        src.dispose();
                        display.asyncExec(new DisplayAsyncThread(i, preview));

                        TestUtils.printTime("--read 2(In Memory)---");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    class DisplayAsyncThread implements Runnable {

        private Image preview;

        private int index;

        public DisplayAsyncThread(int index, Image preview) {
            this.index = index;
            this.preview = preview;
        }

        @Override
        public void run() {
            defaultGroup.getItem(exists + index).setImage(preview);
        }
    }
}
