package ru.gazpromneft.gfemproto;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;


public class ExcelTreeModel extends DefaultTreeModel implements Serializable {

    private final DefaultMutableTreeNode schemasNode;
    private final DefaultMutableTreeNode modelsNode;

    public ExcelTreeModel() {
        super(new DefaultMutableTreeNode(ResourceBundle.getBundle("strings", new UTF8Control()).getString("tree.root")));
        String schemasNodeName = ResourceBundle.getBundle("strings", new UTF8Control()).getString("tree.data");
        String modelsNodeName = ResourceBundle.getBundle("strings", new UTF8Control()).getString("tree.models");
        schemasNode = new DefaultMutableTreeNode(schemasNodeName, true);
        modelsNode = new DefaultMutableTreeNode(modelsNodeName, true);
        ((DefaultMutableTreeNode) root).add(schemasNode);
        ((DefaultMutableTreeNode) root).add(modelsNode);
        reload(modelsNode);

    }

    public void addSchemaNode(Object obj) {
        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(obj);
        schemasNode.add(newNode);

        reload(schemasNode);
    }

    public void addModelNode(Object obj) {
        if (modelsContain(obj))
            throw new IllegalArgumentException("Node already registered!");
        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(obj);
        modelsNode.add(newNode);
        reload(modelsNode);
    }

    private void deleteSchemaNode(MutableTreeNode obj) {
        if (!schemasContain(obj))
            throw new IllegalArgumentException("Node not registered!");
        schemasNode.remove(obj);
        reload(schemasNode);
    }

    private void deleteModelNode(MutableTreeNode obj) {
        if (!modelsContain(obj))
            throw new IllegalArgumentException("Node not registered!");
        modelsNode.remove(obj);
        reload(modelsNode);
    }


    public boolean modelsContain(Object obj) {
        for (Enumeration<TreeNode> it = modelsNode.children(); it.hasMoreElements(); ) {
            DefaultMutableTreeNode tn = (DefaultMutableTreeNode) it.nextElement();
            if (tn.getUserObject().toString().equals(obj.toString()))
                return true;
        }
        return false;
    }

    public boolean schemasContain(Object obj) {
        for (Enumeration<TreeNode> it = schemasNode.children(); it.hasMoreElements(); ) {
            DefaultMutableTreeNode tn = (DefaultMutableTreeNode) it.nextElement();
            if (tn.getUserObject().toString().equals(obj.toString()))
                return true;
        }
        return false;
    }

    public void deleteNode(DefaultMutableTreeNode selectedNode) {
        if (schemasContain(selectedNode)) {
            deleteSchemaNode(selectedNode);
        } else if (modelsContain(selectedNode)) {
            deleteModelNode(selectedNode);
        } else {
            throw new IllegalArgumentException("Node not registered!");
        }
    }

    public List<Object> getModels() {
        return getObjects(modelsNode);
    }

    public List<Object> getSchemas() {
        return getObjects(schemasNode);
    }

    protected List<Object> getObjects(DefaultMutableTreeNode treeNode) {
        List<Object> list = new ArrayList<>();
        Enumeration<TreeNode> it = treeNode.children();
        while (it.hasMoreElements()) {
            TreeNode tn = it.nextElement();
            list.add(((DefaultMutableTreeNode)tn).getUserObject());
        }
        return list;
    }

    public void refreshNodes() {
        schemasNode.children();
        Enumeration<TreeNode> it = schemasNode.children();
        while (it.hasMoreElements()) {
            TreeNode node = it.nextElement();
            reload(node);
        }
    }
}
