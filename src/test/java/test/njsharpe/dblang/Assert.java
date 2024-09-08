package test.njsharpe.dblang;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.api.function.ThrowingConsumer;
import org.junit.jupiter.api.function.ThrowingSupplier;

import static org.junit.jupiter.api.AssertionFailureBuilder.assertionFailure;

import java.util.Collection;
import java.util.function.Supplier;

class Assert {

    // region AssertSingle

    static <T> T assertSingle(Collection<T> collection) {
        return assertSingle(collection, (String) null);
    }

    static <T> T assertSingle(Collection<T> collection, String message) {
        if(collection.size() != 1) {
            failNotSingle(collection.size(), message);
        }
        return collection.iterator().next();
    }

    static <T> T assertSingle(Collection<T> collection, Supplier<String> message) {
        if(collection.size() != 1) {
            failNotSingle(collection.size(), message.get());
        }
        return collection.iterator().next();
    }

    private static void failNotSingle(Object actual, Object messageOrSupplier) {
        assertionFailure()
                .message(messageOrSupplier)
                .expected(1)
                .actual(actual)
                .buildAndThrow();
    }

    // endregion

    // region AssertCollection

    @SafeVarargs
    static <T> void assertCollection(Collection<T> collection, ThrowingConsumer<T>... consumers) {
        if(collection.size() != consumers.length) {
            failSizeMismatch(consumers.length, collection.size());
        }
        int index = 0;
        for(T t : collection) {
            ThrowingConsumer<T> consumer = consumers[index];
            Assertions.assertDoesNotThrow(() -> consumer.accept(t));
            index++;
        }
    }

    private static void failSizeMismatch(Object expected, Object actual) {
        assertionFailure()
                .expected(expected)
                .actual(actual)
                .buildAndThrow();
    }

    // endregion

}
