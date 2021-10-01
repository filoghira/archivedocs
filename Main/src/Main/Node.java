package Main;

import java.util.ArrayList;
import java.util.List;

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

    public boolean nodeExists(String tagName){
        if(data==null)
            if(children==null)
                return false;
            else {
                boolean res = false;
                for (Node t : children)
                    res |= t.nodeExists(tagName);
                return res;
            }
        else
            return data.getName().equals(tagName);
    }

    public boolean nodeExists(int id){
        if(data==null)
            if(children==null)
                return false;
            else {
                boolean res = false;
                for (Node t : children)
                    res |= t.nodeExists(id);
                return res;
            }
        else
            return data.getID() == id;
    }

    public Node getNode(String tagName){
        if(!nodeExists(tagName))
            return null;
        else{
            if(data != null && data.getName().equals(tagName))
                return this;
            else
                for(Node t : children)
                    return t.getNode(tagName);
        }
        return null;
    }

    public Node getNode(int id){
        if(!nodeExists(id))
            return null;
        else{
            if(data != null && data.getID()==id)
                return this;
            else
                for(Node t : children)
                    return t.getNode(id);
        }
        return null;
    }

    public void print(int depth){
        if(data == null)
            System.out.println("Root");
        else {
            for (int i = 0; i < depth; i++)
                System.out.println("\t");
            System.out.println(data.getName());
        }

        if(children!=null)
            for(Node n : children)
                n.print(depth+1);
    }
}
