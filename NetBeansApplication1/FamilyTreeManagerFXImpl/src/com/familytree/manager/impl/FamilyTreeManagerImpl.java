package com.familytree.manager.impl;

import com.familytree.model.FamilyTreeManager;
import com.familytree.model.Person;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = FamilyTreeManager.class)
public class FamilyTreeManagerImpl implements FamilyTreeManager {

    private final ObservableMap<Long, Person> observableMap = 
            FXCollections.observableMap(new ConcurrentHashMap<Long, Person>());
    
    @Override
    public void addListener(MapChangeListener<? super Long, ? super Person> m1) {
        observableMap.addListener(m1);
    }

    @Override
    public void removeListener(MapChangeListener<? super Long, ? super Person> m1) {
        observableMap.removeListener(m1);
    }

    @Override
    public void addListener(InvalidationListener i1) {
        observableMap.addListener(i1);
    }

    @Override
    public void removeListener(InvalidationListener i1) {
        observableMap.removeListener(i1);
    }

    @Override
    public void addPerson(Person p) {
        Person person = new Person(p);
        observableMap.put(person.getId(), person);
    }

    @Override
    public void updatePerson(Person p) {
        Person person = new Person(p);
        observableMap.put(person.getId(), person);
    }

    @Override
    public void deletePerson(Person p) {
        observableMap.remove(p.getId());
    }

    @Override
    public List<Person> getAllPeople() {
        List<Person> copyList = new ArrayList<>();
        observableMap.values().stream().forEach(p ->
                copyList.add(new Person(p)));
        return copyList;
    }
    
}
