# jni-ubmk #

micro-benchmarking JNI ... is that really necessary for modern JVM?!?

> millibenchmarks are not really hard
> 
> microbenchmarks are challenging, but OK
> 
> nanobenchmarks are the damned beast!
> 
> picobenchmarks ...

* do not expect too much from micro-benchmarks; they measure only a limited
  range of JVM performance characteristics.

* always include a warmup phase which runs your test kernel all the way
  through, enough to trigger all initializations and compilations before
  timing phase(s).

* always run with `-XX:+PrintCompilation`, `-verbose:gc`, etc.

* be aware of initialization, deoptimization and recompliation effects.

* use appropriate tools to read the compiler's mind, and expect to be surprised
  by the code it produces.

* reduce noise in your measurements. run your benchmark on a quiet machine, and
  run it serveral times, discarding outliers.

* use a library, such as JMH and Caliper.
