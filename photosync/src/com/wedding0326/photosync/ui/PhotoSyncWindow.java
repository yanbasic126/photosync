package com.wedding0326.photosync.ui;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;

import com.wedding0326.photosync.compare.CompareResult;
import com.wedding0326.photosync.compare.MediaComparer;
import com.wedding0326.photosync.core.DuplicatesModel;
import com.wedding0326.photosync.core.MediaModel;
import com.wedding0326.photosync.ui.provider.ResultViewContentProvider;
import com.wedding0326.photosync.ui.provider.ResultViewLabelProvider;

/**
 * DOC yyi class global comment. Detailled comment <br/>
 *
 * $Id$
 *
 */
public class PhotoSyncWindow extends ApplicationWindow {

    private Composite previewComp;

    private Set<String> pathList;

    private TreeViewer treeViewer;

    private ListViewer listViewer;

    /**
     * Create the application window.
     */
    public PhotoSyncWindow() {
        super(null);
        createActions();
        addToolBar(SWT.FLAT | SWT.WRAP);
        addMenuBar();
        addStatusLine();
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

        previewComp = new Composite(grpPreview, SWT.BORDER);
        previewComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        previewComp.setLayout(new GridLayout(1, false));
        listViewer.setContentProvider(new ArrayContentProvider());
        listViewer.setLabelProvider(new ResultViewLabelProvider());
        listViewer.setInput(pathList);
        pathSash.setWeights(new int[] { 1, 1 });

        Group grpResult = new Group(sashForm, SWT.NONE);
        grpResult.setText("Result");
        grpResult.setLayout(new GridLayout(1, false));

        treeViewer = new TreeViewer(grpResult, SWT.BORDER | SWT.MULTI);
        treeViewer.setContentProvider(new ResultViewContentProvider());
        treeViewer.setLabelProvider(new ResultViewLabelProvider());

        Tree tree = treeViewer.getTree();
        tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
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

                    ImageLoader loader = new ImageLoader();
                    loader.load(p);
                    Image src = new Image(null, loader.data[0]);
                    GC gc = new GC(previewComp);
                    gc.setAdvanced(true);

                    Rectangle previewBounds = previewComp.getBounds();
                    Rectangle imageBounds = src.getBounds();

                    double a = (double) (previewBounds.height / imageBounds.height);
                    double b = (double) (previewBounds.width / imageBounds.width);

                    double ratio = Math.min(a, b);

                    // double ratio = (double) src.getBounds().height / (double) src.getBounds().width;

                    gc.drawImage(src, 0, 0, imageBounds.width, imageBounds.height, 0, 0, (int) (imageBounds.width * ratio),
                            (int) (imageBounds.height * ratio));
                }
            }
        });
        sashForm.setWeights(new int[] { 1, 2 });

        return container;
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
}
