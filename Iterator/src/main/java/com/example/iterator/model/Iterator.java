package com.example.iterator.model;

public interface Iterator {
    boolean hasNext();
    Object next();  //возвращаем Object вместо Image
    Object preview();
}
