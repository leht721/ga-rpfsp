package code.ga; /**
 * @author neulht @create
 * 2023-03-10 10:52
 */
import code.RPFSP;
import util.Util;

import java.util.*;

public class GA {

    // 种群中的染色体数量
    private static final int POPULATION_SIZE = 50;

    // 最大迭代次数
    private static final int MAX_ITERATIONS = 1000;

    // 交叉概率
    private static final double CROSSOVER_RATE = 0.8;

    // 变异概率
    private static final double MUTATION_RATE = 0.1;

    // 种群
    private RPFSP[] population;

    // 最好个体
    private RPFSP best;

    // 目标值迭代数组
    private double[] record = new double[MAX_ITERATIONS];

    public double[] getRecord() {
        return record;
    }

    // 选择染色体进行交叉操作--顺序交叉
    private RPFSP[] crossover(RPFSP[] population) throws CloneNotSupportedException {
        RPFSP[] newPopulation = new RPFSP[POPULATION_SIZE];
        Random random = new Random();
        int i = 0;
        while (i < POPULATION_SIZE) {
            RPFSP parent1 = population[random.nextInt(POPULATION_SIZE)].clone();
            RPFSP parent2 = population[random.nextInt(POPULATION_SIZE)].clone();
            if(random.nextDouble() < CROSSOVER_RATE){
                int[][] offspring = orderCrossover(parent1.chromosome, parent2.chromosome);
                parent1.chromosome = offspring[0];
                parent2.chromosome = offspring[1];
                newPopulation[i++] = parent1;
                newPopulation[i++] = parent2;
            }else {
                newPopulation[i++] = parent1;
                newPopulation[i++] = parent2;
            }
        }
        return newPopulation;
    }

    /**
     * 顺序交叉
     * @param parent1
     * @param parent2
     * @return
     */
    public int[][] orderCrossover(int[] parent1, int[] parent2) {
        int length = parent1.length;
        int startPos = (int) (Math.random() * length); // 随机生成起始位置
        int endPos = (int) (Math.random() * (length - startPos)) + startPos; // 随机生成结束位置
        int[][] offspring = new int[2][parent1.length];
        Arrays.fill(offspring[0], -1);
        Arrays.fill(offspring[1], -1);
        Map<Integer, Integer> map1 = new HashMap<>();
        Map<Integer, Integer> map2 = new HashMap<>();
        for (int i = startPos; i <= endPos; i++) {
            offspring[0][i] = parent1[i];
            map1.put(parent1[i], map1.getOrDefault(parent1[i], 0) + 1);
            offspring[1][i] = parent2[i];
            map2.put(parent2[i], map2.getOrDefault(parent2[i], 0) + 1);
        }
        int[] tmpParent1 = Arrays.copyOf(parent1, parent1.length);
        for (int i = 0; i < tmpParent1.length; i++) {
            if(map2.containsKey(tmpParent1[i]) && map2.get(tmpParent1[i]) > 0){
                map2.put(tmpParent1[i], map2.get(tmpParent1[i]) - 1);
                tmpParent1[i] = -1;
            }
        }
        int[] tmpParent2 = Arrays.copyOf(parent2, parent2.length);
        for (int i = 0; i < tmpParent2.length; i++) {
            if(map1.containsKey(tmpParent2[i]) && map1.get(tmpParent2[i]) > 0){
                map1.put(tmpParent2[i], map1.get(tmpParent2[i]) - 1);
                tmpParent2[i] = -1;
            }
        }
        // 处理剩余部分
        int index1 = 0;
        int index2 = index1;
        while (index1 != parent1.length && index2 != parent1.length){
            while (index1 != parent1.length && offspring[0][index1] != -1) index1++;
            while (index2 != parent1.length && tmpParent2[index2] == -1) index2++;
            if(index1 == parent1.length || index2 == parent1.length) break;
            offspring[0][index1++] = tmpParent2[index2++];
        }
        index1 = 0;
        index2 = index1;
        while (index1 != parent2.length && index2 != parent1.length){
            while (index1 != parent2.length && offspring[1][index1] != -1) index1++;
            while (index2 != parent2.length && tmpParent1[index2] == -1) index2++;
            if(index1 == parent1.length || index2 == parent1.length) break;
            offspring[1][index1++] = tmpParent1[index2++];
        }
        return offspring;
    }

    // 对染色体进行变异操作
    private void mutate(RPFSP[] population) {
        Random random = new Random();
        int length = population[0].chromosome.length;
        for (int i = 0; i < POPULATION_SIZE; i++) {
            if (random.nextDouble() < MUTATION_RATE) {
                int index1 = random.nextInt(length);
                int index2 = random.nextInt(length);
                while(population[i].chromosome[index1] == population[i].chromosome[index2]){
                    index2 = random.nextInt(length);
                }
                int tmp = population[i].chromosome[index1];
                int tmp2 = population[i].chromosome[index2];
                population[i].chromosome = Arrays.stream(population[i].chromosome).map(a -> a == tmp ? -1 : a).toArray();
                population[i].chromosome = Arrays.stream(population[i].chromosome).map(a -> a == tmp2 ? tmp : a).toArray();
                population[i].chromosome = Arrays.stream(population[i].chromosome).map(a -> a == -1 ? tmp2 : a).toArray();
            }
        }
    }



    // 执行遗传算法来求解可重入置换流水车间问题
    public RPFSP solve() throws CloneNotSupportedException {
        // 初始化种群
        population = new RPFSP[POPULATION_SIZE];
        for (int i = 0; i < population.length; i++) {
            population[i] = new RPFSP();
        }
        Map<Double, Integer> chushi = new TreeMap<>();
        for (int j = 0; j < population.length; j++) {
            chushi.put(population[j].getMaxCompletionTime(population[j].decodeChromosome(population[j].chromosome)), j);
        }
        double sum = 0;
        int in = 0;
        for (Double d : chushi.keySet()) {
//                System.out.println(d);
            sum += d;
            in++;
        }
        System.out.println("avg=" + (sum/in));
        System.out.println(" ===");
//        population[0] = new RPFSP(new int[]{18, 5, 13, 10, 4, 6, 18, 11, 17, 10, 5, 2, 14, 11, 0, 18, 8, 19, 12, 6, 16, 0, 3, 9, 11, 8, 1, 10, 6, 12, 0, 9, 16, 15, 2, 14, 1, 4, 7, 19, 3, 17, 1, 14, 15, 13, 16, 7, 4, 15, 5, 13, 12, 2, 3, 9, 7, 8, 19, 17});
//        population[0] = new RPFSP(new int[]{18, 5, 13, 10, 4, 6, 18, 11, 17, 10, 5, 2, 14, 11, 0, 18, 8, 19, 12, 6, 16, 0, 3, 9, 11, 8, 1, 10, 6, 12, 0, 9, 16, 15, 2, 14, 1, 4, 7, 19, 3, 17, 1, 14, 15, 13, 16, 7, 4, 15, 5, 13, 12, 2, 3, 9, 7, 8, 19, 17});
//        population[0] = new RPFSP(new int[]{18, 5, 13, 10, 4, 6, 18, 11, 17, 10, 5, 2, 14, 11, 0, 18, 8, 19, 12, 6, 16, 0, 3, 9, 11, 8, 1, 10, 6, 12, 0, 9, 16, 15, 2, 14, 1, 4, 7, 19, 3, 17, 1, 14, 15, 13, 16, 7, 4, 15, 5, 13, 12, 2, 3, 9, 7, 8, 19, 17});
//        population[0] = new RPFSP(new int[]{18, 5, 13, 10, 4, 6, 18, 11, 17, 10, 5, 2, 14, 11, 0, 18, 8, 19, 12, 6, 16, 0, 3, 9, 11, 8, 1, 10, 6, 12, 0, 9, 16, 15, 2, 14, 1, 4, 7, 19, 3, 17, 1, 14, 15, 13, 16, 7, 4, 15, 5, 13, 12, 2, 3, 9, 7, 8, 19, 17});
//        population[0] = new RPFSP(new int[]{18, 5, 13, 10, 4, 6, 18, 11, 17, 10, 5, 2, 14, 11, 0, 18, 8, 19, 12, 6, 16, 0, 3, 9, 11, 8, 1, 10, 6, 12, 0, 9, 16, 15, 2, 14, 1, 4, 7, 19, 3, 17, 1, 14, 15, 13, 16, 7, 4, 15, 5, 13, 12, 2, 3, 9, 7, 8, 19, 17});
//        population[0] = new RPFSP(new int[]{18, 5, 13, 10, 4, 6, 18, 11, 17, 10, 5, 2, 14, 11, 0, 18, 8, 19, 12, 6, 16, 0, 3, 9, 11, 8, 1, 10, 6, 12, 0, 9, 16, 15, 2, 14, 1, 4, 7, 19, 3, 17, 1, 14, 15, 13, 16, 7, 4, 15, 5, 13, 12, 2, 3, 9, 7, 8, 19, 17});
//        population[0] = new RPFSP(new int[]{18, 5, 13, 10, 4, 6, 18, 11, 17, 10, 5, 2, 14, 11, 0, 18, 8, 19, 12, 6, 16, 0, 3, 9, 11, 8, 1, 10, 6, 12, 0, 9, 16, 15, 2, 14, 1, 4, 7, 19, 3, 17, 1, 14, 15, 13, 16, 7, 4, 15, 5, 13, 12, 2, 3, 9, 7, 8, 19, 17});
//        population[0] = new RPFSP(new int[]{9, 1, 4, 3, 6, 8, 2, 5, 0, 4, 3, 5, 8, 7, 4, 2, 9, 1, 0, 6, 7, 8, 3, 0, 5, 7, 2, 6, 9, 1});
//        population[0] = new RPFSP(new int[]{9, 1, 4, 3, 6, 8, 2, 5, 0, 4, 3, 5, 8, 7, 4, 2, 9, 1, 0, 6, 7, 8, 3, 0, 5, 7, 2, 6, 9, 1});
//        population[1] = new RPFSP(new int[]{4, 9, 1, 7, 5, 8, 0, 6, 2, 3, 4, 9, 7, 5, 6, 2, 3, 0, 9, 8, 4, 7, 5, 1, 6, 3, 0, 2, 8, 1});
//        population[2] = new RPFSP(new int[]{4, 9, 1, 7, 5, 8, 0, 6, 2, 3, 4, 9, 7, 5, 6, 2, 3, 0, 9, 8, 4, 7, 5, 1, 6, 3, 0, 2, 8, 1});
//        population[3] = new RPFSP(new int[]{4, 9, 1, 7, 5, 8, 0, 6, 2, 3, 4, 9, 7, 5, 6, 2, 3, 0, 9, 8, 4, 7, 5, 1, 6, 3, 0, 2, 8, 1});
//        population[3] = new RPFSP(new int[]{7, 0, 7, 4, 1, 2, 3, 4, 1, 6, 5, 9, 7, 8, 0, 2, 1, 3, 8, 6, 5, 9, 3, 4, 2, 8, 9, 6, 5, 0});//2682
//        population[0] = new RPFSP(new int[]{28, 25, 3, 24, 20, 4, 11, 24, 17, 25, 5, 22, 0, 21, 12, 14, 5, 17, 27, 28, 11, 16, 6, 12, 26, 10, 3, 18, 22, 27, 16, 8, 9, 24, 21, 26, 29, 13, 20, 1, 17, 19, 4, 6, 7, 14, 16, 23, 22, 5, 8, 29, 4, 2, 14, 10, 15, 9, 6, 29, 7, 25, 19, 27, 3, 28, 15, 20, 26, 0, 1, 21, 19, 2, 18, 13, 8, 15, 23, 9, 10, 2, 13, 12, 7, 18, 0, 1, 23, 11});
//        population[1] = new RPFSP(new int[]{28, 25, 3, 24, 20, 4, 11, 24, 17, 25, 5, 22, 0, 21, 12, 14, 5, 17, 27, 28, 11, 16, 6, 12, 26, 10, 3, 18, 22, 27, 16, 8, 9, 24, 21, 26, 29, 13, 20, 1, 17, 19, 4, 6, 7, 14, 16, 23, 22, 5, 8, 29, 4, 2, 14, 10, 15, 9, 6, 29, 7, 25, 19, 27, 3, 28, 15, 20, 26, 0, 1, 21, 19, 2, 18, 13, 8, 15, 23, 9, 10, 2, 13, 12, 7, 18, 0, 1, 23, 11});
//        population[2] = new RPFSP(new int[]{28, 25, 3, 24, 20, 4, 11, 24, 17, 25, 5, 22, 0, 21, 12, 14, 5, 17, 27, 28, 11, 16, 6, 12, 26, 10, 3, 18, 22, 27, 16, 8, 9, 24, 21, 26, 29, 13, 20, 1, 17, 19, 4, 6, 7, 14, 16, 23, 22, 5, 8, 29, 4, 2, 14, 10, 15, 9, 6, 29, 7, 25, 19, 27, 3, 28, 15, 20, 26, 0, 1, 21, 19, 2, 18, 13, 8, 15, 23, 9, 10, 2, 13, 12, 7, 18, 0, 1, 23, 11});
//        population[3] = new RPFSP(new int[]{28, 25, 3, 24, 20, 4, 11, 24, 17, 25, 5, 22, 0, 21, 12, 14, 5, 17, 27, 28, 11, 16, 6, 12, 26, 10, 3, 18, 22, 27, 16, 8, 9, 24, 21, 26, 29, 13, 20, 1, 17, 19, 4, 6, 7, 14, 16, 23, 22, 5, 8, 29, 4, 2, 14, 10, 15, 9, 6, 29, 7, 25, 19, 27, 3, 28, 15, 20, 26, 0, 1, 21, 19, 2, 18, 13, 8, 15, 23, 9, 10, 2, 13, 12, 7, 18, 0, 1, 23, 11});
//        population[4] = new RPFSP(new int[]{28, 25, 3, 24, 20, 4, 11, 24, 17, 25, 5, 22, 0, 21, 12, 14, 5, 17, 27, 28, 11, 16, 6, 12, 26, 10, 3, 18, 22, 27, 16, 8, 9, 24, 21, 26, 29, 13, 20, 1, 17, 19, 4, 6, 7, 14, 16, 23, 22, 5, 8, 29, 4, 2, 14, 10, 15, 9, 6, 29, 7, 25, 19, 27, 3, 28, 15, 20, 26, 0, 1, 21, 19, 2, 18, 13, 8, 15, 23, 9, 10, 2, 13, 12, 7, 18, 0, 1, 23, 11});
//        population[0] = new RPFSP(new int[]{39, 38, 21, 29, 8, 13, 11, 12, 17, 6, 24, 4, 35, 22, 39, 0, 5, 17, 28, 33, 13, 39, 35, 10, 11, 9, 3, 0, 22, 15, 26, 2, 1, 28,
//                32, 19, 24, 36, 18, 32, 38, 3, 31, 20, 10, 26, 7, 34, 23, 5, 12, 37, 30, 15, 13, 15, 34, 16, 28, 38, 21, 25, 11, 12, 33, 25, 34, 14, 37, 36,
//                20, 30, 2, 16, 10, 21, 1, 6, 23, 4, 14, 24, 8, 5, 0, 3, 36, 35, 30, 37, 26, 23, 29, 19, 9, 2, 31, 7, 16, 27, 20, 18, 4, 17, 14, 31, 27, 22, 29,
//                32, 19, 8, 25, 7, 1, 9, 27, 33, 18, 6});
//        population[1] = new RPFSP(new int[]{39, 38, 21, 29, 8, 13, 11, 12, 17, 6, 24, 4, 35, 22, 39, 0, 5, 17, 28, 33, 13, 39, 35, 10, 11, 9, 3, 0, 22, 15, 26, 2, 1, 28,
//                32, 19, 24, 36, 18, 32, 38, 3, 31, 20, 10, 26, 7, 34, 23, 5, 12, 37, 30, 15, 13, 15, 34, 16, 28, 38, 21, 25, 11, 12, 33, 25, 34, 14, 37, 36,
//                20, 30, 2, 16, 10, 21, 1, 6, 23, 4, 14, 24, 8, 5, 0, 3, 36, 35, 30, 37, 26, 23, 29, 19, 9, 2, 31, 7, 16, 27, 20, 18, 4, 17, 14, 31, 27, 22, 29,
//                32, 19, 8, 25, 7, 1, 9, 27, 33, 18, 6});
//        population[2] = new RPFSP(new int[]{39, 38, 21, 29, 8, 13, 11, 12, 17, 6, 24, 4, 35, 22, 39, 0, 5, 17, 28, 33, 13, 39, 35, 10, 11, 9, 3, 0, 22, 15, 26, 2, 1, 28,
//                32, 19, 24, 36, 18, 32, 38, 3, 31, 20, 10, 26, 7, 34, 23, 5, 12, 37, 30, 15, 13, 15, 34, 16, 28, 38, 21, 25, 11, 12, 33, 25, 34, 14, 37, 36,
//                20, 30, 2, 16, 10, 21, 1, 6, 23, 4, 14, 24, 8, 5, 0, 3, 36, 35, 30, 37, 26, 23, 29, 19, 9, 2, 31, 7, 16, 27, 20, 18, 4, 17, 14, 31, 27, 22, 29,
//                32, 19, 8, 25, 7, 1, 9, 27, 33, 18, 6});
//        population[3] = new RPFSP(new int[]{39, 38, 21, 29, 8, 13, 11, 12, 17, 6, 24, 4, 35, 22, 39, 0, 5, 17, 28, 33, 13, 39, 35, 10, 11, 9, 3, 0, 22, 15, 26, 2, 1, 28,
//                32, 19, 24, 36, 18, 32, 38, 3, 31, 20, 10, 26, 7, 34, 23, 5, 12, 37, 30, 15, 13, 15, 34, 16, 28, 38, 21, 25, 11, 12, 33, 25, 34, 14, 37, 36,
//                20, 30, 2, 16, 10, 21, 1, 6, 23, 4, 14, 24, 8, 5, 0, 3, 36, 35, 30, 37, 26, 23, 29, 19, 9, 2, 31, 7, 16, 27, 20, 18, 4, 17, 14, 31, 27, 22, 29,
//                32, 19, 8, 25, 7, 1, 9, 27, 33, 18, 6});
//        population[4] = new RPFSP(new int[]{39, 38, 21, 29, 8, 13, 11, 12, 17, 6, 24, 4, 35, 22, 39, 0, 5, 17, 28, 33, 13, 39, 35, 10, 11, 9, 3, 0, 22, 15, 26, 2, 1, 28,
//                32, 19, 24, 36, 18, 32, 38, 3, 31, 20, 10, 26, 7, 34, 23, 5, 12, 37, 30, 15, 13, 15, 34, 16, 28, 38, 21, 25, 11, 12, 33, 25, 34, 14, 37, 36,
//                20, 30, 2, 16, 10, 21, 1, 6, 23, 4, 14, 24, 8, 5, 0, 3, 36, 35, 30, 37, 26, 23, 29, 19, 9, 2, 31, 7, 16, 27, 20, 18, 4, 17, 14, 31, 27, 22, 29,
//                32, 19, 8, 25, 7, 1, 9, 27, 33, 18, 6});
//        population[0] = new RPFSP(new int[]{9, 4, 1, 6, 2, 0, 5, 1, 3, 6, 3, 7, 8, 5, 2, 9, 8, 3, 0, 2, 5, 8, 4, 1, 7, 9, 6, 4, 7, 0});
//        population[1] = new RPFSP(new int[]{9, 4, 1, 6, 2, 0, 5, 1, 3, 6, 3, 7, 8, 5, 2, 9, 8, 3, 0, 2, 5, 8, 4, 1, 7, 9, 6, 4, 7, 0});
//        population[2] = new RPFSP(new int[]{9, 4, 1, 6, 2, 0, 5, 1, 3, 6, 3, 7, 8, 5, 2, 9, 8, 3, 0, 2, 5, 8, 4, 1, 7, 9, 6, 4, 7, 0});
//        population[3] = new RPFSP(new int[]{9, 4, 1, 6, 2, 0, 5, 1, 3, 6, 3, 7, 8, 5, 2, 9, 8, 3, 0, 2, 5, 8, 4, 1, 7, 9, 6, 4, 7, 0});
//        population[4] = new RPFSP(new int[]{9, 4, 1, 6, 2, 0, 5, 1, 3, 6, 3, 7, 8, 5, 2, 9, 8, 3, 0, 2, 5, 8, 4, 1, 7, 9, 6, 4, 7, 0});

        // 迭代执行交叉和变异操作，更新种群
        for (int i = 0; i < MAX_ITERATIONS; i++) {
            RPFSP[] offspring = crossover(population);
            mutate(offspring);
            RPFSP[] combinedPopulation = new RPFSP[POPULATION_SIZE * 2];
            System.arraycopy(population, 0, combinedPopulation, 0, POPULATION_SIZE);
            System.arraycopy(offspring, 0, combinedPopulation, POPULATION_SIZE, POPULATION_SIZE);
            Map<Double, Integer> treemap = new TreeMap<>();
            for (int j = 0; j < combinedPopulation.length; j++) {
                treemap.put(combinedPopulation[j].getMaxCompletionTime(combinedPopulation[j].decodeChromosome(combinedPopulation[j].chromosome)), j);
            }
            int index = 0;
//            population = new RPFSP[population.length];
            for (Double d : treemap.keySet()) {
//                System.out.println(d);
                population[index++] = combinedPopulation[treemap.get(d)];
                if(index == POPULATION_SIZE) break;
            }
            best = population[0].clone();
            best.init();
            double val = Util.change(best.getMaxCompletionTime(best.decodeChromosome(best.chromosome)));
            record[i] = val;
            System.out.print("第" + (i+1) + "代:CMAX = " + val);
            System.out.println("   序列：" + Arrays.toString(best.chromosome));
            if(i == MAX_ITERATIONS - 1){
                break;
            }else init(population);
        }

        // 返回种群中最优解
        RPFSP optIndividual = population[0];
        optIndividual.init();
        double bestfit = optIndividual.calculateFitness();
        for (int i = 1; i < POPULATION_SIZE; i++) {
            population[i].init();
            double completionFit = population[i].calculateFitness();
            if (completionFit > bestfit) {
                optIndividual = population[i];
                bestfit = completionFit;
            }
        }
        return optIndividual;
    }

    private void init(RPFSP[] population) {
        for (int i = 0; i < population.length; i++) {
            population[i].init();
        }
    }
}