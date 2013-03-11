package arrayexplorer.eclipseuiactions;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IWatchExpression;
import org.eclipse.jdt.debug.core.IJavaFieldVariable;
import org.eclipse.jdt.debug.core.IJavaValue;
import org.eclipse.jdt.debug.core.IJavaVariable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.internal.PluginAction;

import arrayexplorer.control.ArraysMainController;

/**
 * This class acts as an {@link IObjectActionDelegate} to handle the "Visualize"
 * Action invoked on an {@link IJavaVariable} object in the "Variables" view in
 * Eclipse or on an {@link IWatchExpression} object in the "Expressions" view in
 * Eclipse
 * 
 * @author Bilal
 * 
 */
@SuppressWarnings("restriction")
public class ValueVisActionDelegate implements IObjectActionDelegate {

	public ValueVisActionDelegate() {
	}

	@Override
	public void run(IAction action) {
		// selection must has 1 and only 1 element (as defined in the extension
		StructuredSelection selection = (StructuredSelection) ((PluginAction) action)
				.getSelection();
		String itemName = null;
		IJavaValue value = null;
		try {
			if (selection.getFirstElement() instanceof IJavaVariable) {
				IJavaFieldVariable variable = (IJavaFieldVariable) selection
						.getFirstElement();
				variable.getReferenceTypeName();
				variable.getDeclaringType();
				itemName = variable.getReferenceTypeName();
				variable.getGenericSignature();
				value = (IJavaValue) variable.getValue();
			} else if (selection.getFirstElement() instanceof IWatchExpression) {
				IWatchExpression expression = (IWatchExpression) selection
						.getFirstElement();
				itemName = expression.getExpressionText();
				value = (IJavaValue) expression.getValue();
			}
			if (value != null) {
				ArraysMainController.handleValue(itemName, value, null);
			}
		} catch (DebugException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {

	}

}
