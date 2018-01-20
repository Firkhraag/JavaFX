package com.familytree.personeditor;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import com.familytree.model.FamilyTreeManager;
import com.familytree.model.Person;
import java.util.Collection;
import org.openide.LifecycleManager;
import org.openide.util.Lookup;

public class PersonFXEditorController implements Initializable {
    
    private FamilyTreeManager ftm = null;
    private Person thePerson;
    private BooleanProperty enableUpdateProperty = 
            new SimpleBooleanProperty(this, "enableUpdate", false);
    private boolean changeOK = false;
    
    @FXML
    private TextField firstnameField;
    
    @FXML
    private TextField middlenameField;
    
    @FXML
    private TextField lastnameField;
    
    @FXML
    private TextField suffixField;
    
    @FXML
    private GridPane gridPane;
    
    @FXML
    private RadioButton maleRadio;
    
    @FXML
    private RadioButton femaleRadio;
    
    @FXML
    private RadioButton unknownRadio;
    
    @FXML
    private HBox buttonsBox;
    
    @FXML
    private VBox bottomVBox;
    
    @FXML
    private TextArea notesArea;
    
    @FXML
    private Button updateButton;
    
    private ObjectBinding<Person.Gender> genderBinding;
 
    public void doUpdate(Collection<? extends Person> people) {
        enableUpdateProperty.set(false);
        changeOK = false;
        if (people.isEmpty()) {
            clearForm();
            return;
        }
        thePerson = people.iterator().next();
        configureEditPanelBindings(thePerson);
        if (thePerson.getGender().equals(Person.Gender.MALE))
            maleRadio.setSelected(true);
        else if (thePerson.getGender().equals(Person.Gender.FEMALE))
            femaleRadio.setSelected(true);
        else
            unknownRadio.setSelected(true);
        thePerson.genderProperty().bind(genderBinding);
        changeOK = true;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {       
        ftm = Lookup.getDefault().lookup(FamilyTreeManager.class);
        if (ftm == null) {
            LifecycleManager.getDefault().exit();
        }
        setGaps();
        
        genderBinding = new ObjectBinding<Person.Gender>() {
            {
                super.bind(maleRadio.selectedProperty(), 
                        femaleRadio.selectedProperty(), 
                        unknownRadio.selectedProperty());
            }

            @Override
            protected Person.Gender computeValue() {
                if (maleRadio.isSelected())
                    return Person.Gender.MALE;
                else if (femaleRadio.isSelected())
                    return Person.Gender.FEMALE;
                else
                    return Person.Gender.UNKNOWN;
            }
        };
        
        updateButton.disableProperty().bind(enableUpdateProperty.not());
    }  
    
    private void clearForm() {
        firstnameField.setText("");
        middlenameField.setText("");
        lastnameField.setText("");
        suffixField.setText("");
        maleRadio.setSelected(false);
        femaleRadio.setSelected(false);
        unknownRadio.setSelected(false);
        notesArea.setText("");
        enableUpdateProperty.set(false);
    }
    
    private void configureEditPanelBindings(Person p) {
        firstnameField.textProperty().bindBidirectional(p.firstnameProperty());
        middlenameField.textProperty().bindBidirectional(p.middlenameProperty());
        lastnameField.textProperty().bindBidirectional(p.lastnameProperty());
        suffixField.textProperty().bindBidirectional(p.suffixProperty());
        notesArea.textProperty().bindBidirectional(p.notesProperty());
    }
    
    private void setGaps() {
        buttonsBox.setPadding(new Insets(10, 10, 10, 20));
        bottomVBox.setPadding(new Insets(10, 10, 10, 20));
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10, 20, 20, 20));
    }
    
    @FXML
    private void updateAction(ActionEvent event) {
        enableUpdateProperty.set(false);
        ftm.updatePerson(thePerson);
    }
    
    @FXML
    private void handleKeyAction(KeyEvent ke) {
        if(changeOK) {
            enableUpdateProperty.set(true);
        }
    }
    
    @FXML
    private void genderSelectionAction() {
        if(changeOK) {
            enableUpdateProperty.set(true);
        }
    }
    
}
