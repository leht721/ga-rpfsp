package code.spso;

import code.RPFSP;

import java.util.Arrays;
import java.util.Random;

public class SA {

    // 初始温度
    private static final double T = 100;
    // 终止温度
    private static final double T_Min = 20;
    // 降温速率
    private static final double COOLINGRATE = 0.95;
    // 内循环次数
    private static final int N = 100;

    // 传入的pso的全局最优粒子
    private int[] psoSolution;
    private double psoCmax;

    // 模拟退火算法最优解
    private int[] bestSolution;
    private double bestCmax;

    public SA(int[] solution, double cmax) {
        this.psoSolution = Arrays.copyOf(solution, solution.length);
        this.psoCmax = cmax;
        this.bestSolution = Arrays.copyOf(solution, solution.length);
        this.bestCmax = cmax;
    }

    // 产生邻域解
    private int[] generateNeighbor(int[] order) {
        int length = order.length;
        Random random = new Random();
        int index1 = random.nextInt(length);
        int index2 = random.nextInt(length);
        while(order[index1] == order[index2]){
            index2 = random.nextInt(length);
        }
        int tmp = order[index1];
        int tmp2 = order[index2];
        int[] res = Arrays.copyOf(order, length);
        res = Arrays.stream(res).map(a -> a == tmp ? -1 : a).toArray();
        res = Arrays.stream(res).map(a -> a == tmp2 ? tmp : a).toArray();
        res = Arrays.stream(res).map(a -> a == -1 ? tmp2 : a).toArray();
        return res;
    }

    // 模拟退火算法
    public int[] solve() {
        // 初始化初始解
        int[] currentOrder = Arrays.copyOf(bestSolution, bestSolution.length);
        double currentCmax = bestCmax;
        System.out.println("SA开始           " + bestCmax);
        double t = T;
        // 在每个温度下迭代一定次数，进行状态转移
        while (t > T_Min) {
            for (int i = 0; i < N; i++) {
                // 产生邻域解
                int[] neighborOrder = generateNeighbor(currentOrder);
                RPFSP rpfsp = new RPFSP(neighborOrder);
                double neighborMaxTime = rpfsp.getMaxCompletionTime(rpfsp.decodeChromosome(rpfsp.chromosome));

                // 计算能量差
                double delta = neighborMaxTime - currentCmax;

                // 如果邻域解更优，则接受邻域解
                if (delta < 0) {
                    currentOrder = neighborOrder;
                    currentCmax = neighborMaxTime;
                    // 更新最优解
                    if (currentCmax < bestCmax) {
                        bestSolution = Arrays.copyOf(currentOrder, currentOrder.length);
                        bestCmax = currentCmax;
                    }
                } else {
                    // 否则以一定概率接受邻域解
                    double p = Math.exp(-delta / t);
                    if (Math.random() < p) {
                        currentOrder = neighborOrder;
                        currentCmax = neighborMaxTime;
                    }
                }
            }
            // 更新温度
            t *= COOLINGRATE;
        }
        if(bestCmax < psoCmax){
            System.out.println("SA结束      " + bestCmax);
            return bestSolution;
        }else {
            System.out.println("SA结束      " + psoCmax);
            return psoSolution;
        }
    }

}