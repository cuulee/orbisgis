/**
 * OrbisToolBox is an OrbisGIS plugin dedicated to create and manage processing.
 *
 * OrbisToolBox is distributed under GPL 3 license. It is produced by CNRS <http://www.cnrs.fr/> as part of the
 * MApUCE project, funded by the French Agence Nationale de la Recherche (ANR) under contract ANR-13-VBDU-0004.
 *
 * OrbisToolBox is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * OrbisToolBox is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with OrbisToolBox. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/> or contact directly: info_at_orbisgis.org
 */

package org.orbisgis.orbistoolbox.model;

import java.net.URI;
import java.net.URISyntaxException;


/**
 * Enumeration of the LiteralData type.
 *
 * For more information : http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html
 *
 * @author Sylvain PALOMINOS
 */


public enum DataType {
    STRING("http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#string"),
    INTEGER("http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#integer"),
    BOOLEAN("http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#boolean"),
    DOUBLE("http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#double"),
    FLOAT("http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#float"),
    SHORT("http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#short"),
    BYTE("http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#byte"),
    UNSIGNED_BYTE("http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#unsignedByte"),
    LONG("http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#long"),
    NONE("none");

    /** URI for the data type. */
    private URI uri;

    /**
     * Main constructor.
     * @param uriString String of the URI to the reference of the type.
     */
    DataType(String uriString) {
        try {
            this.uri = new URI(uriString);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the URI of the type.
     * @return The URI of the type.
     */
    public URI getUri() {
        return uri;
    }
}
