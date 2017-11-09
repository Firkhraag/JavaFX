package persons;

import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.Objects;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public final class Person implements Serializable {
    
    private final long id;
    
    private final StringProperty firstname = 
            new SimpleStringProperty(this, "firstname", "");
    public final StringProperty firstnameProperty() {
        return this.firstname;
    }
    
    private final StringProperty middlename = 
            new SimpleStringProperty(this, "middlename", "");
    public final StringProperty middlenameProperty() {
        return this.middlename;
    }
    
    private final StringProperty lastname = 
            new SimpleStringProperty(this, "lastname", "");
    public final StringProperty lastnameProperty() {
        return this.lastname;
    }
    
    private final StringProperty suffix = 
            new SimpleStringProperty(this, "suffix", "");
    public final StringProperty suffixProperty() {
        return this.suffix;
    }
    
    private final ObjectProperty<Person.Gender> gender = 
            new SimpleObjectProperty();
    public final ObjectProperty<Person.Gender> genderProperty() {
        return this.gender;
    }
    
    private final StringProperty notes = 
            new SimpleStringProperty(this, "notes", "");
    public final StringProperty notesProperty() {
        return this.notes;
    }
    
    private final ReadOnlyStringWrapper fullname = 
            new ReadOnlyStringWrapper(this, "fullname");
    
    public final ReadOnlyStringProperty fullnameProperty() {
            return fullname.getReadOnlyProperty();
    }
    
    private final StringBinding fullNameBinding = new StringBinding() {
        {
            super.bind(firstname, middlename, lastname, suffix);
        }
        
        @Override
        protected String computeValue() {
            StringBuilder sb = new StringBuilder();
        if(!firstname.get().isEmpty())
            sb.append(firstname.get());
        if(!middlename.get().isEmpty())
            sb.append(" ").append(middlename.get());
        if(!lastname.get().isEmpty())
            sb.append(" ").append(lastname.get());
        if(!suffix.get().isEmpty())
            sb.append(" ").append(suffix.get());
        return sb.toString();
        }
    };
    
    private PropertyChangeSupport propertyChangeSupport = null;
    
    private static long count = 0;
    
    public static final String PROP_FIRST = "firstname";
    public static final String PROP_MIDDLE = "middlename";
    public static final String PROP_LAST = "lastname";
    public static final String PROP_SUFFIX = "suffix";
    public static final String PROP_GENDER = "gender";
    public static final String PROP_NOTES = "notes";
    
    public enum Gender {
        MALE, FEMALE, UNKNOWN
    }
    
    public Person() {
        this("", "", Gender.UNKNOWN);
    }
    
    public Person(String firstname, String lastname, 
            Person.Gender gender)
    {
        this.firstname.set(firstname);
        this.middlename.set("");
        this.lastname.set(lastname);
        this.gender.set(gender);
        this.suffix.set("");
        this.notes.set("");
        this.id = count++;
        this.fullname.bind(fullNameBinding);
    }
    
    public Person(Person person) {
        this.firstname.set(person.getFirstname());
        this.middlename.set(person.getMiddlename());
        this.lastname.set(person.getLastname());
        this.gender.set(person.getGender());
        this.suffix.set(person.getSuffix());
        this.notes.set(person.getNotes());
        this.id = person.getId();
        this.fullname.bind(fullNameBinding);
    }

    public long getId() {
        return id;
    }

    public String getFirstname() {
        return this.firstnameProperty().get();
    }

    public void setFirstname(String firstname) {
        this.firstnameProperty().set(firstname);
    }

    public String getMiddlename() {
        return this.middlenameProperty().get();
    }

    public void setMiddlename(String middlename) {
        this.middlenameProperty().set(middlename);
    }

    public String getLastname() {
        return this.lastnameProperty().get();
    }

    public void setLastname(String lastname) {
        this.lastnameProperty().set(lastname);
    }

    public String getSuffix() {
        return this.suffixProperty().get();
    }

    public void setSuffix(String suffix) {
        this.suffixProperty().set(suffix);
    }

    public Person.Gender getGender() {
        return this.genderProperty().get();
    }

    public void setGender(Person.Gender gender) {
        this.genderProperty().set(gender);
    }

    public String getNotes() {
        return this.notesProperty().get();
    }

    public void setNotes(String notes) {
        this.notesProperty().set(notes);
    }
    
    public final String getFullname() {
        return fullname.get();
    }
    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.id)
                + Objects.hashCode(this.fullname.get())
                + Objects.hashCode(this.notes.get())
                + Objects.hashCode(this.gender.get());
        return hash;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj == null || getClass() != obj.getClass())
            return false;
        final Person other = (Person) obj;
        return Objects.equals(this.id, other.id)
                && Objects.equals(this.fullname.get(), other.fullname.get())
                && Objects.equals(this.notes.get(), other.notes.get())
                && Objects.equals(this.gender.get(), other.gender.get());
    }
    
    @Override
    public String toString() {
        return fullname.get();
    }
    
}
