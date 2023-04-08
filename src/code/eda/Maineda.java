package code.eda;

import code.RPFSP;
import util.Util;

import java.io.IOException;

/**
 * @author neulht @create
 * 2023-03-28 20:47
 */
public class Maineda {
    public static void main(String[] args) throws CloneNotSupportedException, IOException {
        HEDA heda = new HEDA();
        RPFSP best = heda.solve();
        double[] arr = heda.getRecord();
        Util.write(arr, "C:\\Users\\82413\\Desktop\\pso.xlsx", 0, 2);
        System.out.println(Util.change(best.getMaxCompletionTime(best.decodeChromosome(best.chromosome))));
    }
}
