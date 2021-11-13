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

    void setParent(Node parent) {
        this.parent = parent;
    }

    public Node getParent() {
        return parent;
    }

    public boolean nodeExists(String tagName) {
        if(tagName == null || tagName.isEmpty())
            return false;
        if(tagName.equals("root"))
            return true;
        if (data == null)
            if (children == null)
                return false;
            else {
                boolean res = false;
                for (Node t : children)
                    res |= t.nodeExists(tagName);
                return res;
            }
        else {
            boolean res = false;
            for (Node t : children)
                res |= t.nodeExists(tagName);
            return data.getName().equals(tagName) || res;
        }
    }

    /**
     * Check if a node exists within itself or its children
     * @param id id of the node
     * @return true if it exists, otherwise false
     */
    public boolean nodeExists(int id){
        boolean res = false;
        if(data==null) {
            if (children == null)
                return false;
        }else
            res = data.getID() == id;

        for (Node t : children)
            res |= t.nodeExists(id);
        return res;
    }

    public Node getNode(String tagName){
        if(!nodeExists(tagName))
            return null;
        else{
            if(data != null && data.getName().equals(tagName))
                return this;
            else
                for(Node t : children){
                    Node n = t.getNode(tagName);
                    if(n!=null)
                        return n;
                }
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
                for(Node t : children){
                    Node n = t.getNode(id);
                    if(n!=null)
                        return n;
                }
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

    /**
     * Returns the leaves at the very end of the current node
     * @return List of nodes
     */
    public List<Node> getLeaves() {
        List<Node> out = new ArrayList<>();
        if(children.isEmpty())
            out.add(this);
        else
            for (Node n : children)
                out.addAll(n.getLeaves());
        return out;
    }

    public String getTagName() {
        return data==null ? null : data.getName();
    }

    /**
     * Delete the node from the tree
     * @param name Name of the node to delete
     * @return The documents that belong to the deleted node
     */
    public List<Document> removeNode(String name) {
        for (Node n : children)
            if (n.getTagName().equals(name)) {
                children.remove(n);
                return  n.getData().getDocuments();
            }else
                return n.removeNode(name);
        return null;
    }
}
