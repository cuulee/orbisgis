package org.urbsat.landcoverIndicators.function;

import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionValidator;
import org.gdms.sql.function.WarningException;

import com.vividsolutions.jts.geom.Geometry;

public class Compacity implements Function {
	public Function cloneFunction() {
		return new Compacity();
	}

	public Value evaluate(Value[] args) throws FunctionException,
			WarningException {
		FunctionValidator.failIfBadNumberOfArguments(this, args, 1);
		FunctionValidator.warnIfNull(args[0]);
		FunctionValidator.warnIfNotOfType(args[0], Type.GEOMETRY);
		FunctionValidator.warnIfGeometryNotValid(args[0]);

		final Geometry geomBuild = args[0].getAsGeometry();
		final double sBuild = geomBuild.getArea();
		final double pBuild = geomBuild.getLength();
		// final double ratioBuild = sBuild / pBuild;

		final double correspondingCircleRadius = Math.sqrt(sBuild / Math.PI);
		// final double sCircle = sBuild;
		final double pCircle = 2 * Math.PI * correspondingCircleRadius;
		// final double ratioCircle = sCircle / pCircle;

		// return ValueFactory.createValue(ratioCircle / ratioBuild);
		return ValueFactory.createValue(pBuild / pCircle);
	}

	public String getDescription() {
		return "Calculate the compacity of each building's geometry.";
	}

	public String getName() {
		return "Compacity";
	}

	public int getType(int[] paramTypes) {
		return Type.DOUBLE;
	}

	public boolean isAggregate() {
		return false;
	}

	public String getSqlOrder() {
		return "select Compacity(the_geom) from myBuildingsTable;";
	}
}