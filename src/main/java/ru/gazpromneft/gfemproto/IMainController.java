package ru.gazpromneft.gfemproto;

public interface IMainController {
    String loadModel();
    String loadData();
    void calculate();
    default void exit()
    {
        System.exit(0);
    }
}
