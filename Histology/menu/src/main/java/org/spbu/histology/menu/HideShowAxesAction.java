package org.spbu.histology.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Edit",
        id = "org.spbu.histology.menu.HideShowAxesAction"
)
@ActionRegistration(
        displayName = "#CTL_HideShowAxesAction"
)
@ActionReference(path = "Menu/View", position = 75, separatorAfter = 87)
@Messages("CTL_HideShowAxesAction=Скрыть/Показать оси")
public final class HideShowAxesAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        if (ChosenMenuItem.getMenuItem() == 11) {
            ChosenMenuItem.setMenuItem(0);
        }
        ChosenMenuItem.setMenuItem(11);
    }
}
