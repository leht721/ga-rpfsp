package code.pso;

import code.RPFSP;
import util.Util;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author neulht @create
 * 2023-04-02 20:05
 */
public class MainPso {
    public static void main(String[] args) throws IOException {
        PSO pso = new PSO();
        RPFSP solve = pso.solve();
        double[] arr = pso.getRecord();
        Util.write(arr, "C:\\Users\\82413\\Desktop\\pso.xlsx", 0, 0);
        System.out.println(solve.getMaxCompletionTime(solve.decodeChromosome(solve.chromosome)));
        System.out.println(Arrays.toString(solve.chromosome));
    }
}
