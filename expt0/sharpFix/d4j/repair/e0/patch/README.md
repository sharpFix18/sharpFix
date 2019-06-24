# Correct Patches

** Chart 24 **
> The generated patch is semantically equivalent to the developer patch in using the value `Math.min(v, this.upperBound)` for calculating `g`.

** Closure 126 **
> The developer patch deletes the statement `if (NodeUtil.hasFinally(n)){...}`. The generated patch creates an if-condition `n.isLabel()` to guard the body statements of `if (NodeUtil.hasFinally(n)){...}`. The patch is semantically equivalent to the developer patch because the if-condition can never be `true` since `if (NodeUtil.hasFinally(n)){...}` is enclosed in an if-statement as `if (n.isTry()){...}` and `n.isTry()` and `n.isLabel()` can never be both `true`.

** Closure 20 **
> The developer patch creates a new condition `value != null && value.getNext() == null && NodeUtil.isImmutableValue(value)` to guard a sequence of statements. The generated patch is semantically equivalent to the developer patch in that (1) when the condition is satisfied, the sequence of statements should be executed and (2) when the condition is not satisified (when `value == null`, or `value.getNext() != null`, or `!NodeUtil.isImmutableValue(value)`), the program should return `n`.

** Closure 17 **
> The generated patch is semantically equivalent to the developer patch though they are not syntactically identical. For the same variable, the two patches use different names: the generated patch uses `i` but the developer patch uses `pos`.

** Lang 55 **
> The generated patch used `this.stopTime` and the developer patch used `stopTime`. Since `stopTime` is a class field, the generated patch is semantically equivalent to the developer patch.

** Math 11 **
> The generated patch used `-dim / 2.0` and the developer patch used `-0.5 * dim`. This is semantically equivalent.

** Math 58 **
> The developer patch saved `(new ParameterGuesser(getObservations())).guess()` as a variable, and passed the value of that variable to the method `fit`. The generated patch directly passed the value of `(new ParameterGuesser(getObservations())).guess()` to the method `fit`. So the two patches are semantically equivalent.

** Math 85 **
> The generated patch created the condition `fa * fb > 0.0` to guard the exception-throwing statement. Though the new if-statement is guarded by `fa * fb >= 0.0`, the condition `fa * fb > 0.0` is stronger than `fa * fb >= 0.0`. So the generated patch is semantically equivalent to the developer patch.

** Others **
> The patches for `Chart 20`, `Chart 4`, `Chart 8`, `Closure 14`, `Closure 73`, `Lang 16`, `Lang 21`, `Lang 22`, `Lang 24`, `Lang 33`, `Lang 38`, `Lang 39`, `Lang 40`, `Lang 43`, `Lang 45`, `Lang 49`, `Lang 52`, `Lang 54`, `Lang 55`, `Math 106`, `Math 25`, `Math 41`, `Math 53`, `Math 57`, `Math 59`, `Math 69`, `Math 70`, `Math 75`, `Math 79` are identical to the corresponding developer patches.

