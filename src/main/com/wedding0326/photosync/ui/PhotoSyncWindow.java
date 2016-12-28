package com.wedding0326.photosync.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.nebula.widgets.gallery.DefaultGalleryGroupRenderer;
import org.eclipse.nebula.widgets.gallery.Gallery;
import org.eclipse.nebula.widgets.gallery.GalleryItem;
import org.eclipse.nebula.widgets.gallery.ListItemRenderer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Tree;

import com.wedding0326.photosync.compare.CompareResult;
import com.wedding0326.photosync.compare.MediaComparer;
import com.wedding0326.photosync.core.MediaModel;
import com.wedding0326.photosync.ui.fileBrowser.FileBrowser;
import com.wedding0326.photosync.ui.photoExplorer.PhotoExplorer;
import com.wedding0326.photosync.ui.provider.ResultViewContentProvider;
import com.wedding0326.photosync.ui.provider.ResultViewLabelProvider;
import com.wedding0326.photosync.util.MediaBrowser;
import com.wedding0326.photosync.util.TestUtils;

/**
 * DOC yyi class global comment. Detailled comment <br/>
 *
 * $Id$
 *
 */
public class PhotoSyncWindow extends ApplicationWindow {

    private Set<String> pathList;

    private TreeViewer treeViewer;

    private ListViewer listViewer;

    private Canvas previewCanvas;

    private Image previewImage;

    private PhotoExplorer photoExplorer;

    private Action analysisAction;

    private Action gcAction;

    private Action actionSleak;

    private Action openAction;

    private Action clearAction;

    private FileBrowser fileBrowser;

    public PhotoSyncWindow() {
        super(null);
        createActions();
        addToolBar(SWT.FLAT | SWT.WRAP);
        addMenuBar();
        addStatusLine();

        pathList = new HashSet<>();
        pathList.add("E:\\kuaipan\\yanyi.dell\\Pictures");
    }

    /**
     * Create contents of the application window.
     * 
     * @param parent
     */
    @Override
    protected Control createContents(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(1, false));

        SashForm sashForm = new SashForm(container, SWT.SMOOTH);
        sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        SashForm pathSash = new SashForm(sashForm, SWT.SMOOTH | SWT.VERTICAL);

        Group grpFolder = new Group(pathSash, SWT.NONE);
        grpFolder.setText("Folder");
        grpFolder.setLayout(new GridLayout(1, false));

        // Composite btnComp = new Composite(grpFolder, SWT.NONE);
        // btnComp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        // btnComp.setSize(94, 35);
        // btnComp.setLayout(new GridLayout(2, false));

        // Button btnAddFolder = new Button(btnComp, SWT.NONE);
        // btnAddFolder.addSelectionListener(new SelectionAdapter() {
        //
        // @Override
        // public void widgetSelected(SelectionEvent e) {
        // if (pathList == null) {
        // pathList = new HashSet<>();
        // }
        //
        // DirectoryDialog folderdlg = new DirectoryDialog(Display.getDefault().getActiveShell());
        // String selecteddir = folderdlg.open();
        // if (selecteddir == null) {
        // return;
        // } else {
        // pathList.add(selecteddir);
        // listViewer.setInput(pathList);
        // listViewer.refresh();
        // }
        // }
        // });
        // btnAddFolder.setText("Add");
        //
        // Button btnDelfolder = new Button(btnComp, SWT.NONE);
        // btnDelfolder.setText("Delete");

        fileBrowser = new FileBrowser();
        fileBrowser.createFolderView(grpFolder);

        // listViewer = new ListViewer(grpFolder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        // org.eclipse.swt.widgets.List list = listViewer.getList();
        // list.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        // list.setSize(21, 85);

        // Group grpPreview = new Group(pathSash, SWT.NONE);
        // grpPreview.setLayout(new GridLayout(1, false));
        // grpPreview.setText("Preview");
        //
        // Composite previewComp = new Composite(grpPreview, SWT.BORDER);
        // previewComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        // GridLayout gl_previewComp = new GridLayout(1, false);
        // gl_previewComp.verticalSpacing = 0;
        // gl_previewComp.marginWidth = 0;
        // gl_previewComp.horizontalSpacing = 0;
        // gl_previewComp.marginHeight = 0;
        // previewComp.setLayout(gl_previewComp);
        //
        // previewCanvas = new Canvas(previewComp, SWT.NONE);
        // previewCanvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        // GridLayout gl_previewCanvas = new GridLayout(1, false);
        // gl_previewCanvas.marginHeight = 0;
        // gl_previewCanvas.verticalSpacing = 0;
        // gl_previewCanvas.marginWidth = 0;
        // gl_previewCanvas.horizontalSpacing = 0;
        // previewCanvas.setLayout(gl_previewCanvas);
        //
        // previewCanvas.addPaintListener(new PaintListener() {
        //
        // @Override
        // public void paintControl(PaintEvent e) {
        // redraw(e);
        // }
        // });

        // listViewer.setContentProvider(new ArrayContentProvider());
        // listViewer.setLabelProvider(new ResultViewLabelProvider());
        // listViewer.setInput(pathList);

        Group grpResult = new Group(sashForm, SWT.NONE);
        grpResult.setText("Result");
        grpResult.setLayout(new GridLayout(1, false));

        TabFolder tabFolder = new TabFolder(grpResult, SWT.NONE);
        tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        // TabItem tbtmSummary = new TabItem(tabFolder, SWT.NONE);
        // tbtmSummary.setText("Summary");
        //
        // treeViewer = new TreeViewer(tabFolder, SWT.BORDER | SWT.MULTI);
        // treeViewer.setContentProvider(new ResultViewContentProvider());
        // treeViewer.setLabelProvider(new ResultViewLabelProvider());
        //
        // Tree tree = treeViewer.getTree();
        // tbtmSummary.setControl(tree);
        // tree.setSize(85, 85);
        // tree.setLinesVisible(true);
        // tree.addSelectionListener(new SelectionAdapter() {
        //
        // @Override
        // public void widgetSelected(SelectionEvent e) {
        // // String p = "";
        // // if (e.item.getData() instanceof MediaModel) {
        // // MediaModel m = (MediaModel) e.item.getData();
        // // p = m.getFullPath();
        // // } else if (e.item.getData() instanceof DuplicatesModel) {
        // // DuplicatesModel m = (DuplicatesModel) e.item.getData();
        // // p = m.getDuplicates().get(0).getFullPath();
        // // }
        // // if (!p.isEmpty()) {
        // // drawPreview(p);
        // // }
        // }
        // });

        TabItem tbtmExplore = new TabItem(tabFolder, SWT.NONE);
        tbtmExplore.setText("Explore");

        Composite explorerComp = new Composite(tabFolder, SWT.BORDER);
        tbtmExplore.setControl(explorerComp);
        explorerComp.setLayout(new GridLayout(1, false));
        explorerComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        photoExplorer = new PhotoExplorer();
        photoExplorer.createExplorer(explorerComp);
        TabItem tbtmTest = new TabItem(tabFolder, SWT.NONE);
        tbtmTest.setText("test");

        Gallery gallery = new Gallery(tabFolder, SWT.VIRTUAL | SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
        gallery.setVirtualGroupsCompatibilityMode(true);
        gallery.setVirtualGroups(true);
        gallery.setLowQualityOnUserAction(true);
        tbtmTest.setControl(gallery);
        gallery.setGroupRenderer(new DefaultGalleryGroupRenderer());
        gallery.setItemRenderer(new ListItemRenderer());

        GalleryItem galleryItem = new GalleryItem(gallery, SWT.NONE);
        galleryItem.setText("New Item");

        GalleryItem galleryItem_2 = new GalleryItem(galleryItem, SWT.NONE);
        galleryItem_2.setText("New Item");

        GalleryItem galleryItem_1 = new GalleryItem(gallery, SWT.NONE);
        galleryItem_1.setText("New Item");

        GalleryItem galleryItem_3 = new GalleryItem(galleryItem_1, SWT.NONE);
        // galleryItem_3.setBackground(SWTResourceManager
        // .getColor(SWT.COLOR_WIDGET_BACKGROUND));
        galleryItem_3.setText("New Item");

        GalleryItem galleryItem_4 = new GalleryItem(galleryItem_1, SWT.NONE);
        galleryItem_4.setText("New Item");
        galleryItem_1.setExpanded(true);

        sashForm.setWeights(new int[] { 1, 3 });

        return container;
    }

    private void drawPreview(String path) {
        if (null != path) {
            ImageLoader loader = new ImageLoader();
            loader.load(path);
            previewImage = new Image(null, loader.data[0]);
            previewCanvas.redraw();
        }
    }

    private void redraw(PaintEvent e) {
        if (previewImage != null) {

            Rectangle previewBounds = previewCanvas.getBounds();

            double a = (double) previewBounds.height / (double) previewImage.getBounds().height;
            double b = (double) previewBounds.width / (double) previewImage.getBounds().width;
            double ratio = Math.min(a, b);

            int scaledWidth = (int) (previewImage.getBounds().width * ratio);
            int scaledHeight = (int) (previewImage.getBounds().height * ratio);

            int x = 0;
            if (scaledWidth < previewBounds.width) {
                x = (previewBounds.width - scaledWidth) / 2;
            }
            e.gc.drawImage(previewImage, 0, 0, previewImage.getBounds().width, previewImage.getBounds().height, x, 0,
                    scaledWidth, scaledHeight);
        }
    }

    /**
     * Create the actions.
     */
    private void createActions() {
        // Create the actions
        {
            analysisAction = new Action("Analysis") {

                @Override
                public void run() {
                    analysisPhoto();
                }
            };
        }
        {
            gcAction = new Action("GC") {
            };
        }
        {
            actionSleak = new Action("Sleak") {

                @Override
                public void run() {
                    TestUtils.openSleak();
                }
            };
        }
        {
            openAction = new Action("Open") {

                public void run() {
                    try {

                        addNewPath();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            };
        }
        {
            clearAction = new Action("Clear") {

                public void run() {
                    photoExplorer.emptyDefaultGroup();
                }
            };
        }
    }

    /**
     * Create the menu manager.
     * 
     * @return the menu manager
     */
    @Override
    protected MenuManager createMenuManager() {
        MenuManager menuManager = new MenuManager("menu");
        return menuManager;
    }

    /**
     * Create the toolbar manager.
     * 
     * @return the toolbar manager
     */
    @Override
    protected ToolBarManager createToolBarManager(int style) {
        ToolBarManager toolBarManager = new ToolBarManager(SWT.NONE);
        toolBarManager.add(analysisAction);
        toolBarManager.add(gcAction);
        toolBarManager.add(actionSleak);
        toolBarManager.add(openAction);
        toolBarManager.add(clearAction);
        return toolBarManager;
    }

    // @Override
    // protected StatusLineManager createStatusLineManager() {
    // statusLineManager = new StatusLineManager();
    // statusLineManager.setCancelEnabled(true);
    // statusLineManager.createControl(previewCanvas, CANCEL)
    // return statusLineManager;
    // }

    /**
     * Configure the shell.
     * 
     * @param newShell
     */
    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("New Analysis");
    }

    /**
     * Return the initial size of the window.
     */
    @Override
    protected Point getInitialSize() {
        return new Point(694, 450);
    }

    protected void addNewPath() throws IOException {

        System.out.println(fileBrowser.getCurrentDirectory());
        if (pathList == null) {
            pathList = new HashSet<>();
        }

        // DirectoryDialog folderdlg = new DirectoryDialog(Display.getDefault().getActiveShell());
        // String selecteddir = folderdlg.open();
        // if (selecteddir == null) {
        // return;
        // } else {
        // pathList.add(selecteddir);
        // listViewer.setInput(pathList);
        // listViewer.refresh();

        List<MediaModel> mediaList = new ArrayList<>();
        MediaBrowser.readMedias(fileBrowser.getCurrentDirectory().getAbsolutePath(), mediaList);
        photoExplorer.addMedias(mediaList);
        // }

    }

    protected void analysisPhoto() {
        // monitor.setTaskName("hahaha");
        // statusLineManager.setMessage("xixixi");
        getStatusLineManager().setCancelEnabled(true);
        getStatusLineManager().setMessage("aaaa");
        MediaComparer comp = new MediaComparer();
        pathList.forEach(p -> comp.addPath(p));
        CompareResult result = comp.doCompare();
        // treeViewer.setInput(result);
        photoExplorer.setCompareResult(result, getStatusLineManager().getProgressMonitor());
        // try {
        // run(true,true,new IRunnableWithProgress() {
        //
        // public void run(IProgressMonitor monitor) throws
        // InvocationTargetException {
        // monitor.setTaskName("begining....");
        // MediaComparer comp = new MediaComparer();
        // monitor.setTaskName("add paths...");
        // pathList.forEach(p -> comp.addPath(p));
        // monitor.setTaskName("analysising...");
        // CompareResult result = comp.doCompare();
        //
        // Display.getDefault().asyncExec(new Runnable() {
        //
        // @Override
        // public void run() {
        // // treeViewer.setInput(result);
        // photoExplorer.setCompareResult(result);
        // }
        // });
        // }
        // });
        // } catch (InvocationTargetException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // } catch (InterruptedException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }

        // ProgressMonitorDialog progress = new ProgressMonitorDialog(null);
        // try {
        // progress.run(true, true, new IRunnableWithProgress() {
        //
        // public void run(IProgressMonitor monitor) throws
        // InvocationTargetException {
        // monitor.setTaskName("begining....");
        // MediaComparer comp = new MediaComparer();
        // monitor.setTaskName("add paths...");
        // pathList.forEach(p -> comp.addPath(p));
        // monitor.setTaskName("analysising...");
        // CompareResult result = comp.doCompare();
        //
        // Display.getDefault().asyncExec(new Runnable() {
        //
        // @Override
        // public void run() {
        // // treeViewer.setInput(result);
        // photoExplorer.setCompareResult(result);
        // }
        // });
        // }
        // });
        // } catch (Exception e1) {
        // e1.printStackTrace();
        // }
    }
}
