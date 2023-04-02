package code.eda;

import code.RPFSP;
import util.Util;

/**
 * @author neulht @create
 * 2023-03-28 20:47
 */
public class Main1 {
    public static void main(String[] args) throws CloneNotSupportedException {
        HEDA heda = new HEDA();
        RPFSP best = heda.solve();
        System.out.println(Util.change(best.getMaxCompletionTime(best.decodeChromosome(best.chromosome))));
    }
}
