package code.vns_pso;

import code.RPFSP;
import code.pso.Particle;
import util.Util;

import java.util.Arrays;
import java.util.Random;

public class VNS_PSO {
    private static final int NUM_PARTICLES = 100;
    private static final int NUM_ITERATIONS = 1000;
    private static final double C1 = 2.0;
    private static final double C2 = 2.0;
    private static final double W = 0.7;
    private Particle gBest; // 全局最优
    private double[] record;

    public double[] getRecord() {
        return record;
    }

    private Particle[] particles; // 粒子群

    private Particle[] initialize(){
        this.particles = new Particle[NUM_PARTICLES];
        gBest = new Particle(RPFSP.getN() * RPFSP.getL());

        // Initialize particles
        Random rand = new Random();
        for (int i = 0; i < NUM_PARTICLES; i++) {
            if(i < NUM_PARTICLES / 2){
//                particles[i] = new Particle(RPFSP.getN() * RPFSP.getL(), new int[]{9, 4, 1, 6, 2, 0, 5, 1, 3, 6, 3, 7, 8, 5, 2, 9, 8, 3, 0, 2, 5, 8, 4, 1, 7, 9, 6, 4, 7, 0});
//                particles[i] = new Particle(RPFSP.getN() * RPFSP.getL(), new int[]{7, 0, 7, 4, 1, 2, 3, 4, 1, 6, 5, 9, 7, 8, 0, 2, 1, 3, 8, 6, 5, 9, 3, 4, 2, 8, 9, 6, 5, 0});
                particles[i] = new Particle(RPFSP.getN() * RPFSP.getL(), new int[]{28, 25, 3, 24, 20, 4, 11, 24, 17, 25, 5, 22, 0, 21, 12, 14, 5, 17, 27, 28, 11, 16, 6,
                        12, 26, 10, 3, 18, 22, 27, 16, 8, 9, 24, 21, 26, 29, 13, 20, 1, 17, 19, 4, 6, 7, 14, 16, 23, 22, 5, 8, 29, 4, 2, 14, 10, 15, 9, 6, 29, 7,
                        25, 19, 27, 3, 28, 15, 20, 26, 0, 1, 21, 19, 2, 18, 13, 8, 15, 23, 9, 10, 2, 13, 12, 7, 18, 0, 1, 23, 11});
//                particles[i] = new Particle(RPFSP.getN() * RPFSP.getL(), new int[]{18, 5, 13, 10, 4, 6, 18, 11, 17, 10, 5, 2, 14, 11, 0, 18, 8, 19, 12, 6, 16, 0,
//                        3, 9, 11, 8, 1, 10, 6, 12, 0, 9, 16, 15, 2, 14, 1, 4, 7, 19, 3, 17, 1, 14, 15, 13, 16, 7, 4, 15, 5, 13, 12, 2, 3, 9, 7, 8, 19, 17});
//                particles[i] = new Particle(RPFSP.getN() * RPFSP.getL(), new int[]{37, 6, 9, 34, 17, 39, 7, 20, 30, 10, 11, 33, 37, 38, 10, 27, 0, 37, 15, 28, 19, 24, 9, 5, 1, 39, 14,
//                        4, 27, 8, 26, 22, 10, 21, 11, 16, 31, 30, 36, 15, 36, 28, 3, 18, 35, 25, 23, 29, 24, 19, 38, 0, 39, 16, 32, 3, 11, 5, 2, 30, 21, 6, 24, 23, 27, 34, 8, 28, 17, 15, 13, 29, 26,
//                        4, 25, 2, 38, 14, 22, 35, 3, 20, 7, 36, 12, 32, 5, 14, 4, 1, 35, 7, 22, 12, 17, 26, 13, 8, 9, 32, 12, 21, 16, 31, 33, 2, 29, 23, 20, 34, 1, 18, 18, 13, 19, 0, 31, 6, 33, 25});
//                particles[i] = new Particle(RPFSP.getN() * RPFSP.getL(), new int[]{1, 4, 0, 3, 14, 11, 19, 7, 8, 9, 16, 10, 15, 13, 7, 15, 3, 4, 12, 10, 6, 1,
//                        19, 2, 5, 14, 18, 12, 1, 2, 7, 16, 17, 5, 12, 19, 18, 11, 17, 6, 2, 3, 9, 8, 13, 4, 15, 5, 0, 17, 18, 13, 16, 8, 6, 9, 14, 11, 0, 10});
            }else {
                particles[i] = new Particle(RPFSP.getN() * RPFSP.getL());
                particles[i].initialize(rand);
            }
//            particles[i] = new Particle(RPFSP.getN() * RPFSP.getL());
//            particles[i].initialize(rand);
            double p = particles[i].getFitness(particles[i].getPosition());
            double g = gBest.getFitness(gBest.getPosition());
            if (i == 0 || p > g) {
                gBest.setPosition(particles[i].getPosition());
            }
        }
        RPFSP best = new RPFSP(RPFSP.getJobOrder(gBest.getPosition()));
        // Output results
//        System.out.println("初始: " + best.getMaxCompletionTime(best.decodeChromosome(best.chromosome)));
        return particles;
    }

    public RPFSP solve(){
        this.initialize();
        Random rand = new Random();
        record = new double[NUM_ITERATIONS];
        System.out.println(gBest.getCmax(gBest.getPosition()));
        int asd = 0;
        double sum = 0;
        for (Particle particle : particles
             ) {
            sum += particle.getCmax(particle.getPosition());
            asd++;
        }
        System.out.println("pingjun---" + (sum/asd));
        for (int iter = 0; iter < NUM_ITERATIONS; iter++) {
            System.out.println("前" + Arrays.toString(RPFSP.getJobOrder(gBest.getPosition())));
            VNS vns = new VNS(RPFSP.getJobOrder(gBest.getPosition()), gBest.getCmax(gBest.getPosition()));
//            VNS_Shaking vns = new VNS_Shaking(RPFSP.getJobOrder(gBest.getPosition()), gBest.getCmax(gBest.getPosition()), gBest.getPosition());
            int[] solve = vns.solve();
            double[] pNewGbest = convertToPosition1(gBest.getPosition(), solve);
            gBest.setPosition(pNewGbest);
            System.out.println("后" + Arrays.toString(RPFSP.getJobOrder(gBest.getPosition())));
            for (Particle particle : particles) {
                particle.updateVelocity(gBest, C1, C2, W, rand);
                particle.updatePosition(rand);
            }
            for (Particle particle : particles){
                double p = particle.getFitness(particle.getPosition());
                if(p > particle.getFitness(particle.getpBest())){
                    particle.setpBest(Arrays.copyOf(particle.getPosition(), particle.getPosition().length));
                }
                if (p > gBest.getFitness(gBest.getPosition())) {
                    gBest.setPosition(particle.getPosition());
                }
            }
            RPFSP best = new RPFSP(RPFSP.getJobOrder(gBest.getPosition()));
            double val = Util.change(best.getMaxCompletionTime(best.decodeChromosome(best.chromosome)));
            record[iter] = val;
            System.out.print("第" + (iter+1) + "代:CMAX = " + val);
            System.out.println("   序列：" + Arrays.toString(best.chromosome));
        }
        RPFSP best = new RPFSP(RPFSP.getJobOrder(gBest.getPosition()));
        // Output results
        System.out.println("Minimum makespan: " + best.getMaxCompletionTime(best.decodeChromosome(best.chromosome)));
        System.out.println("Optimal job order: " + Arrays.toString(RPFSP.getJobOrder(gBest.getPosition())));
        best.init();
        return best;
    }

    private double[] convertToPosition1(double[] position, int[] solve) {
        double[] doubles = Arrays.copyOf(position, position.length);
        Arrays.sort(doubles);
        double[] res = new double[doubles.length];
        int[] index = new int[RPFSP.getN()];
        for (int i = 0; i < index.length; i++) {
            index[i] = i * RPFSP.getL();
        }
        int f = 0;
        for (int i = 0; i < res.length; i++) {
            res[index[solve[i]]] = doubles[i];
            index[solve[i]]++;
        }
        return res;
    }

}
