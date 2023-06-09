package code.vns_pso;

import code.RPFSP;

import java.util.Arrays;
import java.util.Random;

/**
 * @author neulht @create
 * 2023-04-17 10:24
 */
public class VNS {
    private static final int IMAX = 1;

    // 传入的pso的全局最优粒子
    private int[] psoSolution;
    private double psoCmax;

    // 变邻域搜索算法最优解
    private int[] bestSolution;
    private double bestCmax;

    public VNS(int[] psoSolution, double psoCmax) {
        this.psoSolution = Arrays.copyOf(psoSolution, psoSolution.length);
        this.psoCmax = psoCmax;
        this.bestCmax = psoCmax;
        this.bestSolution = Arrays.copyOf(psoSolution, psoSolution.length);
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

    /**
     * 交换相邻
     * @param order
     * @return
     */
    public  int[] generateNeighbor3(int[] order) {
        int[] copy = Arrays.copyOf(order, order.length);
        int n = order.length;
        Random rand = new Random();
        int a = rand.nextInt(n - 3) + 2; // 生成大于1小于n-2的随机整数a
        int temp = order[a]; // 交换arr[a]和arr[a-1]
        copy[a] = order[a-1];
        copy[a-1] = temp;
        return copy;
    }


    /**
     * 随机交换两个不同位置
     * @param order
     * @return
     */
    public int[] generateNeighbor5(int[] order) {
        int n = order.length;
        Random rand = new Random();
        int i, j;
        do {
            i = rand.nextInt(n); // 随机选择第一个位置
            j = rand.nextInt(n - 1); // 随机选择第二个位置，但不能与第一个位置相同
            if (j >= i) {
                j++; // 如果第二个位置在第一个位置之后，将其向后移动一位
            }
        } while (i == j); // 如果两个位置相同，则重新生成位置
        int[] newArr = Arrays.copyOf(order, n); // 复制原始数组
        int temp = newArr[i];
        newArr[i] = newArr[j];
        newArr[j] = temp;
        return newArr;
    }

    /**
     * 交换首尾
     * @param order
     * @return
     */
    public  int[] generateNeighbor6(int[] order) {
        int[] copy = Arrays.copyOf(order, order.length);
        int b = order.length - 1;
        int temp = order[b]; // 交换arr[a]和arr[a-1]
        copy[b] = order[0];
        copy[0] = temp;
        return copy;
    }

    // 变邻域搜索算法
    public int[] solve() {
//        System.out.println("VNS前=====" + bestCmax);
        // 初始化初始解
        int[] currentOrder = Arrays.copyOf(bestSolution, bestSolution.length);
        double currentCmax = bestCmax;
        int[] neighborOrder;
//        System.out.println("SA开始           " + bestCmax);
        // 在每个温度下迭代一定次数，进行状态转移
        for (int i = 0; i < IMAX; i++) {
            int l = 1;
            // 产生邻域解
            while(l <= 6){
                boolean flag = true;
                switch (l){
                    case 1:
                        neighborOrder = generateNeighbor(currentOrder);
                        break;
                    case 2:
                        neighborOrder = generateNeighbor1(currentOrder);
                        break;
                    case 3:
                        neighborOrder = generateNeighbor2(currentOrder);
                        break;
                    case 4:
                        neighborOrder = generateNeighbor3(currentOrder);
                        break;
                    case 5:
                        neighborOrder = generateNeighbor5(currentOrder);
                        break;
                    case 6:
                        neighborOrder = generateNeighbor6(currentOrder);
                        break;
                    default:
                        neighborOrder = Arrays.copyOf(currentOrder, currentOrder.length);
                        flag = false;
                        break;
                }
                if(flag){
                    RPFSP rpfsp = new RPFSP(neighborOrder);
                    double neighborMaxTime = rpfsp.getMaxCompletionTime(rpfsp.decodeChromosome(rpfsp.chromosome));
                    if(neighborMaxTime < bestCmax){
                        bestSolution = Arrays.copyOf(neighborOrder, neighborOrder.length);
                        bestCmax = neighborMaxTime;
                        currentOrder = Arrays.copyOf(neighborOrder, neighborOrder.length);
                        currentCmax = neighborMaxTime;
                        l = 1;
                    }else if(neighborMaxTime < currentCmax){
                        currentOrder = Arrays.copyOf(neighborOrder, neighborOrder.length);
                        currentCmax = neighborMaxTime;
                        l = 1;
                    }else {
                        // 计算能量差
                        double delta = neighborMaxTime - currentCmax;
                        // 否则以一定概率接受邻域解
                        double landa;
                        if(i < 100){
                            landa = 0.4;
                        }else if(i < 250){
                            landa = 0.25;
                        }else{
                            landa = 0.15;
                        }
                        double t = landa * bestCmax * 0.02;
                        double p = Math.exp(-delta / t);
                        if (Math.random() < p) {
                            currentOrder =  Arrays.copyOf(neighborOrder, neighborOrder.length);
                            currentCmax = neighborMaxTime;
                            l = 1;
                        }else {
                            l++;
                        }
                    }
                }else {
                    break;
                }

            }
        }
//        System.out.println("vns后===" + bestCmax);
        return bestSolution;
    }
}
