package util;

import java.util.Objects;

/**
 * @author neulht @create
 * 2023-03-17 21:45
 */
public class Pair {
    private int machine;
    private int job;

    public Pair(int machine, int job) {
        this.machine = machine;
        this.job = job;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair pair = (Pair) o;
        return machine == pair.machine && job == pair.job;
    }

    @Override
    public int hashCode() {
        return Objects.hash(machine, job);
    }

    public void setMachine(int machine) {
        this.machine = machine;
    }

    public void setJob(int job) {
        this.job = job;
    }

    public int getMachine() {
        return machine;
    }

    public int getJob() {
        return job;
    }

    public Pair() {
    }
}
