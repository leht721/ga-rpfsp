package code.pso;

import code.RPFSP;

import java.util.Arrays;
import java.util.Random;

class Particle {
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

    public void setPosition(double[] position) {
        this.position = position;
    }

    public void initialize(Random rand) {
        for (int i = 0; i < jobLength; i++) {
            position[i] = rand.nextDouble();
            velocity[i] = (rand.nextDouble() - 0.5) * 2.0;
        }
        System.arraycopy(position, 0, pBest, 0, jobLength);
    }

    public void updateVelocity(Particle gBest, double c1, double c2, double w, Random rand) {
        for (int i = 0; i < jobLength; i++) {
            double r1 = rand.nextDouble();
            double r2 = rand.nextDouble();
            velocity[i] = w * velocity[i] + c1 * r1 * (pBest[i] - position[i]) + c2 * r2 * (gBest.position[i] - position[i]);
        }
    }

    public void updatePosition(Random rand) {
        for (int i = 0; i < jobLength; i++) {
            position[i] += velocity[i];
        }

        double fitness = getFitness(position);
        if (fitness > getFitness(pBest)) {
            System.arraycopy(pBest, 0, position, 0, jobLength);
        }

    }

    public double getFitness(double[] position) {
        int[] jobOrder = RPFSP.getJobOrder(position);
        RPFSP r = new RPFSP(jobOrder);
        return r.calculateFitness();
    }


}

