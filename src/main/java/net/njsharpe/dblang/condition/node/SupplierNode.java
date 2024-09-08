package net.njsharpe.dblang.condition.node;

import net.njsharpe.dblang.object.TableDefinition;

import java.util.function.Function;

public class SupplierNode extends Node implements EqualitySelector {

    public SupplierNode(TableDefinition from) {
        super(from);
    }

    public BlockNode group(Function<SupplierNode, Node> function) {
        SupplierNode node = new SupplierNode(this.getFrom());
        return new BlockNode(this.getFrom(), function.apply(node));
    }

    public InvertNode not(Function<SupplierNode, Node> function) {
        SupplierNode node = new SupplierNode(this.getFrom());
        return new InvertNode(this.getFrom(), function.apply(node));

    }

}
