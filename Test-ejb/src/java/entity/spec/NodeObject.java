package entity.spec;

import java.util.List;

public interface NodeObject<NodeType extends NodeObject> {
    Long getId();
    boolean isRoot();
    boolean isChildAvailable();
    NodeType getParent();
    List<NodeType> getChildren();
}
