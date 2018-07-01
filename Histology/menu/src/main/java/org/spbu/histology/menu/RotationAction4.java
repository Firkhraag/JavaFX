/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spbu.histology.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Edit",
        id = "org.spbu.histology.menu.RotationAction4"
)
@ActionRegistration(
        displayName = "#CTL_RotationAction4"
)
@ActionReference(path = "Menu/View", position = 3)
@Messages("CTL_RotationAction4=Модель X: 180 Y: 0 Z: 0")
public final class RotationAction4 implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        if (ChosenMenuItem.getMenuItem() == 4) {
            ChosenMenuItem.setMenuItem(0);
        }
        ChosenMenuItem.setMenuItem(4);
    }
}
