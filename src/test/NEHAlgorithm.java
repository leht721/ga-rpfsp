package test;

import code.RPFSP;
import util.Pair;
import util.Util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static util.Util.shuffleArray;

public class NEHAlgorithm {


    private static int[][] processingTimes; // 工件在各个生产阶段的加工时间
    private static int m;  // 生产车间中的机器数量
    private static int n; // 工件个数
    private static int beta; //Weibull分布形状参数β
    private static int yita; //Weibull分布尺度参数η
    private static double tmr; //MR时间
    private static double tpm; //PM时间
    private static double T; //维护周期
    private static double[] lamda; //恶化因子
    private static double[] learning; //学习因子
    private static final double M = 0.6; // 压缩系数
    private static int l; //重入层数
    private static int cLength; //染色体长度

    private int[][] pmMatrix; //PM矩阵
    private double pmTime; //PM总时间
    private Map<Pair, Double> recordPM; //PM记录
    private double[] ctime; //每台机器连续加工时间
    private double[] etime; //每台机器由于线性恶化
    private double[] mrtime; //每台机器MR造成的恶化时间
    private double[] ltime; //学习效应造成的时间
    private double[][][] schedule; //实际加工时间矩阵
    public int[] chromosome; //染色体加工序列
    public double[] starts; //计算每个时间窗的开始时间

    //初始化加工时间矩阵
    static {
        BufferedReader reader = null;
        String line = null; // 读取第一行
        try {
//            reader = new BufferedReader(new FileReader("C:\\Users\\Administrator\\Desktop\\a.txt"));
            reader = new BufferedReader(new FileReader("C:\\Users\\82413\\Desktop\\a.txt"));
            line = reader.readLine();
            String[] firstLine = line.split(" ");
            m = Integer.parseInt(firstLine[0]);
            n = Integer.parseInt(firstLine[1]);
            l = Integer.parseInt(firstLine[2]);
            cLength = n * l;
            processingTimes = new int[m][cLength];
            for (int i = 0; i < m; i++) {
                line = reader.readLine();
                String[] values = line.split(" ");
                for (int j = 0; j < cLength; j++) {
                    processingTimes[i][j] = Integer.parseInt(values[j]);
                }
            }
            String[] arr = reader.readLine().split(" ");
            lamda = new double[m];
            for (int i = 0; i < arr.length; i++) {
                lamda[i] = Double.valueOf(arr[i]);
            }
            String[] arr1 = reader.readLine().split(" ");
            learning = new double[m];
            for (int i = 0; i < arr.length; i++) {
                learning[i] = Double.valueOf(arr1[i]);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //初始化相关参数
    static {
        BufferedReader reader = null;
        try {
//            reader = new BufferedReader(new FileReader("C:\\Users\\Administrator\\Desktop\\b.txt"));
            reader = new BufferedReader(new FileReader("C:\\Users\\82413\\Desktop\\b.txt"));
            String[] sArr = reader.readLine().split(" ");
            tpm = Double.valueOf(sArr[0]);
            tmr = Double.valueOf(sArr[1]);
            beta = Integer.valueOf(sArr[2]);
            yita = Integer.valueOf(sArr[3]);
            T = Util.change(yita * Math.pow(-Math.log(0.8), 1.0 / beta));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public double decodeChromosome(int[] chromosome) {
        this.pmMatrix = new int[m][cLength];
        this.pmTime = 0.0;
        this.recordPM = new HashMap<Pair, Double>();
        this.ctime = new double[m];
        this.etime = new double[m];
        this.mrtime = new double[m];
        this.ltime = new double[m];
        this.schedule = new double[m][cLength][2];
        chromosome = new int[cLength];
        this.starts = new double[m];
        this.schedule = new double[processingTimes.length][chromosome.length][2];
        schedule[0][0] = new double[]{0, processingTimes[0][chromosome.length]};
        ctime[0] = schedule[0][0][1];
        for (int i = 1; i < processingTimes.length; i++) {
            schedule[i][0] = new double[]{schedule[i - 1][0][1], schedule[i - 1][0][1] + processingTimes[i][chromosome[0]]};
            ctime[i] = schedule[i][0][1] - schedule[i - 1][0][1];
        }
        for (int i = 0; i < starts.length; i++) {
            starts[i] = schedule[i][0][0];
        }
        Map<Integer, Double> map = new HashMap();
        Map<Integer, Integer> recordL = new HashMap<>();
        map.put(chromosome[0], schedule[processingTimes.length - 1][0][1]);
        recordL.put(chromosome[0], 1);
        for (int i = 1; i < chromosome.length; i++) {
            if(map.containsKey(chromosome[i])){
                calculateAndInsertWithMap(i, chromosome[i], chromosome[i] + n * recordL.getOrDefault(chromosome[i], 0), map);
                for (int j = 1; j < processingTimes.length; j++) {
                    calculateAndInsert(j, i, chromosome[i], chromosome[i] + n * recordL.getOrDefault(chromosome[i], 0));
                }
                recordL.put(chromosome[i], recordL.getOrDefault(chromosome[i], 0) + 1);
                map.put(chromosome[i], schedule[processingTimes.length - 1][i][1]);
            }else {
                for (int j = 0; j < processingTimes.length; j++) {
                    calculateAndInsert(j, i, chromosome[i], chromosome[i]);
                }
                map.put(chromosome[i], schedule[processingTimes.length - 1][i][1]);
                recordL.put(chromosome[i], 1);
            }
        }
        return schedule[processingTimes.length - 1][chromosome.length - 1][1];
    }

    /**
     * 计算工件的实际加工时间并且判断是否进行PM,然后重新计算schedule数组
     * @param indexM
     * @param indexPJob
     * @param indexC
     */
    public void calculateAndInsert(int indexM, int indexPJob, int indexC, int ptIndex) {
        double prevM;
        if(indexM == 0){
            prevM = 0.0;
        }else {
            prevM = schedule[indexM - 1][indexPJob][1];
        }
        Pair pair = new Pair(indexM, indexPJob - 1);
        double[] learningTime = getLearningTime(processingTimes[indexM][ptIndex], indexM, indexPJob);
        ltime[indexM] += learningTime[1];
        if(pmMatrix[indexM][indexPJob - 1] == 1){
            starts[indexM] = schedule[indexM][indexPJob][0] = Math.max(recordPM.get(pair) + tpm, prevM);
            double age_e = learningTime[0];//预测役龄
            schedule[indexM][indexPJob][1] = schedule[indexM][indexPJob][0] + learningTime[0] + Util.change(Math.pow(age_e / yita, beta)) * tmr;//MR时间计算也是使用预测役龄
            mrtime[indexM] += Math.pow(age_e / yita, beta) * tmr;
            ctime[indexM] += schedule[indexM][indexPJob][1] - schedule[indexM][indexPJob][0];//实际役龄迭代需要计算攻坚实际加工时间
        }else {
            double start = Math.max(schedule[indexM][indexPJob - 1][1], prevM);
            double age_e = ctime[indexM] + learningTime[0] + lamda[indexM] * ctime[indexM];//预测役龄只计算了工件的加工时间和恶化时间，不算MR
            double end_e = start + learningTime[0] + lamda[indexM] * ctime[indexM] +
                    Util.change(Math.pow(age_e / yita, beta) - Math.pow(ctime[indexM] / yita, beta)) * tmr;
            if(age_e <= T){
                schedule[indexM][indexPJob][0] = Util.change(start);
                schedule[indexM][indexPJob][1] = end_e;
                etime[indexM] += lamda[indexM] * ctime[indexM];
                mrtime[indexM] += Util.change(Math.pow(age_e / yita, beta) - Math.pow(ctime[indexM] / yita, beta)) * tmr;
                ctime[indexM] += schedule[indexM][indexPJob][1] - schedule[indexM][indexPJob][0];
            }else {
                pmMatrix[indexM][indexPJob - 1] = 1;
                recordPM.put(pair, schedule[indexM][indexPJob - 1][1]);
                pmTime += tpm;
                starts[indexM] = schedule[indexM][indexPJob][0] = Math.max(recordPM.get(pair) + tpm, prevM);
                double age_e_e = learningTime[0];//役龄预测不算MR
                schedule[indexM][indexPJob][1] = schedule[indexM][indexPJob][0] + learningTime[0] + Util.change(Math.pow(age_e_e / yita, beta)) * tmr;
                mrtime[indexM] += Math.pow(age_e_e / yita, beta) * tmr;
                ctime[indexM] = schedule[indexM][indexPJob][1] - schedule[indexM][indexPJob][0];
            }
        }
    }

    /**
     * 在零号机器时，当前工件不是在第一层加工————计算工件的实际加工时间并且判断是否进行PM,然后重新计算schedule数组
     * @param indexPJob 当前工件在加工工序中的索引
     * @param indexC 工件索引
     * @param ptIndex 加工时间数组索引
     * @param map 记录上一层加工结束时间
     */
    public void calculateAndInsertWithMap(int indexPJob, int indexC, int ptIndex, Map<Integer, Double> map) {
        Pair pair = new Pair(0, indexPJob - 1);
        double[] learningTime = getLearningTime(processingTimes[0][ptIndex], 0, indexPJob);
        ltime[0] += learningTime[1];
        if(pmMatrix[0][indexPJob - 1] == 1){
            starts[0] = schedule[0][indexPJob][0] = Math.max(recordPM.get(pair) + tpm, map.get(indexC));
            double age_e = learningTime[0];//役龄预测不算MR
            schedule[0][indexPJob][1] = schedule[0][indexPJob][0] + learningTime[0] + Util.change(Math.pow(age_e / yita, beta)) * tmr;//MR时间计算也是使用预测役龄
            mrtime[0] += Math.pow(age_e / yita, beta) * tmr;
            ctime[0] += schedule[0][indexPJob][1] - schedule[0][indexPJob][0];//实际役龄迭代需要计算攻坚实际加工时间
        }else {
            double start = Math.max(schedule[0][indexPJob - 1][1], map.get(indexC));
            double age_e = ctime[0] + learningTime[0] + lamda[0] * ctime[0];//预测役龄只计算了工件的加工时间和恶化时间
            double end_e = start + learningTime[0] + lamda[0] * ctime[0] +
                    Util.change(Math.pow(age_e / yita, beta) - Math.pow(ctime[0] / yita, beta)) * tmr;// 预测加工结束时间
            if(age_e <= T){
                schedule[0][indexPJob][0] = Util.change(start);
                schedule[0][indexPJob][1] = end_e;
                etime[0] += lamda[0] * ctime[0];
                mrtime[0] += Util.change(Math.pow(age_e / yita, beta) - Math.pow(ctime[0] / yita, beta)) * tmr;
                ctime[0] += schedule[0][indexPJob][1] - schedule[0][indexPJob][0];
            }else {
                pmMatrix[0][indexPJob - 1] = 1;
                recordPM.put(pair, schedule[0][indexPJob - 1][1]);
                pmTime += tpm;
                starts[0] = schedule[0][indexPJob][0] = Math.max(recordPM.get(pair) + tpm, map.get(indexC));
                double age_e_e = learningTime[0];//役龄预测不算MR
                schedule[0][indexPJob][1] = schedule[0][indexPJob][0] + learningTime[0] + Util.change(Math.pow(age_e_e / yita, beta)) * tmr;
                mrtime[0] += Math.pow(age_e_e / yita, beta) * tmr;
                ctime[0] = schedule[0][indexPJob][1] - schedule[0][indexPJob][0];
            }
        }
    }

    public double[] getLearningTime(double time, int indexM, int indexJ){
        double[] res = new double[2];
        res[0] = time * (M + (1 - M) * Math.pow(indexJ + 1, learning[indexM]));
        res[1] = time - res[0];
        return res;
    }

    // NEH启发式算法
//    public ArrayList<Integer> NEH(int[][] jobs) {
//        ArrayList<Integer> res = new ArrayList<Integer>();
//        int[] l = new int[NEHAlgorithm.n];
//        for (int i = 0; i < l.length; i++) {
//            l[i] = NEHAlgorithm.l - 1;
//        }
//        int[] flag = new int[NEHAlgorithm.n];
//        for (int i = 0; i < NEHAlgorithm.n; i++) {
//            flag[i] = 0;
//        }
//        int[] index = new int[NEHAlgorithm.n];
//        for (int i = 0; i < index.length; i++) {
//            index[i] = 0;
//        }
//        TreeMap<Double, Integer> treemap = new TreeMap<Double, Integer>(new Comparator<Double>() {
//            @Override
//            public int compare(Double o1, Double o2) {
//                return o2.compareTo(o1);
//            }
//        });
//        for (int i = 0; i < NEHAlgorithm.n; i++) {
//            double sum = 0;
//            for (int j = 0; j < processingTimes.length; j++) {
//                sum += processingTimes[j][i];
//            }
//            treemap.put(sum + Math.random(), i);
//        }
//        Double F1 = treemap.firstKey();
//        Double F2 = treemap.firstKey();
//        int integer = treemap.get(F1);
//        int integer1 = treemap.get(F2);
//        int[] ints = {integer, integer1};
//        int[] ints1 = {integer1, integer};
//        if(cal(ints) > cal(ints1)){
//            res.add(integer1);
//            res.add(integer);
//        }else {
//            res.add(integer);
//            res.add(integer1);
//        }
//        flag[integer]++;
//        flag[integer1]++;
//        l[integer]--;
//        l[integer1]--;
//        treemap.remove(F1);
//        treemap.remove(F2);
//        Double sum = 0.0;
//        for (int i = 0; i < processingTimes.length; i++) {
//            sum += processingTimes[i][integer + flag[integer] * NEHAlgorithm.n];
//        }
//        treemap.put(sum, integer);
//        sum = 0.0;
//        for (int i = 0; i < processingTimes.length; i++) {
//            sum += processingTimes[i][integer1 + flag[integer1] * NEHAlgorithm.n];
//        }
//        treemap.put(sum, integer1);
//        while (treemap.size() != 0){
//
//        }
//
//    }
//
//
//    // 测试NEH算法
//    public static void main(String[] args) {
//        int[][] jobs = RPFSP.getProcessingTimes();
//        ArrayList<Integer> order = NEH(jobs);
//        System.out.println("最优调度顺序为：" + order);
//        System.out.println("最小完成时间为：" + getCompletionTime(jobs, order));
//    }

    public double cal(int[] arr){
        NEHAlgorithm nehAlgorithm = new NEHAlgorithm();
        double v = nehAlgorithm.decodeChromosome(new int[]{6,6});
        return v;
    }

    public static void main(String args[]){
        NEHAlgorithm nehAlgorithm = new NEHAlgorithm();
        double v = nehAlgorithm.decodeChromosome(new int[]{6,6});
        System.out.println(v);

    }
 }