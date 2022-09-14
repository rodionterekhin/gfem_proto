package ru.gazpromneft.gfemproto;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class ExcelTreeModel extends DefaultTreeModel implements Serializable {

    private final DefaultMutableTreeNode casesNode;
    private final DefaultMutableTreeNode modelsNode;

    public ExcelTreeModel() {
        super(new DefaultMutableTreeNode("Данные проекта"));
        casesNode = new DefaultMutableTreeNode("Кейсы", true);
        modelsNode = new DefaultMutableTreeNode("Модели", true);
        ((DefaultMutableTreeNode) root).add(casesNode);
        ((DefaultMutableTreeNode) root).add(modelsNode);
        reload(modelsNode);

    }

    public void addCaseNode(Object obj) {
        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(obj);
        casesNode.add(newNode);

        reload(casesNode);
    }

    public void addModelNode(Object obj) {
        if (modelsContain(obj))
            throw new IllegalArgumentException("Node already registered!");
        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(obj);
        modelsNode.add(newNode);
        reload(modelsNode);
    }

    private void deleteCaseNode(MutableTreeNode obj) {
        if (!casesContain(obj))
            throw new IllegalArgumentException("Node not registered!");
        casesNode.remove(obj);
        reload(casesNode);
    }

    private void deleteModelNode(MutableTreeNode obj) {
        if (!modelsContain(obj))
            throw new IllegalArgumentException("Node not registered!");
        modelsNode.remove(obj);
        reload(modelsNode);
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

    public List<Object> getModels() {
        return getObjects(modelsNode);
    }

    public List<Object> getCases() {
        return getObjects(casesNode);
    }

    protected List<Object> getObjects(DefaultMutableTreeNode treeNode) {
        List<Object> list = new ArrayList<>();
        treeNode.children().asIterator().forEachRemaining((tn) ->
                list.add(((DefaultMutableTreeNode)tn).getUserObject()));
        return list;
    }

    public void refreshNodes() {
        for (Iterator<TreeNode> it = casesNode.children().asIterator(); it.hasNext(); ) {
            TreeNode node = it.next();
            reload(node);
        }
    }
}
