package com.wedding0326.photosync.ui.explorer;

import java.util.List;
import java.util.Stack;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.nebula.widgets.gallery.DefaultGalleryItemRenderer;
import org.eclipse.nebula.widgets.gallery.Gallery;
import org.eclipse.nebula.widgets.gallery.GalleryItem;
import org.eclipse.nebula.widgets.gallery.NoGroupRenderer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.gdip.Gdip;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.wedding0326.photosync.compare.CompareResult;
import com.wedding0326.photosync.core.MediaModel;

public class PhotoExplorer {

	private static final int PREVIEW_SIZE = 102;

	private Gallery gallery;

	private GalleryItem defaultGroup;

	private int[] generated;

	private String[] paths;

	private Stack<Integer> virtualItemStack = new Stack<>();

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
					virtualItemStack.push(index-exists);
				}
			}
		});
	}

	public void addMedias(List<MediaModel> mediaList) {
		exists = defaultGroup.getItemCount();
		defaultGroup.setItemCount(exists + mediaList.size());
		defaultGroup.setExpanded(true);
		gallery.setFocus();
		generated = new int[mediaList.size()];
		paths = new String[mediaList.size()];
		virtualItemStack.clear();
		if (mediaList != null) {
			for (int i = mediaList.size() - 1; i >= 0; i--) {
				virtualItemStack.push(i);
				paths[i] = mediaList.get(i).getFullPath();
			}
			new Thread(new ImageLoadThread()).start();
//			new Thread(new ImageLoadThread()).start();
		}

	}

	public void setCompareResult(CompareResult result, IProgressMonitor monitor) {
		addMedias(result.getIdenticalList());
	}

	private Image generatePreview(Image image) {
		Image scaled = new Image(Display.getDefault(), PREVIEW_SIZE,
				PREVIEW_SIZE);
		GC gc = new GC(scaled, Gdip.SmoothingModeNone);
		// gc.setAntialias(SWT.ON);//smooth
		// gc.setInterpolation(SWT.LOW);

		int size = Math.min(image.getBounds().width, image.getBounds().height);
		int x = image.getBounds().width > image.getBounds().height ? (image
				.getBounds().width - image.getBounds().height) / 2 : 0;
		int y = image.getBounds().height > image.getBounds().width ? (image
				.getBounds().height - image.getBounds().width) / 2 : 0;
		gc.drawImage(image, x, y, size, size, 0, 0, PREVIEW_SIZE, PREVIEW_SIZE);
		gc.dispose();
		return scaled;
	}

	class ImageLoadThread implements Runnable {

		@Override
		public void run() {
			Image src;
			Image preview;
			Display display = Display.getDefault();
			while (!virtualItemStack.isEmpty()) {
				int i = virtualItemStack.pop();
				if (generated[i] == 0) {
					System.out.println(paths[i]);
					generated[i] = 1;
					src = new Image(display, paths[i]);
					preview = generatePreview(src);
					src.dispose();
					display.asyncExec(new ImageSetterThread(i, preview));
				}
			}
		}
	}

	class ImageSetterThread implements Runnable {

		private Image preview;
		private int index;

		public ImageSetterThread(int i, Image preview) {
			this.index = i;
			this.preview = preview;
		}

		@Override
		public void run() {
			defaultGroup.getItem(exists + index).setImage(preview);
		}
	}
}
