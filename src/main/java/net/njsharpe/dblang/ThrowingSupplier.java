package net.njsharpe.dblang;

@FunctionalInterface
public interface ThrowingSupplier<T> {

    T get() throws Exception;

}
