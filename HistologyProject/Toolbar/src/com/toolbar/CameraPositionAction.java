/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.toolbar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Edit",
        id = "com.toolbar.CameraPositionAction"
)
@ActionRegistration(
        iconBase = "com/toolbar/move.png",
        displayName = "#CTL_CameraPositionAction"
)
@ActionReference(path = "Toolbars/File", position = 375)
@Messages("CTL_CameraPositionAction=Camera position")
public final class CameraPositionAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        ChosenTool.setToolNumber(2);
    }
}