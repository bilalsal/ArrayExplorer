package arrayexplorer.model;

import org.eclipse.jdt.debug.core.IJavaArray;
import org.eclipse.jdt.debug.core.IJavaReferenceType;
import org.eclipse.jdt.debug.core.IJavaType;
import org.eclipse.jdt.debug.core.IJavaValue;

import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.Field;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.Value;

public class ArrayExpression extends IterableExpression {

	IJavaType compType;

	public ArrayExpression(String name, IJavaArray value, IJavaType compType) {
		super(name, value, value);
		this.compType = compType;
		if (compType instanceof IJavaReferenceType) {
			fillFieldsAndSubFields((IJavaReferenceType) compType);
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
			return !(((Field)field).type() instanceof ReferenceType);
		} catch (ClassNotLoadedException e) {
			return false;
		}
	}

}
