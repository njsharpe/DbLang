package net.njsharpe.dblang.query;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OrderBy {

    private final Class<?> type;
    private final String field;
    private final Direction direction;

    public enum Direction {

        ASC("asc"),
        DESC("desc")

        ;

        @Getter
        private final String translation;

        Direction(String translation) {
            this.translation = translation;
        }

    }

}
