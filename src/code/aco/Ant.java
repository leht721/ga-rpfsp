package code.aco;

import code.RPFSP;

import java.util.*;

/**
 * @author neulht @create
 * 2023-04-03 21:32
 */
public class Ant {
    private int currentJob; // 当前所在的工作站
    private List<Integer> finishedJobs; // 已完成的作业
    private List<Integer> unfinishedJobs; // 未完成的作业
    private double pathLength; // 蚂蚁所经过的路径长度--最大完成时间
    private int[] solution; // 经过的路径
    private int index;

    public Ant(int[] arr){
        this.solution = Arrays.copyOf(arr, arr.length);
        RPFSP rpfsp = new RPFSP(RPFSP.getJobOrder(arr));
        this.pathLength = rpfsp.getMaxCompletionTime(rpfsp.decodeChromosome(rpfsp.chromosome));
    }

    public double getPathLength() {
        return pathLength;
    }

    public Ant(int numJobs) {
        this.solution = new int[numJobs];
        Random random = new Random();
        int start = random.nextInt(RPFSP.getN());
        this.currentJob = start;
        this.finishedJobs = new ArrayList<Integer>();
        this.unfinishedJobs = new ArrayList<Integer>();
        this.finishedJobs.add(start);
        for (int i = 0; i < numJobs; i++) {
            if(i == start) continue;
            this.unfinishedJobs.add(i);
        }
        this.index = 1;
        this.pathLength = 0.0;
    }

    public List<Integer> getFinishedJobs() {
        return finishedJobs;
    }

    public List<Integer> getUnfinishedJobs() {
        return unfinishedJobs;
    }

    public List<Integer> getReasonableJob(int cLength){
        ArrayList<Integer> reasonable = new ArrayList<>();
        for (int i = 0; i < RPFSP.getN(); i++) {
            for (int j = 0; j < RPFSP.getL(); j++) {
                int index = i + j * RPFSP.getN();
                if (index < cLength && !finishedJobs.contains(index)){
                    reasonable.add(index);
                    break;
                }
            }
        }
        return reasonable;
    }

    public double caculatePathLength() {
        int[] order = finishedJobs.stream().mapToInt(Integer::intValue).toArray();
        RPFSP rpfsp = new RPFSP(RPFSP.getJobOrder(order));
        return rpfsp.getMaxCompletionTime(rpfsp.decodeChromosome(rpfsp.chromosome));
    }

    // 选择下一个工作站
    public int selectNextjob(double[][] pheromone, double[][] heuristicInfo) {
        double sumProbabilities = 0.0;
        List<Integer> reasonable = getReasonableJob(RPFSP.getN() * RPFSP.getL());
        double[] probabilities = new double[reasonable.size()];
        int index = 0;
        for (int i = 0; i < heuristicInfo.length; i++) {
            if (reasonable.contains(i)) {
                probabilities[index] = Math.pow(pheromone[currentJob][i], AntSolver.ALPHA)
                        * Math.pow(heuristicInfo[currentJob][i], AntSolver.BETA);
                sumProbabilities += probabilities[index++];
            }
        }
        // 轮盘赌选择下一个工作站
        double rouletteWheel = Math.random() * sumProbabilities;
        double currentProb = 0.0;
        for (int i = 0; i < probabilities.length; i++) {
            if (unfinishedJobs.contains(i)) {
                currentProb += probabilities[i] / sumProbabilities;
                if (currentProb > rouletteWheel) {
                    this.solution[index++] = reasonable.get(i);
                    return reasonable.get(i);
                }
            }
        }
        // 如果没有选择到下一个工作站，返回第一个未完成的作业
        return unfinishedJobs.get(0);
    }

    // 更新路径长度和状态
    public void updatePathLength(int jobIndex) {
        unfinishedJobs.remove(new Integer(jobIndex));
        finishedJobs.add(currentJob);
    }

}
