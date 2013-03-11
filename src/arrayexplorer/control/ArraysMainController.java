package arrayexplorer.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.jdt.debug.core.IJavaArray;
import org.eclipse.jdt.debug.core.IJavaArrayType;
import org.eclipse.jdt.debug.core.IJavaClassType;
import org.eclipse.jdt.debug.core.IJavaInterfaceType;
import org.eclipse.jdt.debug.core.IJavaObject;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.debug.core.IJavaType;
import org.eclipse.jdt.debug.core.IJavaValue;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import arrayexplorer.eclipseuiactions.InspectObjectReferenceAction;
import arrayexplorer.model.ArrayExpression;
import arrayexplorer.model.CollectionExpression;
import arrayexplorer.model.IterableExpression;
import arrayexplorer.model.MapExpression;
import arrayexplorer.view.ArraysViewPart;

import com.sun.jdi.ObjectReference;

/**
 * The main controller of the arrays {@link ViewPart}. It reacts to changes from
 * the front-end and from the backend.
 * 
 * @author Bilal
 * 
 */
public class ArraysMainController {

	private ArraysViewPart view;

	private HashMap<String, IterableExpression> arrExpressions;

	private static ArraysMainController instance;

	/**
	 * Connsturcts the controller for the main view.
	 * 
	 * @param view
	 *            the main view
	 */
	private ArraysMainController(final ArraysViewPart view) {
		this.view = view;
		arrExpressions = new HashMap<String, IterableExpression>();

		initArrayExpressionsViewer();

	}

	private void initArrayExpressionsViewer() {

		final TreeViewer watchedVariablesViewer = view
				.getTreeArrays();
		MenuManager menuMgr = new MenuManager();
		menuMgr.setRemoveAllWhenShown(true);
		Menu menu = menuMgr.createContextMenu(watchedVariablesViewer
				.getControl());
		watchedVariablesViewer.getControl().setMenu(menu);
		menuMgr.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager manager) {
				if (watchedVariablesViewer.getSelection().isEmpty()) {
					return;
				}

				if (watchedVariablesViewer.getSelection() instanceof ITreeSelection) {
					ITreeSelection selection = (ITreeSelection) watchedVariablesViewer
							.getSelection();
					Object selectedItem = selection.getFirstElement();
					Shell shell = watchedVariablesViewer.getControl()
							.getShell();

					final IterableExpression expr = (IterableExpression) selection
							.getPaths()[0].getSegment(0);
					final Object field;
					if (selectedItem != expr) {
						field = selectedItem;
					} else {
						field = null;
						manager.add(new InspectObjectReferenceAction(shell,
								expr.getIterableValue()));
					}

					manager.add(new Action("Show as table") {
						@Override
						public void run() {
							showTableFor(expr, field);
						}
					});

					if (expr.isNumericType(field)) {
						manager.add(new Action("Show as hisogram") {
							@Override
							public void run() {
								showHisogramFor(expr, field);
							}
						});
						manager.add(new Action("Show as line chart") {
							@Override
							public void run() {
								showLineChartFor(expr, field);
							}
						});
					} else {
						manager.add(new Action("Show as bar chart") {
							@Override
							public void run() {
								showBarchartFor(expr, field);
							}
						});
					}
				}
			}
		});

		// sets the content provider (provides the watchpoints and instances for
		// the selected target)
		watchedVariablesViewer.setContentProvider(new ITreeContentProvider() {

			@Override
			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
			}

			@Override
			public void dispose() {
			}

			@Override
			public Object getParent(Object element) {
				return null;
			}

			@Override
			public Object[] getElements(Object inputElement) {
				return arrExpressions.values().toArray();
			}

			@Override
			public boolean hasChildren(Object element) {

				return element instanceof IterableExpression
						&& ((IterableExpression) element).hasChildren();
			}

			@Override
			public Object[] getChildren(Object element) {
				return ((IterableExpression) element).getChildren().toArray();
			}
		});
		// the input is the debug target (initially null)
		watchedVariablesViewer.setInput("");

		// the label provider (to show the tree items in a readable format)
		watchedVariablesViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ObjectReference) {
					String instanceName = element.toString();
					int startPos = instanceName.lastIndexOf('=') + 1;
					int endPos = instanceName.lastIndexOf(')');
					if (startPos >= 0 && endPos >= startPos) {
						instanceName = "id="
								+ instanceName.substring(startPos, endPos);
					}
					return instanceName;
				} else {
					return super.getText(element);
				}
			}
		});
		// a listener to open the value-history view for the clicked instance
		watchedVariablesViewer.getTree().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				ISelection sel = view.getTreeArrays().getSelection();
				if (sel != null) {
					ITreeSelection treeSel = (ITreeSelection) sel;
					if (treeSel.size() == 1) {
						Object element = treeSel.getFirstElement();
						if (element instanceof IterableExpression) {
							showTableFor((IterableExpression) element, null);
						} else {
							IterableExpression expr = (IterableExpression) treeSel
									.getPaths()[0].getSegment(0);
							showTableFor(expr, element);
						}
					}
				}
			}
		});
	}

	// shows the array for the given parameters (it will be created
	// if it does not exist yet)
	public void showTableFor(IterableExpression exp, Object field) {
		TableController arrayController = exp.getTableController(field);
		if (arrayController == null) {
			arrayController = new TableController(view.getViewsArea(), exp,
					field);
			exp.setTableController(field, arrayController);
		}
		((StackLayout) view.getViewsArea().getLayout()).topControl = arrayController
				.getView();
		view.getViewsArea().layout(true, true);
	}

	public void showHisogramFor(IterableExpression exp, Object field) {
		HistogramController arrayController = exp.getHistogramController(field);
		if (arrayController == null) {
			arrayController = new HistogramController(view.getViewsArea(), exp,
					field);
			exp.setHistogramController(field, arrayController);
		}
		((StackLayout) view.getViewsArea().getLayout()).topControl = arrayController
				.getView();
		view.getViewsArea().layout(true, true);
	}
	
	public void showLineChartFor(IterableExpression exp, Object field) {
		LineChartController arrayController = exp.getLineChartController(field);
		if (arrayController == null) {
			arrayController = new LineChartController(view.getViewsArea(), exp,
					field);
			exp.setLineChartController(field, arrayController);
		}
		((StackLayout) view.getViewsArea().getLayout()).topControl = arrayController
				.getView();
		view.getViewsArea().layout(true, true);
	}
	
	public void showBarchartFor(IterableExpression exp, Object field) {
		BarChartController arrayController = exp.getBarchartController(field);
		if (arrayController == null) {
			arrayController = new BarChartController(view.getViewsArea(), exp,
					field);
			exp.setBarChartController(field, arrayController);
		}
		((StackLayout) view.getViewsArea().getLayout()).topControl = arrayController
				.getView();
		view.getViewsArea().layout(true, true);
	}
	
	public static ArraysMainController getInstance() {
		if (instance == null) {
			ArraysViewPart view = null;
			try {
				IWorkbenchPage page = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage();
				view = (ArraysViewPart) page.showView(ArraysViewPart.ID);
			} catch (PartInitException e) {
				e.printStackTrace();
			}
			instance = new ArraysMainController(view);
		}
		return instance;
	}

	public static void handleValue(String name, IJavaValue value,
			IJavaStackFrame stackFrame) {

		IterableExpression expression = getInstance().arrExpressions.get(name);
		if (expression != null) {
			getInstance().showTableFor(expression, null);
			return;
		}
		try {
			IterableExpression exp = null;
			IJavaType type = value.getJavaType();
			if (type instanceof IJavaArrayType) {
				exp = new ArrayExpression(name, (IJavaArray) value,
						((IJavaArrayType) type).getComponentType());
			} else {
				IJavaInterfaceType[] allInterfaces = new IJavaInterfaceType[0];
				if (type instanceof IJavaClassType) {
					allInterfaces = ((IJavaClassType) type).getAllInterfaces();
				} else if (type instanceof IJavaInterfaceType) {
					List<IJavaInterfaceType> interfacesList = new ArrayList<IJavaInterfaceType>();
					getAllSuperInterfaces((IJavaInterfaceType) type,
							interfacesList);
					allInterfaces = interfacesList.toArray(allInterfaces);
				}

				for (IJavaInterfaceType iface : allInterfaces) {
					if (iface.toString().equals("java.util.Collection")) {
						exp = new CollectionExpression(name,
								(IJavaObject) value, stackFrame);
						break;
					}
					if (iface.toString().equals("java.util.Map")) {
						exp = new MapExpression(name, (IJavaObject) value,
								stackFrame);
						break;
					}
				}
			}
			if (exp != null) {
				instance.arrExpressions.put(exp.getName(), exp);
				getInstance().view.getTreeArrays().refresh();
				instance.showTableFor(exp, null);
			}
		} catch (DebugException e) {

		}
	}

	private static void getAllSuperInterfaces(IJavaInterfaceType type,
			List<IJavaInterfaceType> interfacesList) throws DebugException {
		interfacesList.add(type);
		for (IJavaInterfaceType inf : type.getSuperInterfaces()) {
			getAllSuperInterfaces(inf, interfacesList);
		}
	}
}
