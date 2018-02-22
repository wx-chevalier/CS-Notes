# Modern JavaScript Development Glossary

JavaScript was created in 1995 by Brendan Eich with the objective to be a language which designers could easily implement dynamic interfaces,
in other words, it wasn’t built to be fast; it was created to add behavior layer on HTML pages comfortably and straightforwardly.

Engine is a program that converts Javascript code into lower level or machine code that microprocessors can understand.

# Engine & Runtime: Code Parse, Compile and Execution

After loading, the source code will be transformed into a tree representation called Abstract Syntax Tree or AST. Then, depending on the engine/operational system/platform, either a baseline version of this code is compiled, or a bytecode is created to be interpreted.

## JIT

Initially, JavaScript was an interpreted language, making the startup phase faster because the interpreter only needs to read the first instruction, translate it into bytecode and run it right away. For the 90’s internet needs, JavaScript did its job very well. The problem lies when applications start to be more complex.

In the decade of 2000, technologies like Ajax made web applications been more dynamic, Gmail in 2004 and Google Maps in 2005 was a trending on this use case of Ajax technology. This new “way” of building web applications end up with more logic written on the client side. In 2008 with the appearance of Google and it’s engine V8 which compiled all JavaScript code into bytecode right away.

In computing, just-in-time (JIT) compilation, also known as dynamic translation, is a way of executing computer code that involves compilation during execution of a program – at run time – rather than prior to execution.

Most often this consists of source code or more commonly bytecode translation to machine code, which is then executed directly. A system implementing a JIT compiler typically continuously analyses the code being executed and identifies parts of the code where the speedup gained from compilation or recompilation would outweigh the overhead of compiling that code.

In summary, the phases of JIT compiler could be described as:

* Parse

* Compile

* Optimize/deoptimize

* Execution

* Garbage Collector

The following is great example of JIT phases on V8 by Addy Osmani:

![](https://cdn-images-1.medium.com/max/1600/1*N6eUu1Wy0xyu7dR54Pn5bQ.png)

### The Profiler

The Profiler is another entity to be observed, which monitors and collect code execution data. I’ll describe it in summary how it works, taking into account that are differences among browsers engines.

At the first time, everything passes through the interpreter; this process guarantees that the code runs faster after AST is generated. When a piece of code is executed multiple times, as our getNextState() function, the interpreter loses his performance since it needs to interpret the same piece of code over and over again, when this happens, the profiler marks this piece of code as warm and baseline compiler comes to action.

We will use the following snippet to illustrate how JIT works:

```js
function sum(x, y) {
  return x + y;
}
[1, 2, 3, 4, 5, "6", 7, 8, 9, 10].reduce((prev, curr) => sum(prev, curr), 0);
```

When profiler marks a piece of code as warm, the JIT sends this code to the baseline compiler, which creates a stub for this part of code while the profiler keeps collecting data regarding the frequency and types used on this code section (among other data). When this code section is executed (on our hypothetical example return x + y;), the JIT only needs to take this compiled piece again. When a warm code is called several times in the same manner (like same types), it’s marked as hot.

## Optimizer Compiler

JIT 本身并不能带来必然的性能优化

The optimizer compiler generates an even faster version of this code, which is marked as hot. It is only possible based on some assumptions that the optimizer compiler makes like the type of the variables or the shape of objects used in this code. On our example, we can say that a hot code of return x + y; will assume that both x and y are typed as anumber.

The problem is when this code has been hit with something not expected by this optimized compiler, in our case the sum(15, '6') call, since y is a string. When this happens, the Profiler assumes that its assumptions were wrong throwing everything out returning to the base compiled (or interpreted) version again. This phase is called deoptimization. Sometimes this happens so often that it makes the optimized version slower than using the base compiled code.
