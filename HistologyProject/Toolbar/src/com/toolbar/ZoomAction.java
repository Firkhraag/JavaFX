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
        id = "com.toolbar.ZoomAction"
)
@ActionRegistration(
        iconBase = "com/toolbar/magnifying-glass.png",
        displayName = "#CTL_ZoomAction"
)
@ActionReference(path = "Toolbars/File", position = 400)
@Messages("CTL_ZoomAction=Field of view")
public final class ZoomAction implements ActionListener {
    
    @Override
    public void actionPerformed(ActionEvent e) {
        ChosenTool.setToolNumber(3);
    }
}
