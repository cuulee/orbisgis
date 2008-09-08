package org.orbisgis.views.geocognition.actions;

import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.customQuery.QueryManager;
import org.orbisgis.Services;
import org.orbisgis.errorManager.ErrorManager;
import org.orbisgis.geocognition.Geocognition;
import org.orbisgis.geocognition.GeocognitionElement;
import org.orbisgis.geocognition.sql.GeocognitionBuiltInCustomQuery;
import org.orbisgis.geocognition.sql.GeocognitionCustomQueryFactory;
import org.orbisgis.views.geocognition.action.IGeocognitionAction;

public class RegisterBuiltInCustomQuery implements IGeocognitionAction {

	@Override
	public boolean accepts(Geocognition geocog, GeocognitionElement element) {
		if (GeocognitionCustomQueryFactory.BUILT_IN_QUERY_ID.equals(element
				.getTypeId())) {
			String registered = element.getProperties().get(
					GeocognitionBuiltInCustomQuery.REGISTERED);
			if ((registered != null)
					&& registered
							.equals(GeocognitionBuiltInCustomQuery.IS_NOT_REGISTERED)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public boolean acceptsSelectionCount(Geocognition geocog, int selectionCount) {
		return selectionCount > 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void execute(Geocognition geocognition, GeocognitionElement element) {
		if (GeocognitionCustomQueryFactory.BUILT_IN_QUERY_ID.equals(element
				.getTypeId())) {
			Class<? extends CustomQuery> queryClass = (Class<? extends CustomQuery>) element
					.getObject();
			try {
				QueryManager.remove(queryClass.newInstance().getName());
				QueryManager.registerQuery(queryClass);
			} catch (InstantiationException e) {
				Services.getService(ErrorManager.class).error("Bug!", e);
			} catch (IllegalAccessException e) {
				Services.getService(ErrorManager.class).error("Bug!", e);
			}
		}
	}
}
