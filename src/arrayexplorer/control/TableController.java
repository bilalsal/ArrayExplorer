package arrayexplorer.control;

import org.eclipse.jdt.debug.core.IJavaValue;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridColumnGroup;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;

import arrayexplorer.eclipseuiactions.InspectObjectReferenceAction;
import arrayexplorer.model.IterableExpression;
import arrayexplorer.view.TabularView;

import com.sun.jdi.Value;

public class TableController extends AbstractArrayController {

	TabularView view;

	final Color gray;

	public TableController(Composite parent, IterableExpression exp,
			Object field) {
		super(parent, exp, field);
		view = new TabularView(parent, SWT.BORDER);
		gray = new Color(parent.getDisplay(), 200, 200, 200);
		initView(exp, field);
	}

	private void initView(final IterableExpression exp, final Object field) {
		GridTableViewer tableViewer = view.getTableViewer();
		Grid grid = tableViewer.getGrid();
		if (field != null) {

			GridColumnGroup colGroup = new GridColumnGroup(grid, SWT.TOGGLE);
			colGroup.setExpanded(false);
			colGroup.setText(field.toString());
			for (final Object child : exp.getSubFields(field)) {

				final GridColumn column = new GridColumn(colGroup, SWT.LEFT);
				new GridViewerColumn(tableViewer, column)
						.setLabelProvider(new CellLabelProvider() {

							@Override
							public void update(ViewerCell cell) {
								IJavaValue val = (IJavaValue) cell.getElement();
								updateCellValue(cell,
										exp.getChildValue(val, field, child));
							}
						});
				column.setText(child.toString());
			}
			final GridColumn column = new GridColumn(colGroup, SWT.LEFT);
			new GridViewerColumn(tableViewer, column)
					.setLabelProvider(new CellLabelProvider() {

						@Override
						public void update(ViewerCell cell) {
							IJavaValue val = (IJavaValue) cell.getElement();
							updateCellValue(cell,
									exp.getChildValue(val, field, null));
						}
					});
			column.setText("toString()");
			column.setSummary(true);
			column.setDetail(colGroup.getColumns().length == 1);

		} else {

			for (final Object child : exp.getChildren()) {
				GridColumnGroup colGroup = null;
				if (exp.hasSubField(child)) {
					colGroup = new GridColumnGroup(grid, SWT.TOGGLE);
					colGroup.setExpanded(false);
					colGroup.setText(child.toString());
					for (final Object subField : exp.getSubFields(child)) {
						GridColumn column = new GridColumn(colGroup, SWT.LEFT);
						column.setText(subField.toString());
						column.setDetail(true);
						column.setSummary(false);
						new GridViewerColumn(tableViewer, column)
								.setLabelProvider(new CellLabelProvider() {

									@Override
									public void update(ViewerCell cell) {
										IJavaValue val = (IJavaValue) cell
												.getElement();
										updateCellValue(cell, exp
												.getChildValue(val, child,
														subField));

									}
								});
					}
				}

				final GridColumn column = colGroup == null ? new GridColumn(
						grid, SWT.LEFT) : new GridColumn(colGroup, SWT.LEFT);
				new GridViewerColumn(tableViewer, column)
						.setLabelProvider(new CellLabelProvider() {

							@Override
							public void update(ViewerCell cell) {
								IJavaValue val = (IJavaValue) cell.getElement();
								updateCellValue(cell,
										exp.getChildValue(val, child, null));
							}
						});
				column.setText(child.toString());
				if (colGroup != null) {
					column.setSummary(true);
					column.setDetail(colGroup.getColumns().length == 1);
					column.setText("toString()");
				}
			}
		}

		if (grid.getColumnCount() == 0) {
			final GridColumn column = new GridColumn(grid, SWT.LEFT);
			new GridViewerColumn(tableViewer, column)
					.setLabelProvider(new CellLabelProvider() {

						@Override
						public void update(ViewerCell cell) {
							IJavaValue val = (IJavaValue) cell.getElement();
							updateCellValue(cell,
									exp.getChildValue(val, field, null));
						}
					});
			column.setText(field != null ? field.toString() : exp.toString());
		}
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
				return exp.getValues();
			}
		});
		tableViewer.setInput(exp);

		for (GridColumn col : grid.getColumns()) {
			col.pack();
		}

		installContextMenu(view.getTableViewer());
	}

	public static void installContextMenu(final GridTableViewer viewer) {
		final Grid grid = viewer.getGrid();
		MenuManager menuMgr = new MenuManager();
		menuMgr.setRemoveAllWhenShown(true);
		Menu menu = menuMgr.createContextMenu(grid);
		grid.setMenu(menu);
		menuMgr.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				if (viewer.getSelection() instanceof IStructuredSelection) {
					Shell shell = grid.getShell();
					IStructuredSelection sel = (IStructuredSelection) viewer
							.getSelection();
					manager.add(new InspectObjectReferenceAction(shell,
							(IJavaValue) sel.getFirstElement()));
				}

			}
		});
	}

	protected void updateCellValue(ViewerCell cell, Value fieldVal) {
		if (fieldVal == null) {
			cell.setBackground(gray);
		}
		cell.setText(getText(fieldVal));

	}

	@Override
	public Composite getView() {
		return view;
	}

}
