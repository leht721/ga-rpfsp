package test;

import code.RPFSP;
import code.ga.GA;
import code.vns_pso.VNS_PSO;
import util.Util;

import java.util.Arrays;
import java.util.concurrent.*;
import java.util.stream.*;

public class MultiThreadProgram {
    public static void main(String[] args) {
        int n = 20; // 数组长度
        int b = 20; // 最大线程数

        double[] arr = new double[n]; // 创建数组

        Arrays.parallelSetAll(arr, i -> a());// 并行执行函数a，并将返回的值插入数组arr中

        double sum = DoubleStream.of(arr).sum(); // 求数组arr的和
        double average = sum / n; // 求平均值
        System.out.println("Array average: " + average); // 打印平均值
        System.out.println(Arrays.toString(arr));
    }

    // 模拟函数a
    public static double a(){

        VNS_PSO vns_pso = new VNS_PSO();
        RPFSP best = vns_pso.solve();
//        GA ga = new GA();
//        RPFSP best = null;
//        try {
//            best = ga.solve();
//        } catch (CloneNotSupportedException e) {
//            e.printStackTrace();
//        }
        double a = Util.change(best.getMaxCompletionTime(best.decodeChromosome(best.chromosome)));
        return a;// 返回一个随机值
    }
}


