package arrayexplorer.control;

import java.text.NumberFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;

import arrayexplorer.model.IterableExpression;
import arrayexplorer.view.ChartView;

import com.sun.jdi.PrimitiveValue;

/**
 * Controller for the history of a numerical variable.
 * It maps the numerical variable to the y-value of the points representing the history values 
 * @author Bilal
 *
 */
public abstract class AbstractChartController extends AbstractArrayController {

	public final Cursor HAND_CURSOR;

	protected int[] startCoord = new int[2],
		  		dimLength = new int[2];

	ChartView view;
	
	int[] barStartMargin = new int[] { 30, 50 }, barEndMargin = new int[] { 30, 50 };
	
	int zoomFactor = 1;

	int defaultItemWidth = 15;
	int itemWidth = defaultItemWidth;
	int minItemWidth = 3;

	final NumberFormat formatter;
	
	double minVal, maxVal;
	
	public void computeMinMax() {
		minVal = Integer.MAX_VALUE;
		maxVal = Integer.MIN_VALUE; 
		for(int i = 0; i < getArraySize(); i++) {
			double val = getArrayElement(i).doubleValue();
			if (minVal > val) {
				minVal = (int)val;
			}
			if (maxVal < val) {
				maxVal = (int)Math.ceil(val);
			}
		}
	}


	
	public AbstractChartController(Composite parent, final IterableExpression exp, Object field) {
		super(parent, exp, field);
		formatter = NumberFormat.getInstance();
		formatter.setMaximumFractionDigits(3);
		formatter.setMinimumFractionDigits(0);
		view = new ChartView(parent, SWT.BORDER);
		computeMinMax();
		view.getCanvas(). addPaintListener(new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {		
				draw(e);
			}
		});	
		view.getCanvas().addMouseMoveListener(new MouseMoveListener() {
			
			@Override
			public void mouseMove(MouseEvent e) {
				int ind =  getItemIndAt(e.x, e.y);
				if (ind >= 0) {
					getView().setCursor(HAND_CURSOR);
					getView().setToolTipText(ind + ": " + getArrayElement(ind));
				}
				else {
					getView().setCursor(null);
					getView().setToolTipText(null);
				}
			}

		});		
		HAND_CURSOR = new Cursor(getView().getDisplay(), SWT.CURSOR_HAND);
		
	}	

	protected abstract int getItemIndAt(int x, int y);

	protected void draw(PaintEvent e) {
		startCoord[0] = e.x;
		startCoord[1] = e.y;
		dimLength[0] = e.width;
		dimLength[1] = e.height;
		
		e.gc.setBackground(WHITE);
		e.gc.fillRectangle(e.x, e.y, e.width, e.height);
		String name = exp.getName();
		if (field != null)
			name += "." + field;
		e.gc.drawText(name, 5, 10);
		int from = Math.max(getItemIndAtOrAfter(e.x, e.y) - 1, 0);
		int to = Math.min(getItemIndAtOrAfter(e.x + e.width, e.y) + 1, getItemCount() - 1);

		draw(e.gc, from, to);
	}
	
	int getItemIndAtOrAfter(int x, int y) {
		int reqWidth = getRequiredWidth() - barStartMargin[0] - barEndMargin[0];			
		return (x - barStartMargin[0]) * getItemCount() / reqWidth;
	}
	
	public int getItemX(int index) {
		return barStartMargin[0] + index * itemWidth * zoomFactor;
	}
	protected abstract int getItemCount();
	
	public int getRequiredWidth() {
		int width;
		width = (int)(getItemCount() * itemWidth * zoomFactor);
		return width + barStartMargin[0] + barEndMargin[0];
	}


	@Override
	protected PrimitiveValue getArrayElement(int i) {
			return (PrimitiveValue)super.getArrayElement(i);
	}


	/**
	 */
	protected abstract void draw(GC g, int from, int to);
	

	@Override
	public Composite getView() {
		return view;
	}
}
