package com.wedding0326.photosync.ui.photoExplorer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Stack;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.nebula.widgets.gallery.DefaultGalleryItemRenderer;
import org.eclipse.nebula.widgets.gallery.Gallery;
import org.eclipse.nebula.widgets.gallery.GalleryItem;
import org.eclipse.nebula.widgets.gallery.NoGroupRenderer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.gdip.Gdip;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.drew.imaging.ImageProcessingException;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.exif.ExifThumbnailDirectory;
import com.wedding0326.photosync.compare.CompareResult;
import com.wedding0326.photosync.core.MediaModel;
import com.wedding0326.photosync.ui.photoViewer.PhotoViewer;
import com.wedding0326.photosync.util.TestUtils;

public class PhotoExplorer {

    private static final int PREVIEW_SIZE = 102;

    private static final String JEPG_LOWERCASE = "jepg";

    private static final String JPG_LOWERCASE = "jpg";

    private Gallery gallery;

    private GalleryItem defaultGroup;

    private volatile boolean[] generated;

    private String[] paths;

    /**
     * this stack is the default stack, contains all index id, if the image is rejeced by
     * {@link JpgThumbnailPreviewGenerator}, will push the id to slow stack immediately.
     */
    private Stack<Integer> displayItemFastStack = new Stack<>();

    private Stack<Integer> displayItemSlowStack = new Stack<>();

    private int exists;

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

        addGalleryListener(gallery);
        defaultGroup = new GalleryItem(gallery, SWT.NONE);

    }

    private void addGalleryListener(Gallery gallery) {
        gallery.addMouseListener(new MouseListener() {

            @Override
            public void mouseDoubleClick(MouseEvent e) {
                // TODO Auto-generated method stub
                GalleryItem item = gallery.getSelection()[0];
                System.out.println(paths[defaultGroup.indexOf(item) - exists]);
                Display display = Display.getDefault();
                PhotoViewer photoViewer = new PhotoViewer(display, paths[defaultGroup.indexOf(item) - exists]);
                photoViewer.open();
                photoViewer.layout();
            }

            @Override
            public void mouseDown(MouseEvent paramMouseEvent) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseUp(MouseEvent paramMouseEvent) {
                // TODO Auto-generated method stub

            }

        });

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
                    displayItemFastStack.push(index - exists);
                }
            }
        });
    }

    public void addMedias(List<MediaModel> mediaList) {
        if (mediaList != null) {
            exists = defaultGroup.getItemCount();
            defaultGroup.setItemCount(exists + mediaList.size());
            defaultGroup.setExpanded(true);
            gallery.setFocus();
            displayItemFastStack.clear();
            generated = null;
            generated = new boolean[mediaList.size()];
            paths = null;
            paths = new String[mediaList.size()];

            // for (int i = 0; i < mediaList.size() - 1; i++) {
            for (int i = mediaList.size() - 1; i >= 0; i--) {
                displayItemFastStack.push(i);
                paths[i] = mediaList.get(i).getFullPath();
            }

            new Thread(new JpgThumbnailPreviewGenerator()).start();
            new Thread(new JpgThumbnailPreviewGenerator()).start();
            // new Thread(new JpgThumbnailPreviewGenerator()).start();
            // new Thread(new JpgThumbnailPreviewGenerator()).start();
            // new Thread(new PreviewGenerator()).start();
            // new Thread(new PreviewGenerator()).start();
            // new Thread(new PreviewGenerator()).start();
        }
    }

    public void setCompareResult(CompareResult result, IProgressMonitor monitor) {
        addMedias(result.getIdenticalList());
    }

    public void emptyDefaultGroup() {
        defaultGroup.clearAll();
    }

    class PreviewGenerator implements Runnable {

        @Override
        public void run() {
            Image src = null;
            Image preview;
            ByteArrayInputStream inputByteStream;
            ByteArrayInputStream thumbByteStream;
            ExifThumbnailDirectory thumbDirectory;
            Display display = Display.getDefault();
            ImageLoader loader = new ImageLoader();
            int i;
            String extention;

            while (!displayItemFastStack.isEmpty()) {
                i = displayItemFastStack.pop();
                if (!generated[i]) {
                    generated[i] = true;
                    try {
                        TestUtils.printTime("--read 0---");
                        inputByteStream = new ByteArrayInputStream(Files.readAllBytes(Paths.get(paths[i])));
                        TestUtils.printTime("--read 1(I/O)---");
                        extention = paths[i].substring(paths[i].lastIndexOf('.')).toLowerCase();
                        if (extention.endsWith(JPG_LOWERCASE) || extention.endsWith(JEPG_LOWERCASE)) {
                            thumbDirectory = JpegMetadataReader.readMetadata(inputByteStream).getFirstDirectoryOfType(
                                    ExifThumbnailDirectory.class);
                            if (thumbDirectory != null) {
                                thumbByteStream = new ByteArrayInputStream(thumbDirectory.getThumbnailData());
                                src = new Image(display, loader.load(thumbByteStream)[0]);
                            } else {

                            }
                        } else {
                            src = new Image(display, loader.load(inputByteStream)[0]);
                        }
                        preview = scaleToPreview(src);
                        src.dispose();
                        display.asyncExec(new DisplayAsyncThread(i, preview));

                        TestUtils.printTime("--read 2(In Memory)---");
                    } catch (IOException | ImageProcessingException e) {
                    }
                }
            }

            src = null;
            inputByteStream = null;
            thumbByteStream = null;
            thumbDirectory = null;
            display = null;
            loader = null;
            extention = null;
        }

        private Image scaleToPreview(Image image) {
            Image scaled = new Image(Display.getDefault(), PREVIEW_SIZE, PREVIEW_SIZE);
            GC gc = new GC(scaled, Gdip.SmoothingModeNone);
            // gc.setAntialias(SWT.ON);//smooth
            // gc.setInterpolation(SWT.LOW);

            Rectangle rectangle = image.getBounds();
            int width = rectangle.width;
            int height = rectangle.height;
            int size = (width <= height) ? width : height;
            int x = (width > height) ? (width - height) / 2 : 0;
            int y = (height > width) ? (height - width) / 2 : 0;
            gc.drawImage(image, x, y, size, size, 0, 0, PREVIEW_SIZE, PREVIEW_SIZE);

            rectangle = null;
            gc.dispose();
            gc = null;
            return scaled;
        }
    }

    /**
     * read jpeg thumbnail, if read fail, read the real content.
     *
     */
    class JpgThumbnailPreviewGenerator implements Runnable {

        @Override
        public void run() {
            Image src;
            Image preview;
            ByteArrayInputStream inputByteStream;
            ByteArrayInputStream thumbByteStream;
            ExifThumbnailDirectory thumbDirectory;
            Display display = Display.getDefault();
            ImageLoader loader = new ImageLoader();
            int i;

            while (!displayItemFastStack.isEmpty()) {
                i = displayItemFastStack.pop();
                if (!generated[i]) {
                    generated[i] = true;
                    try {
                        TestUtils.printTime("--read 0---");
                        inputByteStream = new ByteArrayInputStream(Files.readAllBytes(Paths.get(paths[i])));
                        System.out.println("------>" + inputByteStream.available());
                        TestUtils.printTime("--read 1(I/O)---");
                        thumbDirectory = JpegMetadataReader.readMetadata(inputByteStream).getFirstDirectoryOfType(
                                ExifThumbnailDirectory.class);
                        if (thumbDirectory != null) {
                            thumbByteStream = new ByteArrayInputStream(thumbDirectory.getThumbnailData());
                            src = new Image(display, loader.load(thumbByteStream)[0]);
                            preview = scaleToPreview(src);
                            src.dispose();
                            display.asyncExec(new DisplayAsyncThread(i, preview));
                        }
                        // displayItemSlowStack.push(i);
                        // // inputByteStream.reset();
                        // // src = new Image(display, loader.load(inputByteStream)[0]);
                        // } else {
                        TestUtils.printTime("--read 2(In Memory)---");
                    } catch (IOException | ImageProcessingException e) {
                    }
                }
            }

            src = null;
            inputByteStream = null;
            thumbByteStream = null;
            thumbDirectory = null;
            display = null;
            loader = null;
        }

        private Image scaleToPreview(Image image) {
            Image scaled = new Image(Display.getDefault(), PREVIEW_SIZE, PREVIEW_SIZE);
            GC gc = new GC(scaled, Gdip.SmoothingModeNone);
            // gc.setAntialias(SWT.ON);//smooth
            // gc.setInterpolation(SWT.LOW);

            Rectangle rectangle = image.getBounds();
            int width = rectangle.width;
            int height = rectangle.height;
            int size = (width <= height) ? width : height;
            int x = (width > height) ? (width - height) / 2 : 0;
            int y = (height > width) ? (height - width) / 2 : 0;
            gc.drawImage(image, x, y, size, size, 0, 0, PREVIEW_SIZE, PREVIEW_SIZE);

            rectangle = null;
            gc.dispose();
            gc = null;
            return scaled;
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
