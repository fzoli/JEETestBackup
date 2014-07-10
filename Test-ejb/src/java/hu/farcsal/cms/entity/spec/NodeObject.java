package hu.farcsal.cms.entity.spec;

import java.util.List;

public interface NodeObject<NodeType extends NodeObject> {
    Long getId();
    int getPriority();
    boolean isRoot();
    boolean isChildAvailable();
    boolean isDisabled();
    NodeType getParent();
    List<NodeType> getChildren();
    List<NodeType> getOrderedChildren();
}
