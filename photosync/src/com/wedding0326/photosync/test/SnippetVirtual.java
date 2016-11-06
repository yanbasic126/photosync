// ============================================================================
//
// Talend Community Edition
//
// Copyright (C) 2006-2013 Talend – www.talend.com
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
//
// ============================================================================
package com.wedding0326.photosync.test;

import org.eclipse.nebula.widgets.gallery.DefaultGalleryGroupRenderer;
import org.eclipse.nebula.widgets.gallery.DefaultGalleryItemRenderer;
import org.eclipse.nebula.widgets.gallery.Gallery;
import org.eclipse.nebula.widgets.gallery.GalleryItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

/**
 * DOC yyi class global comment. Detailled comment <br/>
 *
 * $Id$
 *
 */
public class SnippetVirtual {

    public static void main(String[] args) {
        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setLayout(new FillLayout());
        final Gallery gallery = new Gallery(shell, SWT.V_SCROLL | SWT.VIRTUAL);

        // Renderers
        DefaultGalleryGroupRenderer gr = new DefaultGalleryGroupRenderer();
        gr.setItemSize(64, 64);
        gr.setMinMargin(3);
        DefaultGalleryItemRenderer ir = new DefaultGalleryItemRenderer();

        gallery.setGroupRenderer(gr);
        gallery.setItemRenderer(ir);

        gallery.setVirtualGroups(true);

        gallery.addListener(SWT.SetData, new Listener() {

            public void handleEvent(Event event) {
                GalleryItem item = (GalleryItem) event.item;
                int index;
                if (item.getParentItem() != null) {
                    index = item.getParentItem().indexOf(item);
                    item.setItemCount(0);
                } else {
                    index = gallery.indexOf(item);
                    item.setItemCount(100);
                }

                System.out.println("setData index " + index); //$NON-NLS-1$
                // Your image here
                // item.setImage(eclipseImage);
                item.setText("Item " + index); //$NON-NLS-1$
            }

        });

        gallery.setItemCount(100);
        shell.pack();
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        display.dispose();
    }
}
