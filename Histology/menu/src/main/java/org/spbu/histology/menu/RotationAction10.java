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
        id = "org.spbu.histology.menu.RotationAction10"
)
@ActionRegistration(
        displayName = "#CTL_RotationAction10"
)
@ActionReference(path = "Menu/View", position = 9)
@Messages("CTL_RotationAction10=Модель X: 0 Y: 0 Z: 180")
public final class RotationAction10 implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        if (ChosenMenuItem.getMenuItem() == 10) {
            ChosenMenuItem.setMenuItem(0);
        }
        ChosenMenuItem.setMenuItem(10);
    }
}
