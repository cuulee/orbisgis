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
package org.orbisgis.mapeditor.map.mapsManager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.sif.components.fstree.FileTree;
import org.orbisgis.sif.components.fstree.FileTreeModel;
import org.orbisgis.sif.components.fstree.TreeNodeFileFactoryManager;
import org.orbisgis.mapeditor.map.mapsManager.jobs.ReadStoredMap;
import org.orbisgis.sif.edition.EditorManager;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * A title and a tree that show local and remote map contexts
 * @author Nicolas Fortin
 */
public class MapsManager extends JPanel {
        // Minimal tree size is incremented by this emptySpace
        private static final long serialVersionUID = 1L;
        private static final I18n I18N = I18nFactory.getI18n(MapsManager.class);
        private FileTree tree;
        private MutableTreeNode rootNode = new DefaultMutableTreeNode();
        private TreeNodeRemoteRoot rootRemote;
        private JScrollPane scrollPane;
        private File loadedMap;
        private TreeNodeLocalRoot rootFolder;
        private DataManager dataManager;
        private EditorManager editorManager;

        // Store all the compatible map context
        
        private AtomicBoolean initialized = new AtomicBoolean(false);
        /**
         * Default constructor
         */
        public MapsManager(String mapContextPath, DataManager dataManager, EditorManager editorManager) {
                super(new BorderLayout());
                this.editorManager = editorManager;
                this.dataManager = dataManager;
                DefaultTreeModel treeModel = new FileTreeModel(rootNode, true);
                treeModel.setAsksAllowsChildren(true);
                // Add the tree in the panel                
                tree = new FileTree(treeModel);
                tree.setEditable(true);
                tree.setShowsRootHandles(true);
                // Add the root folder
                File rootFolderPath = new File(mapContextPath);
                if(!rootFolderPath.exists()) {
                    rootFolderPath.mkdirs();
                }
                TreeNodeDiskFolder workspaceFolder = new TreeNodeDiskFolder(rootFolderPath,tree);
                workspaceFolder.setLabel(I18N.tr("default"));
                rootFolder = new TreeNodeLocalRoot(tree);
                rootRemote = new TreeNodeRemoteRoot(dataManager, new File(mapContextPath));
                initInternalFactories(); // Init file readers
                treeModel.insertNodeInto(rootFolder, rootNode, rootNode.getChildCount());
                treeModel.insertNodeInto(workspaceFolder, rootFolder, rootFolder.getChildCount());
                treeModel.insertNodeInto(rootRemote, rootNode, rootNode.getChildCount());
                tree.setRootVisible(false);
                scrollPane = new JScrollPane(tree);
                JLabel title = new JLabel(I18N.tr("Maps manager"));
                // Disable mouse event propagation on this label
                title.addMouseListener(new MouseAdapter(){}); 
                add(title,BorderLayout.NORTH);
                add(scrollPane,BorderLayout.CENTER);
                setBorder(BorderFactory.createEtchedBorder());
        }

        /**
         * The server list will keep this instance updated
         * @param mapsManagerPersistence
         */
        public void setMapsManagerPersistence(MapsManagerPersistenceImpl mapsManagerPersistence) {
            rootRemote.setMapsManagerPersistence(mapsManagerPersistence);
            rootFolder.setMapsManagerPersistence(mapsManagerPersistence);
        }

        /**
         * Used by the UI to convert a File into a MapElement
         * @return The Map file factory manager
         */
        public TreeNodeFileFactoryManager getFactoryManager() {
                return tree;
        }
        /**
         * Update the shown elements in the disk tree
         */
        public void updateDiskTree() {
                rootFolder.updateTree();
                applyLoadedMapHint();
        }
        private List<TreeLeafMapElement> getAllMapElements(TreeNode parentNode) {
                List<TreeLeafMapElement> mapElements = new ArrayList<TreeLeafMapElement>();
                if(!parentNode.isLeaf()) {
                        for(int childIndex=0; childIndex < parentNode.getChildCount(); childIndex++) {
                                TreeNode nodeElement = parentNode.getChildAt(childIndex);
                                if(nodeElement instanceof TreeLeafMapElement) {
                                        mapElements.add((TreeLeafMapElement)nodeElement);
                                } else {
                                        mapElements.addAll(getAllMapElements(nodeElement));
                                }
                        }
                }                
                return mapElements;
        }
         
        @Override
        public void setVisible(boolean visible) {
                super.setVisible(visible);
                if(visible && !initialized.getAndSet(true)) {
                        // Set a listener to the root folder
                        rootFolder.updateTree(); //Read the file system tree
                        // Expand Local and remote nodes
                        tree.expandPath(new TreePath(new Object[] {rootNode,rootFolder}));  
                        tree.expandPath(new TreePath(new Object[] {rootNode,rootRemote}));  
                        updateMapsTitle();
                        // Apply loaded map property on map nodes
                        applyLoadedMapHint();
                }
        }
        
        private void updateMapsTitle() {
                // Fetch all maps to find their titles
                new ReadStoredMap(getAllMapElements(rootFolder)).execute();
        }
        
       /**
        *  Load built-ins map factory
        */
        private void initInternalFactories() {
                tree.addFactory("ows",new TreeNodeOwsMapContextFactory(dataManager, editorManager));
        }
        /**
         * 
         * @return The internal tree
         */
        public JTree getTree() {
                return tree;
        }

        /**
         * The map manager will read and update the map server list
         * @param mapCatalogServers List of Map catalog servers
         */
        public void setServerList(List<String> mapCatalogServers) {
                rootRemote.setServerList(mapCatalogServers);
        }

        /**
         * @return Loaded server list
         */
        public List<String> getServerList() {
                return rootRemote.getServerList();
        }
        /**
         * Update the state of the tree to show to the user a visual hint that a
         * map is currently shown in the MapEditor or not.
         *
         * @param loadedMap Set a visual hint on this file
         
         */
        public void setLoadedMap(File loadedMap) {
                this.loadedMap = loadedMap;
                applyLoadedMapHint();
        }
        private void applyLoadedMapHint() {
                if(loadedMap!=null) {
                        List<TreeLeafMapElement> mapElements = getAllMapElements(rootFolder);
                        for(TreeLeafMapElement mapEl : mapElements) {
                            if(mapEl.getFilePath().equals(loadedMap)) {
                                    mapEl.setLoaded(true);
                            } else {
                                    mapEl.setLoaded(false);
                            }
                        }
                }
        }
        
        /**
         * Compute the best height to show all the items of the JTree 
         * plus the decoration height.
         * @return Height in pixels
         */
        public Dimension getMinimalComponentDimension() {                
                Dimension panel = getPreferredSize();
                Dimension treeDim = tree.getPreferredSize();
                // Get the vertical scrollbar width
                JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
                if(scrollBar!=null && scrollBar.isVisible()) {
                        return new Dimension(panel.width+scrollBar.getWidth(),treeDim.height+getMinimumSize().height);
                } else {
                        return new Dimension(panel.width,treeDim.height+getMinimumSize().height);
                }
        }
}
