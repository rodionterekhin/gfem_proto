package ru.gazpromneft.gfemproto;


public interface IMainController {
    String loadModel();
    String loadCase();
    void calculate();
    default void exit()
    {
        System.exit(0);
    }
    void about();

    void treeSelectionChanged(Object lastSelectedPathComponent);
    void deleteNode();
    void duplicateNode();
}
