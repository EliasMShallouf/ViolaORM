package io.viola.orm.query;

public class UnionQuery<T> {
    public enum UnionType { ALL, DEFAULT }

    private SelectQuery<T> query;

    private UnionType type;

    public UnionQuery(SelectQuery<T> query, UnionType type) {
        this.query = query;
        this.type = type;
    }

    public SelectQuery<T> getQuery() {
        return query;
    }

    public void setQuery(SelectQuery<T> query) {
        this.query = query;
    }

    public UnionType getType() {
        return type;
    }

    public void setType(UnionType type) {
        this.type = type;
    }
}
