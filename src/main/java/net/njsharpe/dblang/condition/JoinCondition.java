package net.njsharpe.dblang.condition;

import lombok.*;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class JoinCondition implements FromSelector, ToSelector {

    private final Class<?> from;
    private final Class<?> to;

    private String fromField;
    private String toField;

    @Override
    public ToSelector from(String field) {
        this.fromField = field;
        return this;
    }

    @Override
    public JoinCondition to(String field) {
        this.toField = field;
        return this;
    }

}
