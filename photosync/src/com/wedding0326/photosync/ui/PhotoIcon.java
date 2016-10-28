package com.wedding0326.photosync.ui;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

public class PhotoIcon extends Canvas {

    Image photo;

    Color white;

    public PhotoIcon(Composite parent, int style) {
        super(parent, style);
        white = new Color(null, 255, 255, 255);
        setBackground(white);

        addDisposeListener(new DisposeListener() {

            public void widgetDisposed(DisposeEvent e) {
                white.dispose();
            }
        });

        addDisposeListener(new DisposeListener() {

            public void widgetDisposed(DisposeEvent e) {

                PhotoIcon.this.widgetDisposed(e);

            }

        });

        addPaintListener(new PaintListener() {

            public void paintControl(PaintEvent e) {

                PhotoIcon.this.paintControl(e);

            }

        });
    }

    void widgetDisposed(DisposeEvent e) {
        white.dispose();

    }

    void paintControl(PaintEvent e) {

        GC gc = e.gc;

        int x = 1;

        if (photo != null) {

            gc.drawImage(photo, x, 1);

            x = photo.getBounds().width + 5;
        }

    }

    public Image getPhoto() {
        return photo;
    }

    public void setPhoto(Image image) {
        this.photo = image;
        redraw();
    }

    public Point computeSize(int wHint, int hHint, boolean changed) {
        int width = 0, height = 0;
        if (photo != null) {
            Rectangle bounds = photo.getBounds();
            width = bounds.width + 5;
            height = bounds.height;
        }
        return new Point(width + 2, height + 2);

    }

}
