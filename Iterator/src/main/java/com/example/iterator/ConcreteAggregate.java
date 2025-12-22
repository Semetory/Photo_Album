package com.example.iterator;

import com.example.iterator.model.Aggregate;
import com.example.iterator.model.Iterator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ConcreteAggregate implements Aggregate {
    private List<File> imageFiles = new ArrayList<>();

    public ConcreteAggregate(String directoryPath, String filter) {
        loadImages(new File(directoryPath), filter);
    }

    private void loadImages(File directory, String filter) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        loadImages(file, filter); // рекурсивно
                    } else if (matchesFilter(file.getName(), filter)) {
                        imageFiles.add(file);
                    }
                }
            }
        }
    }

    private boolean matchesFilter(String fileName, String filter) {
        String lowerName = fileName.toLowerCase();

        if (filter.equals("Все изображения") || filter.equals("*")) {
            return lowerName.endsWith(".png") ||
                    lowerName.endsWith(".jpg") ||
                    lowerName.endsWith(".jpeg") ||
                    lowerName.endsWith(".gif") ||
                    lowerName.endsWith(".bmp");
        }

        if (filter.equals("PNG")) return lowerName.endsWith(".png");
        if (filter.equals("JPEG")) return lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg");
        if (filter.equals("GIF")) return lowerName.endsWith(".gif");
        if (filter.equals("BMP")) return lowerName.endsWith(".bmp");

        return false;
    }

    @Override
    public Iterator getIterator() {
        return new Iterator() {
            private int current = -1;

            @Override
            public boolean hasNext() {
                return !imageFiles.isEmpty() && current < imageFiles.size() - 1;
            }

            @Override
            public Object next() {
                if (imageFiles.isEmpty()) return null;

                if (hasNext()) {
                    current++;
                } else {
                    current = 0; // вернуться к началу
                }
                return imageFiles.get(current);
            }

            @Override
            public Object preview() {
                if (imageFiles.isEmpty()) return null;

                if (current > 0) {
                    current--;
                } else {
                    current = imageFiles.size() - 1; // перейти к концу
                }
                return imageFiles.get(current);
            }
        };
    }

    public int getFileCount() { return imageFiles.size(); }

    public File getFile(int index) {
        if (index >= 0 && index < imageFiles.size()) {
            return imageFiles.get(index);
        }
        return null;
    }
}