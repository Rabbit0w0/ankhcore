package org.inksnow.ankh.core.common.benchmark;

public class BenchmarkTask {
  private final int warmUpTimes;
  private final int runTimes;
  private final InputSeed inputSeed;
  private final Target target;

  private BenchmarkTask(
    final int warmUpTimes, final int runTimes,
    final InputSeed<?> inputSeed, final Target<?,?> target
  ) {
    this.warmUpTimes = warmUpTimes;
    this.runTimes = runTimes;
    this.inputSeed = inputSeed;
    this.target = target;
  }

  public BenchmarkRecord run(){
    System.gc();
    Target target = this.target;
    for (int j = 0; j < warmUpTimes; j++) {
      target.run(inputSeed.seed(j));
    }
    final BenchmarkRecord.Builder builder = BenchmarkRecord.builder(runTimes);
    for (int i = 0; i < runTimes; i++) {
      Object input = inputSeed.seed(i);
      long start = System.nanoTime();
      target.run(input);
      long end = System.nanoTime();
      builder.record(end - start);
    }
    return builder.build();
  }

  public static <T> Builder<T> builder(){
    return new Builder<>();
  }

  public static class Builder<T> {
    private int warmUpTimes = 1000_000;
    private int runTimes = 1000_000;
    private double[] reports = new double[]{ 100, 99.9, 99.5, 99, 50 };
    private InputSeed<T> inputSeed = it -> null;
    private Target<T,?> target;

    private Builder(){
      //
    }

    public Builder<T> warmUpTimes(final int warmUpTimes) {
      this.warmUpTimes = warmUpTimes;
      return this;
    }

    public Builder<T> runTimes(final int runTimes) {
      this.runTimes = runTimes;
      return this;
    }

    public Builder<T> reports(final double[] reports) {
      this.reports = reports;
      return this;
    }

    public Builder<T> inputSeed(final InputSeed<T> inputSeed){
      this.inputSeed = inputSeed;
      return this;
    }

    public Builder<T> target(final Target<T,?> target) {
      this.target = target;
      return this;
    }

    public BenchmarkTask build(){
      return new BenchmarkTask(warmUpTimes, runTimes, inputSeed, target);
    }
  }
}
