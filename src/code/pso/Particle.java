package code.pso;

import code.RPFSP;
import code.spso.SA_PSO;

import java.util.Arrays;
import java.util.Random;

public class Particle {
    private final int jobLength;
    private double[] position;
    private double[] velocity;
    private double[] pBest;

    public double[] getPosition() {
        return Arrays.copyOf(position, position.length);
    }

    public Particle(int jobLength) {
        this.jobLength = jobLength;
        this.position = new double[jobLength];
        this.velocity = new double[jobLength];
        this.pBest = new double[jobLength];
    }
    public Particle(int jobLength,int[] chrom) {
        this.jobLength = jobLength;
        this.position = SA_PSO.convertToPosition(chrom);
        this.velocity = new double[jobLength];
        Random random = new Random();
        for (int i = 0; i < velocity.length; i++) {
            velocity[i] = (random.nextDouble() - 0.5) * 2.0;
        }
        this.pBest = Arrays.copyOf(position, jobLength);;
    }

    public double[] getpBest() {
        return pBest;
    }

    public void setpBest(double[] pBest) {
        this.pBest = pBest;
    }

    public void setPosition(double[] position) {
        this.position = position;
    }

    public void initialize(Random rand) {
        for (int i = 0; i < jobLength; i++) {
            position[i] = rand.nextDouble();
            velocity[i] = (rand.nextDouble() - 0.5) * 2.0;
        }
        pBest = Arrays.copyOf(position, jobLength);
    }

    public void updateVelocity(Particle gBest, double c1, double c2, double w, Random rand) {
        for (int i = 0; i < jobLength; i++) {
            double r1 = rand.nextDouble();
            double r2 = rand.nextDouble();
            velocity[i] = w * velocity[i] + c1 * r1 * (pBest[i] - position[i]) + c2 * r2 * (gBest.position[i] - position[i]);
            if(velocity[i] > 1){
                velocity[i] = 1;
            }else if(velocity[i] < -1){
                velocity[i] = -1;
            }
        }
    }

    public void updatePosition(Random rand) {
        for (int i = 0; i < jobLength; i++) {
            position[i] += velocity[i];
        }

    }

    public double getFitness(double[] position) {
        int[] jobOrder = RPFSP.getJobOrder(position);
        RPFSP r = new RPFSP(jobOrder);
        return r.calculateFitness();
    }

    public double getCmax(double[] position){
        int[] jobOrder = RPFSP.getJobOrder(position);
        RPFSP r = new RPFSP(jobOrder);
        return r.getMaxCompletionTime(r.decodeChromosome(r.chromosome));
    }


}

