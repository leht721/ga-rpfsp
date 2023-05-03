package test;

import code.RPFSP;
import util.Util;

/**
 * @author neulht @create
 * 2023-05-03 17:22
 */
public class Xuexi {
    public static void main(String[] args) {
        System.out.println(((4444.28-5030.92)/5030.92)/-0.1);
        System.out.println(((4039.85-5030.92)/5030.92)/-0.1);
        System.out.println(((3755.62-5030.92)/5030.92)/-0.1);
        System.out.println(((3630.44-5030.92)/5030.92)/-0.1);
        int[] ints = {5, 8, 4, 0, 3, 6, 5, 1, 2, 9, 7, 0, 3, 4, 5, 6, 1, 9, 2, 7, 8, 0, 3, 4, 5, 6, 1, 9, 2, 7, 8, 0, 3, 4, 6, 1, 9, 5, 2, 7, 8, 0, 3, 4, 6, 9, 1, 2, 8, 7, 0, 3, 5, 6, 9, 1, 2, 8, 7, 4};

        RPFSP best = new RPFSP(ints);
        System.out.println(Util.change(best.getMaxCompletionTime(best.decodeChromosome(best.chromosome))));
    }
}
