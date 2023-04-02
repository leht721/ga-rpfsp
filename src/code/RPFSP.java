package code;


import util.Pair;
import util.Util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static util.Util.shuffleArray;

/**
 * @author neulht @create
 * 2023-03-14 16:23
 */
public class RPFSP implements Cloneable {
    private static int[][] processingTimes; // 工件在各个生产阶段的加工时间
    private static int[][] mTimes; // 工件在各个生产阶段的加工时间
    private static int m;  // 生产车间中的机器数量
    private static int n; // 工件个数
    private static int beta; //Weibull分布形状参数β
    private static int yita; //Weibull分布尺度参数η
    private static double tmr; //MR时间
    private static double tpm; //PM时间
    private static double T; //维护周期
    private static int up; //时间窗上界
    private static int low; //时间窗下界
    private static double[] lamda; //恶化因子
    private static int l; //重入层数
    private static int cLength; //染色体长度

    private int[][] pmMatrix; //PM矩阵
    private double pmTime; //PM总时间
    private Map<Pair, Double> recordPM; //PM记录
    private double[] ctime; //每台机器连续加工时间
    private double[] etime; //每台机器由于恶化和MR造成的恶化时间
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
            mTimes = change(processingTimes);
            String[] arr = reader.readLine().split(" ");
            lamda = new double[m];
            for (int i = 0; i < arr.length; i++) {
                lamda[i] = Double.valueOf(arr[i]);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int[][] change(int[][] processingTimes) {
        int[][] mTimes = new int[processingTimes[0].length][processingTimes.length];
        for (int i = 0; i < processingTimes.length; i++) {
            for (int j = 0; j < processingTimes[0].length; j++) {
                mTimes[j][i] = processingTimes[i][j];
            }
        }
        return mTimes;
    }
    public RPFSP() {
        this.pmMatrix = new int[m][cLength];
        this.pmTime = 0.0;
        this.recordPM = new HashMap<Pair, Double>();
        this.ctime = new double[m];
        this.etime = new double[m];
        this.schedule = new double[m][cLength][2];
        chromosome = new int[cLength];
        this.starts = new double[m];
        int k = 0;
        for (int j = 0; j < chromosome.length; j++) {
            chromosome[j++] = k;
            chromosome[j] = k++;
        }
        chromosome = shuffleArray(chromosome);
    }

    public RPFSP(int[] chromosome) {
        this.pmMatrix = new int[m][cLength];
        this.pmTime = 0.0;
        this.recordPM = new HashMap<Pair, Double>();
        this.ctime = new double[m];
        this.etime = new double[m];
        this.schedule = new double[m][cLength][2];
        this.chromosome = Arrays.copyOf(chromosome, chromosome.length);
        this.starts = new double[m];
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
            T = Util.change(yita * Math.pow(tpm / (tmr * (beta - 1)), 1.0 / beta));
            String[] sArr1 = reader.readLine().split(" ");
            up = (int) (T + Integer.parseInt(sArr1[0]) + tpm);
            low = (int) (T - Integer.parseInt(sArr1[1]));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public RPFSP clone() throws CloneNotSupportedException {
        RPFSP clone = new RPFSP();
        clone = (RPFSP) super.clone();
        clone.chromosome = Arrays.copyOf(this.chromosome, this.chromosome.length);
        clone.recordPM = new HashMap<>(this.recordPM);
        clone.pmMatrix = new int[m][cLength];
        clone.ctime = new double[m];
        clone.etime = new double[m];
        for (int i = 0; i < clone.ctime.length; i++) {
            clone.ctime[i] = this.ctime[i];
            clone.etime[i] = this.etime[i];
        }
        clone.schedule = new double[m][cLength][2];
        for (int i = 0; i < clone.schedule.length; i++) {
            for (int j = 0; j < clone.schedule[0].length; j++) {
                clone.schedule[i][j][0] = this.schedule[i][j][0];
                clone.schedule[i][j][1] = this.schedule[i][j][1];
            }
        }
        clone.starts = Arrays.copyOf(this.starts, this.starts.length);
        return clone;
    }

    public static double getTpm() {
        return tpm;
    }

    public static double getT() {
        return T;
    }

    public static int getUp() {
        return up;
    }

    public static int getLow() {
        return low;
    }

    public static int getM() {
        return m;
    }

    public static int getN() {
        return n;
    }

    public int[][] getPmMatrix() {
        return pmMatrix;
    }

    public static int getL() {
        return l;
    }

    public double getPmTime() {
        return pmTime;
    }

    public Map<Pair, Double> getRecordPM() {
        return recordPM;
    }

    public double[] getEtime() {
        return etime;
    }

    public double[][][] getSchedule() {
        return schedule;
    }


    public void init(){
        this.pmMatrix = new int[m][cLength];
        this.pmTime = 0.0;
        this.recordPM = new HashMap<Pair, Double>();
        this.ctime = new double[m];
        this.etime = new double[m];
        this.schedule = new double[m][cLength][2];
    }

    // 计算染色体的适应度
    public double calculateFitness() {
        double[][][] schedule = decodeChromosome(this.chromosome);
        double maxCompletionTime = getMaxCompletionTime(schedule);
        return 1.0 / maxCompletionTime;
    }



    /**
     * 解码染色体为生产计划---待测试
     * TODO 测试
     * @param chromosome
     * @return
     */
    public double[][][] decodeChromosome(int[] chromosome) {
        this.schedule = new double[processingTimes.length][processingTimes[0].length][2];
        schedule[0][0] = new double[]{0, processingTimes[0][chromosome[0]]};
        ctime[0] = schedule[0][0][1];
        for (int i = 1; i < processingTimes.length; i++) {
            schedule[i][0] = new double[]{schedule[i - 1][0][1], schedule[i - 1][0][1] + processingTimes[i][chromosome[0]]};
            ctime[i] = schedule[i][0][1] - schedule[i - 1][0][1];
        }
        for (int i = 0; i < starts.length; i++) {
            starts[i] = schedule[i][0][0];
        }
        Map<Integer, Double> map = new HashMap();
        Map<Integer, Integer> recordL = new HashMap<Integer, Integer>();
        map.put(chromosome[0], schedule[processingTimes.length - 1][0][1]);
        recordL.put(chromosome[0], 0);
        for (int i = 1; i < chromosome.length; i++) {
            if(map.containsKey(chromosome[i])){
                calculateAndInsertWithMap(i, chromosome[i], chromosome[i] + n * recordL.getOrDefault(chromosome[i], 0), map);
                for (int j = 1; j < processingTimes.length; j++) {
                    calculateAndInsert(j, i, chromosome[i], chromosome[i] + n * recordL.getOrDefault(chromosome[i], 0));
                }
                recordL.put(chromosome[i], recordL.getOrDefault(chromosome[i], 0) + 1);
            }else {
                for (int j = 0; j < processingTimes.length; j++) {
                    calculateAndInsert(j, i, chromosome[i], chromosome[i]);
                }
                map.put(chromosome[i], schedule[processingTimes.length - 1][i][1]);
                recordL.put(chromosome[i], 0);
            }
        }
        return schedule;
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
        if(pmMatrix[indexM][indexPJob - 1] == 1){
            starts[indexM] = schedule[indexM][indexPJob][0] = Math.max(recordPM.get(pair) + tpm, prevM);
            double age_e = processingTimes[indexM][ptIndex];//预测役龄
            schedule[indexM][indexPJob][1] = schedule[indexM][indexPJob][0] + processingTimes[indexM][ptIndex] + Util.change(Math.pow(age_e / yita, beta)) * tmr;//MR时间计算也是使用预测役龄
            etime[indexM] += Math.pow(age_e / yita, beta);
            ctime[indexM] += schedule[indexM][indexPJob][1] - schedule[indexM][indexPJob][0];//实际役龄迭代需要计算攻坚实际加工时间
        }else {
            double start = Math.max(schedule[indexM][indexPJob - 1][1], prevM);
            double age_e = ctime[indexM] + processingTimes[indexM][ptIndex] + lamda[indexM] * ctime[indexM];//预测役龄只计算了工件的加工时间和恶化时间，不算MR
            double end_e = start + processingTimes[indexM][ptIndex] + lamda[indexM] * ctime[indexM] +
                    Util.change(Math.pow(age_e / yita, beta) - Math.pow(ctime[indexM] / yita, beta)) * tmr;
            if(end_e - starts[indexM] <= low){
                schedule[indexM][indexPJob][0] = Util.change(start);
                schedule[indexM][indexPJob][1] = end_e;
                etime[indexM] += lamda[indexM] * ctime[indexM] + Util.change(Math.pow(age_e / yita, beta) - Math.pow(ctime[indexM] / yita, beta)) * tmr;
                ctime[indexM] += schedule[indexM][indexPJob][1] - schedule[indexM][indexPJob][0];
            }else if(end_e - starts[indexM] + tpm <= up){
                schedule[indexM][indexPJob][0] = Util.change(start);
                schedule[indexM][indexPJob][1] = end_e;
                etime[indexM] += lamda[indexM] * ctime[indexM] + Util.change(Math.pow(age_e / yita, beta) - Math.pow(ctime[indexM] / yita, beta)) * tmr;
                ctime[indexM] += schedule[indexM][indexPJob][1] - schedule[indexM][indexPJob][0];
                pmMatrix[indexM][indexPJob] = 1;
                recordPM.put(new Pair(indexM, indexPJob), schedule[indexM][indexPJob][1]);
                pmTime += tpm;
                ctime[indexM] = 0;
            }else {
                pmMatrix[indexM][indexPJob - 1] = 1;
                recordPM.put(pair, starts[indexM] + low);
                pmTime += tpm;
                starts[indexM] = schedule[indexM][indexPJob][0] = Math.max(recordPM.get(pair) + tpm, prevM);
                double age_e_e = processingTimes[indexM][ptIndex];//役龄预测不算MR
                schedule[indexM][indexPJob][1] = schedule[indexM][indexPJob][0] + processingTimes[indexM][ptIndex] + Util.change(Math.pow(age_e_e / yita, beta)) * tmr;
                etime[indexM] += Math.pow(age_e_e / yita, beta);
                ctime[indexM] = schedule[indexM][indexPJob][1] - schedule[indexM][indexPJob][0];
            }
        }
    }

    /**
     * 在零号机器时，当前工件不是在第一层加工————计算工件的实际加工时间并且判断是否进行PM,然后重新计算schedule数组
     * @param indexPJob 当前工件在加工工序中的索引
     * @param indexC 工件索引
     * @param map 记录上一层加工结束时间
     */
    public void calculateAndInsertWithMap(int indexPJob, int indexC, int ptIndex, Map<Integer, Double> map) {
        Pair pair = new Pair(0, indexPJob - 1);
        if(pmMatrix[0][indexPJob - 1] == 1){
            starts[0] = schedule[0][indexPJob][0] = Math.max(recordPM.get(pair) + tpm, map.get(indexC));
            double age_e = processingTimes[0][ptIndex];//役龄预测不算MR
            schedule[0][indexPJob][1] = schedule[0][indexPJob][0] + processingTimes[0][ptIndex] + Util.change(Math.pow(age_e / yita, beta)) * tmr;//MR时间计算也是使用预测役龄
            etime[0] += Math.pow(age_e / yita, beta);
            ctime[0] += schedule[0][indexPJob][1] - schedule[0][indexPJob][0];//实际役龄迭代需要计算攻坚实际加工时间
        }else {
            double start = Math.max(schedule[0][indexPJob - 1][1], map.get(indexC));
            double age_e = ctime[0] + processingTimes[0][ptIndex] + lamda[0] * ctime[0];//预测役龄只计算了工件的加工时间和恶化时间
            double end_e = start + processingTimes[0][ptIndex] + lamda[0] * ctime[0] +
                    Util.change(Math.pow(age_e / yita, beta) - Math.pow(ctime[0] / yita, beta)) * tmr;// 预测加工结束时间
            if((end_e - starts[0]) <= low){
                schedule[0][indexPJob][0] = Util.change(start);
                schedule[0][indexPJob][1] = end_e;
                etime[0] += lamda[0] * ctime[0] + Util.change(Math.pow(age_e / yita, beta) - Math.pow(ctime[0] / yita, beta)) * tmr;
                ctime[0] += schedule[0][indexPJob][1] - schedule[0][indexPJob][0];
            }else if(end_e - starts[0] + tpm <= up){
                schedule[0][indexPJob][0] = Util.change(start);
                schedule[0][indexPJob][1] = end_e;
                etime[0] += lamda[0] * ctime[0] + Util.change(Math.pow(age_e / yita, beta) - Math.pow(ctime[0] / yita, beta)) * tmr;
                ctime[0] += schedule[0][indexPJob][1] - schedule[0][indexPJob][0];
                pmMatrix[0][indexPJob] = 1;
                recordPM.put(new Pair(0, indexPJob), schedule[0][indexPJob][1]);
                pmTime += tpm;
                ctime[0] = 0;
            }else {
                pmMatrix[0][indexPJob - 1] = 1;
                recordPM.put(pair, starts[0] + low);
                pmTime += tpm;
                starts[0] = schedule[0][indexPJob][0] = Math.max(recordPM.get(pair) + tpm, map.get(indexC));
                double age_e_e = processingTimes[0][ptIndex];//役龄预测不算MR
                schedule[0][indexPJob][1] = schedule[0][indexPJob][0] + processingTimes[0][ptIndex] + Util.change(Math.pow(age_e_e / yita, beta)) * tmr;
                etime[0] += Math.pow(age_e_e / yita, beta);
                ctime[0] = schedule[0][indexPJob][1] - schedule[0][indexPJob][0];
            }
        }
    }

    // 获取生产计划中最大的完成时间
    public double getMaxCompletionTime(double[][][] schedule) {
        return schedule[processingTimes.length - 1][processingTimes[0].length - 1][1];
    }

    public int[] decode(String encoding) {
        int[] permutation = new int[cLength];
        switch (encoding) {
            case "random":
                for (int i = 0; i < n; i++) {
                    permutation[i] = i;
                }
                Util.shuffleArray(permutation);
                break;
            // Other decoding methods can be implemented here
            default:
                throw new IllegalArgumentException("Invalid encoding method: " + encoding);
        }
        return permutation;
    }

    public static int[] getJobOrder(double[] position) {
        JobIndexPair[] jobIndexPairs = new JobIndexPair[cLength];
        int index = 0;
        int i = 0;
        while (i < cLength) {
            for (int j = 0; j < l; j++) {
                jobIndexPairs[i] = new JobIndexPair(index, position[i]);
                i++;
            }
            index++;
        }
        Arrays.sort(jobIndexPairs, Comparator.comparingDouble(j -> j.position));

        int[] jobOrder = new int[cLength];
        i = index = 0;
        while (i < cLength) {
            for (int j = 0; j < l; j++) {
                jobOrder[i] = jobIndexPairs[i].index;
                i++;
            }
        }
        jobOrder[index] = jobIndexPairs[index].index;
        return jobOrder;
    }

}
