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
package org.orbisgis.view.toc.actions.cui.legend.panels;

import org.orbisgis.coremap.renderer.se.fill.SolidFill;
import org.orbisgis.legend.structure.fill.constant.ConstantSolidFill;
import org.orbisgis.legend.structure.fill.constant.ConstantSolidFillLegend;
import org.orbisgis.legend.structure.fill.constant.NullSolidFillLegend;
import org.orbisgis.legend.thematic.constant.IUniqueSymbolArea;
import org.orbisgis.sif.ComponentUtil;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;
import org.orbisgis.view.toc.actions.cui.legend.components.ColorLabel;
import org.orbisgis.view.toc.actions.cui.legend.components.LineOpacitySpinner;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;

/**
 * "Unique Symbol - Area" settings panel.
 *
 * @author Adam Gouge
 */
public class AreaPanel extends AbsOptionalPanel {

    private static final I18n I18N = I18nFactory.getI18n(AreaPanel.class);

    private ConstantSolidFill fillLegendMemory;

    private ColorLabel colorLabel;
    private LineOpacitySpinner fillOpacitySpinner;
    private boolean stateActive = true;

    /**
     * Constructor
     *
     * @param legend       Legend
     * @param preview      Preview
     * @param title        Title
     * @param showCheckBox Draw the Enable checkbox?
     */
    public AreaPanel(IUniqueSymbolArea legend,
                     CanvasSE preview,
                     String title,
                     boolean showCheckBox) {
        super(legend, preview, title, showCheckBox);
        init();
        addComponents();
    }

    @Override
    protected IUniqueSymbolArea getLegend() {
        return (IUniqueSymbolArea) legend;
    }

    @Override
    protected void init() {
        fillLegendMemory = getLegend().getFillLegend();
        if(fillLegendMemory == null || fillLegendMemory instanceof NullSolidFillLegend){
            if(showCheckBox){
                enableCheckBox.setSelected(false);
            }
            stateActive = false;
            fillLegendMemory = new ConstantSolidFillLegend(new SolidFill());
        }
        colorLabel = new ColorLabel(fillLegendMemory, preview);
        fillOpacitySpinner = new LineOpacitySpinner(fillLegendMemory, preview);
    }

    @Override
    public void addComponents() {
        if (showCheckBox) {
            add(enableCheckBox, "align l");
        } else {
            // Just add blank space
            add(Box.createGlue());
        }
        // Color
        add(colorLabel);
        // Opacity
        add(new JLabel(I18N.tr(OPACITY)));
        add(fillOpacitySpinner, "growx");
        setFieldsState(stateActive);
    }

    @Override
    protected void onClickOptionalCheckBox() {
        if (enableCheckBox.isSelected()) {
            getLegend().setFillLegend(fillLegendMemory);
            setFieldsState(true);
        } else {
            // Remember the old configuration.
            fillLegendMemory = getLegend().getFillLegend();
            getLegend().setFillLegend(new NullSolidFillLegend());
            setFieldsState(false);
        }
        preview.imageChanged();
    }

    @Override
    protected void setFieldsState(boolean state) {
        ComponentUtil.setFieldState(state, colorLabel);
        ComponentUtil.setFieldState(state, fillOpacitySpinner);
    }
}
