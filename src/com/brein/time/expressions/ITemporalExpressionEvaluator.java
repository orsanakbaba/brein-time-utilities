package com.brein.time.expressions;

import java.io.Closeable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * A temporal expression evaluator is an instance which is able to evaluate temporal expressions, e.g., formulars
 * like {@code 4h + 2min}. It can also be used to utilize variables within the expression, e.g., {@code t + 4h}. The
 * syntax of the expression may vary on the concrete implementation used.
 */
public interface ITemporalExpressionEvaluator extends Closeable {

    void init(final Map<String, Object> settings);

    /**
     * Adds a formula to the collection of available formulas.
     *
     * @param formula the actual formula
     *
     * @return the {@code identifier} of the formula
     *
     * @throws IllegalArgumentException if the formula is invalid or not parseable
     */
    default String addFormula(final String formula) throws IllegalArgumentException {
        return addFormula(formula, Collections.emptySet());
    }

    /**
     * Adds a formula to the collection of available formulas.
     *
     * @param identifier the identifier to be associated to the formula
     * @param formula    the actual formula
     *
     * @return the {@code identifier} of the formula
     *
     * @throws IllegalArgumentException if the formula is invalid or not parseable
     */
    default String addFormula(final String identifier, final String formula) throws IllegalArgumentException {
        return addFormula(identifier, formula, Collections.emptySet());
    }

    /**
     * Adds a formula to the collection of available formulas.
     *
     * @param formula   the actual formula
     * @param variables the variables and their values bound within the context of the formula
     *
     * @return the {@code identifier} of the formula
     *
     * @throws IllegalArgumentException if the formula is invalid or not parseable
     */
    default String addFormula(final String formula,
                              final Collection<String> variables) throws IllegalArgumentException {
        final String id = UUID.randomUUID().toString();
        return addFormula(id, formula, variables);
    }

    /**
     * Adds a formula to the collection of available formulas.
     *
     * @param identifier the identifier to be associated to the formula
     * @param formula    the actual formula
     * @param variables  the variables and their values bound within the context of the formula
     *
     * @return the {@code identifier} of the formula
     *
     * @throws IllegalArgumentException if the formula is invalid or not parseable
     */
    default String addFormula(final String identifier,
                              final String formula,
                              final Collection<String> variables) throws IllegalArgumentException {
        return addFormula(identifier, formula, new HashSet<>(variables));
    }

    /**
     * Adds a formula to the collection of available formulas.
     *
     * @param identifier the identifier to be associated to the formula
     * @param formula    the actual formula
     * @param variables  the variables and their values bound within the context of the formula
     *
     * @return the {@code identifier} of the formula
     *
     * @throws IllegalArgumentException if the formula is invalid or not parseable
     */
    String addFormula(final String identifier,
                      final String formula,
                      final Set<String> variables) throws IllegalArgumentException;

    /**
     * Removed the formula with the specified {@code identifier} from the collection of formulas.
     *
     * @param identifier the identifier associated to the formula
     */
    void removeFormula(final String identifier);

    /**
     * Helper method that should be used if a formula is only used once. If it is used multiple times, it is
     * recommended to bind the formula to an identifier, this may increase performance if the underlying
     * implementation supports a pre-parsing of the formula.
     *
     * @param formula the formula to be evaluated
     *
     * @return the evaluation result
     *
     * @throws IllegalArgumentException if the {@code identifier} is invalid, i.e., if no formula is known or the
     *                                  formula cannot be evaluated
     * @see #evaluate(String)
     */
    default long evaluateFormula(final String formula) throws IllegalArgumentException {
        return evaluateFormula(formula, Collections.emptyMap());
    }

    /**
     * Helper method that should be used if a formula is only used once. If it is used multiple times, it is
     * recommended to bind the formula to an identifier, this may increase performance if the underlying
     * implementation supports a pre-parsing of the formula.
     *
     * @param formula          the formula to be evaluated
     * @param variableBindings the variables bound within the context
     *
     * @return the evaluation result
     *
     * @throws IllegalArgumentException if the {@code identifier} is invalid, i.e., if no formula is known or the
     *                                  formula cannot be evaluated
     * @see #evaluate(String, Map)
     */
    default long evaluateFormula(final String formula,
                                 final Map<String, Long> variableBindings) throws IllegalArgumentException {

        // create the formula and remove it again
        final String id = addFormula(formula);
        final long result = evaluate(id, variableBindings);
        removeFormula(id);

        return result;
    }

    /**
     * Method to evaluate the temporal expression specified for the {@code identifier}.
     *
     * @param identifier the identifier of the formula to be evaluated
     *
     * @return the result of the evaluation, whereby the unit of the evaluation can be determined by {@code
     * getEvaluationUnit}
     *
     * @throws IllegalArgumentException if the {@code identifier} is invalid, i.e., if no formula is known or the
     *                                  formula cannot be evaluated
     * @see #getEvaluationUnit()
     */
    default long evaluate(final String identifier) throws IllegalArgumentException {
        return evaluate(identifier, Collections.emptyMap());
    }

    /**
     * Method to evaluate the temporal expression specified for the {@code identifier}.
     *
     * @param identifier       the formula to be evaluated
     * @param variableBindings the variables bound within the context
     *
     * @return the evaluation result
     *
     * @throws IllegalArgumentException if the {@code identifier} is invalid, i.e., if no formula is known
     * @see #evaluate(String, Map)
     */
    long evaluate(final String identifier, final Map<String, Long> variableBindings) throws IllegalArgumentException;

    /**
     * Returns the unit of the values returned by the {@code evaluate} or {@code evaluateFormula} methods. The
     * returned unit may be definable via the {@code formula}. In that case, the methods will return the unit based
     * on the specific formula.
     *
     * @return the general unit of the result when evaluating a formula, may be changed within the formula itself (if
     * supported by the underlying implementation)
     */
    TimeUnit getEvaluationUnit();
}
