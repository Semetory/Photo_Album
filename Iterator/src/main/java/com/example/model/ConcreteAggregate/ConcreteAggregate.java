package com.example.model.ConcreteAggregate;

import com.example.iterator.model.Aggregate;
import com.example.iterator.model.Iterator;
import javafx.scene.image.Image;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ConcreteAggregate implements Aggregate {

    private List<String> imagePaths; // список путей к изображениям
    private String filetopic;        // папка для поиска
    private String[] allowedExtensions; // допустимые расширения

    public ConcreteAggregate(String filetopic, String... allowedExtensions) {
        this.filetopic = filetopic;
        this.allowedExtensions = allowedExtensions;
        this.imagePaths = new ArrayList<>();
        scanFolder(new File("src/main/resources/" + filetopic));
    }

    @Override
    public Iterator getIterator() {
        return new ImageIterator();
    }

    /** Рекурсивное сканирование папок */
    private void scanFolder(File folder) {
        if (!folder.exists() || !folder.isDirectory()) return;

        File[] files = folder.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                scanFolder(file); // рекурсия для вложенных папок
            } else if (isAllowed(file.getName())) {
                imagePaths.add(file.toURI().toString());
            }
        }
    }

    /** Проверка расширения файла */
    private boolean isAllowed(String filename) {
        for (String ext : allowedExtensions) {
            if (filename.toLowerCase().endsWith(ext.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /** Итератор для изображений */
    private class ImageIterator implements Iterator {

        private int current = 0;

        @Override
        public boolean hasNext(int x) {
            return current + x - 1 < imagePaths.size();
        }

        @Override
        public Object next() {
            if (hasNext(1)) {
                return new Image(imagePaths.get(current++));
            }
            current = 0; // зацикливаем слайд-шоу
            return new Image(imagePaths.get(current++));
        }

        @Override
        public Object preview() {
            current = 0;
            return new Image(imagePaths.get(current));
        }
    }

    /** Для тестирования */
    public List<String> getImagePaths() {
        return imagePaths;
    }
}
