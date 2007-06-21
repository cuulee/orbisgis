package org.urbsat;

import org.gdms.sql.customQuery.QueryManager;
import org.urbsat.custom.CreateGrid;
import org.urbsat.custom.Density;

public class Register {
	static {
		QueryManager.registerQuery(new CreateGrid());
		QueryManager.registerQuery(new Density());
	}
}