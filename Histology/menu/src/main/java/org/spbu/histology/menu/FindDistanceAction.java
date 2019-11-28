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
import org.spbu.histology.util.FindDistance;

@ActionID(
        category = "Edit",
        id = "org.spbu.histology.menu.FindDistanceAction"
)
@ActionRegistration(
        displayName = "#CTL_FindDistanceAction"
)
@ActionReference(path = "Menu/Tools", position = 25)
@Messages("CTL_FindDistanceAction=Найти расстояние")
public final class FindDistanceAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        Platform.runLater(() -> FindDistance.display());
    }
}
