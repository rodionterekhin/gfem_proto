package ru.gazpromneft.gfemproto;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import java.io.Serializable;
import java.util.Iterator;


public class ExcelTreeModel extends DefaultTreeModel implements Serializable {

    private DefaultMutableTreeNode casesNode;
    private DefaultMutableTreeNode modelsNode;

    public ExcelTreeModel() {
        super(new DefaultMutableTreeNode("Данные проекта"));
        casesNode = new DefaultMutableTreeNode("Кейсы", true);
        modelsNode = new DefaultMutableTreeNode("Модели", true);
        ((DefaultMutableTreeNode) root).add(casesNode);
        ((DefaultMutableTreeNode) root).add(modelsNode);
        reload(modelsNode);

    }

    public DefaultMutableTreeNode addCaseNode(Object obj) {
        if (casesContain(obj))
            throw new IllegalArgumentException("Node already registered!");
        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(obj);
        casesNode.add(newNode);
        reload(casesNode);
        return newNode;
    }

    public DefaultMutableTreeNode addModelNode(Object obj) {
        if (modelsContain(obj))
            throw new IllegalArgumentException("Node already registered!");
        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(obj);
        modelsNode.add(newNode);
        reload(modelsNode);
        return newNode;
    }

    private void deleteCaseNode(MutableTreeNode obj) {
        if (!casesContain(obj))
            throw new IllegalArgumentException("Node not registered!");
        casesNode.remove(obj);
        reload(casesNode);
        return;
    }

    private void deleteModelNode(MutableTreeNode obj) {
        if (!modelsContain(obj))
            throw new IllegalArgumentException("Node not registered!");
        modelsNode.remove(obj);
        reload(modelsNode);
        return;
    }


    public boolean modelsContain(Object obj) {
        for (Iterator<TreeNode> it = modelsNode.children().asIterator(); it.hasNext(); ) {
            DefaultMutableTreeNode tn = (DefaultMutableTreeNode) it.next();
            if (tn.getUserObject().toString().equals(obj.toString()))
                return true;
        }
        return false;
    }

    public boolean casesContain(Object obj) {
        for (Iterator<TreeNode> it = casesNode.children().asIterator(); it.hasNext(); ) {
            DefaultMutableTreeNode tn = (DefaultMutableTreeNode) it.next();
            if (tn.getUserObject().toString().equals(obj.toString()))
                return true;
        }
        return false;
    }

    public void deleteNode(DefaultMutableTreeNode selectedNode) {
        if (casesContain(selectedNode)) {
            deleteCaseNode(selectedNode);
        } else if (modelsContain(selectedNode)) {
            deleteModelNode(selectedNode);
        } else {
            throw new IllegalArgumentException("Node not registered!");
        }
    }
}
