package com.brein.time.expressions;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.function.Function;
import net.objecthunter.exp4j.tokenizer.UnknownFunctionOrVariableException;
import org.apache.log4j.Logger;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Exp4JTemporalExpressionEvaluator implements ITemporalExpressionEvaluator {
    public static final String GENERAL_VARIABLES = "generalVariables";
    public static final String ADDITIONAL_FUNCTIONS = "additionalFunctions";
    public static final String LOWEST_TIME_GRANULARITY = "smallestTimeUnit";
    public static final TimeUnit DEFAULT_LOWEST_TIME_GRANULARITY = TimeUnit.MILLISECONDS;

    private static final Logger LOGGER = Logger.getLogger(Exp4JTemporalExpressionEvaluator.class);
    private static final Map<String, TimeUnit> UNITS = Stream.of(
            new SimpleEntry<>("ns", TimeUnit.NANOSECONDS),
            new SimpleEntry<>("μs", TimeUnit.MICROSECONDS),
            new SimpleEntry<>("ms", TimeUnit.MILLISECONDS),
            new SimpleEntry<>("sec", TimeUnit.SECONDS),
            new SimpleEntry<>("min", TimeUnit.MINUTES),
            new SimpleEntry<>("h", TimeUnit.HOURS),
            new SimpleEntry<>("d", TimeUnit.DAYS)
    ).collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue));

    private final Map<String, Expression> expressions = new ConcurrentHashMap<>();

    private Map<String, Double> generalVariables;
    private List<Function> additionalFunctions;
    private TimeUnit lowestTimeGranularity;

    public void init() {
        init(Collections.emptyMap());
    }

    @Override
    public void init(final Map<String, Object> settings) {

        // this has to be done first, so that all the other methods work properly
        this.lowestTimeGranularity = readLowestTimeGranularity(settings);

        /*
         * The settings may contain a map of general-variables, which bind a numeric value
         * to a named variable.
         */
        this.generalVariables = new HashMap<>();
        this.generalVariables.putAll(createDefaultVariables());
        this.generalVariables.putAll(readGeneralVariables(settings));

        /*
         * Now add all the additional functions, which may have been defined via settings
         * or are additional functions, which are provided in the temporal context.
         */
        this.additionalFunctions = new ArrayList<>();
        this.additionalFunctions.addAll(createDefaultAdditionalFunctions());
        this.additionalFunctions.addAll(readAdditionalFunctions(settings));
    }

    @Override
    public String addFormula(final String identifier,
                             final String formula,
                             final Set<String> variables) throws IllegalArgumentException {
        final Set<String> normalizedVariables = variables == null ? Collections.emptySet() : variables;

        try {
            this.expressions.put(identifier, new ExpressionBuilder(formula)
                    .functions(this.additionalFunctions)
                    .variables(this.generalVariables.keySet())
                    .variables(normalizedVariables)
                    .build());
        } catch (final UnknownFunctionOrVariableException e) {
            throw new IllegalArgumentException("Unable to parse the formula: " + formula, e);
        }

        return identifier;
    }

    @Override
    public void removeFormula(final String identifier) {
        this.expressions.remove(identifier);
    }

    @Override
    public long evaluate(final String identifier,
                         final Map<String, Long> variableBindings) throws IllegalArgumentException {
        final Expression expression = this.expressions.get(identifier);

        if (expression == null) {
            throw new IllegalArgumentException("The identifier '" + identifier + "'" +
                    " does not have any valid formula attached.");
        }

        // add the variables (first general, afterwards specific) and evaluate
        final Double result = expression
                .setVariables(this.generalVariables)
                .setVariables(variableBindings.entrySet().stream()
                        .map(e -> new SimpleEntry<>(e.getKey(), e.getValue().doubleValue()))
                        .collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue)))
                .evaluate();

        // return the long-value
        if (result.isNaN() || result.isInfinite()) {
            LOGGER.warn("Received invalid long-result, returning 0 instead.");
            return 0L;
        } else {
            return result.longValue();
        }
    }

    @Override
    public TimeUnit getEvaluationUnit() {
        return this.lowestTimeGranularity;
    }

    /**
     * There is no need to actually close this implementation of the {@code ITemporalExpressionEvaluator}, since
     * it does not bind any resources. Nevertheless, to fulfill the interface the method is implemented.
     *
     * @see ITemporalExpressionEvaluator
     */
    @Override
    public void close() {
        this.expressions.clear();
        this.generalVariables = null;
        this.additionalFunctions = null;
    }

    protected TimeUnit readLowestTimeGranularity(final Map<String, Object> settings) {
        final Object lowestGranularityObj = settings.get(LOWEST_TIME_GRANULARITY);

        if (lowestGranularityObj == null) {
            return DEFAULT_LOWEST_TIME_GRANULARITY;
        } else if (TimeUnit.class.isInstance(lowestGranularityObj)) {
            return TimeUnit.class.cast(lowestGranularityObj);
        } else if (String.class.isInstance(lowestGranularityObj)) {
            try {
                return TimeUnit.valueOf(String.class.cast(lowestGranularityObj).toUpperCase());
            } catch (final IllegalArgumentException e) {
                LOGGER.error(String.format("Unable to parse the specified '%s' value '%s', using default '%s'.",
                        LOWEST_TIME_GRANULARITY, lowestGranularityObj, DEFAULT_LOWEST_TIME_GRANULARITY), e);
                return DEFAULT_LOWEST_TIME_GRANULARITY;
            }
        } else {
            LOGGER.warn(String.format("Unable to parse the specified '%s' value '%s', using default '%s'.",
                    LOWEST_TIME_GRANULARITY, lowestGranularityObj, DEFAULT_LOWEST_TIME_GRANULARITY));
            return DEFAULT_LOWEST_TIME_GRANULARITY;
        }
    }

    protected Map<String, Double> createDefaultVariables() {
        return UNITS.entrySet().stream()
                .filter(e -> this.lowestTimeGranularity.compareTo(e.getValue()) <= 0)
                .map(e -> {
                    final Long value = this.lowestTimeGranularity.convert(1, e.getValue());
                    return new SimpleEntry<>(e.getKey(), value.doubleValue());
                })
                .collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue));
    }

    protected Map<String, Double> readGeneralVariables(final Map<String, Object> settings) {
        if (settings == null) {
            return Collections.emptyMap();
        }

        final Object generalVariablesObj = settings.get(GENERAL_VARIABLES);
        if (generalVariablesObj == null) {
            return Collections.emptyMap();
        } else if (!(generalVariablesObj instanceof Map)) {
            return Collections.emptyMap();
        }

        /*
         * Otherwise let's make sure the elements in the map are valid and
         * create a map that contains double values.
         */
        @SuppressWarnings("unchecked")
        final Map<Object, Object> map = Map.class.cast(generalVariablesObj);
        return map.entrySet().stream()
                .map(e -> {
                    final Object nameObj = e.getKey();
                    final Object valueObj = e.getValue();

                    if (String.class.isInstance(nameObj) && Number.class.isInstance(valueObj)) {
                        return new SimpleEntry<>(String.class.cast(nameObj),
                                Number.class.cast(valueObj).doubleValue());
                    } else {
                        LOGGER.warn(String.format("Found invalid '%s' settings " + "(key: %s, value: %s)",
                                GENERAL_VARIABLES, nameObj, valueObj));
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue));
    }

    protected List<Function> createDefaultAdditionalFunctions() {
        final List<Function> defaultAdditionalFunctions = new ArrayList<>();

        defaultAdditionalFunctions.add(new Function("now", 0) {

            @Override
            public double apply(final double... doubles) {
                final TimeUnit unit = Exp4JTemporalExpressionEvaluator.this.lowestTimeGranularity;
                return unit.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
            }
        });

        defaultAdditionalFunctions.addAll(UNITS.entrySet().stream()
                .filter(e -> this.lowestTimeGranularity.compareTo(e.getValue()) <= 0)
                .map(e -> new Function(createToFunctionName(e.getValue()), 1) {

                    @Override
                    public double apply(final double... args) {
                        final Double value = args[0];
                        final long mappedValue = value.longValue();
                        final TimeUnit unit = Exp4JTemporalExpressionEvaluator.this.lowestTimeGranularity;

                        return e.getValue().convert(mappedValue, unit);
                    }
                })
                .collect(Collectors.toList()));

        return defaultAdditionalFunctions;
    }

    protected String createToFunctionName(final TimeUnit unit) {
        final String lowerCase = unit.name().toLowerCase();
        return "to" + lowerCase.substring(0, 1).toUpperCase() + lowerCase.substring(1);
    }

    protected List<Function> readAdditionalFunctions(final Map<String, Object> settings) {
        if (settings == null) {
            return Collections.emptyList();
        }

        final Object additionalFunctionsObj = settings.get(ADDITIONAL_FUNCTIONS);
        if (additionalFunctionsObj == null) {
            return Collections.emptyList();
        } else if (!(additionalFunctionsObj instanceof Map)) {
            return Collections.emptyList();
        }

        /*
         * Otherwise let's make sure the elements in the map are valid and
         * create a map that contains functions as values.
         */
        @SuppressWarnings("unchecked")
        final Map<Object, Object> map = Map.class.cast(additionalFunctionsObj);
        return map.entrySet().stream()
                .map(e -> {
                    final Object nameObj = e.getKey();
                    final Object valueObj = e.getValue();

                    if (!String.class.isInstance(nameObj)) {
                        LOGGER.warn(String.format("Found invalid %s (key: %s, value: %s)",
                                ADDITIONAL_FUNCTIONS, nameObj, valueObj));
                        return null;
                    }

                    final String name = String.class.cast(nameObj);
                    if (Function.class.isInstance(valueObj)) {
                        final Function func = Function.class.cast(valueObj);
                        if (!name.equals(func.getName())) {
                            LOGGER.warn(String.format("Mismatch in function name (%s vs. %s)", name, func.getName()));
                        }

                        return func;
                    } else if (java.util.function.Supplier.class.isInstance(valueObj)) {
                        return new Function(name, 0) {

                            @Override
                            @SuppressWarnings("unchecked")
                            public double apply(final double... args) {
                                final Object result = java.util.function.Supplier.class.cast(valueObj).get();
                                return toDouble(result);
                            }
                        };
                    } else if (java.util.function.Function.class.isInstance(valueObj)) {
                        return new Function(name, 1) {

                            @Override
                            @SuppressWarnings("unchecked")
                            public double apply(final double... args) {
                                final Object result = java.util.function.Function.class.cast(valueObj)
                                        .apply(toDouble(args[0]));
                                return toDouble(result);
                            }
                        };
                    } else if (java.util.function.BiFunction.class.isInstance(valueObj)) {
                        return new Function(name, 2) {

                            @Override
                            @SuppressWarnings("unchecked")
                            public double apply(final double... args) {
                                final Object result = java.util.function.BiFunction.class.cast(valueObj)
                                        .apply(toDouble(args[0]), toDouble(args[1]));
                                return toDouble(result);
                            }
                        };
                    } else {
                        LOGGER.warn(String.format("Found invalid additionalFunction (key: %s, value: %s)",
                                name, valueObj));
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Simple helper method to transform an {@code Object} into a {@code double}. The method returns {@code NaN}, if
     * the value cannot be transformed.
     *
     * @param val the value to transform
     *
     * @return the created double
     */
    protected double toDouble(final Object val) {

        if (val == null) {
            return Double.NaN;
        } else if (Double.class.isInstance(val)) {
            return Double.class.cast(val);
        } else if (Number.class.isInstance(val)) {
            return Number.class.cast(val).doubleValue();
        } else if (double.class.isInstance(val)) {
            return double.class.cast(val);
        } else if (long.class.isInstance(val)) {
            return long.class.cast(val);
        } else if (int.class.isInstance(val)) {
            return int.class.cast(val);
        } else if (byte.class.isInstance(val)) {
            return byte.class.cast(val);
        } else if (float.class.isInstance(val)) {
            return float.class.cast(val);
        } else {
            return Double.NaN;
        }
    }
}
