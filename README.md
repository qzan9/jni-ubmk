# jni-ubmk #

micro-benchmarking JNI/Java ... is that really necessary for modern JVM?!?

> millibenchmarks are not really hard.
> 
> microbenchmarks are challenging, but OK.
> 
> nanobenchmarks are the damned beast!
> 
> picobenchmarks ...

most of the time performance needs to be balanced against other requirements,
such as functionality, reliability, maintainability, extensibility, time to
market, and other business and engineering considerations. as for Java language
constructs, it is much harder to measure the performance than it looks.

> you're not always measuring what you think you're measuring.

in fact, you're *usually not* measuring what you think you're measuring.

# JVM Performance #

JIT compiler is the heart of the JVM. nothing in the JVM affects performance
more than the compiler, and understanding dynamic compilation and optimization
is the key to understanding how to tell a good microbenchmark (and there are
woefully few of these) from the bad ones.

## Just-in-time compilation ##

when Java source codes are converted into JVM bytecodes, unlike static
compilers, `javac` does very little optimization -- the optimizations that
would be done by the compiler in a statically compiled language are performed
instead by the runtime when the program is executed.

strictly defined, a JIT-based virtual machine converts all bytecodes into
machine code before execution, but does so in a lazy fashion: the JIT only
compiles a code path when it knows that code path is about to be executed.
to avoid a significant startup penalty, the JIT compiler has to be fast, which
means that it cannot spend as much time optimizing.

## HotSpot dynamic compilation ##

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

the default is to use the client compiler. JVM developers often refer to the
compilers by the names `C1` (compiler 1, client compiler) and `C2` (compiler 2,
server compiler).

the primary difference between the two compilers is their aggressiveness in
compiling code. the client compiler begins compiling sooner than the server
compiler does. this means that during the beginning of code execution, the
client compiler will be faster, because it will have compiled correspondingly
more code than the server compiler.

the engineering trade-off here is the knowledge the server compiler gains
while it waits: that knowledge allows the server compiler to make better
optimizations in the compiled code.

in Java 7, to use tiered compilation, specify the server compiler with
`-server` and include the flag `-XX:+TieredCompilation`.

## Continuous recompilation ##

HotSpot compilation is not an all-or-nothing proposition. after interpreting a
code path a certain number of times, it is compiled into machine code. but the
JVM continues profiling, and may recompile the code again later with a higher
level of optimization if it decides the code path is particularly hot or future
profiling data suggests opportunities for additional optimization. the JVM may
recompile the same bytecodes many times in a single application execution.

## On-stack replacement ##

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

# JVM compiler #

## Dead-code elimination ##

optimizing compilers are adept at spotting dead code -- code that has no effect
on the outcome of the program execution. many microbenchmarks perform much
"better" when run with `-server` than with `-client`, not because the server
compiler is faster (though it often is) but because the server compiler is more
adept at optimizing away blocks of dead code.

## Warmup ##

if you're looking to measure the performance of idiom X, you generally want to
measure its compiled performance, not its interpreted performance. to do so
requires "warming up" the JVM -- executing your target operation enough times
that the compiler will have had time to run and replace the interpreted code
with compiled code before starting to time the execution.

today's dynamic compiler runs at less predictable times, the JVM switches from
interpreted to compiled code at will, and the same code path may be compiled
and recompiled more than once during a run. if you don't account for the timing
of these events, they can seriously distort your timing results.

so, how much warmup is enough? you don't know. the best you can do is run your
benchmarks with `-XX: +PrintCompilation`, observe what causes the compiler to
kick in, then restructure your benchmark program to ensure that all of this
compilation occurs before you start timing and that no further compilation
occurs in the middle of your timing loops.

## Garbage collection ##

don't forget GC -- a small change in the number of iterations could mean the
difference between no garbage collection and one garbage collection, skewing
the "time per iteration" measurement.

run your benchmarks with `-verbose:gc`, you can see how much time is spent in
garbage collection and adjust your timing data accordingly.

## Dynamic deoptimization ##

many standard optimizations can only be performed within a "basic block" and
so inlining method calls is often important to achieve good optimization.

inconveniently, virtual functions pose an impediment to inlining, and virtual
function calls are more common in Java language than in C++. considering that
classes can be loaded dynamically, the compiler can make aggressive inlining
decisions to achieve higher performance, then back out those decisions later
if they are no longer based on valid assumptions, and will invalidate the
generated code and revert to interpretation or recompilation.

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
