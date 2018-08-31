# Correct Patches

** Chart 24 **
> The generated patch is semantically equivalent to the developer patch in using the value `Math.min(v, this.upperBound)` for calculating `g`.

** Math 50 **
> The generated patch deletes the faulty statement `x0 = 0.5 * (x0 + x1 - delta);` and issemantically equivalent to the deletion of the enclosing if-statement as the developer patch does. This is because after deleting `x0 = 0.5 * (x0 + x1 - delta);`, `delta` would never be used, so it makes no difference that the declaration statement for `delta` is not deleted. It also makes no difference that the assignment `f0 = computeObjectiveValue(x0);` is not deleted since there is a break at the end under the case `REGULA_FALSI`, and the local variable `f0` is never used afterwards.

** Math 58 **
> The developer patch saved `(new ParameterGuesser(getObservations())).guess()` as a variable, and passed the value of that variable to the method `fit`. The generated patch directly passed the value of `(new ParameterGuesser(getObservations())).guess()` to the method `fit`. So the two patches are semantically equivalent.

** Others **
> The patches for `Chart 8`, `Closure 14`, `Closure 73`, `Lang 16`, `Lang 21`, `Lang 33`, `Lang 39`, `Lang 43`, `Lang 45`, `Lang 49`, `Lang 52`, `Lang 59`, `Lang 6`, `Math 33`, `Math 41`, `Math 53`, `Math 57`, `Math 59`, and `Math 70` are identical to the corresponding developer patches.