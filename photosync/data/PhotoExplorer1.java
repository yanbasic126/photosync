package com.wedding0326.photosync.ui.explorer;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.operation.ModalContext;
import org.eclipse.nebula.widgets.gallery.DefaultGalleryItemRenderer;
import org.eclipse.nebula.widgets.gallery.Gallery;
import org.eclipse.nebula.widgets.gallery.GalleryItem;
import org.eclipse.nebula.widgets.gallery.NoGroupRenderer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.ImageTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.internal.gdip.Gdip;
import org.eclipse.swt.internal.image.JPEGFileFormat;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.wedding0326.photosync.compare.CompareResult;
import com.wedding0326.photosync.util.TestUtils;

public class PhotoExplorer {

	private Gallery gallery;

	private Composite parent;

	private GalleryItem defaultGroup;

	private Image defaultPreviewImage;

	private CompareResult result;

	private List<Image> previewImages;

	private int threadCapacity = 500000;

	private List<Integer> indexs;

	public PhotoExplorer() {
		// TODO Auto-generated constructor stub

		Image src = new Image(null, "E:\\0326background\\boy.jpg");
		defaultPreviewImage = generatePreview(src);
		previewImages = new ArrayList<>();
		indexs = new ArrayList<>();
	}

	public void createExplorer(Composite parent) {
		this.parent = parent;
		gallery = new Gallery(parent, SWT.V_SCROLL | SWT.MULTI | SWT.VIRTUAL);

		gallery.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		gallery.setLayout(new GridLayout(1, false));

		NoGroupRenderer gr = new NoGroupRenderer();
		// gr.setMinMargin(2);
		gr.setItemSize(102, 102);
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

		defaultGroup = new GalleryItem(gallery, SWT.NONE);
		defaultGroup.setText("DEFAULT");
		galleryDragListener(gallery);
	}

	private void galleryDragListener(Gallery gallery) {
		Transfer[] types = new Transfer[] { ImageTransfer.getInstance() };
		DropTarget target = new DropTarget(gallery, DND.DROP_MOVE
				| DND.DROP_COPY | DND.DROP_DEFAULT);
		target.setTransfer(types);

		target.addDropListener(new DropTargetAdapter() {

			public void dragEnter(DropTargetEvent event) {
				if (event.detail == DND.DROP_DEFAULT) {
					event.detail = (event.operations & DND.DROP_COPY) != 0 ? DND.DROP_COPY
							: DND.DROP_NONE;
					System.out.println("------>end");
				}
			}

			@Override
			public void drop(DropTargetEvent event) {
				System.out.println("------>drop");
			}

			@Override
			public void dropAccept(DropTargetEvent event) {
				System.out.println("------>drop");
			}
		});

		DragSource source = new DragSource(gallery, DND.DragEnter
				| DND.DragOver | DND.DragLeave);
		source.setTransfer(types);
		source.addDragListener(new DragSourceAdapter() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.swt.dnd.DragSourceAdapter#dragStart(org.eclipse.swt
			 * .dnd.DragSourceEvent)
			 */
			@Override
			public void dragStart(DragSourceEvent event) {
				super.dragStart(event);
				System.out.println("------>start");
			}
		});

		gallery.addListener(SWT.SetData, new Listener() {

			public void handleEvent(Event event) {
				GalleryItem item = (GalleryItem) event.item;
				int index;
				if (item.getParentItem() != null) {
					index = item.getParentItem().indexOf(item);
					// item.getImage().dispose();
					// addImageOri(item,
					// "D:\\yyi.talendbj.esb\\picture\\6simport\\102APPLE\\IMG_5005.PNG");
					// item.setItemCount(0);
				} else {
					index = gallery.indexOf(item);
					// item.setItemCount(100);
					// addImageOri(item,
					// "D:\\yyi.talendbj.esb\\picture\\6simport\\102APPLE\\IMG_5005.PNG");
				}

				System.out.println("setData index " + index); //$NON-NLS-1$

				if (index < previewImages.size()
						&& previewImages.get(index) != null) {
					defaultGroup.getItem(index).setImage(
							previewImages.get(index));
				} else {
					// defaultGroup.getItem(index).setImage(defaultPreviewImage);
				}
				// Display.getDefault().asyncExec(new Runnable() {
				//
				// public void run() {
				// readMoreImagesOnce(index);
				// readMoreImagesOnce(index + 1);
				// readMoreImagesOnce(index + 2);
				// readMoreImagesOnce(index + 3);
				// readMoreImagesOnce(index + 4);
				// readMoreImagesOnce(index + 5);
				// readMoreImagesOnce(index + 6);
				// indexs.clear();
				// }
				// });
				// Your image here
			}

		});
	}

	private void readMoreImagesOnce(int i) {
		addImageOri(defaultGroup.getItem(i), result.getIdenticalList().get(i)
				.getFullPath());
	}

	public void setCompareResult(CompareResult result, IProgressMonitor monitor) {
		this.result = result;
		defaultGroup.setItemCount(result.getIdenticalList().size());
		if (result != null)
			// return;
			try {
				ModalContext.run(new IRunnableWithProgress() {

					@Override
					public void run(IProgressMonitor monitor)
							throws InvocationTargetException,
							InterruptedException {
						monitor.beginTask("Reading image background:", result
								.getIdenticalList().size());
						String[] tmp = new String[threadCapacity];

						for (int i = 0; i < result.getIdenticalList().size(); i = i
								+ threadCapacity) {

							for (int j = 0; j < threadCapacity; j++) {
								if (i + j < result.getIdenticalList().size()) {
									tmp[j] = result.getIdenticalList()
											.get(i + j).getFullPath();
								}
							}
							ImageLoadThread r = new ImageLoadThread();
							r.setPosition(i);
							r.setPaths(tmp);
							Thread t = new Thread(r, "Thread-"
									+ (i / threadCapacity));
							t.start();
							monitor.worked(threadCapacity);

						}
						monitor.done();
					}
				}, true, monitor, parent.getDisplay());
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	private void addImageBatch(GalleryItem group, String[] imagePaths) {
		for (String p : imagePaths) {
			// addImage(group, p);
			GalleryItem item = new GalleryItem(group, SWT.NONE);
			item.setImage(defaultPreviewImage);
		}
	}

	private void addImageOri(GalleryItem item, String imagePath) {
		if (item.getImage() == null) {

			try {
				Image src = new Image(null, imagePath);
				Image preview = generatePreview(src);
				// Image photo = resize(new Image(null, loader.data[0]), 56 * 2,
				// 72 * 2);
				if (preview != null) {
					// item = new GalleryItem(defaultGroup, SWT.NONE);
					item.setImage(preview);
					item.addListener(SWT.DragDetect, new Listener() {

						@Override
						public void handleEvent(Event event) {
							System.out.println("------>" + 11);
						}
					});
				}
				src.dispose();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void addImage(GalleryItem group, String imagePath) {
		GalleryItem item = new GalleryItem(group, SWT.NONE);
		item.setImage(defaultPreviewImage);
		item.setData("path", imagePath);
	}

	private Image resize(Image image, int width, int height) {
		Image scaled = new Image(Display.getDefault(), width, height);
		GC gc = new GC(scaled);
		gc.setAntialias(SWT.ON);
		gc.setInterpolation(SWT.HIGH);
		gc.drawImage(image, 0, 0, image.getBounds().width,
				image.getBounds().height, 0, 0, width, height);
		gc.dispose();
		image.dispose(); // don't forget about me!
		return scaled;
	}

	private Image generatePreview(Image image) {
		Image scaled = new Image(Display.getDefault(), 102, 102);
		GC gc = new GC(scaled, Gdip.SmoothingModeNone);
		// gc.setAntialias(SWT.ON);//smooth
		// gc.setInterpolation(SWT.LOW);

		int size = Math.min(image.getBounds().width, image.getBounds().height);
		int x = image.getBounds().width > image.getBounds().height ? (image
				.getBounds().width - image.getBounds().height) / 2 : 0;
		int y = image.getBounds().height > image.getBounds().width ? (image
				.getBounds().height - image.getBounds().width) / 2 : 0;
		gc.drawImage(image, x, y, size, size, 0, 0, 102, 102);
		gc.dispose();
		// scaled.dispose(); // don't forget about me!
		return scaled;
	}

	class ImageLoadThread implements Runnable {

		private String[] paths;

		private Image[] images;

		private int position;

		public ImageLoadThread() {
			images = new Image[threadCapacity];
			paths = new String[threadCapacity];
		}

		public void setPaths(String[] paths) {
			this.paths = paths;
		}

		public void setPosition(int position) {
			this.position = position;
		}

		public Image[] getImages() {
			return this.images;
		}

		@Override
		public void run() {
			Image src;
			Image preview;
			Display display = Display.getDefault();
			for (int i = 0; i < threadCapacity; i++) {
				try {
					// synchronized (this) {

					TestUtils.printTime("0");
					if (paths[i] != null) {
						src = new Image(display, paths[i]);
						TestUtils.printTime("1");
						preview = generatePreview(src);
						TestUtils.printTime("2");
						if (preview != null) {
							previewImages.add(preview);
							TestUtils.printTime("------>"
									+ previewImages.size());
						}
						src.dispose();
						// src.dispose();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}
}
