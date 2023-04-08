package code.spso;

import code.RPFSP;

import java.util.Arrays;
import java.util.Random;

public class SA {

    // 初始温度
    private static final double T = 100;
    // 终止温度
    private static final double T_Min = 50;
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

    /**
     * 通过互换两个工件的所有工序产生邻域解
     * @param order
     * @return
     */
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

    /**
     * 通过集体移动一个工件的所有工序来产生邻域解
     * @param order
     * @return
     */
    private int[] generateNeighbor1(int[] order) {
        int[] copy = Arrays.copyOf(order, order.length);
        Random rand = new Random();
        int a = copy[rand.nextInt(copy.length)];
        if(rand.nextDouble() < 0.5){
            for (int i = 0; i < copy.length - 1; i++) {
                if (copy[i] == a) {
                    if (i < copy.length - 1) {
                        int temp = copy[i];
                        copy[i] = copy[i+1];
                        copy[i+1] = temp;
                        i++; // 跳过交换后的下一个元素
                    }
                }
            }
        }else {
            for (int i = copy.length - 1; i > 0; i--) {
                if (copy[i] == a) {
                    if (i > 0) {
                        int temp = copy[i];
                        copy[i] = copy[i-1];
                        copy[i-1] = temp;
                        i--; // 跳过交换后的下一个元素
                    }
                }
            }
        }
        return copy;
    }

    /**
     * 通过逆序一定区间内的工序
     * @param order
     * @return
     */
    private int[] generateNeighbor2(int[] order) {
        int[] copy = Arrays.copyOf(order, order.length);
        Random rand = new Random();
        int index1 = rand.nextInt(copy.length);
        int index2 = rand.nextInt(copy.length);
        while (index2 == index1) { // 确保index2与index1不同
            index2 = rand.nextInt(copy.length);
        }
        if (index1 > index2) { // 确保index1小于等于index2
            int temp = index1;
            index1 = index2;
            index2 = temp;
        }
        for (int i = index1, j = index2; i < j; i++, j--) {
            int temp = copy[i];
            copy[i] = copy[j];
            copy[j] = temp;
        }
        return copy;
    }


    // 模拟退火算法
    public int[] solve() {
        // 初始化初始解
        int[] currentOrder = Arrays.copyOf(bestSolution, bestSolution.length);
        double currentCmax = bestCmax;
//        System.out.println("SA开始           " + bestCmax);
        double t = T;
        // 在每个温度下迭代一定次数，进行状态转移
        while (t > T_Min) {
            for (int i = 0; i < N; i++) {
                // 产生邻域解
                int[] neighborOrder = generateNeighbor2(currentOrder);
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
//            System.out.println("SA结束      " + bestCmax);
            return bestSolution;
        }else {
//            System.out.println("SA结束      " + psoCmax);
            return psoSolution;
        }
    }

}