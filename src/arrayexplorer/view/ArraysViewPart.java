package arrayexplorer.view;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

/**
 * A {@link ViewPart} which shows modification to the variables of installed watch points as a table.
 * This View will be loaded automatically in Eclipse and can be opened either from
 * Window -> Show View -> Other -> Other or by clicking the orange button in the toolbar.
 * @author Bilal
 *
 */
public class ArraysViewPart extends ViewPart {

	public static final String ID = "arrayexplorer.viewarray";
	
    
    Composite viewsArea;
    
	private TreeViewer treeArrays;
	
	public ArraysViewPart() {
				
	}

	public void createPartControl(Composite parent) {
		
		SashForm form = new SashForm(parent, SWT.NONE);
				
		Composite dataComp = new Composite(form, SWT.NONE);
		
		dataComp.setLayout(new GridLayout(1, false));
		
		Label lblExpressions = new Label(dataComp, SWT.NONE);
		lblExpressions.setText("Array Expressions:");
		

		treeArrays = new TreeViewer(dataComp, SWT.BORDER);
		treeArrays.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		viewsArea = new Composite(form, SWT.NONE);
		StackLayout layout = new StackLayout();
		viewsArea.setLayout(layout);
		Label hintLabel = new Label(viewsArea, SWT.NONE | SWT.MULTI);
		hintLabel.setText("Add Array expressions \n Double-click an expression to show it");
		layout.topControl = hintLabel;
		form.setWeights(new int[]{1, 3});
	}
	
	@Override
    public void setFocus() {

	}
	
	
    public Composite getViewsArea() {
		return viewsArea;
	}    

	public TreeViewer getTreeArrays() {
		return treeArrays;
	}
	
}
