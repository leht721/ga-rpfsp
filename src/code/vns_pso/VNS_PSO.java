package code.vns_pso;

import code.RPFSP;
import code.pso.Particle;
import util.Util;

import java.util.Arrays;
import java.util.Random;

public class VNS_PSO {
    private static final int NUM_PARTICLES = 30;
    private static final int NUM_ITERATIONS = 200;
    private static final double C1 = 2.0;
    private static final double C2 = 2.0;
    private static final double W = 0.7;
    private static Particle gBest; // 全局最优
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
            particles[i] = new Particle(RPFSP.getN() * RPFSP.getL());
            particles[i].initialize(rand);
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
            System.out.println("前" + Arrays.toString(RPFSP.getJobOrder(gBest.getPosition())));
            VNS vns = new VNS(RPFSP.getJobOrder(gBest.getPosition()), gBest.getCmax(gBest.getPosition()));
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

}