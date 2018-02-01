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
        id = "com.toolbar.HomeAction"
)
@ActionRegistration(
        iconBase = "com/toolbar/home.png.png",
        displayName = "#CTL_HomeAction"
)
@ActionReference(path = "Toolbars/File", position = 300)
@Messages("CTL_HomeAction=Home")
public final class HomeAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        ChosenTool.setToolNumber(0);
    }
}
