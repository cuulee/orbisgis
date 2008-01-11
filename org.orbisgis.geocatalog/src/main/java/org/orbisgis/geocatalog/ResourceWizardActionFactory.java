/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geomatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
/**
 *
 */
package org.orbisgis.geocatalog;

import org.orbisgis.core.actions.IAction;
import org.orbisgis.core.actions.IActionFactory;
import org.orbisgis.core.actions.ISelectableAction;
import org.orbisgis.geocatalog.resources.EPResourceWizardHelper;
import org.orbisgis.geocatalog.resources.IResource;

final class ResourceWizardActionFactory implements IActionFactory {
	/**
	 *
	 */
	private final Catalog catalog;

	/**
	 * @param catalog
	 */
	ResourceWizardActionFactory(Catalog catalog) {
		this.catalog = catalog;
	}

	private final class ResourceWizardAction implements IAction {
		private String wizardId;

		public ResourceWizardAction(Object action) {
			this.wizardId = (String) action;
		}

		public boolean isVisible() {
			return true;
		}

		public boolean isEnabled() {
			return true;
		}

		public void actionPerformed() {
			IResource[] resource = ResourceWizardActionFactory.this.catalog
					.getSelectedResources();
			if (resource.length == 0) {
				EPResourceWizardHelper.runWizard(catalog, wizardId, null);
			} else {
				EPResourceWizardHelper
						.runWizard(catalog, wizardId, resource[0]);
			}
		}
	}

	public IAction getAction(Object action) {
		return new ResourceWizardAction(action);
	}

	public ISelectableAction getSelectableAction(Object action) {
		throw new RuntimeException("bug. Wizard actions are not selectable");
	}
}