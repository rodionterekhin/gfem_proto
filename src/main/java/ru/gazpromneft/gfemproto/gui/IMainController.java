package ru.gazpromneft.gfemproto.gui;


public interface IMainController {
    String loadModel();

    String loadCase();

    void calculate();

    void exit();

    void about();

    void treeSelectionChanged(Object lastSelectedPathComponent);

    void deleteNode();

    void duplicateNode();

    void changedModel();

    void available_functions();

    void saveToExcel();

    void setUI(GfemGUI gui);

    void refresh();
}
