# jni-ubmk #

> millibenchmarks are not really hard.
> 
> microbenchmarks are challenging, but OK.
> 
> nanobenchmarks are the damned beast!
> 
> picobenchmarks ...

most of the time performance needs to be balanced against other requirements,
such as functionality, reliability, maintainability, extensibility, time to
market, and other business and engineering considerations.

as for the Java language constructs, it is *much harder* to measure the
performance than it looks.

> you're not always measuring what you think you're measuring.

in fact, you're *usually not* measuring what you think you're measuring.

# JIT Compiler #

JIT compiler is the HEART of the JVM. nothing in the JVM affects performance
more than the compiler, and understanding dynamic compilation and optimization
is the key to understanding how to tell a good microbenchmark (and there are
woefully few of these) from the bad ones.

## 32-bit and 64-bit ##

there are three versions of the JIT compiler

* a 32-bit client version (`-client`),

* a 32-bit server version (`-server`),

* a 64-bit server version (`-d64`).

32-bit binary is expected to have (up to) two compilers, while the 64-bit
binary will have only a single compiler. in fact, the 64-bit binary will have
two compilers, since the client compiler is needed to support *tiered*
*compilation*. but a 64-bit JVM cannot be run with only the client compiler.

if the size of your heap will be less than about 3GB, the 32-bit version of
Java will be faster and have a smaller footprint. programs that make extensive
use of `long` or `double` variables will be slower on a 32-bit JVM because they
cannot use the CPU's 64-bit registers, though that is a very exceptional case.

## C1 and C2 ##

HotSpot comes with two compilers:

* the client compiler (C1): optimized to reduce application *startup time* and
  *memory footprint*, employing fewer complex optimizations than the server
  compiler, and accordingly requiring less time for compilation.

* the server compiler (C2): optimized to maximize *peak operating speed*, and 
  is intended for long-running server applications; the server compiler can
  perform an impressive variety of optimizations.

the default is to use the client compiler.

the primary difference between the two compilers is their *aggressiveness* in
compiling code. the client compiler begins compiling sooner than the server
compiler does. this means that during the beginning of code execution, the
client compiler will be faster, because it will have compiled correspondingly
more code than the server compiler.

the engineering trade-off here is the knowledge the server compiler gains
while it waits: that knowledge allows the server compiler to make better
optimizations in the compiled code. ultimately, code produced by the server
compiler will be faster than that produced by the client compiler.

*tiered compilation* means the JVM starts with the client compiler, and then
uses the server compiler as code gets hotter. in Java 7, to use tiered
compilation, specify the server compiler with `-server` and include the flag
`-XX:+TieredCompilation`. in Java 8, it is enabled by default.

## Just-in-time compilation ##

when Java source codes are converted into JVM bytecodes, unlike static
compilers, `javac` does very little optimization -- the optimizations that
would be done by the compiler in a statically compiled language are performed
instead *by the runtime* when the program is executed.

strictly defined, a JIT-based virtual machine converts all bytecodes into
machine code before execution, but does so in a lazy fashion: the JIT only
compiles a code path when it knows that code path is about to be executed.
to avoid a significant startup penalty, the JIT compiler has to be fast, which
means that *it cannot spend as much time optimizing*.

## HotSpot dynamic compilation ##

the HotSpot execution process combines interpretation, profiling, and dynamic
compilation. HotSpot *first runs as an interpreter* and only compiles the "hot"
code -- the code executed most frequently. by *deferring compilation*, the
compiler has access to *profiling data*, which can be used to improve
optimization decisions.

compilation is based on two counters in the JVM: the number of times the method
as been called, and the number of times any loops in the method have branched
back. when the JVM executes a Java method, it checks the sum of those two
counters and decides whether or not the method is eligible for compilation.

compilation threshold is set up by the value of the `-XX:CompileThreshold=N`
flag. the default value of `N` for the client compiler is 1,500; for the server
compiler it is `10,000`. changing the `CompileThreshold` flag has been a
popular recommendation in performance circles for quite some time.

periodically (specifically, when the JVM reaches a safepoint), the value of
each counter is reduced. practically speaking, this means that the counters are
a relative measure of the *recent hotness* of the method or loop. one side
effect of this is that somewhat-frequently executed code may never be compiled
(lukewarm [as opposed to hot]). this is one case where reducing the compilation
threshold can be beneficial, and it is another reason why tiered compilation is
usually slightly faster than the server compiler alone.

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
is never used.

more recent versions of HotSpot use a technique called on-stack replacement
(OSR) to allow a switch from interpretation to compiled code (or swapping one
version of compiled code for another) in the middle of a loop; the JVM has the
ability to start executing the compiled version of the loop while the loop is
still running. when the code for the loop has finished compiling, the JVM
replaces the code (on-stack), and the next iteration of the loop will execute
the much-faster compiled version of the code.

OSR compilation is trigged by the value of three flags:

```
    OSR trigger = (CompiledThreshold * (OnStackReplacePercentage - InterpreterProfilePercentage) / 100)
```

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

## Code cache ##

when the JVM compiles code, it holds the set of assembly-language instructions
in the code cache, and its default size is

* 32-bit client, Java 8: 32MB,

* 32-bit server with tiered compilation, Java 8: 240MB,

* 64-bit server with tiered compilation, Java 8: 240MB,

* 32-bit client, Java 7: 32MB,

* 32-bit server, Java 7: 32MB,

* 64-bit server, Java 7: 48MB,

* 64-bit server with tiered compilation, Java 7: 96MB.

the maximum size of the code cache is set via the `-XX:ReservedCodeCacheSize=N`
flag. code cache is managed like most memory in the JVM: there is an initial
size (specified by `-XX:InitialCodeCacheSize=N`). allocation of the code cache
size starts at the initial size and increases as the cache fills up. on Intel
machines, the client compiler starts with a 160KB cache and the server compiler
starts with a 2,496KB cache.

## Compilation Threads ##

when a method (or loop) becomes eligible for compilation, it is queued for
compilation, and that queue is processed by one or more background threads.
this means that compilation is an asynchronous process, and it allows the
program to continue executing even while the code in question is being
compiled.

these queues are not strictly first in, first out: methods whose invocation
counters are higher have priority.

for C1, the JVM starts one compilation thread, for C2, it starts two. when
tiered compilation is in effect, the JVM will by default start multiple client
and server threads based on the number of CPUs on the target platform.

the number of compiler threads can be adjusted by setting the `-XX:CICompilerCount=N`
flag. for tiered compilation, one-third of them will be used to process the
client compiler queue, and the remaining threads (at least one) will be used to
process the server compiler queue.

if `-XX:+BackgroundCompilation` is set to `false`, in which case when a method
is eligible for compilation, code that wants to execute it will wait until it
is in fact compiled (rather than continuing to execute in the interpreter
asynchronously).

## Inlining ##

code that follows good object-oriented design often contains a number of
attributes that are accessed via getters (and perhaps setters). the overhead
for invoking a method call like this is quite high, especially relative to the
amount of code in the method. JVMs now routinely perform code inlining for
these kinds of methods.

inlining is enabled by default. it can be disabled using `-XX:-Inline` flag.

there is *NO basic visibility* into how the JVM inlines code. if you compile
the JVM from source, you can produce a debug version that includes the flag
`-XX:+PrintInlining`.

the basic decision about whether to inline a method depends on *how hot* it is
and its *size*. the JVM determines if a method is hot (i.e., called frequently)
based on an internal calculation; it is *NOT* directly subject to any tunable
parameters. if a method is eligible for inlining because it is called
frequently, then it will be inlined only if its bytecode size is less than 325
bytes (or whatever is specified as the `-XX:MaxFreqInlineSize=N` flag).
otherwise, it is eligible for inlining only if it is small: less than 35 bytes
(or whatever is specified as the `-XX:MaxInlineSize=N` flag).

## Escape analysis ##

server compiler performs some very aggressive optimizations if escape analysis
is enabled (`-XX:+DoEscapeAnalysis`, which is `true` by default).

in rare cases, escape analysis will get things wrong, in which case disabling
it will lead to faster and/or more stable code.

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

## Deoptimization ##

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
