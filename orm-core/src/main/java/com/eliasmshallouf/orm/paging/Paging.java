package com.eliasmshallouf.orm.paging;


import com.eliasmshallouf.orm.ConnectionManager;
import com.eliasmshallouf.orm.NativeConnectionHelper;
import com.eliasmshallouf.orm.functions.aggergation.Count;
import com.eliasmshallouf.orm.query.RawSubQuery;

import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.LongStream;

public class Paging<T> {
    private final ConnectionManager manager;
    private final PreparedStatement statement;
    private final Class<T> clazz;
    private final String qry;
    private final int pageSize;
    private long currentPage;
    private final boolean lazyFetch;
    private long pagesCount;
    private final Map<Long, Page<T>> pages = new HashMap<>();

    public Paging(ConnectionManager connectionManager, Class<T> clazz, String qry, int pageSize, boolean lazyFetch) {
        this.manager = connectionManager;
        this.clazz = clazz;
        this.qry = ConnectionManager.getDialect().removeLimitOffsetFromQuery(qry);
        this.pageSize = pageSize;
        this.currentPage = 0;
        this.lazyFetch = lazyFetch;

        try {
            this.statement = this.manager
                .getConnection()
                .prepareStatement(
                    lazyFetch
                        ? ConnectionManager.getDialect().buildLimitOffsetQuery(this.qry, pageSize, "?")
                        : this.qry
                );

            init();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void init() throws Exception {
        if(lazyFetch) {
            pagesCount = (long) Math.ceil(manager
                .<T>query()
                .table(new RawSubQuery<T>(this.qry).aliased("subQuery"))
                .select(Count.all())
                .find(Long.class) / (double) pageSize);

            createPage(0);
        } else {
            List<T> all = NativeConnectionHelper.fetch(this.statement.executeQuery(), clazz);

            pagesCount = (long) Math.ceil(all.size() / (double) pageSize);

            LongStream.range(0, pagesCount).forEach(i -> {
                int from = (int) i * pageSize;
                int to = from + pageSize;

                pages.put(
                    i + 1,
                    new Page<T>(i).setRecords(all.subList(
                        from,
                        Math.min(to, all.size())
                    ))
                );
            });
        }
    }

    private Page<T> createPage(long page) {
        Page<T> p = new Page<>(page + 1);

        if(!lazyFetch)
            return p;

        try {
            this.statement.setLong(1, page * pageSize);

            return p.setRecords(NativeConnectionHelper.fetch(
                this.statement.executeQuery(),
                clazz
            ));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Page<T> getPage(long page) {
        if(!pages.containsKey(page))
            pages.put(page, createPage(page));

        return pages.get(page);
    }

    public Page<T> current() {
        return getPage(currentPage);
    }

    public boolean hasNext() {
        return currentPage < pagesCount;
    }

    public boolean hasPrevious() {
        return currentPage > 0;
    }

    public boolean canMoveTo(int page) {
        return page >= 0 && page < pagesCount;
    }

    public Page<T> next() {
        Page<T> page = current();

        if (hasNext()) {
            currentPage++;
        }

        return page;
    }

    public Page<T> previous() {
        Page<T> page = current();

        if (hasPrevious()) {
            currentPage--;
        }

        return page;
    }

    public boolean moveTo(int page) {
        if (canMoveTo(page)) {
            currentPage = page;
            return true;
        }

        return false;
    }

    public void reload() {
        this.currentPage = 0;
        this.pagesCount = 0;
        this.pages.clear();

        try {
            init();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public long getPagesCount() {
        return pagesCount;
    }

    public int getPageSize() {
        return pageSize;
    }

    public boolean isLazyFetch() {
        return lazyFetch;
    }
}
