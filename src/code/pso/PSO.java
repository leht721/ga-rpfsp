package code.pso;

import code.RPFSP;
import util.Util;

import java.util.Arrays;
import java.util.Random;

public class PSO {
    private static final int NUM_PARTICLES = 30;
    private static final int NUM_ITERATIONS = 400;
    private static final double C1 = 2.0;
    private static final double C2 = 2.0;
    private static final double W = 0.7;
    private static Particle gBest; // 全局最优
    private double[] record;

    private Particle[] particles; // 粒子群

    private Particle[] initialize(){
        this.particles = new Particle[NUM_PARTICLES];
        gBest = new Particle(RPFSP.getN() * RPFSP.getL());

        // Initialize particles
        Random rand = new Random();
        for (int i = 0; i < NUM_PARTICLES; i++) {
            particles[i] = new Particle(RPFSP.getN() * RPFSP.getL());
            particles[i].initialize(rand);
            if (gBest == null || particles[i].getFitness(particles[i].getPosition()) < gBest.getFitness(gBest.getPosition())) {
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
            for (Particle particle : particles) {
                particle.updateVelocity(gBest, C1, C2, W, rand);
                particle.updatePosition(rand);
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

}
