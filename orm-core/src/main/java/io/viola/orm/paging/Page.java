package io.viola.orm.paging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Page<T> {
    private long index;
    private List<T> records = new ArrayList<>();

    public Page(long page) {
        this.index = page;
    }

    public Page<T> setRecords(List<T> items) {
        this.records = items;
        return this;
    }

    public void clear() {
        this.records.clear();
    }

    @SafeVarargs
    public final Page<T> add(T... t) {
        records.addAll(Arrays.asList(t));
        return this;
    }

    public void setIndex(int page) {
        this.index = page;
    }

    public long getIndex() {
        return index;
    }

    public List<T> getRecords() {
        return records;
    }

    public int getSize() {
        return records == null ? 0 : records.size();
    }
}
