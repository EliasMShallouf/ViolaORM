package com.eliasmshallouf.orm.logger;

public abstract class Logger {
    public static Logger defaultLogger = new Logger() {
        @Override
        public void log(String heading, String qry, LogLevel level) {
            String msg = heading.toUpperCase() + " : " + qry;

            if (level == LogLevel.ERROR)
                System.err.println(msg);
            else
                System.out.println(msg);
        }

        @Override
        public LogLevel level() {
            return LogLevel.ALL;
        }
    };

    public static Logger noLogger = new Logger() {
        @Override
        public void log(String heading, String qry, LogLevel level) { }

        @Override
        public LogLevel level() {
            return LogLevel.NO_LOG;
        }
    };

    public static Logger throwExceptionLogger = new Logger() {
        @Override
        public void log(String heading, String qry, LogLevel level) {
            String msg = heading.toUpperCase() + " : " + qry;

            if (level == LogLevel.ERROR) {
                throw new RuntimeException(msg);
            } else
                System.out.println(msg);
        }

        @Override
        public LogLevel level() {
            return LogLevel.ALL;
        }
    };

    public enum LogLevel {
        ALL, LOG, ERROR, NO_LOG
    }

    abstract protected void log(String heading, String qry, LogLevel level);

    abstract protected LogLevel level();

    public void printLog(String heading, String qry, LogLevel level) {
        if(level() == LogLevel.NO_LOG)
            return;

        if(level() != LogLevel.ALL && level() != level)
            return;

        log(heading, qry, level);
    }
}
