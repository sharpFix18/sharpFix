# Correct Patches

** Chart 24 **
> The generated patch is semantically equivalent to the developer patch in using the value `Math.min(v, this.upperBound)` for calculating `g`.

** Closure 13 **
> The generated patch is semantically equivalent to the developer patch. The developer patch used a while-loop that contains three statements. The generated patch used a for-loop which contains the same three statements.

** Math 58 **
> The developer patch saved `(new ParameterGuesser(getObservations())).guess()` as a variable, and passed the value of that variable to the method `fit`. The generated patch directly passed the value of `(new ParameterGuesser(getObservations())).guess()` to the method `fit`. So the two patches are semantically equivalent.

** Others **
> The patches for `Chart 8`, `Closure 14`, `Closure 70`, `Closure 73`, `Closure 86`, `Lang 16`, `Lang 21`, `Lang 24`, `Lang 26`, `Lang 33`, `Lang 39`, `Lang 43`, `Lang 44`, `Lang 45`, `Lang 49`, `Lang 54`, `Lang 6`, `Math 33`, `Math 41`, `Math 53`, `Math 57`, `Math 59`, and `Math 70` are identical to the corresponding developer patches.