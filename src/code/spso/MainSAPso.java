package code.spso;

import code.RPFSP;
import code.pso.PSO;

import java.util.Arrays;

/**
 * @author neulht @create
 * 2023-04-02 20:05
 */
public class MainSAPso {
    public static void main(String[] args) {
        SA_PSO sa_pso = new SA_PSO();
        RPFSP solve = sa_pso.solve();
        System.out.println(solve.getMaxCompletionTime(solve.decodeChromosome(solve.chromosome)));
        System.out.println(Arrays.toString(solve.chromosome));
    }
}
