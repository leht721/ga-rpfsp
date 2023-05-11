package test;

import util.Util;

import java.util.Arrays;
import java.util.Random;

public class NormalDistributionGenerator {

    public static double calculateVariance(double[] data) {
        double mean = 0;
        double sum = 0;
        double variance = 0;

        // 计算平均值
        for (int i = 0; i < data.length; i++) {
            mean += data[i];
        }
        mean /= data.length;

        // 计算方差
        for (int i = 0; i < data.length; i++) {
            sum += Math.pow(data[i] - mean, 2);
        }
        variance = sum / (data.length - 1);

        return variance;
    }

    /**
     * 均值为b，方差为c，最小值为d
     * @param a
     * @param b
     * @param c
     * @param d
     * @return
     */
    public static double[] generate(int a, double b, double c, double d) {
        double[] result = new double[a];
        Random random = new Random();

        for (int i = 0; i < a; i++) {
            double num = random.nextGaussian() * Math.sqrt(c) + b;
            result[i] = Math.max(num, d);
        }

        // 确保数组中包含最小值d
        boolean containsD = false;
        for (double num : result) {
            if (num == d) {
                containsD = true;
                break;
            }
        }
        if (!containsD) {
            result[random.nextInt(a)] = d;
        }

        return result;
    }

    public static void main(String[] args) {
        double[] generate = generate(20, 2099, 1190, 1995);
        for (int i = 0; i < generate.length; i++) {
            System.out.println(Util.change(generate[i]));
        }
//        System.out.println(Arrays.toString(generate));

    }
}