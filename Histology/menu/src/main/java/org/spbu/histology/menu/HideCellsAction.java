/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spbu.histology.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import org.openide.LifecycleManager;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.spbu.histology.model.CrossSectionVisualization;
import org.spbu.histology.model.HideCells;
import org.spbu.histology.model.HistionManager;

@ActionID(
        category = "Edit",
        id = "org.spbu.histology.menu.HideCellsAction"
)
@ActionRegistration(
        displayName = "#CTL_HideCellsAction"
)
@ActionReference(path = "Menu/View", position = 81)
@Messages("CTL_HideCellsAction=Скрыть все клетки")
public final class HideCellsAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {

        Platform.runLater(() -> {

            HistionManager hm = Lookup.getDefault().lookup(HistionManager.class);
            if (hm == null) {
                LifecycleManager.getDefault().exit();
            }

            hm.getAllHistions().forEach(h -> {
                h.getItems().forEach(c -> {
                    if (c.getShow()) {
                        if (!HideCells.getCellIdToHideList().contains(c.getId())) {
                            HideCells.addCellIdToHide(c.getId());
                        }
                        CrossSectionVisualization.getPolygonMap().
                                get(c.getId()).forEach(p -> {
                            p.setFill(Color.WHITE);
                        });
                    }
                });
            });
        });
    }
}
