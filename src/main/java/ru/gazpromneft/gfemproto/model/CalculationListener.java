package ru.gazpromneft.gfemproto.model;

import java.io.Serializable;

public interface CalculationListener extends Serializable {
    public void onCalculationDone();
}
