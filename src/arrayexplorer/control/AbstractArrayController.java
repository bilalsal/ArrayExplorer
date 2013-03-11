package arrayexplorer.control;

import org.eclipse.jdt.debug.core.IJavaValue;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;

import arrayexplorer.model.IterableExpression;

import com.sun.jdi.Value;

/**
 * Controller for the history of a numerical variable. It maps the numerical
 * variable to the y-value of the points representing the history values
 * 
 * @author Bilal
 * 
 */
public abstract class AbstractArrayController {

	protected final IterableExpression exp;
	protected Composite parent;
	protected Object field;
	static Color WHITE;
	static Color itemColor;


	public AbstractArrayController(Composite parent,
			final IterableExpression exp, Object field) {
		this.exp = exp;
		this.parent = parent;
		this.field = field;
		if (itemColor == null) {
			WHITE = new Color(parent.getDisplay(), 255, 255, 255);
			itemColor = new Color(parent.getDisplay(), 200, 200, 200);
		}

	}

	public abstract Composite getView();

	protected Value getArrayElement(int i) {
		IJavaValue value = exp.getValues()[i];
		return exp.getChildValue(value, field, null);
	}

	protected int getArraySize() {
		return exp.getValues().length;
	}

	protected String getText(Value fieldVal) {
		if (fieldVal == null) {
			return "null";
		}
		String string = fieldVal.toString();
		int ind = string.indexOf("(id=");
		if (ind > 0) {
			return string.substring(ind);
		} else {
			return string;
		}
	}
}
