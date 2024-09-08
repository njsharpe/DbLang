package net.njsharpe.dblang;

import net.njsharpe.dblang.sqlite.SqliteDialectProvider;

public class Defaults {

    public static DialectProvider getDialectProvider() {
        return SqliteDialectProvider.INSTANCE;
    }

}
