package org.orbisgis.javaManager.autocompletion;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.orbisgis.Services;
import org.orbisgis.javaManager.JavaManager;
import org.orbisgis.javaManager.PackageReflection;
import org.orbisgis.javaManager.parser.ASTArguments;
import org.orbisgis.javaManager.parser.ASTBooleanLiteral;
import org.orbisgis.javaManager.parser.ASTClassOrInterfaceType;
import org.orbisgis.javaManager.parser.ASTLiteral;
import org.orbisgis.javaManager.parser.ASTPrimaryPrefix;
import org.orbisgis.javaManager.parser.ASTPrimarySuffix;
import org.orbisgis.javaManager.parser.ASTPrimitiveType;
import org.orbisgis.javaManager.parser.ASTReferenceType;
import org.orbisgis.javaManager.parser.ASTType;
import org.orbisgis.javaManager.parser.JavaParserConstants;
import org.orbisgis.javaManager.parser.Node;
import org.orbisgis.javaManager.parser.SimpleNode;
import org.orbisgis.javaManager.parser.Token;

public class CompletionUtils {

	private static String text;

	private static int line;

	private static int col;

	private static SimpleNode root;

	private static VarVisitor vv;

	private static NodeUtils nodeUtils;

	private static ImportsVisitor importsVisitor;

	private static MethodParameterVisitor mpv;

	private static ScriptMethodVisitor scriptMethodVisitor;

	public static void setCompletionCase(String text, Node root, int line,
			int col) {
		CompletionUtils.text = text;
		CompletionUtils.line = line;
		CompletionUtils.col = col;
		CompletionUtils.root = (SimpleNode) root;
		vv = null;
		nodeUtils = null;
		importsVisitor = null;
		mpv = null;
		scriptMethodVisitor = null;
	}

	public static PackageReflection getPr() {
		JavaManager javaManager = Services.getService(JavaManager.class);
		return javaManager.getPackageReflection();
	}

	public static VarVisitor getVarVisitor() {
		if (vv == null) {
			vv = new VarVisitor(line, col);
			vv.visit((SimpleNode) root, null);
		}

		return vv;
	}

	public static MethodParameterVisitor getMethodParameterVisitor() {
		if (mpv == null) {
			mpv = new MethodParameterVisitor();
			mpv.visit((SimpleNode) root, null);
		}

		return mpv;
	}

	public static ScriptMethodVisitor getScriptMethodVisitor() {
		if (scriptMethodVisitor == null) {
			scriptMethodVisitor = new ScriptMethodVisitor();
			scriptMethodVisitor.visit(root, null);
		}

		return scriptMethodVisitor;
	}

	public static NodeUtils getNodeUtils() {
		if (nodeUtils == null) {
			nodeUtils = new NodeUtils(text, line, col);
		}

		return nodeUtils;
	}

	public static ImportsVisitor getImportsVisitor() {
		if (importsVisitor == null) {
			importsVisitor = new ImportsVisitor();
			importsVisitor.visit(root, null);
		}

		return importsVisitor;
	}

	public static Class<? extends Object> getType(
			ASTPrimaryPrefix primaryPrefix, ASTPrimarySuffix[] suffixes)
			throws SecurityException, ClassNotFoundException,
			CannotAutocompleteException, NoSuchFieldException,
			NoSuchMethodException {
		// Get the type of the primaryPrefix
		String[] parts = CompletionUtils.getNodeUtils().getParts(primaryPrefix,
				Integer.MAX_VALUE, Integer.MAX_VALUE);
		Class<? extends Object> clazz = null;
		for (int i = 0; i < parts.length - 1; i++) {
			clazz = getType(clazz, parts[i]);
		}

		if (clazz == null) {
			// It's a literal or a name
			Node literalOrName = primaryPrefix.jjtGetChild(0);
			if (literalOrName instanceof ASTLiteral) {
				clazz = getType((ASTLiteral) literalOrName);
			} else {
				clazz = getType(null, getNodeUtils().getText(literalOrName));
				// Array access []
				for (int i = 0; i < suffixes.length; i++) {
					if (suffixes[i].first_token.kind == JavaParserConstants.LBRACKET) {
						clazz = clazz.getComponentType();
					}
				}
			}
		} else {
			String methodName = parts[parts.length - 1];
			for (int i = 0; i < suffixes.length; i++) {
				if ((suffixes[0].jjtGetNumChildren() == 1)
						&& (suffixes[0].jjtGetChild(0) instanceof ASTArguments)) {
					Class<? extends Object>[] params = getParams((ASTArguments) suffixes[0]
							.jjtGetChild(0));
					Method method = clazz.getMethod(methodName, params);
					clazz = method.getReturnType();
					methodName = null;
				} else if (suffixes[0].jjtGetNumChildren() == 0) {
					methodName = suffixes[0].first_token.next.image;
				} else {
					throw new CannotAutocompleteException();
				}
			}
		}

		return clazz;
	}

	private static Class<? extends Object> getType(ASTLiteral literal) {
		if (literal.jjtGetNumChildren() == 1) {
			if (literal.jjtGetChild(0) instanceof ASTBooleanLiteral) {
				return boolean.class;
			} else {
				return null;
			}
		} else {
			switch (literal.first_token.kind) {
			case JavaParserConstants.INTEGER_LITERAL:
				return int.class;
			case JavaParserConstants.FLOATING_POINT_LITERAL:
				return double.class;
			case JavaParserConstants.CHARACTER_LITERAL:
				return char.class;
			case JavaParserConstants.STRING_LITERAL:
				return String.class;
			}

			throw new RuntimeException("Unknown literal!");
		}
	}

	public static Class<? extends Object>[] getParams(ASTArguments arguments) {
		if (arguments.jjtGetNumChildren() == 0) {
			return new Class<?>[0];
		} else {
			ArrayList<Class<? extends Object>> ret = new ArrayList<Class<? extends Object>>();
			Node argumentList = arguments.jjtGetChild(0);
			for (int i = 0; i < argumentList.jjtGetNumChildren(); i++) {
				ExpressionTypeVisitor etv = new ExpressionTypeVisitor();
				etv.visit((SimpleNode) argumentList.jjtGetChild(i), null);
				ret.add(etv.getType());
			}

			return ret.toArray(new Class<?>[0]);
		}
	}

	public static Class<? extends Object> getType(Class<? extends Object> type,
			String part) throws ClassNotFoundException,
			CannotAutocompleteException, SecurityException,
			NoSuchFieldException {
		if (type == null) {
			// it's a class
			type = getClassType(part);
			if (type == null) {
				// it's a variable
				type = getVarType(part);
			}
			if (type == null) {
				// it's a parameter
				type = getArgType(part);
			}
			if (type == null) {
				throw new CannotAutocompleteException("Unknown variable:"
						+ part);
			}
		} else {
			String fieldName = getFieldName(type, part);
			if (fieldName != null) {
				Field f = type.getField(fieldName);
				type = f.getType();
			}
			//
			// throw new UnsupportedOperationException("Check the methods!");
		}
		return type;
	}

	public static String getFieldName(Class<? extends Object> type,
			String prefix) {
		Field[] fields = type.getFields();
		for (Field field : fields) {
			String fieldName = field.getName();
			if ((prefix == null) || (fieldName.startsWith(prefix))) {
				return fieldName;
			}
		}

		return null;
	}

	public static Class<? extends Object> getVarType(String part)
			throws ClassNotFoundException {
		return CompletionUtils.getVarVisitor().getVarType(part);
	}

	public static Class<? extends Object> getArgType(String part)
			throws ClassNotFoundException {
		return CompletionUtils.getMethodParameterVisitor().getArgType(part);
	}

	public static Class<? extends Object> getClassType(String part)
			throws ClassNotFoundException {
		String classFullName = CompletionUtils.getImportsVisitor()
				.getClassTypeName(part);
		if (classFullName != null) {
			return Class.forName(classFullName);
		} else {
			ArrayList<String> classes = CompletionUtils.getPr().getClasses(
					"java.lang");
			for (String clazz : classes) {
				if (clazz.equals(part)) {
					return Class.forName("java.lang." + clazz);
				}
			}
		}

		return null;
	}

	public static Class<? extends Object> getPrimitiveType(
			ASTPrimitiveType primitiveType, boolean isArray) {
		if (isArray) {
			switch (((SimpleNode) primitiveType).first_token.kind) {
			case JavaParserConstants.BOOLEAN:
				return boolean[].class;
			case JavaParserConstants.CHAR:
				return char[].class;
			case JavaParserConstants.BYTE:
				return byte[].class;
			case JavaParserConstants.SHORT:
				return short[].class;
			case JavaParserConstants.INT:
				return int[].class;
			case JavaParserConstants.LONG:
				return long[].class;
			case JavaParserConstants.FLOAT:
				return float[].class;
			case JavaParserConstants.DOUBLE:
				return double[].class;
			}
		} else {
			switch (((SimpleNode) primitiveType).first_token.kind) {
			case JavaParserConstants.BOOLEAN:
				return boolean.class;
			case JavaParserConstants.CHAR:
				return char.class;
			case JavaParserConstants.BYTE:
				return byte.class;
			case JavaParserConstants.SHORT:
				return short.class;
			case JavaParserConstants.INT:
				return int.class;
			case JavaParserConstants.LONG:
				return long.class;
			case JavaParserConstants.FLOAT:
				return float.class;
			case JavaParserConstants.DOUBLE:
				return double.class;
			}
		}

		throw new RuntimeException("bug!");
	}

	public static Class<? extends Object> getClassOrInterfaceType(
			ASTClassOrInterfaceType allocatedType, boolean isArray)
			throws ClassNotFoundException {
		int typeArgToJump = 0;
		Token t = ((SimpleNode) allocatedType).first_token;
		String strType = "";
		String separator = "";
		while (t != ((SimpleNode) allocatedType).last_token.next) {
			if ((allocatedType.jjtGetNumChildren() > 0)
					&& (t == allocatedType.jjtGetChild(typeArgToJump))) {
				t = ((SimpleNode) allocatedType.jjtGetChild(typeArgToJump)).last_token.next;
			}
			strType += separator + t.image;
			separator = ".";
			t = t.next;
		}

		if (strType.indexOf(".") == -1) {
			String qname = CompletionUtils.getImportsVisitor()
					.getClassTypeName(strType);
			if (qname == null) {
				try {
					qname = "java.lang." + strType;
					Class.forName(qname);
					strType = qname;
				} catch (ClassNotFoundException e) {
					strType = null;
				}
			} else {
				strType = qname;
			}
		}
		if (strType != null) {
			if (isArray) {
				return Class.forName("[L" + strType + ";");
			} else {
				return Class.forName(strType);
			}
		} else {
			return null;
		}
	}

	public static Class<? extends Object> getType(ASTType type)
			throws ClassNotFoundException {
		if (type.jjtGetChild(0) instanceof ASTPrimitiveType) {
			return getPrimitiveType((ASTPrimitiveType) type.jjtGetChild(0),
					false);
		} else if (type.jjtGetChild(0) instanceof ASTReferenceType) {
			Node referenceType = type.jjtGetChild(0);
			boolean isArray = false;
			if (type.last_token.kind == JavaParserConstants.RBRACKET) {
				isArray = true;
			}
			Node primitiveType = referenceType.jjtGetChild(0);
			if (primitiveType instanceof ASTPrimitiveType) {
				return getPrimitiveType((ASTPrimitiveType) primitiveType,
						isArray);
			} else {
				return getClassOrInterfaceType(
						(ASTClassOrInterfaceType) primitiveType, isArray);
			}
		}

		throw new RuntimeException("bug!");
	}

	public static void showTree(Node node, String prefix) {
		System.out.println(prefix + node + ": "
				+ CompletionUtils.getNodeUtils().getText(node));
		for (int i = 0; i < node.jjtGetNumChildren(); i++) {
			showTree(node.jjtGetChild(i), prefix + "   ");
		}
	}

	public static String getClassSimpleName(String clazz) {
		String[] parts = clazz.split("\\.");

		return parts[parts.length - 1];
	}

	public static String getClassPackage(String qName) {
		return qName.substring(0, qName.lastIndexOf('.'));
	}

}
