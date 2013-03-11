package arrayexplorer.control;

import java.util.HashMap;

import org.eclipse.jdt.debug.core.IJavaValue;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget;
import org.eclipse.jdt.internal.debug.core.model.JDIValue;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridCellRenderer;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import arrayexplorer.eclipseuiactions.InspectObjectReferenceAction;
import arrayexplorer.model.IterableExpression;
import arrayexplorer.view.TabularView;

import com.sun.jdi.Value;

public class BarChartController extends AbstractArrayController {

	int defaultBarSpacing = 10;

	int defaultBarThickness = 15;

	int minBarSpacing = 1;

	int minBarThickness = 3;

	int barSpacing = defaultBarSpacing;

	int barThickness = defaultBarThickness;

	int maxFreq;

	Integer[] freqs;
	Value[] vals;

	int[] barStartMargin = new int[] { 30, 70 }, barEndMargin = new int[] { 20,
			50 };

	private TabularView view;

	public BarChartController(Composite parent, IterableExpression exp,
			Object field) {
		super(parent, exp, field);
		updateBars();
		initViewer();
	}

	private void initViewer() {
		view = new TabularView(parent, SWT.BORDER);
		GridTableViewer tableViewer = view.getTableViewer();
		tableViewer.setContentProvider(new IStructuredContentProvider() {

			@Override
			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {

			}

			@Override
			public void dispose() {

			}

			@Override
			public Object[] getElements(Object inputElement) {
				Integer[] elements = new Integer[vals.length];
				for (int i = 0; i < elements.length; i++) {
					elements[i] = i;
				}
				return elements;
			}
		});
		final Grid grid = tableViewer.getGrid();
		grid.setSelectionEnabled(false);
		grid.setLinesVisible(false);
		final GridColumn valCol = new GridColumn(grid, SWT.LEFT);
		valCol.setText("Value");
		final GridViewerColumn valColViewer = new GridViewerColumn(tableViewer,
				valCol);
		valColViewer.setLabelProvider(new CellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {
				int i = (Integer) cell.getElement();
				cell.setText(getText(vals[i]));
			}
		});
		final GridColumn freqCol = new GridColumn(grid, SWT.LEFT);
		freqCol.setText("Frequency");
		final GridViewerColumn freqColViewer = new GridViewerColumn(
				tableViewer, freqCol);
		freqColViewer.setLabelProvider(new CellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {
				int i = (Integer) cell.getElement();
				cell.setText(freqs[i].toString());
			}
		});
		freqCol.setCellRenderer(new GridCellRenderer() {

			@Override
			public void paint(GC gc, Object value) {
				Rectangle rect = getBounds();
				int i = (Integer) ((GridItem) value).getData();
				gc.setBackground(WHITE);
				gc.fillRectangle(rect);
				rect.width = rect.width * freqs[i] / maxFreq;
				rect.height -= 2;
				gc.setBackground(itemColor);
				gc.fillRectangle(rect);
				int labelY = rect.y + 2;
				gc.drawString(String.valueOf(freqs[i]), rect.x + 5, labelY);
			}

			@Override
			public Point computeSize(GC gc, int wHint, int hHint, Object value) {
				int width = grid.getSize().x - valCol.getWidth() - 5;
				if (grid.getVerticalBar() != null) {
					width -= grid.getVerticalBar().getSize().x;
				}
				return new Point(Math.min(width, 50), barThickness + barSpacing);
			}

			@Override
			public boolean notify(int event, Point point, Object value) {
				return false;
			}
		});
		tableViewer.setInput(exp);
		valCol.pack();
		freqCol.setWidth(300);
		grid.pack(true);
		installContextMenu(tableViewer);
	}

	public void installContextMenu(final GridTableViewer viewer) {
		final Grid grid = viewer.getGrid();
		final MenuManager menuMgr = new MenuManager();
		menuMgr.setRemoveAllWhenShown(true);
		final Menu menu = new Menu(grid.getShell(), SWT.POP_UP); // menuMgr.createContextMenu(grid);
		grid.getShell().setMenu(menu);
		MenuItem itemInspect = new MenuItem(menu, SWT.NONE);
		itemInspect.setText("Inspect");
		final InspectObjectReferenceAction action = new InspectObjectReferenceAction(
				grid.getShell(), null);

		itemInspect.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
					action.run();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		grid.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				Point cell = grid.getCell(new Point(e.x, e.y));
				if (cell != null &&  vals[cell.y] != null && e.button == 3) {
					JDIDebugTarget target = (JDIDebugTarget) exp
							.getIterableValue().getDebugTarget();
					IJavaValue javaVal = new JDIValue(target, vals[cell.y]);
					action.setValue(javaVal);
					menu.setLocation(grid.toDisplay(e.x, e.y));
					menu.setVisible(true);
				}

			}
		});
		//
		// menuMgr.addMenuListener(new IMenuListener() {
		// @Override
		// public void menuAboutToShow(IMenuManager manager) {
		//
		// if (viewer.getSelection() instanceof IStructuredSelection) {
		// Shell shell = grid.getShell();
		// viewer.getCell(get);
		// IStructuredSelection sel = (IStructuredSelection) viewer
		// .getSelection();
		// }
		//
		// }
		// });
	}

	private void updateBars() {
		HashMap<Value, Integer> histogram = new HashMap<Value, Integer>();
		for (int i = 0; i < getArraySize(); i++) {
			Value key = super.getArrayElement(i);
			Integer val = histogram.get(key);
			if (val == null) {
				val = Integer.valueOf(0);
			}
			histogram.put(key, val + 1);
		}
		vals = histogram.keySet().toArray(new Value[histogram.size()]);
		maxFreq = 0;
		freqs = histogram.values().toArray(new Integer[histogram.size()]);
		for (int i = 0; i < freqs.length; i++) {
			if (freqs[i] > maxFreq) {
				maxFreq = freqs[i];
			}
		}

	}

	// @Override
	protected void draw(GC g, int from, int to) {

		// if (getView().getSize().y <= properties.border * 2) {
		// if (getView().getSize().x > properties.border * 2)
		// g.drawLine(properties.border, getView().getSize().y / 2,
		// getView().getSize().x - properties.border, getView()
		// .getSize().y / 2);
		// return;
		// }
		int currX = getCoordForElemInd(from);
		int barStart = view.getSize().y - barStartMargin[1];
		int maxBarLength = barStart - barEndMargin[1];
		// g.drawLine(barStartMargin[0], barStart, getRequiredWidth(),
		// barStart);
		int maxFreqY = barStart - maxBarLength;
		String maxFreqText = String.valueOf(maxFreq);
		int maxFreqTextX = barStartMargin[0] - 5
				- g.getFontMetrics().getAverageCharWidth()
				* maxFreqText.length();
		g.drawLine(barStartMargin[0], barStart + 1, barStartMargin[0],
				maxFreqY - 10);
		g.drawLine(barStartMargin[0] - 3, maxFreqY, barStartMargin[0], maxFreqY);
		g.drawString(maxFreqText, maxFreqTextX, maxFreqY
				- g.getFontMetrics().getHeight() / 2);
		for (int ind = from; ind <= to; ind++) {
			int barLength = Math.max(
					(int) (maxBarLength * freqs[ind] / maxFreq), 1);
			int startX = currX;
			int startY = barStart - barLength;
			// g.setBackground(properties.pointColor);
			g.fillRectangle(startX, startY, barThickness, barLength);
			g.drawRectangle(startX, startY, barThickness, barLength);
			String str = String.valueOf(vals[ind]);
			g.setBackground(WHITE);
			g.drawString(str, currX, barStart + 5);
			currX += barThickness + barSpacing;
		}
	}

	protected int getCoordForElemInd(int ind) {
		return barSpacing + ind * (barThickness + barSpacing)
				+ barStartMargin[0];
	}

	@Override
	public Composite getView() {
		// if (view == null) {
		// view = new BarChartView(parent, SWT.BORDER);
		// }
		return view;
	}

	// @Override
	protected int getItemCount() {
		return freqs.length;
	}
}
