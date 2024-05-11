package com.monpays.services.interfaces._generic;

import java.util.List;

public interface IHistoryService<Entry, SnapshotedClass> {
    // inserts a new entry in the history
    Entry addEntry(SnapshotedClass snapshotedObject);
    // returns all entries in history
    List<Entry> getHistory();
    // returns the entries for the specified object
    List<Entry> getByObject(SnapshotedClass snapshotedObject);
}
