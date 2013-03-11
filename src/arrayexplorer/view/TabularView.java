package arrayexplorer.view;

import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * This is an abstract view for Value-History Views.
 * It implements common functionalities like a close button 
 * @author Bilal
 *
 */
public class TabularView extends Composite {
	
	private Text txtFullTextSearch;
    
    private GridTableViewer gridViewer;

	public TabularView(Composite parent, int style) {
		super(parent, style);
		this.setLayout(new GridLayout(1, false));
		Composite viewsHeader = new Composite(this, SWT.NONE); 
		viewsHeader.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		GridLayout headerLayout = new GridLayout(6, false);
		headerLayout.marginTop = headerLayout.marginBottom = headerLayout.marginHeight = 0;
		viewsHeader.setLayout(headerLayout);

		new Label(viewsHeader, SWT.NONE).setText("Highlight items containing:");
		txtFullTextSearch = new Text(viewsHeader, SWT.SINGLE | SWT.BORDER);
		
		gridViewer = new GridTableViewer(this);
		gridViewer.getGrid().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		gridViewer.getGrid().setHeaderVisible(true);
		gridViewer.getGrid().setRowHeaderVisible(true);

	}
	 	
	public Text getInstantSearch() {
		return txtFullTextSearch;
	}

	public GridTableViewer getTableViewer() {
		return gridViewer;
	}


}
