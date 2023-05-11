package test;

import code.RPFSP;
import util.Pair;
import util.Util;

import java.util.Arrays;
import java.util.Map;

/**
 * @author neulht @create
 * 2023-05-04 21:04
 */
public class Print {
    public static void main(String[] args) {
        int[] arr = new int[]{28, 25, 3, 24, 20, 4, 11, 24, 17, 25, 5, 22, 0, 21, 12, 14, 5, 17, 27, 28, 11, 16, 6,
                12, 26, 10, 3, 18, 22, 27, 16, 8, 9, 24, 21, 26, 29, 13, 20, 1, 17, 19, 4, 6, 7, 14, 16, 23, 22, 5, 8, 29, 4, 2, 14, 10, 15, 9, 6, 29, 7,
                25, 19, 27, 3, 28, 15, 20, 26, 0, 1, 21, 19, 2, 18, 13, 8, 15, 23, 9, 10, 2, 13, 12, 7, 18, 0, 1, 23, 11};
        RPFSP best = new RPFSP(arr);
        System.out.println("最优序列:" + Arrays.toString(best.chromosome));
        best.init();
//        RPFSP best = new RPFSP(new int[]{1, 0, 3, 2, 1, 0, 3, 2});
        System.out.println("最小目标值:" + Util.change(best.getMaxCompletionTime(best.decodeChromosome(best.chromosome))));
        System.out.println("总线性恶化时间:" + Arrays.stream(best.getEtime()).sum());
        System.out.println("总MR时间:" + Arrays.stream(best.getMrtime()).sum());
        System.out.println("学习效应时间:" + Arrays.stream(best.getLtime()).sum());
        System.out.println("PM总次数：" + (int)(best.getPmTime()/RPFSP.getTpm()) + "  PM总时间：" + best.getPmTime());
        System.out.println("PM矩阵:");
        int[][] pmMatrix = best.getPmMatrix();
        for (int i = pmMatrix.length - 1; i >= 0; i--) {
            for (int j = 0; j < pmMatrix[i].length; j++) {
                System.out.print(pmMatrix[i][j] + " ");
            }
            System.out.println(); // 换行
        }
        Map<Pair, Double> recordPM = best.getRecordPM();
        System.out.println(" ");
    }
}
