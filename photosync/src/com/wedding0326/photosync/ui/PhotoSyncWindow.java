package com.wedding0326.photosync.ui;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Tree;

import com.wedding0326.photosync.compare.CompareResult;
import com.wedding0326.photosync.compare.MediaComparer;
import com.wedding0326.photosync.core.DuplicatesModel;
import com.wedding0326.photosync.core.MediaModel;
import com.wedding0326.photosync.ui.provider.ResultViewContentProvider;
import com.wedding0326.photosync.ui.provider.ResultViewLabelProvider;

import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.custom.ScrolledComposite;

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

    private Composite explorerComp;

    /**
     * Create the application window.
     */
    public PhotoSyncWindow() {
        super(null);
        createActions();
        addToolBar(SWT.FLAT | SWT.WRAP);
        addMenuBar();
        addStatusLine();

        pathList = new HashSet<>();
        pathList.add("D:\\yyi.talendbj.esb\\dev\\comparetest");
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

        Composite buttonComp = new Composite(container, SWT.NONE);
        buttonComp.setSize(80, 65);
        buttonComp.setLayout(new GridLayout(1, false));

        Button btnAnalysis = new Button(buttonComp, SWT.NONE);
        btnAnalysis.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                analysisPhoto();
            }
        });
        btnAnalysis.setBounds(0, 0, 75, 25);
        btnAnalysis.setText("Analysis");

        SashForm sashForm = new SashForm(container, SWT.SMOOTH);
        sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        SashForm pathSash = new SashForm(sashForm, SWT.SMOOTH | SWT.VERTICAL);

        Group grpFolder = new Group(pathSash, SWT.NONE);
        grpFolder.setText("Folder");
        grpFolder.setLayout(new GridLayout(1, false));

        Composite btnComp = new Composite(grpFolder, SWT.NONE);
        btnComp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        btnComp.setSize(94, 35);
        btnComp.setLayout(new GridLayout(2, false));

        Button btnAddFolder = new Button(btnComp, SWT.NONE);
        btnAddFolder.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (pathList == null) {
                    pathList = new HashSet<>();
                }

                DirectoryDialog folderdlg = new DirectoryDialog(Display.getDefault().getActiveShell());
                String selecteddir = folderdlg.open();
                if (selecteddir == null) {
                    return;
                } else {
                    pathList.add(selecteddir);
                    listViewer.setInput(pathList);
                    listViewer.refresh();
                }
            }
        });
        btnAddFolder.setText("Add");

        Button btnDelfolder = new Button(btnComp, SWT.NONE);
        btnDelfolder.setText("Delete");

        listViewer = new ListViewer(grpFolder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        org.eclipse.swt.widgets.List list = listViewer.getList();
        list.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        list.setSize(21, 85);

        Group grpPreview = new Group(pathSash, SWT.NONE);
        grpPreview.setLayout(new GridLayout(1, false));
        grpPreview.setText("Preview");

        Composite previewComp = new Composite(grpPreview, SWT.BORDER);
        previewComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        GridLayout gl_previewComp = new GridLayout(1, false);
        gl_previewComp.verticalSpacing = 0;
        gl_previewComp.marginWidth = 0;
        gl_previewComp.horizontalSpacing = 0;
        gl_previewComp.marginHeight = 0;
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

        listViewer.setContentProvider(new ArrayContentProvider());
        listViewer.setLabelProvider(new ResultViewLabelProvider());
        listViewer.setInput(pathList);

        Group grpResult = new Group(sashForm, SWT.NONE);
        grpResult.setText("Result");
        grpResult.setLayout(new GridLayout(1, false));

        TabFolder tabFolder = new TabFolder(grpResult, SWT.NONE);
        tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        TabItem tbtmSummary = new TabItem(tabFolder, SWT.NONE);
        tbtmSummary.setText("Summary");

        treeViewer = new TreeViewer(tabFolder, SWT.BORDER | SWT.MULTI);
        treeViewer.setContentProvider(new ResultViewContentProvider());
        treeViewer.setLabelProvider(new ResultViewLabelProvider());

        Tree tree = treeViewer.getTree();
        tbtmSummary.setControl(tree);
        tree.setSize(85, 85);
        tree.setLinesVisible(true);
        tree.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                String p = "";
                if (e.item.getData() instanceof MediaModel) {
                    MediaModel m = (MediaModel) e.item.getData();
                    p = m.getFullPath();
                } else if (e.item.getData() instanceof DuplicatesModel) {
                    DuplicatesModel m = (DuplicatesModel) e.item.getData();
                    p = m.getDuplicates().get(0).getFullPath();
                }
                if (!p.isEmpty()) {
                    drawPreview(p);
                }
            }
        });

        TabItem tbtmExplore = new TabItem(tabFolder, SWT.NONE);
        tbtmExplore.setText("Explore");

        ScrolledComposite scrolledComp = new ScrolledComposite(tabFolder, SWT.H_SCROLL | SWT.V_SCROLL);
        tbtmExplore.setControl(scrolledComp);
        scrolledComp.setExpandHorizontal(true);
        scrolledComp.setExpandVertical(true);

        explorerComp = new Composite(scrolledComp, SWT.BORDER);
        scrolledComp.setContent(explorerComp);
        explorerComp.addListener(SWT.Resize, new Listener() {

            public void handleEvent(Event e) {
                Point point = new Point(scrolledComp.getBounds().width, scrolledComp.getBounds().height);
                System.out.println("------>" + explorerComp.getBounds().width);
                System.out.println("------>" + explorerComp.getBounds().height);
                System.out.println("------>");
                // explorerComp.setRedraw(true);
                // explorerComp.layout(true);
                // scrolledComp.setMinSize(point);
                explorerComp.setBounds(0,0,scrolledComp.getParent().getBounds().width, 1000);
                // scrolledComp.setMinSize(explorerComp.computeSize(SWT.DEFAULT_WIDTH, SWT.DEFAULT));

            }
        });
        RowLayout rl_explorerComp = new RowLayout(SWT.HORIZONTAL);
        rl_explorerComp.fill = true;
        explorerComp.setLayout(rl_explorerComp);

        Composite composite = new Composite(explorerComp, SWT.NONE);
        composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));

        Composite composite_1 = new Composite(explorerComp, SWT.NONE);
        composite_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));

        Composite composite_2 = new Composite(explorerComp, SWT.NONE);
        composite_2.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));

        Composite composite_3 = new Composite(explorerComp, SWT.NONE);
        composite_3.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));

        Composite composite_4 = new Composite(explorerComp, SWT.NONE);
        composite_4.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));

        Composite composite_5 = new Composite(explorerComp, SWT.NONE);
        composite_5.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));

        Composite composite_6 = new Composite(explorerComp, SWT.NONE);
        composite_6.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));

        Composite composite_7 = new Composite(explorerComp, SWT.NONE);
        composite_7.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));

        Composite composite_8 = new Composite(explorerComp, SWT.NONE);
        composite_8.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));

        Composite composite_9 = new Composite(explorerComp, SWT.NONE);
        composite_9.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));

        Composite composite_10 = new Composite(explorerComp, SWT.NONE);
        composite_10.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));

        Composite composite_11 = new Composite(explorerComp, SWT.NONE);
        composite_11.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));

        Composite composite_12 = new Composite(explorerComp, SWT.NONE);
        composite_12.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));

        Composite composite_13 = new Composite(explorerComp, SWT.NONE);
        composite_13.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));

        Composite composite_14 = new Composite(explorerComp, SWT.NONE);
        composite_14.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));

        Composite composite_15 = new Composite(explorerComp, SWT.NONE);
        composite_15.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));

        Composite composite_16 = new Composite(explorerComp, SWT.NONE);
        composite_16.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));

        Composite composite_17 = new Composite(explorerComp, SWT.NONE);
        composite_17.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));

        Composite composite_18 = new Composite(explorerComp, SWT.NONE);
        composite_18.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));

        Composite composite_19 = new Composite(explorerComp, SWT.NONE);
        composite_19.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));

        Composite composite_20 = new Composite(explorerComp, SWT.NONE);
        composite_20.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));

        Composite composite_21 = new Composite(explorerComp, SWT.NONE);
        composite_21.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));

        Composite composite_22 = new Composite(explorerComp, SWT.NONE);
        composite_22.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));

        Composite composite_23 = new Composite(explorerComp, SWT.NONE);
        composite_23.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));

        Composite composite_24 = new Composite(explorerComp, SWT.NONE);
        composite_24.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));

        Composite composite_25 = new Composite(explorerComp, SWT.NONE);
        composite_25.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));

        Composite composite_26 = new Composite(explorerComp, SWT.NONE);
        composite_26.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));

        Composite composite_27 = new Composite(explorerComp, SWT.NONE);
        composite_27.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));

        Composite composite_28 = new Composite(explorerComp, SWT.NONE);
        composite_28.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));

        Composite composite_29 = new Composite(explorerComp, SWT.NONE);
        composite_29.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));

        Composite composite_30 = new Composite(explorerComp, SWT.NONE);
        composite_30.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));

        Composite composite_31 = new Composite(explorerComp, SWT.NONE);
        composite_31.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));

        Composite composite_32 = new Composite(explorerComp, SWT.NONE);
        composite_32.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));

        Composite composite_33 = new Composite(explorerComp, SWT.NONE);
        composite_33.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));

        Composite composite_34 = new Composite(explorerComp, SWT.NONE);
        composite_34.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));

        Composite composite_35 = new Composite(explorerComp, SWT.NONE);
        composite_35.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));

        Composite composite_36 = new Composite(explorerComp, SWT.NONE);
        composite_36.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));

        Composite composite_37 = new Composite(explorerComp, SWT.NONE);
        composite_37.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));

        Composite composite_38 = new Composite(explorerComp, SWT.NONE);
        composite_38.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));

        Composite composite_39 = new Composite(explorerComp, SWT.NONE);
        composite_39.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));

        sashForm.setWeights(new int[] { 1, 2 });

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
        ToolBarManager toolBarManager = new ToolBarManager(style);
        return toolBarManager;
    }

    /**
     * Create the status line manager.
     * 
     * @return the status line manager
     */
    @Override
    protected StatusLineManager createStatusLineManager() {
        StatusLineManager statusLineManager = new StatusLineManager();
        return statusLineManager;
    }

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

    protected void analysisPhoto() {
        ProgressMonitorDialog progress = new ProgressMonitorDialog(null);
        try {
            progress.run(true, true, new IRunnableWithProgress() {

                public void run(IProgressMonitor monitor) throws InvocationTargetException {
                    MediaComparer comp = new MediaComparer();
                    pathList.forEach(p -> comp.addPath(p));
                    CompareResult result = comp.doCompare();
                    Display.getDefault().asyncExec(new Runnable() {

                        @Override
                        public void run() {
                            treeViewer.setInput(result);
                        }
                    });
                }
            });
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
}
