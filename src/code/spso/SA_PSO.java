package code.spso;

import code.RPFSP;
import code.pso.Particle;
import util.Util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SA_PSO {
    private static final int NUM_PARTICLES = 50;
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
            if(i < 10){
                particles[i] = new Particle(RPFSP.getN() * RPFSP.getL(), new int[]{9, 4, 1, 6, 2, 0, 5, 1, 3, 6, 3, 7, 8, 5, 2, 9, 8, 3, 0, 2, 5, 8, 4, 1, 7, 9, 6, 4, 7, 0});
            }else {
                particles[i] = new Particle(RPFSP.getN() * RPFSP.getL());
                particles[i].initialize(rand);
            }
            double p = particles[i].getFitness(particles[i].getPosition());
            double g = gBest.getFitness(gBest.getPosition());
            if (i == 0 || p > g) {
                gBest.setPosition(particles[i].getPosition());
            }
        }
        return particles;
    }

    public RPFSP solve(){
        this.initialize();
        Random rand = new Random();
        record = new double[NUM_ITERATIONS];
        for (int iter = 0; iter < NUM_ITERATIONS; iter++) {
            SA sa = new SA(RPFSP.getJobOrder(gBest.getPosition()), gBest.getCmax(gBest.getPosition()));
            int[] solve = sa.solve();
            double[] pNewGbest = convertToPosition1(gBest.getPosition(), solve);
//            double[] position = convertToPosition(solve);
            gBest.setPosition(pNewGbest);
            for (Particle particle : particles) {
                particle.updateVelocity(gBest, C1, C2, W, rand);
                particle.updatePosition(rand);
            }
            for (Particle particle : particles){
                if(particle.getFitness(particle.getPosition()) > particle.getFitness(particle.getpBest())){
                    particle.setpBest(Arrays.copyOf(particle.getPosition(), particle.getPosition().length));
                }
                if (particle.getFitness(particle.getPosition()) > gBest.getFitness(gBest.getPosition())) {
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
        Arrays.sort(position);
        double[] res = new double[position.length];
        int[] index = new int[RPFSP.getN()];
        for (int i = 0; i < index.length; i++) {
            index[i] = i * RPFSP.getL();
        }
        int f = 0;
        for (int i = 0; i < res.length; i++) {
            res[index[solve[i]]] = position[i];
            index[solve[i]]++;
        }
        return res;
    }

    public static double[] convertToPosition(int[] solve) {
        int[] arr = new int[solve.length];
//        System.out.println(Arrays.toString(solve));
        int index = 1;
        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
        for (int j = 0; j < RPFSP.getN(); j++) {
            for (int k = 0; k < solve.length; k++) {
                if(solve[k] == j){
                    map.put(index, k);
                    arr[k] = index;
                    index++;
                }
            }
        }
        double[] position = new double[solve.length];
        for (int i = 0; i < position.length; i++) {
            position[i] = (map.get(i + 1) + Math.random()) / (RPFSP.getN() * RPFSP.getL());
        }
        int[] jobOrder = RPFSP.getJobOrder(position);
//        System.out.println(Arrays.toString(jobOrder));
//        System.out.println(Arrays.toString(arr));
        return position;
    }

}
