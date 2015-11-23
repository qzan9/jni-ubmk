# jni-ubmk #

micro-benchmarking JNI ... is that really necessary for modern JVM?!?

> millibenchmarks are not really hard.
> 
> microbenchmarks are challenging, but OK.
> 
> nanobenchmarks are the damned beast!
> 
> picobenchmarks ...

most of the time performance needs to be balanced against other requirements,
such as functionality, reliability, maintainability, extensibility, time to
market, and other business and engineering considerations. it is much harder
to measure the performance of Java language constructs than it looks.

> if i could offer but one advice on this, it would be this: don't. it is too
> easy to get it wrong and bad advice resulting from bad measurement is like
> cancer.

# JVM Performance #

under the hood of JVM, understanding dynamic compilation and optimization is
the key to understanding how to tell a good microbenchmark (and there are
woefully few of these) from the bad ones.

when Java source codes are converted into JVM bytecodes, unlike static
compilers, `javac` does very little optimization -- the optimizations that
would be done by the compiler in a statically compiled language are performed
instead by the runtime when the program is executed.

strictly defined, a JIT-based virtual machine converts all bytecodes into
machine code before execution, but does so in a lazy fashion: the JIT only
compiles a code path when it knows that code path is about to be executed.
to avoid a significant startup penalty, the JIT compiler has to be fast, which
means that it cannot spend as much time optimizing.

the HotSpot execution process combines interpretation, profiling, and dynamic
compilation. HotSpot first runs as an interpreter and only compiles the "hot"
code -- the code executed most frequently. by deferring compilation, the
compiler has access to profiling data, which can be used to improve
optimization decisions.

to make things more complicated, HotSpot comes with two compilers:

* the client compiler: optimized to reduce application startup time and memory
  footprint, employing fewer complex optimizations than the server compiler,
  and accordingly requiring less time for compilation.

* the server compiler: optimized to maximize peak operating speed, and is
  intended for long-running server applications; the server compiler can
  perform an impressive variety of optimizations.

the default is to use the client compiler.

HotSpot compilation is not an all-or-nothing proposition. after interpreting a
code path a certain number of times, it is compiled into machine code. but the
JVM continues profiling, and may recompile the code again later with a higher
level of optimization if it decides the code path is particularly hot or future
profiling data suggests opportunities for additional optimization. the JVM may
recompile the same bytecodes many times in a single application execution.

initial version of HotSpot performs compilation one method at a time. after the
method is compiled, it does not switch to the compiled version until the method
exits and is re-entered -- the compiled version will only be used for
subsequent invocations. the result, in some cases, is that the compiled version
is never used. more recent versions of HotSpot use a technique called on-stack
replacement (OSR) to allow a switch from interpretation to compiled code (or
swapping one version of compiled code for another) in the middle of a loop.

writing -- and interpreting -- benchmarks is far more difficult and complicated
for dynamically compiled languages than for statically compiled ones. in many
cases, microbenchmarks written in Java language don't tell you what you think
they do. the HotSpot JIT is continuously recompiling Java bytecode into machine
code as your program runs, and recompilation can be triggered at unexpected
times by the accumulation of a certain amount of profiling data, the loading of
new classes, or the execution of code paths that have not yet been traversed in
already-loaded classes. timing measurements in the face of continuous
recompilation can be quite noisy and misleading, and it is often necessary to
run Java code for quite a long time before obtaining useful performance data.

# Tips #

* microbenchmarks are difficult to write correctly and can be misleading
  depending on who is writing the tests ...

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
