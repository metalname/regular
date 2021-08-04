/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package registry.Verify;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import registry.Cell.RegistryCell;
import registry.RegistryHive;
import utils.NumFormat;

/**
 *
 * @author 
 */
public class RegistryChecker {

    private final RegistryHive hive;
    private ArrayList<RegistryCell> cellList;

    public RegistryChecker(RegistryHive hive) {
        this.hive = hive;
    }

    public void check() {
        buildCellList();
        checkCellSizes();
    }

    private void buildCellList() {
        cellList = new ArrayList<>(100000);
        hive.getRootNode().traverse(cell -> {
            cellList.add(cell);
        });

        Comparator comp = (Comparator) (Object o1, Object o2) -> {
            RegistryCell cell1 = (RegistryCell) o1;
            RegistryCell cell2 = (RegistryCell) o2;
            if (cell1.getOffset() == cell2.getOffset()) {
                return (0);
            } else if (cell1.getOffset() < cell2.getOffset()) {
                return (-1);
            } else {
                return (1);
            }
        };
        Collections.sort(cellList, comp);
    }

    private void checkCellSizes() {
        for (int index = 0; index < cellList.size() - 1; index++) {
            // check if this cell overlaps with the next
            // get index of this cell
            RegistryCell cell = cellList.get(index);
            RegistryCell nextCell = cellList.get(index + 1);
            if (cell.getOffset() != nextCell.getOffset()) {
                if ((cell.getOffset() + cell.absSize()) > (nextCell.getOffset())) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Bad cell length: cell at ").append(NumFormat.numToHex(cell.getOffset()));
                    sb.append(" of size ").append(NumFormat.numToHex(Math.abs(cell.size())));
                    sb.append(" overlaps with cell at ").append(NumFormat.numToHex(nextCell.getOffset()));
                    System.out.println(sb.toString());
                    // correct cell size
                    cell.size(-(nextCell.getOffset() - cell.getOffset()));
                    cell.update(hive.getRegistryBuffer());
                    hive.hiveChanged(true);
                }
            }
        }
    }

}
