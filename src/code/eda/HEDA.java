package code.eda;

import code.RPFSP;
import util.Util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * @author neulht @create
 * 2023-03-27 9:47
 */
public class HEDA {
    // 种群中的染色体数量
    private static final int POPULATION_SIZE = 10000;

    // 最大迭代次数
    private static final int MAX_ITERATIONS = 1000;

    // 学习速率
    private static final double ALFA = 0.01;

    // 累计次数阈值
    private static final int COUNT_MAX = 20;

    // 累计次数阈值
    private static final double BORDER = 1.2;

    // 变异概率
    private static final double MUTATION_RATE = 0.45;

    // 局部搜索个体占比
    private static final double LOCAL_RATE = 0.4;

    // 随机搜索个体占比
    private static final double RANDOM_RATE = 0.4;

    // 局部搜索的单个个体搜索的最大次数
    private static final int MAX_LOCALITER = 70;

    // 局部搜索个体占比
    private static final int MAX_RANDOMSEARCH = 40;

    // 概率矩阵
    private static double[][] pMatrix;

    // 种群
    private RPFSP[] population;

    // 更新概率矩阵个体
    private RPFSP best;

    // 更新概率矩阵个体占比
    private static final double UPFATE_RATIO = 0.3;

    // 全局最优个体
    private RPFSP bestGlobal;

    // 目标值迭代数组
    private double[] record = new double[MAX_ITERATIONS];


    public double[] getRecord() {
        return record;
    }

    static {
        BufferedReader reader = null;
        String line = null; // 读取第一行
        try {
//            reader = new BufferedReader(new FileReader("C:\\Users\\Administrator\\Desktop\\a.txt"));
            reader = new BufferedReader(new FileReader("C:\\Users\\82413\\Desktop\\a.txt"));
            line = reader.readLine();
            String[] firstLine = line.split(" ");
            int m = Integer.parseInt(firstLine[0]);
            int n = Integer.parseInt(firstLine[1]);
            int l = Integer.parseInt(firstLine[2]);
            pMatrix = new double[n][n * l];
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // 概率矩阵更新函数
    private void updatePMatrix(RPFSP[] population){
        double v = population.length * UPFATE_RATIO;
        for (int i = 0; i < v; i++) {
            int[] chromosome = population[i].chromosome;
            for (int j = 0; j < chromosome.length; j++) {
                pMatrix[chromosome[j]][j] += ALFA;
            }
            for (int j = 0; j < pMatrix[0].length; j++) {
                for (int z = 0; z < pMatrix.length; z++) {
                    pMatrix[z][j] /= (1 + ALFA);
                }
            }
        }

    }

    // 采样函数 产生新种群
    private RPFSP[] sampling(){
        double[][] matrix = new double[pMatrix.length][pMatrix[0].length];
        for (int i = 0; i < pMatrix.length; i++) {
            for (int j = 0; j < pMatrix[0].length; j++) {
                matrix[i][j] = pMatrix[i][j];
            }
        }
        RPFSP[] newPopulation = new RPFSP[POPULATION_SIZE];
        for (int i = 0; i < newPopulation.length; i++) {
            int[] flag = new int[pMatrix.length];
            int[] chromosome = new int[pMatrix[0].length];
            for (int j = 0; j < chromosome.length; j++) {
                int res = selectJob1(j, flag);
                chromosome[j] = res;
            }
            newPopulation[i] = new RPFSP(chromosome);
        }
        return newPopulation;
    }

    // 工件选择
    private int selectJob(int index, int[] flag) {

        double[] jobProb = new double[flag.length];
        for (int i = 0; i < jobProb.length; i++) {
            jobProb[i] = pMatrix[i][index];
        }
        double[] sortedArr = Arrays.copyOf(jobProb, jobProb.length);
        Arrays.sort(sortedArr);
        int res = -1;
        while (res == -1 || flag[res] == RPFSP.getL()){
            double sum = 0.0;
            Random random = new Random();
            double dest = 0.0;
            ArrayList<Integer> candidateIndices = new ArrayList<Integer>();
            double a = random.nextDouble();
            for (int i = 0; i < jobProb.length; i++) {
                sum += sortedArr[i];
                if (sum >= a) {
                    dest = sortedArr[i];
                    break;
                }
            }
            for (int i = 0; i < jobProb.length; i++) {
                if(jobProb[i] == dest) candidateIndices.add(i);
            }
            if (candidateIndices.size() > 0) {
                Random rand = new Random();
                res = candidateIndices.get(rand.nextInt(candidateIndices.size()));
            }else {
                res = candidateIndices.get(0);
            }
        }
        flag[res]++;
        return res;
    }

    // 工件选择
    private int selectJob1(int index, int[] flag) {

        double[] jobProb = new double[flag.length];
        for (int i = 0; i < jobProb.length; i++) {
            jobProb[i] = pMatrix[i][index];
        }
        double[] sortedArr = Arrays.copyOf(jobProb, jobProb.length);
        int res = -1;
        while (res == -1 || flag[res] == RPFSP.getL()){
            double sum = 0.0;
            Random random = new Random();
            double a = random.nextDouble();
            for (int i = 0; i < jobProb.length; i++) {
                sum += sortedArr[i];
                if (sum >= a) {
                    res = i;
                    break;
                }
            }
        }
        flag[res]++;
        return res;
    }

    // 首次改进跳出原则的局部搜索
    private RPFSP[] findBest(RPFSP[] population){
        int count = (int) (population.length * LOCAL_RATE);
        for (int i = 0; i < count; i++) {
            double yuanshi = 0.0;
            double bianhua = 0.0;
            RPFSP orig, chan = new RPFSP();
            do {
                Random random = new Random();
                int length = population[0].chromosome.length;
                int index1 = random.nextInt(length);
                int index2 = random.nextInt(length);
                while(population[i].chromosome[index1] == population[i].chromosome[index2]){
                    index2 = random.nextInt(length);
                }
                int[] ini = new int[length];
                int[] change = new int[length];
                ini = Arrays.copyOf(population[i].chromosome, length);
                change = Arrays.copyOf(population[i].chromosome, length);
                int tmp = population[i].chromosome[index1];
                int tmp2 = population[i].chromosome[index2];
                change = Arrays.stream(change).map(a -> a == tmp ? -1 : a).toArray();
                change = Arrays.stream(change).map(a -> a == tmp2 ? tmp : a).toArray();
                change = Arrays.stream(change).map(a -> a == -1 ? tmp2 : a).toArray();
                orig = new RPFSP(ini);
                chan = new RPFSP(change);
                yuanshi = Util.change(orig.getMaxCompletionTime(orig.decodeChromosome(orig.chromosome)));
                bianhua = Util.change(chan.getMaxCompletionTime(chan.decodeChromosome(chan.chromosome)));
            }while (yuanshi < bianhua);
            population[i] = chan;
            // 没初始化，算过了目标值
        }
        return population;
    }

    /**
     * 当noncount小于maxcount时，通过此局部搜索找到目标值优于best的解，然后替换掉种群中的一个个体
     * @param population
     * @param best
     * @return
     */
    private RPFSP findBest(RPFSP[] population, RPFSP best){
        double bianhua;
        int length = best.chromosome.length;
        double dest = Util.change(best.getMaxCompletionTime(best.decodeChromosome(best.chromosome)));
        RPFSP chan;
        for (int i = 0; i < population.length * LOCAL_RATE; i++) {
            int[] ini = Arrays.copyOf(population[i].chromosome, population[i].chromosome.length);
            RPFSP orig = new RPFSP(ini);
            int count = 0;
            do {
                Random random = new Random();
                int index1 = random.nextInt(length);
                int index2 = random.nextInt(length);
                while(orig.chromosome[index1] == orig.chromosome[index2]){
                    index2 = random.nextInt(length);
                }
                int[] change = Arrays.copyOf(orig.chromosome, length);
                int tmp = orig.chromosome[index1];
                int tmp2 = orig.chromosome[index2];
                change = Arrays.stream(change).map(a -> a == tmp ? -1 : a).toArray();
                change = Arrays.stream(change).map(a -> a == tmp2 ? tmp : a).toArray();
                change = Arrays.stream(change).map(a -> a == -1 ? tmp2 : a).toArray();
                chan = new RPFSP(change);
                bianhua = Util.change(chan.getMaxCompletionTime(chan.decodeChromosome(chan.chromosome)));
                if(count++ > MAX_LOCALITER){
                    break;
                }
            }while (dest <= bianhua);
            if (dest > bianhua){
                System.out.println("局部搜索成功");
                return chan;
            }
        }
        // 没初始化，算过了目标值
        System.out.println("局部搜索失败");
        return best;
    }


    /**
     * 初始化种群
     * @param population
     */
    private void init(RPFSP[] population) {
        for (int i = 0; i < population.length; i++) {
            population[i].init();
        }
    }

    /**
     * 获取种群中的最优个体
     * @param population
     * @return
     * @throws CloneNotSupportedException
     */
    private RPFSP getBest(RPFSP[] population) throws CloneNotSupportedException {
        RPFSP best;
        Map<Double, Integer> treemap = new TreeMap<>();
        for (int j = 0; j < population.length; j++) {
            treemap.put(population[j].getMaxCompletionTime(population[j].decodeChromosome(population[j].chromosome)), j);
        }
        int index = 0;
        RPFSP[] resource = Arrays.copyOf(population, population.length);
        for (Double d : treemap.keySet()) {
            population[index++] = resource[treemap.get(d)];
            if(index == POPULATION_SIZE) break;
        }
        best = population[0].clone();
        best.init();
        init(population);
        return best;
    }

    // 算法主函数
    public RPFSP solve() throws CloneNotSupportedException {
        // 初始化种群
        population = new RPFSP[POPULATION_SIZE];
        for (int i = 0; i < population.length; i++) {
            population[i] = new RPFSP();
        }
        // 初始化概率矩阵
        for (int i = 0; i < pMatrix.length; i++) {
            for (int j = 0; j < pMatrix[0].length; j++) {
                pMatrix[i][j] = 1.0 / pMatrix.length;
            }
        }
        best = getBest(population);
        int nonICount = 0;
        for (int i = 0; i < MAX_ITERATIONS; i++) {
            updatePMatrix(population);
            population = sampling();
            RPFSP tmp = getBest(population);
            if(tmp.calculateFitness() > best.calculateFitness()){
                System.out.println("++++++++++++++++++++GOOD++++++++++++++++++++++");
                best = tmp;
                best.init();
                nonICount = 0;
                double val = Util.change(best.getMaxCompletionTime(best.decodeChromosome(best.chromosome)));
                record[i] = val;
                System.out.print("第" + (i+1) + "代:CMAX = " + val);
                System.out.println("   序列：" + Arrays.toString(best.chromosome));
                best.init();
            }else {
                nonICount++;
                best.init();
                if (nonICount < COUNT_MAX){
                    best = findBest(population, best);
                }else {
                    nonICount = 0;
                    best = findBestRandom(best);
                }
                best.init();
                mutate(population);
                mutate(pMatrix);
                double bestVal = Util.change(best.getMaxCompletionTime(best.decodeChromosome(best.chromosome)));
                record[i] = bestVal;
                System.out.print("第" + (i+1) + "代:CMAX = " + record[i]);
                System.out.println("   序列：" + Arrays.toString(best.chromosome));
                best.init();
            }
        }

        return best;
    }

    /**
     * 对概率矩阵进行变异
     * @param pMatrix
     */
    private void mutate(double[][] pMatrix) {
        Random random = new Random();
        int length = pMatrix.length;
        for (int i = 0; i < pMatrix[0].length; i++) {
            double v = random.nextDouble();
            if(v < MUTATION_RATE){
                int index1 = random.nextInt(length);
                int index2;
                do {
                    index2 = random.nextInt(length);
                }while (index1 == index2);
                double ratio = random.nextDouble();
                double sum = pMatrix[index1][i] + pMatrix[index2][i];
                pMatrix[index1][i] = sum * ratio;
                pMatrix[index2][i] = sum * (1.0 - ratio);
            }
        }
    }

    /**
     * 对种群进行变异，这个方法不用之后算法不会被堵塞了，沙雕方法真是该死
     * @param population
     */
    private void mutate(RPFSP[] population) {
        Random random = new Random();
        int length = population[0].chromosome.length;
        for (int i = 0; i < population.length; i++) {
            if(random.nextDouble() < MUTATION_RATE){
                int index1 = random.nextInt(length);
                int index2 = random.nextInt(length);
                int temp = population[i].chromosome[index1];  // 保存arr[a]的值
                if (index1 < index2) {
                    // a+1到b-1的元素向后移动一个位置
                    for (int j = index2-1; j > index1; j--) {
                        population[i].chromosome[j+1] = population[i].chromosome[j];
                    }
                    // 将arr[a]放到arr[b]的位置
                    population[i].chromosome[index2] = temp;
                }else {
                    // a-1到b的元素向前移动一个位置
                    for (int j = index1-1; j >= index2; j--) {
                        population[i].chromosome[j+1] = population[i].chromosome[j];
                    }
                    // 将arr[a]放到arr[b]的位置
                    population[i].chromosome[index2] = temp;
                }
            }
        }
        init(population);
    }

    /**
     * 通过随机生成来寻找全局最优best，如果随机生成一定次数之后没有找到最优解那么
     * @param best
     * @return
     */
    private RPFSP findBestRandom(RPFSP best) {
        int[] ini = Arrays.copyOf(best.chromosome, best.chromosome.length);
        int length = ini.length;
        RPFSP orig = new RPFSP(ini);
        double yuanshi = Util.change(orig.getMaxCompletionTime(orig.decodeChromosome(orig.chromosome)));;
        double bianhua;
        RPFSP chan;
        int i = 0;
        int z = 0;
        do {
            chan = new RPFSP();
            bianhua = Util.change(chan.getMaxCompletionTime(chan.decodeChromosome(chan.chromosome)));
            if(bianhua < yuanshi * BORDER){
                i++;
                int count = 0;
                RPFSP compare;
                do {
                    Random random = new Random();
                    int index1 = random.nextInt(length);
                    int index2 = random.nextInt(length);
                    while(chan.chromosome[index1] == chan.chromosome[index2]){
                        index2 = random.nextInt(length);
                    }
                    int[] change = Arrays.copyOf(chan.chromosome, length);
                    int tmp = chan.chromosome[index1];
                    int tmp2 = chan.chromosome[index2];
                    change = Arrays.stream(change).map(a -> a == tmp ? -1 : a).toArray();
                    change = Arrays.stream(change).map(a -> a == tmp2 ? tmp : a).toArray();
                    change = Arrays.stream(change).map(a -> a == -1 ? tmp2 : a).toArray();
                    compare = new RPFSP(change);
                    bianhua = Util.change(compare.getMaxCompletionTime(compare.decodeChromosome(compare.chromosome)));
                    if(count++ > MAX_LOCALITER){
                        break;
                    }
                } while(yuanshi <= bianhua);
                if (yuanshi > bianhua){
                    System.out.println("------------------------");
                    return compare;
                }
            }else {

            }
        }while (i < MAX_RANDOMSEARCH);
        // 没初始化，算过了目标值
        System.out.println("随机+局部失败");
        return best;
    }
}
