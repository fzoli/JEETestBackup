package entity.spec;

import java.io.Serializable;
import java.util.List;

public interface NodeObject<NodeType extends NodeObject> extends Serializable {
    Long getId();
    boolean isRoot();
    boolean isChildAvailable();
    NodeType getParent();
    List<NodeType> getChildren();
}
