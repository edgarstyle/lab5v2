package org.example.mainClass;


import org.example.models.Flat;
import org.example.models.Furnish;
import org.example.models.View;

import java.time.ZonedDateTime;
import java.util.*;

/**
 * класс, который управляет коллекцией
 */
public class CollectionManager {
    private final Set<Flat> flats;
    private final ZonedDateTime creationDate;
    private final Deque<String> commandHistory = new ArrayDeque<>(6);

    public CollectionManager(List<Flat> flats) {
        this.flats = new HashSet<>();
        this.flats.addAll(flats);
        creationDate = ZonedDateTime.now();
    }

    public Set<Flat> getCollection() {
        return new HashSet<>(flats);
    }

    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    public String getCollectionName() {
        return flats.getClass().toString();
    }

    public int getSize() {
        return flats.size();
    }

    public boolean add(Flat flat) {
        return flats.add(flat);
    }

    public boolean existElementWithId(Long id) {
        return flats.stream().anyMatch(f -> f.getId().equals(id));
    }

    public void updateById(Long id, Flat flat) {
        flats.stream()
                .filter(f -> f.getId().equals(id))
                .findFirst()
                .ifPresent(f -> f.update(flat));
    }

    public void removeById(Long id) {
        flats.removeIf(f -> f.getId().equals(id));
    }

    public void clear() {
        flats.clear();
    }

    public boolean addIfMax(Flat flat) {
        Optional<Flat> maxFlat = flats.stream().max(Comparator.comparing(Flat::getArea));
        if (maxFlat.isEmpty() || flat.getArea() > maxFlat.get().getArea()) {
            return flats.add(flat);
        }
        return false;
    }

    public void removeGreater(Flat flat) {
        flats.removeIf(f -> f.getArea() > flat.getArea());
    }

    public void addToHistory(String command) {
        if (commandHistory.size() == 6) {
            commandHistory.removeFirst();
        }
        commandHistory.addLast(command);
    }

    public List<String> getHistory() {
        return new ArrayList<>(commandHistory);
    }

    public boolean removeAnyByFurnish(Furnish furnish) {
        return flats.removeIf(f -> f.getFurnish() == furnish);
    }

    public Optional<Flat> minByCreationDate() {
        return flats.stream().min(Comparator.comparing(Flat::getCreationDate));
    }

    public Set<View> getUniqueViews() {
        Set<View> uniqueViews = new HashSet<>();
        flats.forEach(f -> uniqueViews.add(f.getView()));
        return uniqueViews;
    }
}