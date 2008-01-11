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

import java.awt.Component;
import java.net.URL;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.sif.CRFlowLayout;
import org.sif.CarriageReturn;
import org.sif.SQLUIPanel;

public class First /* extends AbstractUIPanel */implements SQLUIPanel {
	private final static int LENGTH = 15;
	private FirstJPanel firstJPanel;

	public Component getComponent() {
		if (null == firstJPanel) {
			firstJPanel = new FirstJPanel();
		}
		return firstJPanel;
	}

	public String getTitle() {
		return "Connect to database";
	}

	public String validateInput() {
		// TODO Auto-generated method stub
		return null;
	}

	private class FirstJPanel extends JPanel {
		JComboBox dbType;
		JTextField host;
		JTextField port;
		JTextField dbName;
		JTextField user;
		JPasswordField password;

		FirstJPanel() {
			// dbType = new JTextField("jdbc:postgresql", LENGTH);
			dbType = new JComboBox(
					new String[] { "jdbc:postgresql", "jdbc:h2" });
			host = new JTextField("127.0.0.1", LENGTH);
			port = new JTextField("5432", LENGTH);
			dbName = new JTextField("", LENGTH);
			user = new JTextField("postgres", LENGTH);
			password = new JPasswordField("", LENGTH);

			JPanel textPanel = new JPanel();
			JPanel labelPanel = new JPanel();
			textPanel.setLayout(new CRFlowLayout());
			labelPanel.setLayout(new CRFlowLayout());

			textPanel.add(dbType);
			textPanel.add(new CarriageReturn());
			labelPanel.add(new JLabel("dbType"));
			labelPanel.add(new CarriageReturn());

			textPanel.add(host);
			textPanel.add(new CarriageReturn());
			labelPanel.add(new JLabel("host"));
			labelPanel.add(new CarriageReturn());

			textPanel.add(port);
			textPanel.add(new CarriageReturn());
			labelPanel.add(new JLabel("port"));
			labelPanel.add(new CarriageReturn());

			textPanel.add(dbName);
			textPanel.add(new CarriageReturn());
			labelPanel.add(new JLabel("dbName"));
			labelPanel.add(new CarriageReturn());

			textPanel.add(user);
			textPanel.add(new CarriageReturn());
			labelPanel.add(new JLabel("user"));
			labelPanel.add(new CarriageReturn());

			textPanel.add(password);
			textPanel.add(new CarriageReturn());
			labelPanel.add(new JLabel("password"));
			labelPanel.add(new CarriageReturn());

			add(labelPanel);
			add(textPanel);
		}
	}

	public String getValue(final String fieldName) {
		if (fieldName.equals("dbType")) {
			return firstJPanel.dbType.getSelectedItem().toString();
		} else if (fieldName.equals("host")) {
			return firstJPanel.host.getText();
		} else if (fieldName.equals("port")) {
			return firstJPanel.port.getText();
		} else if (fieldName.equals("dbName")) {
			return firstJPanel.dbName.getText();
		} else if (fieldName.equals("user")) {
			return firstJPanel.user.getText();
		} else if (fieldName.equals("password")) {
			return new String(firstJPanel.password.getPassword());
		}
		return null;
	}

	public String[] getErrorMessages() {
		return new String[] { "dbType must be jdbc:h2 or jdbc:postgresql !" };
	}

	public String[] getFieldNames() {
		return new String[] { "dbType", "host", "port", "dbName", "user",
				"password" };
	}

	public int[] getFieldTypes() {
		return new int[] { STRING, STRING, STRING, STRING, STRING, STRING };
	}

	public String getId() {
		return "org.orbisgis.geocatalog.resources.db.First";
	}

	public String[] getValidationExpressions() {
		return new String[] { "(dbType LIKE 'jdbc:h2') or (dbType LIKE 'jdbc:postgresql')" };
	}

	public String[] getValues() {
		return new String[] { firstJPanel.dbType.getSelectedItem().toString(),
				firstJPanel.host.getText(), firstJPanel.port.getText(),
				firstJPanel.dbName.getText(), firstJPanel.user.getText(),
				new String(firstJPanel.password.getPassword()) };
	}

	public void setValue(String fieldName, String fieldValue) {
		if (fieldName.equals("dbType")) {
			firstJPanel.dbType.setSelectedItem(fieldValue);
		} else if (fieldName.equals("host")) {
			firstJPanel.host.setText(fieldValue);
		} else if (fieldName.equals("port")) {
			firstJPanel.port.setText(fieldValue);
		} else if (fieldName.equals("dbName")) {
			firstJPanel.dbName.setText(fieldValue);
		} else if (fieldName.equals("user")) {
			firstJPanel.user.setText(fieldValue);
		} else if (fieldName.equals("password")) {
			firstJPanel.password.setText(fieldValue);
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

	public String initialize() {
		// TODO Auto-generated method stub
		return null;
	}

	public String postProcess() {
		// TODO Auto-generated method stub
		return null;
	}
}