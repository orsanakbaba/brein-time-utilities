# Temporal Expressions with exp4j

The [exp4j](https://github.com/fasseg/exp4j) library was developed as a "A tiny math expression evaluator for the Java 
programming language". The library itself provides the functionality to add additional `operators`, `functions`, and 
`variables`. The [`Exp4JTemporalExpressionEvaluator`](../src/com/brein/time/expressions/Exp4JTemporalExpressionEvaluator.java) 
implementation of the [`ITemporalExpressionEvaluator`](../src/com/brein/time/expressions/ITemporalExpressionEvaluator.java) 
interface is based on this library, by extending it by some temporal `operators`, `functions`, and `variables`.

## Getting Started

The implementation itself allows the definition of a lowest time granularity, which enables the library to pass on values 
on this lowest time granularity, e.g, seconds (default: milliseconds). In general, any granularity that is available via a `TimeUnit` can be used:

```java
final Exp4JTemporalExpressionEvaluator eval = new Exp4JTemporalExpressionEvaluator();
eval.init(singletonMap(Exp4JTemporalExpressionEvaluator.LOWEST_TIME_GRANULARITY, TimeUnit.SECONDS));
```

Once the `Exp4JTemporalExpressionEvaluator` is initialized, it is possible to add, remove, or evaluate formulas from the  
collection. It is also possible (e.g., if a formula has to be evaluated only once) to directly evaluate a specific 
formula.

```java
System.out.println(eval.evaluateFormula("1sec"));
// output: 1

System.out.println(eval.evaluateFormula("1h"));
// output: 3600

System.out.println(eval.evaluateFormula("now() + 5h"));
// output: 1528928833
```

## Adding Variables

The implementation allows to define formulas which contain formula-specific variables, i.e., variables which receive 
there value when evaluated.

```java
final Exp4JTemporalExpressionEvaluator eval = new Exp4JTemporalExpressionEvaluator();
eval.init(singletonMap(Exp4JTemporalExpressionEvaluator.LOWEST_TIME_GRANULARITY, TimeUnit.SECONDS));

eval.addFormula("sample", "t + 1min", Collections.singleton("t"));
System.out.println(eval.evaluate("sample", singletonMap("t", 55L)));
// output: 115
```

Furthermore, it is possible to add additional "constants" to the evaluator when it gets initialized.

```java
final Exp4JTemporalExpressionEvaluator eval = new Exp4JTemporalExpressionEvaluator();
eval.init(singletonMap(Exp4JTemporalExpressionEvaluator.GENERAL_VARIABLES, singletonMap("five", 5)));

eval.addFormula("sample", "five sec", Collections.singleton("t"));
System.out.println(eval.evaluate("sample"));
// output: 5000   <-- default lowest time granularity is [ms]
```

## Available Constants and Functions

The current implementation provides the following default implementations:

- units (constants): `ns` (nanoseconds), `μs` (microseconds), `ms` (milliseconds), `sec` (seconds), `min` (minutes), `h` (hours), and `d` (days)
- result-transforming (functions): `toNanoseconds()`, `toMicroseconds()`, `toMilliseconds()`, `toSeconds()`, `toMinutes()`, `toHours()`, and `toDays()`

**Note:** 

- The functions and constants are only available if the defined lowest time granularity allows it, e.g., `μs` is 
not available if the lowest time granularity is set to seconds. 
- The available `to...` methods can only be used to transform the final result of a formula and cannot be nested within 
each-other, since the `exp4j` implementation does not allow to keep track of additional information (in that case the 
current unit of the intermediate results), e.g., let's assume that the lowest granularity is `milliseconds`, the 
formula `toSeconds(5min)` is correct and would return `5 * 60 = 300`, whereby the formula `toMinutes(toSeconds(5min))` 
would return an incorrect result:
  ```
                        5min:       5 min --> 300000 ms
             toSeconds(5min):  300000 ms  -->    300 s    // the impl. uses [ms]
  toMinutes(toSeconds(5min)):     300 ms  -->      0 min
  ```
