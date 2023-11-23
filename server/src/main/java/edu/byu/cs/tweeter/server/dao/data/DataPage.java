package edu.byu.cs.tweeter.server.dao.data;

import java.util.List;
import java.util.ArrayList;

public class DataPage<T> {
    private List<T> values;
    private boolean hasMorePages;

    public DataPage() {
        setValues(new ArrayList<T>());
        setHasMorePages(false);
    }

    public void setValues(List<T> values) {
        this.values = values;
    }

    public void setHasMorePages(boolean hasMorePages) {
        this.hasMorePages = hasMorePages;
    }

    public List<T> getValues() {
        return values;
    }

    public boolean isHasMorePages() {
        return hasMorePages;
    }
}