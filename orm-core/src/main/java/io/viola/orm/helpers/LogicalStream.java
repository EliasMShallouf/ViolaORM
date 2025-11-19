package io.viola.orm.helpers;

import java.util.function.Function;
import java.util.function.Predicate;

public class LogicalStream<E> {
    private E value;
    private boolean test;

    private LogicalStream(E e) {
        this.value = e;
        this.test = true;
    }

    public static <E> LogicalStream<E> of(E e) {
        return new LogicalStream<>(e);
    }

    public LogicalStream<E> ifTrue(Predicate<E> fun) {
        this.test = fun.test(value);
        return this;
    }

    public LogicalStream<E> then(Function<E, E> action) {
        if(test)
            this.value = action.apply(this.value);
        return this;
    }

    public <R> LogicalStream<R> thenReturn(Function<E, R> action) {
        LogicalStream<R> res = new LogicalStream<>(null);
        res.test = test;
        if(test)
            res.value = action.apply(this.value);
        return res;
    }

    public LogicalStream<E> otherwise(Function<E, E> action) {
        if(!test)
            this.value = action.apply(this.value);
        return this;
    }

    public E get() {
        return this.value;
    }
}
