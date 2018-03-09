# Correct Patches

** Closure 119 **
> The generated patch used `case Token.CATCH: case Token.INC: ...` and the developer patch used `case Token.INC: case Token.CATCH: ...`. The two patches are semantically equivalent.

** Closure 126 **
> The developer patch deletes the statement `if (NodeUtil.hasFinally(n)) {...}`. The generated patch changes the if-condition from `NodeUtil.hasFinally(n)` to `n.isLabel()`. This is semantically equivalent to deleting the statement since `n.isLabel()` can never be `true` here and the guarded statements can never be executed. This is because the statement is enclosed in an if-statement whose condition is `n.isTry()`. If `n.isTry()` is `true`, then `n.isLabel()` has to be `false`.

** Math 11 **
> The generated patch used `-dim / 2.0` and the developer patch used `-0.5 * dim`. This is semantically equivalent.

** Math 58 **
> The developer patch saved `(new ParameterGuesser(getObservations())).guess()` as a variable, and passed the value of that variable to the method `fit`. The generated patch directly passed the value of `(new ParameterGuesser(getObservations())).guess()` to the method `fit`. So the two patches are semantically equivalent.

** Math 94 **
> The developer patch changed `if (u * v == 0){...}` to `if ((u == 0) || (v == 0)){...}` to avoid the overflow. The generated patch changed the if-statement to `if (u * v == 0){ if (Math.abs(u) <= 1 || Math.abs(v) <= 1){...} }` which also avoids the overflow. Note that the two patches are semantically equivalent. When `u * v` is `0` and the absoluate values of `u` and `v` are both not greater than `1`, this implies mathematically either `u == 0` or `v == 0`.

** Others **
> The patches for `Chart 1`, `Chart 20`, `Chart 8`, `Closure 10`, `Closure 14`, `Closure 66`, `Closure 70`, `Closure 73`, `Closure 83`, `Closure 92`, `Closure 93`, `Lang 21`, `Lang 24`, `Lang 26`, `Lang 33`, `Lang 38`, `Lang 39`, `Lang 43`, `Lang 44`, `Lang 45`, `Lang 49`, `Lang 54`, `Lang 55`, `Lang 58`, `Lang 6`, `Math 25`, `Math 41`, `Math 53`, `Math 57`, `Math 59`, `Math 69`, `Math 70`, `Math 75`, `Math 85` are identical to the corresponding developer patches.
