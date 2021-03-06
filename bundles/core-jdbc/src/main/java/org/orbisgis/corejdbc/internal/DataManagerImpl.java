/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.corejdbc.internal;


import org.h2gis.utilities.JDBCUtilities;
import org.h2gis.utilities.TableLocation;
import org.h2gis.utilities.URIUtilities;
import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.corejdbc.DatabaseProgressionListener;
import org.orbisgis.corejdbc.TableEditEvent;
import org.orbisgis.corejdbc.TableEditListener;
import org.orbisgis.corejdbc.ReadRowSet;
import org.orbisgis.corejdbc.ReversibleRowSet;
import org.orbisgis.corejdbc.StateEvent;
import org.orbisgis.commons.utils.FileUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.FilteredRowSet;
import javax.sql.rowset.JdbcRowSet;
import javax.sql.rowset.JoinRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.WebRowSet;
import java.io.File;
import java.net.URI;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the DataManager service.
 * @author Nicolas Fortin
 */
@Component(service = {DataManager.class, RowSetFactory.class})
public class DataManagerImpl implements DataManager {
    private static Logger LOGGER = LoggerFactory.getLogger(DataManagerImpl.class);
    private DataSource dataSource;
    private boolean isH2 = true;
    private boolean isLocalH2Table = true;
    private static final String H2TRIGGER = "org.orbisgis.h2triggers.H2Trigger";

    /** ReversibleRowSet fire row updates to their DataManager  */
    private Map<String, List<TableEditListener>> tableEditionListener = new HashMap<>();
    private Map<StateEvent.DB_STATES, ArrayList<DatabaseProgressionListener>> progressionListenerMap = new HashMap<>();

    @Override
    public CachedRowSet createCachedRowSet() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public FilteredRowSet createFilteredRowSet() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public ReversibleRowSet createReversibleRowSet() throws SQLException {
        return new ReversibleRowSetImpl(dataSource, this);
    }

    @Override
    public ReadRowSet createReadRowSet() throws SQLException {
        return new ReadRowSetImpl(dataSource);
    }

    @Override
    public JdbcRowSet createJdbcRowSet() throws SQLException {
        return createReversibleRowSet();
    }

    @Override
    public JoinRowSet createJoinRowSet() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public WebRowSet createWebRowSet() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    /**
     * @param dataSource Active DataSource
     */
    public DataManagerImpl(DataSource dataSource) throws SQLException {
        setDataSource(dataSource);
    }

    /**
     * Default constructor for OSGi declarative services. Use {@link #setDataSource(javax.sql.DataSource)}
     */
    public DataManagerImpl() {
    }

    @Override
    public void dispose() {

    }

    @Override
    public String findUniqueTableName(String originalTableName) throws SQLException {
        TableLocation tableLocation = TableLocation.parse(originalTableName);
        String tableName = originalTableName;
        int offset = 0;
        while(isTableExists(tableName)) {
            tableName = new TableLocation(tableLocation.getCatalog(), tableLocation.getSchema(),
                    tableLocation.getTable() + "_" + ++offset).toString();
        }
        return tableName;
    }

    @Override
    public String registerDataSource(URI uri) throws SQLException {
        if(!uri.isAbsolute()) {
            // Uri is incomplete, resolve it by using working directory
            uri = new File("./").toURI().resolve(uri);
        }
        if("file".equalsIgnoreCase(uri.getScheme())) {
            File path = new File(uri);
            if(!path.exists()) {
                throw new SQLException("Specified source does not exists");
            }
            String tableName = findUniqueTableName(TableLocation.capsIdentifier(FileUtils.getNameFromURI(uri), isH2));
            try (Connection connection = dataSource.getConnection()) {
                // Find if a linked table use this file path
                DatabaseMetaData meta = connection.getMetaData();
                try(ResultSet tablesRs = meta.getTables(null,null,null,null)) {
                    while(tablesRs.next()) {
                        String remarks = tablesRs.getString("REMARKS");
                        if(remarks!= null && !remarks.isEmpty()) {
                            File filePath = URIUtilities.fileFromString(remarks);
                            try {
                                if(filePath.equals(path) && filePath.exists()) {
                                    return new TableLocation(tablesRs.getString("TABLE_CAT"), tablesRs.getString("TABLE_SCHEM"), tablesRs.getString("TABLE_NAME")).toString();
                                }
                            } catch (Exception ex) {
                                //Ignore, not an URI
                            }
                        }
                    }
                }
                // Table not found, use table link
                // TODO if tcp, use DriverManager
                PreparedStatement st = connection.prepareStatement("CALL FILE_TABLE(?,?)");
                st.setString(1, path.getAbsolutePath());
                st.setString(2, new TableLocation("","",tableName).toString(isH2));
                st.execute();
            }
            return tableName;
        } else if("jdbc".equalsIgnoreCase(uri.getScheme())) {
            // A link to a remote or local database
            try(Connection connection = dataSource.getConnection()) {
                //Replaces the '%20' character by ' ' to manager URI with encoded spaces.
                String uriStr = uri.toString().replace("%20", " ");
                if(uriStr.contains("?")) {
                    String withoutQuery = uriStr.substring(0,uriStr.indexOf("?"));
                    if(connection.getMetaData().getURL().startsWith(withoutQuery)) {
                        // Extract catalog, schema and table name
                        Map<String,String> query = URIUtilities.getQueryKeyValuePairs(new URI(uri.getRawSchemeSpecificPart()));
                        return new TableLocation(query.get("catalog"),query.get("schema"),query.get("table")).toString();
                    }
                }
                // External JDBC connection not supported yet
                throw new SQLException("URI not supported by DataManager:\n"+uri);
            } catch (Exception ex) {
                throw new SQLException("URI not supported by DataManager:\n"+uri);
            }
        } else {
            throw new SQLException("URI not supported by DataManager:\n"+uri);
        }
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

    @Reference
    public void setDataSource(DataSource dataSource) throws SQLException {
        this.dataSource = dataSource;
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData meta = connection.getMetaData();
            isH2 = JDBCUtilities.isH2DataBase(meta);
            isLocalH2Table = connection.getMetaData().getURL().startsWith("jdbc:h2:")
                    && !connection.getMetaData().getURL().startsWith("jdbc:h2:tcp:/");
        }
    }

    public void unsetDataSource(DataSource dataSource) {
        this.dataSource = null;
        dispose();
    }

    @Override
    public boolean isTableExists(String tableName) throws SQLException {
        TableLocation tableLocation = TableLocation.parse(tableName);
        try (Connection connection = dataSource.getConnection();
            ResultSet rs = connection.getMetaData().getTables(tableLocation.getCatalog(), tableLocation.getSchema(), tableLocation.getTable(), null)) {
            return rs.next();
        }
    }

    @Override
    public boolean hasTableEditListener(String tableIdentifier) {
        TableLocation table = TableLocation.parse(tableIdentifier, isH2);
        if("PUBLIC".equals(table.getSchema()) && !tableEditionListener.containsKey(table.toString(true))) {
            // Maybe schema is not given in listener table identifier
            table = new TableLocation("","",table.getTable());
        }
        String parsedTable = table.toString(isH2);
        return tableEditionListener.containsKey(parsedTable);
    }

    @Override
    public void addTableEditListener(String table, TableEditListener listener) {
        addTableEditListener(table, listener, true);
    }

    @Override
    public void addTableEditListener(String table, TableEditListener listener, boolean addTrigger) {
        String parsedTable = TableLocation.parse(table, isH2).toString(isH2);
        List<TableEditListener> listeners = tableEditionListener.get(parsedTable);
        if(listeners == null) {
            listeners = new ArrayList<>();
            tableEditionListener.put(parsedTable, listeners);
        }
        if(!listeners.contains(listener)) {
            listeners.add(listener);
        }
        try(Connection connection = dataSource.getConnection();
            Statement st = connection.createStatement()) {
            // Add trigger
            if(isLocalH2Table && addTrigger) {
                    String triggerName = getH2TriggerName(table);
                    st.execute("CREATE FORCE TRIGGER IF NOT EXISTS "+triggerName+" AFTER INSERT, UPDATE, DELETE ON "+table+" CALL \""+H2TRIGGER+"\"");
            }
        } catch (SQLException ex) {
            listeners.remove(listener);
            LOGGER.debug(ex.getLocalizedMessage(), ex);
        }
    }

    @Override
    public void clearTableEditListener() {
        for(Map.Entry<String,List<TableEditListener>> entry : new HashMap<>(tableEditionListener).entrySet()) {
            for(TableEditListener listener : new ArrayList<>(entry.getValue())) {
                removeTableEditListener(entry.getKey(), listener);
            }
        }
    }

    @Override
    public void removeTableEditListener(String table, TableEditListener listener) {
        String parsedTable = TableLocation.parse(table).toString();
        List<TableEditListener> listeners = tableEditionListener.get(parsedTable);
        if(listeners != null) {
            listeners.remove(listener);
            if(listeners.isEmpty()) {
                // Remove trigger
                String triggerName = getH2TriggerName(table);
                try(Connection connection = dataSource.getConnection();
                    Statement st = connection.createStatement()) {
                    st.execute("DROP TRIGGER IF EXISTS "+triggerName);
                } catch (SQLException ex) {
                    LOGGER.error(ex.getLocalizedMessage(), ex);
                }
                tableEditionListener.remove(parsedTable);
            }
        }
    }
    private static String getH2TriggerName(String table) {
        TableLocation tableIdentifier = TableLocation.parse(table, true);
        return new TableLocation(tableIdentifier.getCatalog(), tableIdentifier.getSchema(),
                "DM_"+tableIdentifier.getTable()).toString(true);
    }
    @Override
    public void fireTableEditHappened(TableEditEvent e) {
        if(e.getSource() != null) {
            TableLocation table;
            if(e.getSource() instanceof ReadRowSet) {
                table = TableLocation.parse(((ReadRowSet) e.getSource()).getTable(), true);
            } else {
                table = TableLocation.parse(e.getSource().toString(), true);
            }
            if("PUBLIC".equals(table.getSchema()) && !tableEditionListener.containsKey(table.toString(true))) {
                // Maybe schema is not given in listener table identifier
                table = new TableLocation("","",table.getTable());
            }
            List<TableEditListener> listeners = tableEditionListener.get(table.toString(true));
            if(listeners != null) {
                for(TableEditListener listener : new ArrayList<>(listeners)) {
                    try {
                        listener.tableChange(e);
                    } catch (Exception ex) {
                        LOGGER.error(ex.getLocalizedMessage(), ex);
                    }
                }
            }
        }
    }

    @Override
    public void addDatabaseProgressionListener(DatabaseProgressionListener listener, StateEvent.DB_STATES state) {
        ArrayList<DatabaseProgressionListener> listenerList = progressionListenerMap.get(state);
        if(listenerList != null) {
            listenerList = new ArrayList<>(listenerList);
        } else {
            listenerList = new ArrayList<>();
        }
        listenerList.add(listener);
        progressionListenerMap.put(state, listenerList);
    }

    @Override
    public void removeDatabaseProgressionListener(DatabaseProgressionListener listener) {
        for(Map.Entry<StateEvent.DB_STATES,ArrayList<DatabaseProgressionListener>> entry : progressionListenerMap.entrySet()) {
            if(entry.getValue().contains(listener)) {
                ArrayList<DatabaseProgressionListener> newList = new ArrayList<>(entry.getValue());
                newList.remove(listener);
                entry.setValue(newList);
            }
        }
    }

    @Override
    public void fireDatabaseProgression(StateEvent event) {
        ArrayList<DatabaseProgressionListener> listenerList = progressionListenerMap.get(event.getStateIdentifier());
        if(listenerList != null) {
            for(DatabaseProgressionListener listener : new ArrayList<>(listenerList)) {
                listener.progressionUpdate(event);
            }
        }
    }
}
