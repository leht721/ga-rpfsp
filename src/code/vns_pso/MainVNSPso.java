package code.vns_pso;

import code.RPFSP;
import code.spso.SA_PSO;
import util.Util;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author neulht @create
 * 2023-04-02 20:05
 */
public class MainVNSPso {
    public static void main(String[] args) throws IOException {
        VNS_PSO vns_pso = new VNS_PSO();
        RPFSP solve = vns_pso.solve();
        double[] arr = vns_pso.getRecord();
        System.out.println(solve.getMaxCompletionTime(solve.decodeChromosome(solve.chromosome)));
        System.out.println(Arrays.toString(solve.chromosome));
        Util.write(arr, "C:\\Users\\82413\\Desktop\\vns.xlsx", 0, 0);
    }
}
