package com.familytree.personviewer;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import com.familytree.model.FamilyTreeManager;
import com.familytree.model.Person;
import java.util.Collections;
import javafx.beans.value.ChangeListener;
import org.openide.LifecycleManager;
import org.openide.util.Lookup;
import org.openide.util.lookup.InstanceContent;

public class PersonFXViewerController implements Initializable {
    
    private FamilyTreeManager ftm = null;
    private Person selectedPerson = null;
    private final InstanceContent instanceContent = new InstanceContent();
    
    @FXML
    private TreeView<Person> personTreeView;
    
    private final MapChangeListener<Long, Person> familyTreeListener =
            (change) -> {
                if(change.getValueAdded() != null) {
                    for(TreeItem<Person> node : 
                            personTreeView.getRoot().getChildren())
                        if(change.getKey().equals(node.getValue().getId())) {
                            node.setValue(change.getValueAdded());
                            return;
                        }
                }               
            };
    
    private final ChangeListener<TreeItem<Person>> treeSelectionListener =
            (v, oldValue, newValue) -> {
                    TreeItem<Person> treeItem = newValue;
                    if (treeItem == null || treeItem.equals(personTreeView.getRoot())) {
                        instanceContent.remove(selectedPerson);
                        return;
                    }
                    selectedPerson = new Person(treeItem.getValue());
                    instanceContent.set(Collections.singleton(selectedPerson), null);
            };   
 
    public InstanceContent getInstanceContent() {
        return instanceContent;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {  
        ftm = Lookup.getDefault().lookup(FamilyTreeManager.class);
        if (ftm == null) {
            LifecycleManager.getDefault().exit();
        }
                
        buildData();
        loadTreeItems();

        ftm.addListener(familyTreeListener);
        personTreeView.getSelectionModel().selectedItemProperty().addListener(treeSelectionListener);
    }  

    private void loadTreeItems() {
        TreeItem<Person> root = new TreeItem<>(
                new Person("People", "", Person.Gender.UNKNOWN));
        root.setExpanded(true);
        ftm.getAllPeople().forEach(p -> root.getChildren()
                .add(new TreeItem<>(p)));
        personTreeView.setRoot(root);
    }
    
    private void buildData() {
        ftm.addPerson(new Person("Homer", "Simpson", Person.Gender.MALE));
        ftm.addPerson(new Person("Marge", "Simpson", Person.Gender.FEMALE));
        ftm.addPerson(new Person("Bart", "Simpson", Person.Gender.MALE));
        ftm.addPerson(new Person("Lisa", "Simpson", Person.Gender.FEMALE));
        ftm.addPerson(new Person("Maggie", "Simpson", Person.Gender.FEMALE));
    }
    
}
