package org.orbisgis.editors.table.actions;

import org.orbisgis.editors.table.TableEditableElement;
import org.orbisgis.editors.table.TableEditor;
import org.orbisgis.editors.table.action.ITableCellAction;

public class DeleteSelection implements ITableCellAction {

	@Override
	public boolean accepts(TableEditableElement element, int rowIndex,
			int columnIndex) {
		return org.orbisgis.editors.table.editorActions.DeleteSelection
				.isEnabled(element);
	}

	@Override
	public void execute(TableEditor tableEditor, TableEditableElement element,
			int rowIndex, int columnIndex) {
		org.orbisgis.editors.table.editorActions.DeleteSelection
				.removeSelection(element);
	}

}
