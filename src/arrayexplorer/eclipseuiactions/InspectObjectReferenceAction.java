package arrayexplorer.eclipseuiactions;

import org.eclipse.debug.ui.DebugPopup;
import org.eclipse.debug.ui.InspectPopupDialog;
import org.eclipse.jdt.debug.core.IJavaValue;
import org.eclipse.jdt.internal.debug.ui.display.JavaInspectExpression;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Shell;

@SuppressWarnings("restriction")
public class InspectObjectReferenceAction extends Action {

	IJavaValue value;

	Shell shell;

	public InspectObjectReferenceAction(Shell shell, IJavaValue value) {
		super("Inspect");
		this.value = value;
		this.shell = shell;
	}

	@Override
	public void run() {
		JavaInspectExpression expression = new JavaInspectExpression("name",
				value);

		DebugPopup displayPopup = new InspectPopupDialog(shell, null, null,
				expression);
		displayPopup.open();

	}
	
	public void setValue(IJavaValue value) {
		this.value = value;
	}

}
