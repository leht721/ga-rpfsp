package code.aco;

import util.Util;

import java.io.IOException;

/**
 * @author neulht @create
 * 2023-04-04 22:40
 */
public class Main_aco {
    public static void main(String[] args) throws IOException {
        AntSolver antSolver = new AntSolver();
        antSolver.solve();
        double[] arr = antSolver.getRecord();
        Util.write(arr, "C:\\Users\\82413\\Desktop\\pso.xlsx", 0, 1);
    }
}
