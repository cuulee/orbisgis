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
package org.orbisgis.geocatalog.resources;

import java.util.ArrayList;

import javax.swing.Icon;

public class ResourceDecorator implements INode, IResource {

	private ArrayList<INode> children = null;
	private INode parent;
	private IResourceType resource;
	private String name;
	private ResourceTreeModel treeModel;

	ResourceDecorator(String name, IResourceType resource) {
		children = new ArrayList<INode>();
		this.resource = resource;
		this.name = name;
	}

	public void addNode(INode node) {
		addNode(node, children.size());
	}

	public void addNode(INode node, int index) {
		children.add(index, node);
		node.setParent(this);
		getTreeModel().refresh(this);
	}

	public INode getChildAt(int index) {
		return children.get(index);
	}

	public int getChildCount() {
		return children.size();
	}

	public INode[] getChildren() {
		return children.toArray(new INode[0]);
	}

	public INode[] getChildrenRecursively() {
		ArrayList<INode> childList = new ArrayList<INode>();

		for (INode child : children) {
			INode[] subChildList = child.getChildrenRecursively();
			for (INode subChild : subChildList) {
				childList.add(subChild);
			}
			childList.add(child);
		}

		return childList.toArray(new INode[0]);
	}

	public int getIndex(INode node) {
		return children.indexOf(node);
	}

	public INode getParent() {
		return parent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		getTreeModel().refresh(this);
	}

	public INode[] getPath() {
		ArrayList<INode> path = new ArrayList<INode>();
		INode current = this;
		while (current != null) {
			path.add(current);
			current = current.getParent();
		}

		// Now we must reverse the order
		ArrayList<INode> path2 = new ArrayList<INode>();
		int l = path.size();
		for (int i = 0; i < l; i++) {
			path2.add(i, path.get(l - i - 1));
		}

		return path2.toArray(new INode[0]);
	}

	public IResourceType getResourceType() {
		return resource;
	}

	public void removeNode(INode node) {
		children.remove(node);
		node.setParent(null);
		getTreeModel().refresh(this);
	}

	public void setParent(INode node) {
		parent = node;
	}

	public int getIndex(IResource resource) {
		return getIndex((INode) resource);
	}

	public IResource getResourceAt(int index) {
		return (IResource) getChildAt(index);
	}

	public IResource getParentResource() {
		return (IResource) getParent();
	}

	public IResource[] getResourcePath() {
		ArrayList<IResource> path = new ArrayList<IResource>();
		IResource current = this;
		while (current != null) {
			path.add(current);
			current = current.getParentResource();
		}

		// Now we must reverse the order
		ArrayList<IResource> path2 = new ArrayList<IResource>();
		int l = path.size();
		for (int i = 0; i < l; i++) {
			path2.add(i, path.get(l - i - 1));
		}

		return path2.toArray(new IResource[0]);
	}

	private IResource[] toResource(INode[] nodes) {
		IResource[] ret = new IResource[nodes.length];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = (IResource) nodes[i];
		}
		return ret;

	}

	public IResource[] getResources() {
		return toResource(getChildren());
	}

	public IResource[] getResourcesRecursively() {
		return toResource(getChildrenRecursively());
	}

	public void removeResource(IResource resource) throws ResourceTypeException {
		INode node = (INode) resource;
		try {
			getTreeModel().resourceTypeProcess(true);
			(node).getResourceType().removeFromTree(node);
		} finally {
			getTreeModel().resourceTypeProcess(false);
		}
	}

	public void moveTo(IResource dest) throws ResourceTypeException {
		getResourceType().moveResource(this, (INode) dest);
	}

	public void addResource(IResource resource) throws ResourceTypeException {
		INode node = (INode) resource;
		try {
			getTreeModel().resourceTypeProcess(true);
			node.getResourceType().addToTree(this, node);
		} finally {
			getTreeModel().resourceTypeProcess(false);
		}
	}

	public void setResourceName(String name) throws ResourceTypeException {
		try {
			getTreeModel().resourceTypeProcess(true);
			getResourceType().setName(this, name);
		} finally {
			getTreeModel().resourceTypeProcess(false);
		}
	}

	public Icon getIcon(boolean expanded) {
		return getResourceType().getIcon(this, expanded);
	}

	private ResourceTreeModel getTreeModel() {
		if (parent != null) {
			return ((ResourceDecorator) parent).getTreeModel();
		} else {
			return treeModel;
		}
	}

	public void setResource(IResourceType resource) {
		this.resource = resource;
	}

	public void setTreeModel(ResourceTreeModel treeModel) {
		this.treeModel = treeModel;
	}
}
