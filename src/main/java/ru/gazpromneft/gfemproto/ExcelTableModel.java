package ru.gazpromneft.gfemproto;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.List;

class ExcelTableModel implements TableModel {
    private final List<Number> tableIndex;
    private final List<List<Object>> tableData;

    public ExcelTableModel(List<Number> tableIndex, List<List<Object>> tableData) {
        this.tableIndex = tableIndex;
        this.tableData = tableData;
    }

    @Override
    public int getRowCount() {
        return tableData.size();
    }

    @Override
    public int getColumnCount() {
        return tableData.get(0).size();
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnIndex == 0 ? "Имя" : tableIndex.get(columnIndex - 1).toString();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnIndex == 0 ? String.class : Double.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return tableData.get(rowIndex).get(columnIndex);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        tableData.get(rowIndex).set(columnIndex, aValue);
    }

    @Override
    public void addTableModelListener(TableModelListener l) {

    }

    @Override
    public void removeTableModelListener(TableModelListener l) {

    }
}
