package test;

import code.RPFSP;
import code.ga.GA;
import code.pso.PSO;
import code.spso.SA_PSO;
import code.vns_pso.VNS_PSO;
import util.Util;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.*;

public class MultiThreadProgramGA {
    public static int i = 0;
    public static void main(String[] args) {
        int n = 5; // 数组长度
        int b = 5; // 最大线程数

        double[] arr = new double[n]; // 创建数组

        Arrays.parallelSetAll(arr, i -> a());// 并行执行函数a，并将返回的值插入数组arr中

        double sum = DoubleStream.of(arr).sum(); // 求数组arr的和
        double average = sum / n; // 求平均值
        System.out.println("Array average: " + average); // 打印平均值
        System.out.println(Arrays.toString(arr));
    }
    private static final Lock lock = new ReentrantLock();
    // 模拟函数a
    public static double a() {


        GA ga = new GA();
        RPFSP best = null;

        try {
            best = ga.solve();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        lock.lock();
        try {
            Util.write(ga.getRecord(), "C:\\Users\\82413\\Desktop\\参数\\44.xlsx", i++, 1);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        double a = Util.change(best.getMaxCompletionTime(best.decodeChromosome(best.chromosome)));
        return a;// 返回一个随机值
    }
}


