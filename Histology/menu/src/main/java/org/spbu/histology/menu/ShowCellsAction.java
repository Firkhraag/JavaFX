/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spbu.histology.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javafx.application.Platform;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.spbu.histology.model.CrossSectionVisualization;
import org.spbu.histology.model.HideCells;

@ActionID(
        category = "Edit",
        id = "org.spbu.histology.menu.ShowCellsAction"
)
@ActionRegistration(
        displayName = "#CTL_ShowCellsAction"
)
@ActionReference(path = "Menu/View", position = 84)
@Messages("CTL_ShowCellsAction=Показать все клетки")
public final class ShowCellsAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {

        Platform.runLater(() -> {

            HideCells.getCellIdToHideList().clear();
            CrossSectionVisualization.getPolygonMap().forEach((i, arr) -> {
                arr.forEach(p -> {
                    p.setFill(CrossSectionVisualization.getPolygonColorMap().get(i));
                });
            });
        });
    }
}
