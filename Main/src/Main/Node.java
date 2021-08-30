package Main;

import java.util.ArrayList;
import java.util.List;

import static Database.Database.tagsTableColumns;

public class Node {
    private Tag data;
    private List<Node> children = new ArrayList<>();
    private Node parent = null;

    public Node(Tag data){
        this.data = data;
    }

    public void addChild(Node child){
        child.setParent(this);
        this.children.add(child);
    }

    public void addChildren(List<Node> children) {
        for(Node t : children) {
            t.setParent(this);
        }
        this.children.addAll(children);
    }

    public List<Node> getChildren() {
        return children;
    }

    public Tag getData() {
        return this.data;
    }

    public void setData(Tag data) {
        this.data = data;
    }

    private void setParent(Node parent) {
        this.parent = parent;
    }

    public Node getParent() {
        return parent;
    }

    public boolean searchNode(String tagName){
        if(data==null)
            if(children==null)
                return false;
            else
                for(Node t : children)
                    t.searchNode(tagName);
        else
            return data.getProp(tagsTableColumns[0][0]).equals(tagName);
        return false;
    }
}
