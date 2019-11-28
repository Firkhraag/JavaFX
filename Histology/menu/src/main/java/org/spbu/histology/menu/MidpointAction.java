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
import org.spbu.histology.util.FindMidpoint;

@ActionID(
        category = "Edit",
        id = "org.spbu.histology.menu.MidpointAction"
)
@ActionRegistration(
        displayName = "#CTL_MidpointAction"
)
@ActionReference(path = "Menu/Tools", position = 18)
@Messages("CTL_MidpointAction=Найти середину отрезка")
public final class MidpointAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        Platform.runLater(() -> FindMidpoint.display());
    }
}
