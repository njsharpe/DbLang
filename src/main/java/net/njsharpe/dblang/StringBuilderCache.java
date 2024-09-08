package net.njsharpe.dblang;

public class StringBuilderCache {

    private static final int MAX_BUILDER_SIZE = 360;

    private static StringBuilder instance;

    public static StringBuilder acquire() {
        return acquire(16);
    }

    public static StringBuilder acquire(int capacity) {
        if(capacity <= MAX_BUILDER_SIZE) {
            StringBuilder sb = instance;
            if(sb != null) {
                if(capacity <= sb.capacity()) {
                    instance = null;
                    sb.setLength(0);
                    return sb;
                }
            }
        }
        return new StringBuilder(capacity);
    }

    public static void release(StringBuilder sb) {
        if(sb.capacity() <= MAX_BUILDER_SIZE) {
            instance = sb;
        }
    }

    public static String getStringAndRelease(StringBuilder sb) {
        String result = sb.toString();
        release(sb);
        return result;
    }

}
