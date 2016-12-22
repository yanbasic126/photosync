package com.wedding0326.photosync.ui.provider;

import org.eclipse.jface.viewers.LabelProvider;

import com.wedding0326.photosync.core.DuplicatesModel;
import com.wedding0326.photosync.core.MediaModel;

/**
 * DOC yyi class global comment. Detailled comment <br/>
 *
 * $Id$
 *
 */
public class ResultViewLabelProvider extends LabelProvider {

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
     */
    @Override
    public String getText(Object element) {
        if (element instanceof MediaModel) {
            MediaModel m = (MediaModel) element;
            return m.getFullPath();
        } else if (element instanceof DuplicatesModel) {
            DuplicatesModel m = (DuplicatesModel) element;
            return m.getDuplicates().get(0).getName() + " (" + m.getDuplicates().size() + ")";
        }
        return super.getText(element);
    }

}
