package com.wedding0326.photosync.ui.provider;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.wedding0326.photosync.compare.CompareResult;
import com.wedding0326.photosync.core.DuplicatesModel;

/**
 * DOC yyi class global comment. Detailled comment <br/>
 *
 * $Id$
 *
 */
public class ResultViewContentProvider implements ITreeContentProvider {

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    @Override
    public void dispose() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface .viewers.Viewer, java.lang.Object,
     * java.lang.Object)
     */
    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getElements(java.lang. Object)
     */
    @Override
    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof CompareResult) {
            CompareResult result = (CompareResult) inputElement;
            return result.getResult().values().toArray();

        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang. Object)
     */
    @Override
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof DuplicatesModel) {
            DuplicatesModel m = (DuplicatesModel) parentElement;
            return m.getDuplicates().toArray();
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang. Object)
     */
    @Override
    public Object getParent(Object element) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang. Object)
     */
    @Override
    public boolean hasChildren(Object element) {
        if (element instanceof DuplicatesModel) {
            DuplicatesModel m = (DuplicatesModel) element;
            return m.getDuplicates().size() > 1;
        }
        return false;
    }
}
