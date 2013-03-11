package arrayexplorer.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.jdt.debug.core.IJavaArray;
import org.eclipse.jdt.debug.core.IJavaObject;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.debug.core.IJavaType;
import org.eclipse.jdt.debug.core.IJavaValue;
import org.eclipse.jdt.internal.debug.core.model.JDIObjectValue;
import org.eclipse.jdt.internal.debug.core.model.JDIType;

import com.sun.jdi.Field;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.Value;

public class MapExpression extends IterableExpression {

	public final static String KEY = "key", VALUE = "value";

	List<IJavaValue> pairs;

	IJavaArray keys, values;

	private static IJavaArray getArray(String name, String items,
			IJavaStackFrame stackFrame) {
		String expression = "((java.util.Map)(" + name + "))." + items
				+ ".toArray()";
		return (IJavaArray) evaluateExp(expression, stackFrame);
	}

	public MapExpression(String name, IJavaObject value,
			IJavaStackFrame stackFrame) {
		super(name, value, getArray(name, "entrySet()", stackFrame));
		System.out.println(stackFrame);
		pairs = Arrays.asList(getValues());
		List<Field> keyFields = new ArrayList<Field>();
		List<Field> valueFields = new ArrayList<Field>();
		if (pairs.size() > 0) {
			keys = getArray(name, "keySet()", stackFrame);
			values = getArray(name, "values()", stackFrame);
			try {
				IJavaType type = keys.getValue(0).getJavaType();
				fillFields((ReferenceType)((JDIType)type).getUnderlyingType(), keyFields);
				type = values.getValue(0).getJavaType();
				fillFields((ReferenceType)((JDIType)type).getUnderlyingType(), valueFields);
			} catch (DebugException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		fields.put(KEY, keyFields);
		fields.put(VALUE, valueFields);
	}

	@Override
	public Value getChildValue(IJavaValue val, Object field, Object subField) {
		try {
			int ind = pairs.indexOf(val);
			IJavaValue value = field == KEY ? keys.getValue(ind) : values.getValue(ind);
			if (subField == null) {
				return value == null ? null : ((JDIObjectValue) value).getUnderlyingObject();
			}
			else {
				return getFieldValue(value, (Field)subField, null);
			}
		} catch (DebugException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean isNumericType(Object field) {
		return false;
	}

}
