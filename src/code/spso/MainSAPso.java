package code.spso;

import code.RPFSP;
import code.pso.PSO;
import util.Util;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author neulht @create
 * 2023-04-02 20:05
 */
public class MainSAPso {
    public static void main(String[] args) throws IOException {
        SA_PSO sa_pso = new SA_PSO();
        RPFSP solve = sa_pso.solve();
        double[] arr = sa_pso.getRecord();
        System.out.println(solve.getMaxCompletionTime(solve.decodeChromosome(solve.chromosome)));
        System.out.println(Arrays.toString(solve.chromosome));
        Util.write(arr, "C:\\Users\\82413\\Desktop\\pso.xlsx", 3, 0);
    }
}
