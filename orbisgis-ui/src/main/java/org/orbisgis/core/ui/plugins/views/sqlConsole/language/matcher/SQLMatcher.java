/**
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
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC, scientific researcher, Fernando GONZALEZ
 * CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
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
 * info@orbisgis.org
 **/
package org.orbisgis.core.ui.plugins.views.sqlConsole.language.matcher;

import java.util.Iterator;
import org.fife.ui.autocomplete.ShorthandCompletion;
import org.orbisgis.core.ui.plugins.views.sqlConsole.language.SQLCompletionProvider;

/**
 * This is a hand written SQL pattern matcher that triggers the correct completion actions.
 * @author Antoine Gourlay
 * @since 4.0
 */
public class SQLMatcher {

        private SQLCompletionProvider pr;
        private SQLLexer lexer;
        private Iterator<String> it;

        /**
         * Creates a new SQLMatcher that will register its completions to the 
         * given SQLCompletionProvider.
         * @param pr
         */
        public SQLMatcher(SQLCompletionProvider pr) {
                this.pr = pr;
        }

        /**
         * Main entry point for matching a SQL String.
         * @param str a string
         */
        public void match(String str) {
                lexer = new SQLLexer(str);
                it = lexer.getTokenIterator();

                // main matching entry point
                matchInit();

        }

        private void matchInit() {

                // no text
                if (!it.hasNext()) {
                        // no text
                        addKeyWords("CREATE", "DROP", "SELECT", "INSERT", "UPDATE", "DELETE", "EXECUTE", "ALTER");
                        return;
                }
                String a = it.next();

                if (a.endsWith(".")) {
                        // field name like 'toto.tata'
                        addFieldsForTable(a);
                        return;
                } else if (a.endsWith(";")) {
                        // end of query: start a new one
                        addKeyWords("CREATE", "DROP", "SELECT", "INSERT", "UPDATE", "DELETE", "EXECUTE", "ALTER");
                        return;
                } else if (a.endsWith(",")) {
                        // comma; we have to look deeper to understand what to do.
                        matchAfterComma();
                        return;
                }

                if ("AS".equalsIgnoreCase(a)) {
                        // as: we have to look deeper to understand what to do.
                        matchAs1();
                } else if ("FROM".equalsIgnoreCase(a)) {
                        // FROM tablename
                        addTables(false);
                        addTableFunctions();
                } else if ("TABLE".equalsIgnoreCase(a)) {
                        // DROP TABLE and others ending in TABLE
                        matchSourceNames1();
                } else if ("SELECT".equalsIgnoreCase(a) || "WHERE".equalsIgnoreCase(a)) {
                        // SELECT table.field
                        addScalarFunctions();
                        addTables(true);
                } else if ("CREATE".equalsIgnoreCase(a)) {
                        // CREATE tata
                        addKeyWords("TABLE", "VIEW", "INDEX", "OR REPLACE VIEW");
                } else if ("DROP".equalsIgnoreCase(a)) {
                        // DROP tata
                        addKeyWords("TABLE", "VIEW", "INDEX");
                } else if ("INDEX".equalsIgnoreCase(a)) {
                        // CREATE INDEX ON tutu(field)
                        addKeyWord("ON");
                } else if ("EXECUTE".equalsIgnoreCase(a) || "CALL".equalsIgnoreCase(a)) {
                        // table function call
                        addExecutorFunctions();
                } else if ("ALTER".equalsIgnoreCase(a)) {
                        // ALTER TABLE toto
                        addKeyWord("TABLE");
                } else if ("IF".equalsIgnoreCase(a)) {
                        // IF EXISTS
                        addKeyWord("EXISTS");
                } else if ("OR".equalsIgnoreCase(a)) {
                        // OR REPLACE
                        addKeyWord("REPLACE");
                } else if ("REPLACE".equalsIgnoreCase(a)) {
                        // OR REPLACE
                        addKeyWord("VIEW");
                } else if ("RENAME".equalsIgnoreCase(a)) {
                        // RENAME toto/TO
                        addKeyWord("TO");
                } else if ("UNION".equalsIgnoreCase(a)) {
                        // UNION SELECT ...
                        addKeyWord("SELECT");
                } else if ("ORDER".equalsIgnoreCase(a)) {
                        // ORDER BY
                        addKeyWord("BY");
                } else if ("TO".equalsIgnoreCase(a) || "BY".equalsIgnoreCase(a)) {
                        // RENAME TO
                        return;
                } else {
                        // anything else
                        matchAfterPossibleId();
                }
        }
        
        /**
         * Decides what to do after "By identifier"
         */
        private void matchIdAfterBy() {
                if (!it.hasNext()) {
                        return;
                }
                String a = it.next();
                
                if ("ORDER".equalsIgnoreCase(a)) {
                        // ORDER BY
                        addKeyWords("ASC", "DESC");
                }
        }

        /**
         * Decides what to do after a (probable) id has been matched.
         */
        private void matchAfterPossibleId() {
                boolean inside = false;
                while (it.hasNext()) {
                        String b = it.next();
                        if (b.contains("(")) {
                                inside = true;
                        }
                        if ("SELECT".equalsIgnoreCase(b)) {
                                if (inside) {
                                        return;
                                }
                                addKeyWord("FROM");
                                return;
                        } else if ("ALTER".equalsIgnoreCase(b)) {
                                if (inside) {
                                        return;
                                }
                                addKeyWords("DROP", "ADD", "RENAME");
                                return;
                        } else if ("CREATE".equalsIgnoreCase(b)) {
                                if (inside) {
                                        return;
                                }
                                addKeyWord("AS");
                                return;
                        } else if ("FROM".equalsIgnoreCase(b)) {
                                addKeyWords("WHERE", "UNION", "ORDER BY", "GROUP BY", "LIMIT", "OFFSET", "FETCH", "HAVING");
                                return;
                        } else if ("WHERE".equalsIgnoreCase(b)) {
                                addKeyWords("UNION", "ORDER BY", "GROUP BY", "LIMIT", "OFFSET", "FETCH", "HAVING");
                                return;
                        } else if ("BY".equalsIgnoreCase(b)) {
                                matchIdAfterBy();
                                return;
                        }
                        
                        if (b.contains(";")) {
                                return;
                        }
                }

        }

        /**
         * Decides what to do after a comma (or id+comma) has been match.
         */
        private void matchAfterComma() {
                while (it.hasNext()) {
                        String a = it.next();
                        if ("FROM".equalsIgnoreCase(a)) {
                                // after from, tables and table functions
                                addTables(false);
                                addTableFunctions();
                                return;
                        } else if ("SELECT".equalsIgnoreCase(a) || "WHERE".equalsIgnoreCase(a)
                                || "SET".equalsIgnoreCase(a)) {
                                // after SELECT, WHERE or the sert part of an update
                                addScalarFunctions();
                                addTables(true);
                                return;
                        } else if ("VALUES".equalsIgnoreCase(a)) {
                                // inside an insert, no completion
                                return;
                        } else if ("BY".equalsIgnoreCase(a)) {
                                // GROUP/ORDER BY, no completion
                                return;
                        }
                }
        }

        private void matchAs1() {
                if (!it.hasNext()) {
                        return;
                }
                it.next();
                if (!it.hasNext()) {
                        return;
                }
                String a = it.next();

                if ("TABLE".equalsIgnoreCase(a) || "VIEW".equalsIgnoreCase(a)) {
                        addKeyWord("SELECT");
                }
        }

        private void matchSourceNames1() {
                if (!it.hasNext()) {
                        return;
                }
                String a = it.next();

                if ("ALTER".equalsIgnoreCase(a)) {
                        // ALTER TABLE
                        addTables(false);
                } else if ("DROP".equalsIgnoreCase(a)) {
                        // DROP TABLE
                        addKeyWord("IF EXISTS");
                        addTables(false);
                }
        }

        private void addFieldsForTable(String tableDot) {
                int par = tableDot.lastIndexOf("(");
                par = par == -1 ? 0 : par + 1;
                pr.addFieldsCompletion(tableDot.substring(par, tableDot.length() - 1));
        }

        private void addScalarFunctions() {
                pr.addFunctionCompletions(false, true, false);
        }

        private void addTableFunctions() {
                pr.addFunctionCompletions(true, false, false);
        }

        private void addExecutorFunctions() {
                pr.addFunctionCompletions(false, false, true);
        }

        private void addTables(boolean withFields) {
                pr.addSourceNamesCompletion(withFields);
        }

        private void addKeyWords(String... k) {
                for (int i = 0; i < k.length; i++) {
                        addKeyWord(k[i]);
                }
        }

        private void addKeyWord(String k) {
                pr.addCompletion(new ShorthandCompletion(pr, k, k + ' '));
        }
}