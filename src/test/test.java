package test;

import util.Util;

import java.util.stream.DoubleStream;

/**
 * @author neulht @create
 * 2023-03-22 19:27
 */
public class test {
    public static void main(String[] args) {
        Util.generateMatrix(15, 90, 10, 90);
//        System.out.println(Util.findLow(53, 510, 215.44));
//        int i = 0;
//        i += 2 - 1;
//        System.out.println(-Math.log(0.8));
//        System.out.println(Math.pow(-Math.log(0.8), 1.0/3) * 400);
//        double[] doubles = {1936.75, 1936.75, 2047.08, 1936.75, 1936.75, 2151.92, 2073.9, 1936.75, 2045.63, 1936.75, 2072.55, 1987.24, 2056.17, 1936.75, 1936.75, 2191.43, 2047.08, 2056.17, 2047.08, 1987.24};
//        for (int i = 0; i < doubles.length; i++) {
//            System.out.println(doubles[i]);
//        }
//        double sum = DoubleStream.of(doubles).sum(); // 求数组arr的和
//        double average = sum / 20; // 求平均值
//        System.out.println("Array average: " + average); // 打印平均值
//        double[] arr = new double[]{};
//        printStats(arr);
        System.out.println((7589.4-9107.0) / (9107.0*-0.1));
        System.out.println((5484.362-9107.0) / (9107.0*-0.2));
        System.out.println((1771.8-9107.0) / (9107.0*-0.3));
        System.out.println((5985.414-9107.0) / (9107.0*-0.4));
        System.out.println(1.60*-0.1* 9107.0 +  9107.0);
        System.out.println(1.33*-0.2* 9107.0 +  9107.0);
        System.out.println(1.22*-0.3* 9107.0 +  9107.0);
        System.out.println(1.01*-0.4* 9107.0 +  9107.0);
        System.out.println(Util.change(320 * Math.pow(-Math.log(0.70), 1.0 / 2.00)));
        System.out.println(Util.change(320 * Math.pow(-Math.log(0.75), 1.0 / 2.25)));
        System.out.println(Util.change(320 * Math.pow(-Math.log(0.80), 1.0 / 2.5)));
        System.out.println(Util.change(320 * Math.pow(-Math.log(0.85), 1.0 / 2.75)));
        System.out.println(Util.change(320 * Math.pow(-Math.log(0.90), 1.0 / 3.0)));

        System.out.println(Util.change(340 * Math.pow(-Math.log(0.75), 1.0 / 2.00)));
        System.out.println(Util.change(340 * Math.pow(-Math.log(0.8), 1.0 / 2.25)));
        System.out.println(Util.change(340 * Math.pow(-Math.log(0.85), 1.0 / 2.5)));
        System.out.println(Util.change(340 * Math.pow(-Math.log(0.9), 1.0 / 2.75)));
        System.out.println(Util.change(340 * Math.pow(-Math.log(0.70), 1.0 / 3.0)));

        System.out.println(Util.change(360 * Math.pow(-Math.log(0.8), 1.0 / 2.00)));
        System.out.println(Util.change(360 * Math.pow(-Math.log(0.85), 1.0 / 2.25)));
        System.out.println(Util.change(360 * Math.pow(-Math.log(0.9), 1.0 / 2.5)));
        System.out.println(Util.change(360 * Math.pow(-Math.log(0.7), 1.0 / 2.75)));
        System.out.println(Util.change(360 * Math.pow(-Math.log(0.75), 1.0 / 3.0)));

        System.out.println(Util.change(380 * Math.pow(-Math.log(0.85), 1.0 / 2.00)));
        System.out.println(Util.change(380 * Math.pow(-Math.log(0.9), 1.0 / 2.25)));
        System.out.println(Util.change(380 * Math.pow(-Math.log(0.7), 1.0 / 2.5)));
        System.out.println(Util.change(380 * Math.pow(-Math.log(0.75), 1.0 / 2.75)));
        System.out.println(Util.change(380 * Math.pow(-Math.log(0.8), 1.0 / 3.0)));

        System.out.println(Util.change(380 * Math.pow(-Math.log(0.9), 1.0 / 2.00)));
        System.out.println(Util.change(380 * Math.pow(-Math.log(0.7), 1.0 / 2.25)));
        System.out.println(Util.change(380 * Math.pow(-Math.log(0.75), 1.0 / 2.5)));
        System.out.println(Util.change(380 * Math.pow(-Math.log(0.8), 1.0 / 2.75)));
        System.out.println(Util.change(380 * Math.pow(-Math.log(0.85), 1.0 / 3.0)));


    }

    public static void printStats(double[] arr) {
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        double sum = 0;
        double sumOfSquares = 0;
        int n = arr.length;

        for (double x : arr) {
            if (x < min) {
                min = x;
            }
            if (x > max) {
                max = x;
            }
            sum += x;
            sumOfSquares += x * x;
        }

        double mean = sum / n;
        double variance = (sumOfSquares - n * mean * mean) / (n - 1);
        double stdDev = Math.sqrt(variance);

        System.out.println("Minimum value: " + min);
        System.out.println("Maximum value: " + max);
        System.out.println("Mean value: " + mean);
        System.out.println("Standard deviation: " + stdDev);
    }

}
