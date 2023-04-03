package code.aco;

import java.util.ArrayList;
import java.util.List;

/**
 * @author neulht @create
 * 2023-04-03 21:32
 */
public class Ant {
    private int currentStation; // 当前所在的工作站
    private List<Integer> finishedJobs; // 已完成的作业
    private List<Integer> unfinishedJobs; // 未完成的作业
    private double pathLength; // 蚂蚁所经过的路径长度

    public Ant(int numJobs) {
        this.currentStation = 0;
        this.finishedJobs = new ArrayList<>();
        this.unfinishedJobs = new ArrayList<>();
        for (int i = 0; i < numJobs; i++) {
            this.unfinishedJobs.add(i);
        }
        this.pathLength = 0.0;
    }

    public int getCurrentStation() {
        return currentStation;
    }

    public void setCurrentStation(int currentStation) {
        this.currentStation = currentStation;
    }

    public List<Integer> getFinishedJobs() {
        return finishedJobs;
    }

    public List<Integer> getUnfinishedJobs() {
        return unfinishedJobs;
    }

    public double getPathLength() {
        return pathLength;
    }

    // 选择下一个工作站
    public int selectNextStation(double[][] pheromone, double[][] heuristicInfo) {
        double[] probabilities = new double[heuristicInfo.length];
        double sumProbabilities = 0.0;
        for (int i = 0; i < heuristicInfo.length; i++) {
            if (unfinishedJobs.contains(i)) {
                probabilities[i] = Math.pow(pheromone[currentStation][i], AntSolver.ALPHA)
                        * Math.pow(heuristicInfo[currentStation][i], AntSolver.BETA);
                sumProbabilities += probabilities[i];
            }
        }
        // 轮盘赌选择下一个工作站
        double rouletteWheel = Math.random() * sumProbabilities;
        double currentProb = 0.0;
        for (int i = 0; i < probabilities.length; i++) {
            if (unfinishedJobs.contains(i)) {
                currentProb += probabilities[i] / sumProbabilities;
                if (currentProb > rouletteWheel) {
                    return i;
                }
            }
        }
        // 如果没有选择到下一个工作站，返回第一个未完成的作业
        return unfinishedJobs.get(0);
    }

    // 更新路径长度和状态
    public void updatePathLength(double[][] timeMatrix) {
        if (finishedJobs.isEmpty()) {
            pathLength = timeMatrix[currentStation][unfinishedJobs.get(0)];
        } else {
            int lastJob = finishedJobs.get(finishedJobs.size() - 1);
            pathLength += timeMatrix[lastJob][currentStation];
        }
        int currentJob = unfinishedJobs.remove(unfinishedJobs.indexOf(currentStation));
        finishedJobs.add(currentJob);
    }

    // 释放未完成的作业
    public void releaseUnfinishedJobs() {
        unfinishedJobs.addAll(finishedJobs);
        finishedJobs.clear();
        pathLength = 0.0;
    }
}
