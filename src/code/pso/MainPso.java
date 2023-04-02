package code.pso;

import code.RPFSP;

import java.util.Arrays;

/**
 * @author neulht @create
 * 2023-04-02 20:05
 */
public class MainPso {
    public static void main(String[] args) {
        PSO pso = new PSO();
        RPFSP solve = pso.solve();
        System.out.println(solve.getMaxCompletionTime(solve.decodeChromosome(solve.chromosome)));
        System.out.println(Arrays.toString(solve.chromosome));
    }
}
