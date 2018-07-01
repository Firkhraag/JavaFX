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
import org.spbu.histology.util.Symmetry;

@ActionID(
        category = "Edit",
        id = "org.spbu.histology.menu.SymmetryAction"
)
@ActionRegistration(
        displayName = "#CTL_SymmetryAction"
)
@ActionReference(path = "Menu/Tools", position = 6)
@Messages("CTL_SymmetryAction=Найти симметричную точку")
public final class SymmetryAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        Platform.runLater(() -> Symmetry.display());
    }
}
