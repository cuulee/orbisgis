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
package org.orbisgis.geocatalog.resources.db;

import java.awt.BorderLayout;
import java.awt.Component;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.gdms.data.db.DBSource;
import org.gdms.driver.DBDriver;
import org.gdms.driver.DriverException;
import org.gdms.driver.TableDescription;
import org.gdms.driver.h2.H2spatialDriver;
import org.gdms.driver.postgresql.PostgreSQLDriver;
import org.sif.UIPanel;

public class Second /* extends AbstractUIPanel */implements UIPanel {
	private final static String spatial_ref_sys = "spatial_ref_sys";
	private final static String geometry_columns = "geometry_columns";
	private First firstPanel;
	private SecondJPanel secondJPanel;
	private List<String> allTablesNames = new ArrayList<String>();
	private DBDriver dBDriver;
	private Connection connection;
	private String dbType;
	private String host;
	private Integer port;
	private String dbName;
	private String user;
	private String password;

	public Second(final First firstPanel) {
		this.firstPanel = firstPanel;
	}

	public Component getComponent() {
		if (null == secondJPanel) {
			secondJPanel = new SecondJPanel();
		}
		return new JScrollPane(secondJPanel);
	}

	public String getTitle() {
		return "Select table(s) name(s)...";
	}

	public String initialize() {
		dbType = firstPanel.getValue("dbType");
		host = firstPanel.getValue("host");
		port = new Integer(firstPanel.getValue("port"));
		dbName = firstPanel.getValue("dbName");
		user = firstPanel.getValue("user");
		password = firstPanel.getValue("password");

		try {
			if (dbType.equals("jdbc:h2")) {
				dBDriver = new H2spatialDriver();
			} else if (dbType.equals("jdbc:postgresql")) {
				dBDriver = new PostgreSQLDriver();
			} else {
				throw new RuntimeException("Unsupported DBType !");
			}
			connection = dBDriver.getConnection(host, port, dbName, user,
					password);
			final TableDescription[] tableDescriptions = dBDriver
					.getTables(connection);

			allTablesNames.clear();
			for (int i = 0; i < tableDescriptions.length; i++) {
				final String tblName = tableDescriptions[i].getName();
				if (!((spatial_ref_sys.equals(tblName) || geometry_columns
						.equals(tblName)))) {
					allTablesNames.add(tblName);
				}
			}
			secondJPanel.jList.setListData(allTablesNames
					.toArray(new String[0]));
			connection.close();
			return null;
		} catch (SQLException e) {
			return e.getMessage();
		} catch (DriverException e) {
			return e.getMessage();
		}
	}

	public String validateInput() {
		if (-1 == secondJPanel.jList.getSelectedIndex()) {
			return "Select at least one table !";
		}
		return null;
	}

	public DBSource[] getSelectedDBSources() {
		final Object[] tablesNames = secondJPanel.jList.getSelectedValues();
		final DBSource[] dBSources = new DBSource[tablesNames.length];
		for (int i = 0; i < tablesNames.length; i++) {
			dBSources[i] = new DBSource(host, port, dbName, user, password,
					tablesNames[i].toString(), dbType);
		}
		return dBSources;
	}

	private class SecondJPanel extends JPanel {
		JList jList;

		SecondJPanel() {
			this.setLayout(new BorderLayout());
			// setPreferredSize(new Dimension(300,300));
			jList = new JList(allTablesNames.toArray(new String[0]));
			jList.setToolTipText("You can select several tables");
			// jList.setVisibleRowCount(15);
			jList.setAutoscrolls(true);
			add(jList, BorderLayout.CENTER);
		}
	}

	public URL getIconURL() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getInfoText() {
		// TODO Auto-generated method stub
		return null;
	}

	public String postProcess() {
		// TODO Auto-generated method stub
		return null;
	}
}