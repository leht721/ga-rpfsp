package util;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.io.File;
import java.io.FileOutputStream;

import code.RPFSP;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author neulht @create
 * 2023-03-18 10:41
 */
public class Util {

    /**
     * 保留两位小数
     * @param d
     * @return
     */
    public static double change(double d){
        return Double.valueOf(new DecimalFormat("0.00").format(d));
    }

    /**
     * Fisher-Yates 洗牌算法
     * @param arr
     * @return
     */
    public static int[] shuffleArray(int[] arr) {
        Random rand = new Random();
        for (int i = arr.length - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
        return arr;
    }

    public static double findLow(double now, double T){
        return Math.ceil(now / T) * T;
    }

    public static double findLow(double start, double now, double T){
        double x = Math.ceil((now - start) / T) * T + start;
        return x;
    }

    public static void write(double[][][] arr, int[] chromosome, double[] record,Map<Pair, Double> map, String fileName) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("time_chromosome_obj");
        for (int i = 0; i < arr.length; i++) {
            Row row = sheet.createRow(i);
            for (int j = 0; j < arr[0].length; j++) {
                for (int k = 0; k < 2; k++) {
                    Cell cell = row.createCell(j * 2 + k);
                    cell.setCellValue(arr[i][j][k]);
                }
            }
        }
        Row row = sheet.createRow(sheet.getLastRowNum() + 1);
        for (int i = 0; i < chromosome.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(chromosome[i]);
        }
        Row row1 = sheet.createRow(sheet.getLastRowNum() + 1);
        for (int i = 0; i < record.length; i++) {
            Cell cell = row1.createCell(i);
            cell.setCellValue(record[i]);
        }

        org.apache.poi.ss.usermodel.Sheet sheet1 = workbook.createSheet("pmrecord");
        workbook.setSheetOrder("pmrecord", 1);
        double[][][] arr1 = new double[RPFSP.getM()][RPFSP.getN() * RPFSP.getL()][2];
        for (Map.Entry entry : map.entrySet()){
            Pair key = (Pair) entry.getKey();
            arr1[key.getMachine()][key.getJob()][0] = (double) entry.getValue();
            arr1[key.getMachine()][key.getJob()][1] = (double) entry.getValue() + RPFSP.getTpm() ;

        }
        for (int i = 0; i < arr1.length; i++) {
            Row row2 = sheet1.createRow(i);
            for (int j = 0; j < arr1[0].length; j++) {
                for (int k = 0; k < 2; k++) {
                    Cell cell = row2.createCell(j * 2 + k);
                    cell.setCellValue(arr1[i][j][k]);
                }
            }
        }

        try {
            FileOutputStream outputStream = new FileOutputStream(fileName);
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void generateMatrix(int m, int n, int a, int b) {
        int[][] matrix = new int[m][n];
        Random rand = new Random();

        // 生成矩阵
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                matrix[i][j] = rand.nextInt(b - a + 1) + a;
            }
        }

        // 打印矩阵
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static double[] bound(double[] x) {
        double[] bounded_x = new double[x.length];
        double lower_bound = Arrays.stream(x).min().getAsDouble(); // 获取最小值
        double upper_bound = Arrays.stream(x).max().getAsDouble(); // 获取最大值
        for (int i = 0; i < x.length; i++) {
            double mapped_x = (x[i] - lower_bound) / (upper_bound - lower_bound);
            mapped_x = Math.max(0, Math.min(1, mapped_x));
            bounded_x[i] = mapped_x * (upper_bound - lower_bound) + lower_bound;
        }
        return bounded_x;
    }

}
