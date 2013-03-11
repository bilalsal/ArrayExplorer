package arrayexplorer.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;

/**
 * 
 * This is an abstract view for Value-History Views.
 * It implements common functionalities like a close button 
 * @author Bilal
 *
 */
public class ChartView extends Composite {
	
    private Scale sliderZoom;
    
    private Composite canvas;
	
	public ChartView(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(2, false));
		new Label(this, SWT.NONE).setText("Zoom");
		sliderZoom = new Scale(this, SWT.NONE);
		sliderZoom.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		sliderZoom.setMinimum(0);
		sliderZoom.setSelection(10);
		sliderZoom.setMaximum(20);
		canvas = new  Composite(this, SWT.BORDER);
		canvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
	}
	 	
	public Scale getSliderZoom() {
		return sliderZoom;
	}
	
	public Composite getCanvas() {
		return canvas;
	}
	
}
