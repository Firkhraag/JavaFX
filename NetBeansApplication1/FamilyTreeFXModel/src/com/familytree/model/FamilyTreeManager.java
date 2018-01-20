package com.familytree.model;

import java.util.List;
import javafx.beans.InvalidationListener;
import javafx.collections.MapChangeListener;

public interface FamilyTreeManager {
    
    public void addListener(MapChangeListener<? super Long, ? super Person> m1);
    
    public void removeListener(MapChangeListener<? super Long, ? super Person> m1);
    
    public void addListener(InvalidationListener i1);
    
    public void removeListener(InvalidationListener i1);
    
    public void addPerson(Person p);
    
    public void updatePerson(Person p);
    
    public void deletePerson(Person p);
    
    public List<Person> getAllPeople();
    
}
