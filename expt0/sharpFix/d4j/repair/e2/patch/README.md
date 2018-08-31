# Correct Patches

** Chart 24 **
> The generated patch is semantically equivalent to the developer patch in using the value `Math.min(v, this.upperBound)` for calculating `g`.

** Chart 26 **
> The developer patch creates an if-condition `owner != null` to guard two statements. The generated patch creates an if-condition `plotState != null && plotState.getOwner() != null` to guard the same statements. Note that `owner` is equivalent to `plotState.getOwner()`. `plotState != null` is a redundant check since it is already used in the condition `plotState != null && hotspot != null` of the enclosing if-statement.

** Closure 126 **
> The developer patch deletes the statement `if (NodeUtil.hasFinally(n)){...}`. The generated patch creates an if-condition `n.isLabel()` to guard the body statements of `if (NodeUtil.hasFinally(n)){...}`. The patch is semantically equivalent to the developer patch because the if-condition can never be `true` since `if (NodeUtil.hasFinally(n)){...}` is enclosed in an if-statement as `if (n.isTry()){...}` and `n.isTry()` and `n.isLabel()` can never be both `true`.

** Closure 20 **
> The developer patch creates a new condition `value != null && value.getNext() == null && NodeUtil.isImmutableValue(value)` to guard a sequence of statements. The generated patch is semantically equivalent to the developer patch in that (1) when the condition is satisfied, the sequence of statements should be executed and (2) when the condition is not satisified (when `value == null`, or `value.getNext() != null`, or `!NodeUtil.isImmutableValue(value)`), the program should return `n`.

** Lang 55 **
> The generated patch used `this.stopTime` and the developer patch used `stopTime`. Since `stopTime` is a class field, the generated patch is semantically equivalent to the developer patch.

** Math 11 **
> The generated patch used `-dim / 2.0` and the developer patch used `-0.5 * dim`. This is semantically equivalent.

** Math 85 **
> The generated patch created the condition `fa * fb > 0.0` to guard the exception-throwing statement. Though the new if-statement is guarded by `fa * fb >= 0.0`, the condition `fa * fb > 0.0` is stronger than `fa * fb >= 0.0`. So the generated patch is semantically equivalent to the developer patch.

** Math 94 **
> The developer patch changed `if (u * v == 0){...}` to `if ((u == 0) || (v == 0)){...}` to avoid the overflow. The generated patch changed the if-statement to `if (u * v == 0){ if (Math.abs(u) <= 1 || Math.abs(v) <= 1){...} }` which also avoids the overflow. Note that the two patches are semantically equivalent. When `u * v` is `0` and the absoluate values of `u` and `v` are both not greater than `1`, this implies mathematically either `u == 0` or `v == 0`.

** Others **
> The patches for `Chart 1`, `Chart 20`, `Chart 4`, `Chart 8`, `Closure 14`, `Closure 70`, `Closure 73`, `Closure 83`, `Lang 17`, `Lang 21`, `Lang 22`, `Lang 24`, `Lang 26`, `Lang 33`, `Lang 38`, `Lang 39`, `Lang 40`, `Lang 43`, `Lang 45`, `Lang 49`, `Lang 52`, `Lang 54`, `Lang 6`, `Math 106`, `Math 25`, `Math 41`, `Math 42`, `Math 53`, `Math 57`, `Math 58`, `Math 59`, `Math 69`, `Math 70`, `Math 75`, `Math 79`,  are identical to the corresponding developer patches.

