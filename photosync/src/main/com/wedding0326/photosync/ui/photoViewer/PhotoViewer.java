package com.wedding0326.photosync.ui.photoViewer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.gdip.Gdip;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class PhotoViewer extends Shell {

    /**
     * Create the shell.
     * 
     * @param display
     */
    public PhotoViewer(Display display, String imagePath) {
        super(display, SWT.SHELL_TRIM);
        setLayout(new GridLayout(1, false));

        Composite composite = new Composite(this, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        composite.setLayout(new GridLayout(1, false));

        Canvas canvas = new Canvas(composite, SWT.NONE);
        canvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        Composite composite_1 = new Composite(composite, SWT.NONE);
        composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        composite_1.setBounds(0, 0, 64, 64);

        Button btnNewButton = new Button(composite_1, SWT.NONE);
        btnNewButton.setBounds(0, 0, 75, 25);
        btnNewButton.setText("New Button");
        createContents();

        ByteArrayInputStream inputByteStream;
        try {
            inputByteStream = new ByteArrayInputStream(Files.readAllBytes(Paths.get(imagePath)));
            ImageLoader loader = new ImageLoader();
            Image src = new Image(display, loader.load(inputByteStream)[0]);
            canvas.setBackgroundImage(scaleToPreview(src, canvas.getBounds().width, canvas.getBounds().height));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private Image scaleToPreview(Image image, int scaledWidth, int scaledHeigth) {
        Image scaled = new Image(Display.getDefault(), scaledWidth, scaledHeigth);
        GC gc = new GC(scaled, Gdip.SmoothingModeNone);
        // gc.setAntialias(SWT.ON);//smooth
        // gc.setInterpolation(SWT.LOW);

        Rectangle rectangle = image.getBounds();
        int width = rectangle.width;
        int height = rectangle.height;
        int size = (width <= height) ? width : height;
        int x = (width > height) ? (width - height) / 2 : 0;
        int y = (height > width) ? (height - width) / 2 : 0;
        gc.drawImage(image, x, y, size, size, 0, 0, scaledWidth, scaledHeigth);

        rectangle = null;
        gc.dispose();
        gc = null;
        return scaled;
    }

    /**
     * Create contents of the shell.
     */
    protected void createContents() {
        setText("SWT Application");
        setSize(450, 300);

    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }
}
