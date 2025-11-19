package io.viola.orm.naming;

public interface Naming {
    @FunctionalInterface
    interface NamingFunction {
        String name(String s);
    }

    NamingFunction wrapStrategy(); //for wrap tables and columns names with quotations if needed

    NamingFunction namingStrategy(); //for change the tables and columns names to another case like lower case

    NamingFunction sqlKeywordsNamingStrategy(); //for sql reserved keywords

    default String doChange(String s) {
        if(s == null)
            return "";

        if(s.equals("*"))
            return s;

        return wrapStrategy().name(namingStrategy().name(s));
    }

    default String doKeywordChange(String s) {
        return sqlKeywordsNamingStrategy().name(s);
    }

    static Naming defaults() {
        return new Naming() {
            @Override
            public NamingFunction wrapStrategy() {
                return (s) -> String.format("`%s`", s.toLowerCase());
            }

            @Override
            public NamingFunction namingStrategy() {
                return (s) -> s;
            }

            @Override
            public NamingFunction sqlKeywordsNamingStrategy() {
                return String::toLowerCase;
            }
        };
    }

    static Naming merge(Naming inside, Naming outside) { //result is outside(inside(string))
        return new Naming() {
            @Override
            public NamingFunction wrapStrategy() {
                if(outside.wrapStrategy() == null)
                    return inside.wrapStrategy();

                return s -> outside.wrapStrategy().name(inside.wrapStrategy().name(s));
            }

            @Override
            public NamingFunction namingStrategy() {
                if(outside.namingStrategy() == null)
                    return inside.namingStrategy();

                return s -> outside.namingStrategy().name(inside.namingStrategy().name(s));
            }

            @Override
            public NamingFunction sqlKeywordsNamingStrategy() {
                if(outside.sqlKeywordsNamingStrategy() == null)
                    return inside.sqlKeywordsNamingStrategy();

                return s -> outside.sqlKeywordsNamingStrategy().name(inside.sqlKeywordsNamingStrategy().name(s));
            }
        };
    }
}
