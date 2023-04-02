package code.ga;

import code.RPFSP;
import util.Pair;
import util.Util;

import java.util.Arrays;
import java.util.Map;

/**
 * @author neulht @create
 * 2023-03-20 15:43
 */
public class Main {
    public static void main(String[] args) throws CloneNotSupportedException {
        GA ga = new GA();
        RPFSP best = ga.solve();
        System.out.print("最优序列:" + Arrays.toString(best.chromosome));
        best.init();
        System.out.println("最小目标值:" + Util.change(best.getMaxCompletionTime(best.decodeChromosome(best.chromosome))));
        System.out.println("总恶化时间:" + Arrays.stream(best.getEtime()).sum());
        System.out.println("PM总次数：" + (int)(best.getPmTime()/RPFSP.getTpm()) + "  PM总时间：" + best.getPmTime());
        System.out.println("PM矩阵:");
        int[][] pmMatrix = best.getPmMatrix();
        for (int i = pmMatrix.length - 1; i >= 0; i--) {
            for (int j = 0; j < pmMatrix[i].length; j++) {
                System.out.print(pmMatrix[i][j] + " ");
            }
            System.out.println(); // 换行
        }
        double[][][] arr = best.getSchedule();
        Map<Pair, Double> recordPM = best.getRecordPM();
        Util.write(arr, best.chromosome, ga.getRecord(), recordPM, "C:\\Users\\82413\\Desktop\\rpfsp.xlsx");
        System.out.println(" ");
    }
}
