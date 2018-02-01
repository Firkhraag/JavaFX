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
        id = "com.toolbar.CameraLookAction"
)
@ActionRegistration(
        iconBase = "com/toolbar/eye.png",
        displayName = "#CTL_CameraLookAction"
)
@ActionReference(path = "Toolbars/File", position = 350)
@Messages("CTL_CameraLookAction=Camera look")
public final class CameraLookAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        ChosenTool.setToolNumber(1);
    }
}
