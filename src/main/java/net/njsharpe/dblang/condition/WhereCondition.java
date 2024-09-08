package net.njsharpe.dblang.condition;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.njsharpe.dblang.condition.node.Node;
import net.njsharpe.dblang.object.TableDefinition;

@Getter
@RequiredArgsConstructor
public class WhereCondition {

    private final TableDefinition from;
    private final Node root;

    public WhereCondition join(WhereCondition other) {
        return new WhereCondition(this.from, this.root.append(other.root));
    }

    @Override
    public String toString() {
        return this.root.toString();
    }

}
