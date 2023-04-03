package code.aco;

import code.RPFSP;

import java.util.ArrayList;
import java.util.List;

public class AntSolver {
    public static final double ALPHA = 1.0; // 信息素重要程度
    public static final double BETA = 5.0; // 启发式信息重要程度
    public static final double RHO = 0.5; // 信息素蒸发率
    public static final double Q = 100.0; // 信息素增量
    public static final int NUM_ANTS = 10; // 蚂蚁数量
    public static final int MAX_ITERATIONS = 100; // 最大迭代次数
    public static final int jobLength = RPFSP.getL() * RPFSP.getN();
    private double[][] pheromoneMatrix; // 信息素矩阵
    private double[][] heuristicMatrix; // 启发式信息矩阵
    private List<Ant> ants; // 蚂蚁
    private int[] bestSolution; // 最优解
    private double bestPathLength; // 最短路径长度

    public AntSolver() {
        this.pheromoneMatrix = new double[jobLength][jobLength];
        for (int i = 0; i < jobLength; i++) {
            for (int j = 0; j < jobLength; j++) {
                pheromoneMatrix[i][j] = 1.0;
            }
        }
        this.ants = new ArrayList<>();
        for (int i = 0; i < NUM_ANTS; i++) {
            ants.add(new Ant(numJobs));
        }
        this.bestSolution = new int[numJobs];
        this.bestPathLength = Double.MAX_VALUE;
    }

    // 初始化信息素矩阵
    private void initializePheromoneMatrix() {
        for (int i = 0; i < numStations; i++) {
            for (int j = 0; j < numJobs; j++) {
                pheromoneMatrix[i][j] = 1.0;
            }
        }
    }

    // 计算每只蚂蚁的路径长度
    private void calculatePathLengths() {
        for (Ant ant : ants) {
            ant.calculatePathLength(timeMatrix);
            if (ant.getPathLength() < bestPathLength) {
                bestPathLength = ant.getPathLength();
                bestSolution = ant.getTour().clone();
            }
        }
    }

    // 更新信息素
    private void updatePheromones() {
        // 信息素挥发
        for (int i = 0; i < numStations; i++) {
            for (int j = 0; j < numJobs; j++) {
                pheromoneMatrix[i][j] *= (1.0 - RHO);
                if (pheromoneMatrix[i][j] < 0.0) {
                    pheromoneMatrix[i][j] = 1.0;
                }
            }
        }
        // 信息素增量
        for (Ant ant : ants) {
            double increment = Q / ant.getPathLength();
            int[] tour = ant.getTour();
            for (int i = 0; i < numJobs - 1; i++) {
                int station = tour[i];
                int job = tour[i + 1];
                pheromoneMatrix[station][job] += increment;
            }
        }
    }

    // 执行蚁群算法
    public void solve() {
        for (int i = 0; i < MAX_ITERATIONS; i++) {
            // 每只蚂蚁从未完成的作业中选择下一个作业
            for (Ant ant : ants) {
                ant.chooseNextJob(pheromoneMatrix, timeMatrix);
            }
            // 计算每只蚂蚁的路径长度
            calculatePathLengths();
            // 更新信息素
            updatePheromones();
            // 释放未完成的作业
            for (Ant ant : ants) {
                ant.releaseUnfinishedJobs();
            }
        }
    }

    // 输出最优解
    public void printBestSolution() {
        System.out.print("Best solution: ");
        for (int i = 0; i < numJobs; i++) {
            System.out.print(bestSolution[i] + " ");
        }
        System.out.println();
        System.out.println("Best path length: " + bestPathLength);
    }
}
