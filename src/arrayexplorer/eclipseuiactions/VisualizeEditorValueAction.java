package arrayexplorer.eclipseuiactions;

import org.eclipse.jdt.debug.core.IJavaValue;
import org.eclipse.jdt.debug.eval.IEvaluationResult;
import org.eclipse.jdt.internal.debug.ui.JDIDebugUIPlugin;
import org.eclipse.jdt.internal.debug.ui.actions.EvaluateAction;
import org.eclipse.swt.widgets.Shell;

import arrayexplorer.control.ArraysMainController;

/**
 * This {@link EvaluateAction} reacts to clicking on the "Visualize" entry in
 * the editor's context menu. It visualizes the value of the selected code after
 * evaluating it. The menu entry is visible only in debug mode.
 * 
 * @author Bilal
 * 
 */
@SuppressWarnings("restriction")
public class VisualizeEditorValueAction extends EvaluateAction {

	@Override
	protected void displayResult(final IEvaluationResult result) {
		final Shell shell = JDIDebugUIPlugin.getShell();
		shell.getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				IJavaValue value = result.getValue();
				if (value != null) {
				}
				ArraysMainController.handleValue(result.getSnippet(), value,
						getStackFrameContext());
			}
		});
	}

}
