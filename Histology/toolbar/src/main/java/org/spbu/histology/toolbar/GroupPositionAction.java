/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spbu.histology.toolbar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Edit",
        id = "org.spbu.histology.toolbar.GroupPositionAction"
)
@ActionRegistration(
        iconBase = "org/spbu/histology/toolbar/group-transforms.png",
        displayName = "#CTL_GroupPositionAction"
)
@ActionReference(path = "Toolbars/File", position = 371)
@Messages("CTL_GroupPositionAction=Управление моделью")
public final class GroupPositionAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        ChosenTool.setToolNumber(3);
    }
}
