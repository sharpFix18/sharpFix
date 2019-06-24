/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Mathematics.GoalSeeking;

import Mathematics.*;
import Mathematics.Algorithm.*;
import Mathematics.Equality.*;
import Mathematics.Function.*;
import Mathematics.Result.*;
import Validation.*;

/**
 * {@see Algorithm} implementing the
 * <a href="http://en.wikipedia.org/wiki/Bisection_method">bisection method</a>
 * for a function of double into double.
 * @author Rune Dahl Iversen
 */
public final class Bisection extends GoalSeekFunction<Interval<Double>, Double, Double>
        implements Criterion<Equals<Double>>,
        InitialValue<Interval<Double>>,
        Mathematics.Algorithm.Iterative<Result> {

    private int _maxIter;
    private static final Validator<Double> __validator =
            Validation.Factory.FiniteReal();
    private static final Validator<Integer> __maxIterValidator =
            new IntegerGreaterThan();

    /**
     * Creates an instance of the bisection method {@see Algorithm algorithm}
     * for goal-seeking the input to a function.
     * @param function          Function.
     * @param goalValue         Target value.
     * @param initialValue      Initial values. The 2 interval must encompass
     *                          the solution.
     * @param criterion         End criterion for the iteration.
     * @param maximumIterations Maximum number of iterations allowed.
     */
    public Bisection(final Function<Double, Double> function,
            final double goalValue,
            final Interval<Double> initialValue,
            final Equals<Double> criterion,
            final int maximumIterations) {
        super(criterion, function, __validator, goalValue, initialValue);
        this.setMaximumIterations(maximumIterations);
    }

    @Override
    public int getMaximumIterations() {
        return this._maxIter;
    }

    @Override
    public void setMaximumIterations(final int iterations) {
        if (!__maxIterValidator.isValid(iterations)) {
            throw new IllegalArgumentException(
                    __maxIterValidator.message(iterations, "Maximum iterations"));
        }
        this._maxIter = iterations;
    }

    @Override
    public Result run() {
        Result result = null;
        try {
            Equals<Double> criterion = this.getCriterion();
            Function<Double, Double> value = this.getFunction();
            double goalValue = this.getGoalValue();
            int maxIter = this.getMaximumIterations();
            Interval<Double> initialValue = this.getInitialValue();
            Interval<Double> interval = new IntervalReal(
                    initialValue.getLowerBound(), Interval.EndType.Includes,
                    initialValue.getUpperBound(), Interval.EndType.Includes);
            double output = value.value(initialValue.getLowerBound());
            if (criterion.value(output, goalValue)) {
                result = new SuccessWithValue(initialValue.getLowerBound());
            }
            double lSign = Math.signum(output - goalValue);
            output = value.value(interval.getUpperBound());
            if (criterion.value(output, goalValue)) {
                result = new SuccessWithValue(initialValue.getUpperBound());
            }
            double uSign = Math.signum(output - goalValue);

            if (0.0 < lSign * uSign) {
                result = new SolutionNotEnclosedFailure<Double, Double>(
                        value, interval, goalValue);
            } else {
                double midPoint = Double.NaN;
                int iter;
                for (iter = 0; iter < maxIter
                        && !criterion.value(output, goalValue); iter++) {
                    midPoint = interval.getLowerBound() / 2.0
                            + interval.getUpperBound() / 2.0;
                    if (midPoint == interval.getLowerBound()
                            || midPoint == interval.getUpperBound()) {
                        result = new ResolutionNotFineEnough<Double, Double>(
                                value, interval, goalValue);
                    }
                    output = value.value(midPoint);
                    if (Math.signum(output - goalValue) == lSign) {
                        interval.setLowerBound(midPoint);
                    } else {
                        interval.setUpperBound(midPoint);
                    }
                }
                if (result == null) {
                    if (maxIter <= iter) {
                        result = new MaximumIterationsFailure(iter);
                    } else {
                        result = new IterativeSuccess(iter, midPoint);
                    }
                }
            }
        } catch (Exception e) {
            result = new UnhandledExceptionThrown(e);
        }
        return result;
    }
}