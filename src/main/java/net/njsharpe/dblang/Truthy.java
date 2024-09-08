package net.njsharpe.dblang;

import java.util.function.Predicate;

public class Truthy {

    public static boolean resolve(ThrowingSupplier<Boolean> supplier) {
        try {
            return supplier.get();
        } catch (Exception ex) {
            return false;
        }
    }

    public static <T> boolean resolve(ThrowingSupplier<T> supplier, Predicate<T> predicate) {
        try {
            T t = supplier.get();
            return predicate.test(t);
        } catch (Exception ex) {
            return false;
        }
    }

}
