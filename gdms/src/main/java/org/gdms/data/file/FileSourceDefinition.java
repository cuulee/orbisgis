/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 * 
 *  Team leader Erwan BOCHER, scientific researcher,
 * 
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
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
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.gdms.data.file;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gdms.data.AbstractDataSourceDefinition;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceDefinition;
import org.gdms.driver.DriverException;
import org.gdms.driver.DriverUtilities;
import org.gdms.driver.FileDriver;
import org.gdms.driver.FileReadWriteDriver;
import org.gdms.driver.ReadOnlyDriver;
import org.gdms.driver.gdms.GdmsDriver;
import org.gdms.source.directory.DefinitionType;
import org.gdms.source.directory.FileDefinitionType;
import org.orbisgis.progress.IProgressMonitor;
import org.orbisgis.utils.I18N;

/**
 * Definition of file sources
 * 
 * 
 */
public class FileSourceDefinition extends AbstractDataSourceDefinition {

        public File file;

        public FileSourceDefinition(File file) {
                this.file = file;
        }

        public FileSourceDefinition(String fileName) {
                this.file = new File(fileName);
        }

        public DataSource createDataSource(String tableName, IProgressMonitor pm)
                throws DataSourceCreationException {
                if (!file.exists()) {
                        throw new DataSourceCreationException(file + " "
                                + I18N.getText("gdms.datasource.error.noexits"));
                }
                ((ReadOnlyDriver) getDriver()).setDataSourceFactory(getDataSourceFactory());

                FileDataSourceAdapter ds = new FileDataSourceAdapter(
                        getSource(tableName), file, (FileDriver) getDriver(), true);
                return ds;
        }

        protected ReadOnlyDriver getDriverInstance() {
                return DriverUtilities.getDriver(getDataSourceFactory().getSourceManager().getDriverManager(), file);
        }

        public File getFile() {
                return file;
        }

        public void createDataSource(DataSource contents, IProgressMonitor pm)
                throws DriverException {
                FileReadWriteDriver d = (FileReadWriteDriver) getDriver();
                d.setDataSourceFactory(getDataSourceFactory());
                contents.open();
                boolean fileExisted = file.exists();
                try {
                        d.writeFile(file, contents, pm);
                } catch (DriverException e) {
                        contents.close();
                        // keep the filesystem in the same state as before the call
                        if (!fileExisted) {
                                file.delete();
                        }
                        throw e;
                }
                contents.close();
        }

        public DefinitionType getDefinition() {
                FileDefinitionType ret = new FileDefinitionType();
                ret.setPath(file.getAbsolutePath());
                return ret;
        }

        public static DataSourceDefinition createFromXML(
                FileDefinitionType definitionType) {
                return new FileSourceDefinition(definitionType.getPath());
        }

        @Override
        public String calculateChecksum(DataSource open) throws DriverException {
                long lastModified = file.lastModified();
                return Long.toString(lastModified);
        }

        @Override
        public boolean equals(DataSourceDefinition obj) {
                if (obj instanceof FileSourceDefinition) {
                        FileSourceDefinition dsd = (FileSourceDefinition) obj;
                        if (file.equals(dsd.file)) {
                                return true;
                        } else {
                                return false;
                        }
                } else {
                        return false;
                }
        }

        @Override
        public int getType() {
                try {
                        if (getDriver().getTypeName().equals("GDMS") && getDriver().getMetadata() == null) {
                                GdmsDriver d = (GdmsDriver) getDriver();
                                d.open(file);
                                int type = d.getType();
                                d.close();
                                return type;
                        }

                } catch (DriverException ex) {
                }
                return super.getType();
        }
}
