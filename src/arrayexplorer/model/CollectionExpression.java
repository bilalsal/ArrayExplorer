package arrayexplorer.model;

import java.util.HashMap;
import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.jdt.debug.core.IJavaArray;
import org.eclipse.jdt.debug.core.IJavaObject;
import org.eclipse.jdt.debug.core.IJavaReferenceType;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.debug.core.IJavaType;
import org.eclipse.jdt.debug.core.IJavaValue;

import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.Field;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.Value;

public class CollectionExpression extends IterableExpression {

	HashMap<Field, List<Field>> fields;
	IJavaType compType;

	private static IJavaArray getArray(String name, IJavaStackFrame stackFrame) {
		String expression = "((java.util.Collection)(" + name + ")).toArray()";
		return (IJavaArray) evaluateExp(expression, stackFrame);
	}

	public CollectionExpression(String name, IJavaObject value,
			IJavaStackFrame stackFrame) {
		super(name, value, getArray(name, stackFrame));
		try {
			if (array != null && array.getValues().length > 0) {
				// TODO: more correct type inference
				compType = array.getValue(0).getJavaType();
				fillFieldsAndSubFields((IJavaReferenceType) compType);
			}
		} catch (DebugException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Value getChildValue(IJavaValue val, Object field, Object subField) {
		return val == null ? null : getFieldValue(val, (Field) field,
				(Field) subField);
	}

	@Override
	public boolean isNumericType(Object field) {
		if (field == null) {
			return !(compType instanceof IJavaReferenceType);
		}
		try {
			return !(((Field) field).type() instanceof ReferenceType);
		} catch (ClassNotLoadedException e) {
			return false;
		}
	}

}
