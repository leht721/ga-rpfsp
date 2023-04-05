package code.aco;

import code.RPFSP;
import util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AntSolver {
    public static final double ALPHA = 1.0; // 信息素重要程度
    public static final double BETA = 1.0; // 启发式信息重要程度
    public static double RHO; // 信息素蒸发率---r值大的时候以前走过的路再走的可能性较低，可以增高全局搜索能力
    public static final double Q = 4000.0; // 信息素增量
    public static final int NUM_ANTS = 30; // 蚂蚁数量
    public static final int MAX_ITERATIONS = 1000; // 最大迭代次数
    public static final int jobLength = RPFSP.getL() * RPFSP.getN();
    private double[][] pheromoneMatrix; // 信息素矩阵
    private double[][] heuristicMatrix; // 启发式信息矩阵
    private List<Ant> ants; // 蚂蚁
    private int[] bestSolution; // 最优解
    private double bestPathLength; // 最短路径长度
    private double[] record;

    public AntSolver() {
        this.record = new double[MAX_ITERATIONS];
        this.pheromoneMatrix = new double[jobLength][jobLength];
        this.heuristicMatrix = new double[jobLength][jobLength];
        initializePheromoneMatrix();
        initializeheuristicMatrix();
        this.ants = new ArrayList<>();
        this.bestSolution = new int[jobLength];
        this.bestPathLength = Double.MAX_VALUE;
        for (int i = 0; i < NUM_ANTS; i++) {
            int[] arr = Util.generateAcoArr(RPFSP.getN(), RPFSP.getL());
            Ant ant = new Ant(arr);
            ants.add(ant);
            if(ant.getPathLength() < this.bestPathLength){
                this.bestSolution = Arrays.copyOf(arr, arr.length);
                this.bestPathLength = ant.getPathLength();
            }
        }
    }

    // 初始化信息素矩阵
    private void initializePheromoneMatrix() {
        for (int i = 0; i < jobLength; i++) {
            for (int j = 0; j < jobLength; j++) {
                pheromoneMatrix[i][j] = 1.0;
            }
        }
    }

    // 初始化启发式信息矩阵
    private void initializeheuristicMatrix() {
        for (int i = 0; i < jobLength; i++) {
            for (int j = 0; j < jobLength; j++) {
                heuristicMatrix[i][j] = 1 / calculateComplete(RPFSP.getProcessingTimes(), i, j);
            }
        }
    }

    /**
     * 计算两个工序的完成时间
     * @param processingTimes
     * @param i
     * @param j
     * @return
     */
    private double calculateComplete(int[][] processingTimes, int i, int j) {
        if(i == j || (j < i && (i - j) % RPFSP.getN() == 0) || (j - i > RPFSP.getN() && (j - i) % RPFSP.getN() == 0)) return Double.MAX_VALUE;
        int[][] arr = new int[RPFSP.getM()][2];
        arr[0][0] = processingTimes[0][i];
        for (int k = 1; k < processingTimes.length; k++) {
            arr[k][0] = arr[k - 1][0] + processingTimes[k][i];
        }
        arr[0][1] = arr[0][0] + processingTimes[0][j];
        for (int k = 1; k < processingTimes.length; k++) {
            arr[k][1] = Math.max(arr[k][0], arr[k - 1][1]) + processingTimes[k][j];
        }
        if(j - i == RPFSP.getN()) return arr[RPFSP.getM() - 1][1] * 2;
        return arr[RPFSP.getM() - 1][1];
    }

    // 计算每只蚂蚁的路径长度
    private void calculatePathLengths() {
        for (Ant ant : ants) {
            double tmp = ant.caculatePathLength();
            if (tmp < bestPathLength) {
                bestPathLength = tmp;
                int[] ints = ant.getFinishedJobs().stream().mapToInt(Integer::intValue).toArray();
                bestSolution = Arrays.copyOf(ints, ints.length);
            }
        }
    }

    // 更新信息素
    private void updatePheromones() {
        // 信息素挥发
        for (int i = 0; i < jobLength; i++) {
            for (int j = 0; j < jobLength; j++) {
                pheromoneMatrix[i][j] *= (1.0 - RHO);
                if (pheromoneMatrix[i][j] < 0.0) {
                    pheromoneMatrix[i][j] = 1.0;
                }
            }
        }
        double increment = Q / bestPathLength;
        for (int i = 0; i < bestSolution.length - 1; i++) {
            int start = bestSolution[i];
            int end = bestSolution[i + 1];
            pheromoneMatrix[start][end] += increment;
        }

    }

    // 执行蚁群算法
    public void solve() {
        updatePheromones();
        for (int i = 0; i < MAX_ITERATIONS; i++) {
            if(i < MAX_ITERATIONS * 0.4){
                RHO = 0.75;
            }else if (i < MAX_ITERATIONS * 0.8){
                RHO = 0.6;
            }else {
                RHO = 0.4;
            }
            // 初始化每一只蚂蚁
            ants.clear();
            for (int j = 0; j < NUM_ANTS; j++) {
                ants.add(new Ant(jobLength));
            }
            for (int j = 0; j < jobLength - 1; j++) {
                for (Ant ant : ants) {
                    int nextjob = ant.selectNextjob(pheromoneMatrix, heuristicMatrix);
                    ant.updatePath(nextjob);
                }
//                System.out.println("--");
            }
//            System.out.println(" ");
            // 计算每只蚂蚁的路径长度
            calculatePathLengths();
            // 更新信息素
            updatePheromones();
            printBestSolution();
            record[i] = bestPathLength;
            System.out.print("第" + (i+1) + "代:CMAX = " + record[i]);
            System.out.println("   序列：" + Arrays.toString(bestSolution));
        }
    }

    // 输出最优解
    public void printBestSolution() {
        System.out.print("Best solution: ");
        for (int i = 0; i < jobLength; i++) {
            System.out.print(bestSolution[i] + " ");
        }
        System.out.println();
        System.out.println("Best path length: " + bestPathLength);
    }
}
