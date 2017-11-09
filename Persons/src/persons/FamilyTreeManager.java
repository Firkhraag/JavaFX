package persons;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

public class FamilyTreeManager {
    
    private final ObservableMap<Long, Person> observableMap = 
            FXCollections.observableHashMap();
    
    public static FamilyTreeManager instance = null;
    
    protected FamilyTreeManager() {
        
    }
    
    public static FamilyTreeManager getInstance() {
        if(instance == null) {
            instance = new FamilyTreeManager();
        }
        return instance;
    }
    
    public void addListener(
            MapChangeListener<? super Long, ? super Person> m1) {
        observableMap.addListener(m1);
    }
    
    public void removeListener(
            MapChangeListener<? super Long, ? super Person> m1) {
        observableMap.removeListener(m1);
    }
    
    public void addListener(InvalidationListener i1) {
        observableMap.addListener(i1);
    }
    
    public void removeListener(InvalidationListener i1) {
        observableMap.removeListener(i1);
    }
    
    public void addPerson(Person p) {
        Person person = new Person(p);
        observableMap.put(person.getId(), person);
    }
    
    public void updatePerson(Person p) {
        Person person = new Person(p);
        observableMap.put(person.getId(), person);
    }
    
    public void deletePerson(Person p) {
        observableMap.remove(p.getId());
    }
    
    public List<Person> getAllPeople() {
        List<Person> copyList = new ArrayList<>();
        observableMap.values().stream().forEach(p ->
                copyList.add(new Person(p)));
        return copyList;
    }
    
}
