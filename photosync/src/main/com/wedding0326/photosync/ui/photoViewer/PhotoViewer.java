package com.wedding0326.photosync.ui.photoViewer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;

public class PhotoViewer extends Shell {

    private Canvas previewCanvas;

    private Image previewImage;

    private String imagePath;

    /**
     * Create the shell.
     * 
     * @param display
     */
    public PhotoViewer(Display display, String imagePath) {
        super(display, SWT.SHELL_TRIM);
        this.imagePath = imagePath;
        setLayout(new GridLayout(1, false));

        Composite previewComp = new Composite(this, SWT.NONE);
        previewComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        GridLayout gl_previewComp = new GridLayout(1, false);
        gl_previewComp.marginHeight = 0;
        gl_previewComp.verticalSpacing = 0;
        gl_previewComp.marginWidth = 1;
        gl_previewComp.horizontalSpacing = 0;
        previewComp.setLayout(gl_previewComp);

        previewCanvas = new Canvas(previewComp, SWT.NONE);
        previewCanvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        GridLayout gl_previewCanvas = new GridLayout(1, false);
        gl_previewCanvas.marginHeight = 0;
        gl_previewCanvas.verticalSpacing = 0;
        gl_previewCanvas.marginWidth = 0;
        gl_previewCanvas.horizontalSpacing = 0;
        previewCanvas.setLayout(gl_previewCanvas);

        previewCanvas.addPaintListener(new PaintListener() {

            @Override
            public void paintControl(PaintEvent e) {
                redraw(e);
            }
        });

        Composite buttonsComp = new Composite(previewComp, SWT.NONE);
        GridLayout gl_buttonsComp = new GridLayout(2, false);
        gl_buttonsComp.marginHeight = 0;
        gl_buttonsComp.verticalSpacing = 0;
        gl_buttonsComp.horizontalSpacing = 0;
        gl_buttonsComp.marginWidth = 0;
        buttonsComp.setLayout(gl_buttonsComp);
        buttonsComp.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
        buttonsComp.setBounds(0, 0, 64, 64);

        Button rotateButton1 = new Button(buttonsComp, SWT.NONE);
        rotateButton1.setText("Counterclockwise");

        rotateButton1.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                // TODO Auto-generated method stub
                // rotate();
                super.widgetSelected(e);
            }
        });

        Button rotateButton2 = new Button(buttonsComp, SWT.NONE);
        rotateButton2.setText("Clockwise");

        createContents();

        Display.getDefault().asyncExec(new Runnable() {

            @Override
            public void run() {
                try {
                    ByteArrayInputStream inputByteStream = new ByteArrayInputStream(Files.readAllBytes(Paths.get(imagePath)));
                    ImageLoader loader = new ImageLoader();
                    previewImage = new Image(display, loader.load(inputByteStream)[0]);
                    previewCanvas.redraw();
                    setText(imagePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * Create contents of the shell.
     */
    protected void createContents() {
        // setText("SWT Application");
        // setSize(450, 300);

    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

    private void redraw(PaintEvent e) {
        if (previewImage != null) {

            Rectangle previewBounds = previewCanvas.getBounds();
            Rectangle imageBounds = previewImage.getBounds();
            double a = (double) previewBounds.height / (double) imageBounds.height;
            double b = (double) previewBounds.width / (double) imageBounds.width;
            double ratio = Math.min(a, b);

            int scaledWidth = (int) (imageBounds.width * ratio);
            int scaledHeight = (int) (imageBounds.height * ratio);

            int x = 0;
            if (scaledWidth < previewBounds.width) {
                x = (previewBounds.width - scaledWidth) / 2;
            }
            e.gc.drawImage(previewImage, 0, 0, imageBounds.width, imageBounds.height, x, 0, scaledWidth, scaledHeight);
        }
    }

    static ImageData rotate(ImageData srcData, int direction) {
        int bytesPerPixel = srcData.bytesPerLine / srcData.width;
        int destBytesPerLine = (direction == SWT.DOWN) ? srcData.width * bytesPerPixel : srcData.height * bytesPerPixel;
        byte[] newData = new byte[srcData.data.length];
        int width = 0, height = 0;
        for (int srcY = 0; srcY < srcData.height; srcY++) {
            for (int srcX = 0; srcX < srcData.width; srcX++) {
                int destX = 0, destY = 0, destIndex = 0, srcIndex = 0;
                switch (direction) {
                case SWT.LEFT: // left 90 degrees
                    destX = srcY;
                    destY = srcData.width - srcX - 1;
                    width = srcData.height;
                    height = srcData.width;
                    break;
                case SWT.RIGHT: // right 90 degrees
                    destX = srcData.height - srcY - 1;
                    destY = srcX;
                    width = srcData.height;
                    height = srcData.width;
                    break;
                case SWT.DOWN: // 180 degrees
                    destX = srcData.width - srcX - 1;
                    destY = srcData.height - srcY - 1;
                    width = srcData.width;
                    height = srcData.height;
                    break;
                }
                destIndex = (destY * destBytesPerLine) + (destX * bytesPerPixel);
                srcIndex = (srcY * srcData.bytesPerLine) + (srcX * bytesPerPixel);
                System.arraycopy(srcData.data, srcIndex, newData, destIndex, bytesPerPixel);
            }
        }
        // destBytesPerLine is used as scanlinePad to ensure that no padding is
        // required
        return new ImageData(width, height, srcData.depth, srcData.palette, destBytesPerLine, newData);
    }

    static ImageData flip(ImageData srcData, boolean vertical) {
        int bytesPerPixel = srcData.bytesPerLine / srcData.width;
        int destBytesPerLine = srcData.width * bytesPerPixel;
        byte[] newData = new byte[srcData.data.length];
        for (int srcY = 0; srcY < srcData.height; srcY++) {
            for (int srcX = 0; srcX < srcData.width; srcX++) {
                int destX = 0, destY = 0, destIndex = 0, srcIndex = 0;
                if (vertical) {
                    destX = srcX;
                    destY = srcData.height - srcY - 1;
                } else {
                    destX = srcData.width - srcX - 1;
                    destY = srcY;
                }
                destIndex = (destY * destBytesPerLine) + (destX * bytesPerPixel);
                srcIndex = (srcY * srcData.bytesPerLine) + (srcX * bytesPerPixel);
                System.arraycopy(srcData.data, srcIndex, newData, destIndex, bytesPerPixel);
            }
        }
        // destBytesPerLine is used as scanlinePad to ensure that no padding is
        // required
        return new ImageData(srcData.width, srcData.height, srcData.depth, srcData.palette, destBytesPerLine, newData);
    }
}
