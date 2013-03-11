package arrayexplorer.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IExpressionManager;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IWatchExpression;
import org.eclipse.jdt.debug.core.IJavaArray;
import org.eclipse.jdt.debug.core.IJavaObject;
import org.eclipse.jdt.debug.core.IJavaPrimitiveValue;
import org.eclipse.jdt.debug.core.IJavaReferenceType;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.debug.core.IJavaValue;
import org.eclipse.jdt.internal.debug.core.model.JDIObjectValue;
import org.eclipse.jdt.internal.debug.core.model.JDIType;
import org.eclipse.jdt.internal.debug.core.model.JDIValue;

import arrayexplorer.control.BarChartController;
import arrayexplorer.control.HistogramController;
import arrayexplorer.control.LineChartController;
import arrayexplorer.control.TableController;

import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.Field;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.Type;
import com.sun.jdi.Value;

public abstract class IterableExpression {

	IJavaObject value;

	private String name;

	protected IJavaArray array;

	final protected HashMap<Object, List<Field>> fields;

	final private HashMap<Object, TableController> tableControllers;

	final private HashMap<Object, HistogramController> histogramControllers;

	final private HashMap<Object, LineChartController> lineChartControllers;

	final private HashMap<Object, BarChartController> barchartControllers;

	public static IValue evaluateExp(String expression,
			IJavaStackFrame stackFrame) {
		IExpressionManager manager = DebugPlugin.getDefault()
				.getExpressionManager();
		IWatchExpression newWatchExpression = manager
				.newWatchExpression(expression);
		newWatchExpression.setExpressionContext(stackFrame);
		newWatchExpression.evaluate();
		while (newWatchExpression.isPending()) {
		}
		return newWatchExpression.getValue();
	}

	public IterableExpression(String name, IJavaObject value, IJavaArray array) {
		this.name = name;
		this.value = value;
		this.array = array;
		fields = new HashMap<Object, List<Field>>();
		tableControllers = new HashMap<Object, TableController>();
		histogramControllers = new HashMap<Object, HistogramController>();
		barchartControllers = new HashMap<Object, BarChartController>();
		lineChartControllers = new HashMap<Object, LineChartController>();
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

	public IJavaValue getIterableValue() {
		return value;
	}

	public boolean hasChildren() {

		return fields.size() > 0;
	}

	public boolean hasSubField(Object field) {
		List<Field> list = fields.get(field);
		return list != null && list.size() > 0;
	}

	public Collection<?> getSubFields(Object field) {
		return fields.get(field);
	}

	public Collection<?> getChildren() {
		return fields.keySet();
	}

	public TableController getTableController(Object field) {
		return tableControllers.get(field);
	}

	public void setTableController(Object field, TableController controller) {
		tableControllers.put(field, controller);
	}

	public BarChartController getBarchartController(Object field) {
		return barchartControllers.get(field);
	}

	public void setBarChartController(Object field,
			BarChartController controller) {
		barchartControllers.put(field, controller);
	}

	public void setHistogramController(Object field,
			HistogramController controller) {
		histogramControllers.put(field, controller);
	}

	public void setLineChartController(Object field,
			LineChartController controller) {
		lineChartControllers.put(field, controller);
	}

	public HistogramController getHistogramController(Object field) {
		return histogramControllers.get(field);
	}

	public LineChartController getLineChartController(Object field) {
		return lineChartControllers.get(field);
	}

	public IJavaValue[] getValues() {
		try {
			return array.getValues();
		} catch (DebugException e) {
			return new IJavaValue[0];
		}
	}

	public abstract Value getChildValue(IJavaValue val, Object field,
			Object subField);

	protected void fillFieldsAndSubFields(IJavaReferenceType compType) {
		ReferenceType javaType = (ReferenceType) ((JDIType) compType)
				.getUnderlyingType();
		List<Field> list = new ArrayList<Field>();
		fillFields(javaType, list);
		for (Field field : list) {
			List<Field> subList = new ArrayList<Field>();
			fields.put(field, subList);
			try {
				Type type = field.type();
				if (type instanceof ReferenceType) {
					fillFields((ReferenceType) type, subList);
				}
			} catch (ClassNotLoadedException e) {
				e.printStackTrace();
			}
		}
	}

	protected void fillFields(ReferenceType type, List<Field> list) {
		for (Object subFieldObj : type.allFields()) {
			Field subField = (Field) subFieldObj;
			if (!subField.isSynthetic() && !subField.isStatic()) {
				list.add(subField);
			}
		}
	}

	public Value getFieldValue(IJavaValue val, Field field, Field subField) {
		if (val instanceof IJavaPrimitiveValue) {
			return toJDIValue(val);
		}
		ObjectReference refVal = ((JDIObjectValue) val).getUnderlyingObject();
		if (field == null) {
			return refVal;
		}
		Value fieldVal = refVal.getValue(field);
		if (subField == null) {
			return fieldVal;
		}
		if (fieldVal != null) {
			return ((ObjectReference) fieldVal).getValue(subField);
		}
		return null;
	}

	public abstract boolean isNumericType(Object field);

	public Value toJDIValue(IJavaValue val) {
		try {
			java.lang.reflect.Field fValField = JDIValue.class
					.getDeclaredField("fValue");
			fValField.setAccessible(true);
			return (Value) fValField.get(val);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
