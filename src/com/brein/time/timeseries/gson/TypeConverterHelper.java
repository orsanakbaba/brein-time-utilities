package com.brein.time.timeseries.gson;

import com.brein.time.timeseries.BucketTimeSeries;
import com.brein.time.timeseries.BucketTimeSeriesConfig;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

public class TypeConverterHelper {
    private static final Logger LOG = Logger.getLogger(TypeConverterHelper.class);

    public static JsonObject serialize(final BucketTimeSeries<?> o, final JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();

        // the configuration
        jsonObject.add("timeUnit", context.serialize(o.getConfig().getTimeUnit()));
        jsonObject.add("bucketSize", context.serialize(o.getConfig().getBucketSize()));
        jsonObject.add("bucketContent", TypeConverterHelper.serializeClass(o.getConfig().getBucketContent(), context));
        jsonObject.add("fillNumberWithZero", context.serialize(o.getConfig().isFillNumberWithZero()));
        //TODO: Check if contents are containers and store their template classes as well
        // the values
        jsonObject.add("timeSeries", context.serialize(o.order()));
        jsonObject.add("now", context.serialize(o.getNow()));

        return jsonObject;
    }

    public static Object[] deserialize(final JsonElement jsonElement, final JsonDeserializationContext context) {
        return deserialize(jsonElement, context, (bucketContent, el) -> context.deserialize(el, TypeConverterHelper.arrayClass(bucketContent)));
    }

    public static Object[] deserialize(final JsonElement jsonElement, final JsonDeserializationContext context, final BiFunction<Class<?>, JsonElement, Serializable[]> timeSeriesDeserializer) {
        final JsonObject jsonObject = jsonElement.getAsJsonObject();

        if (LOG.isTraceEnabled()) {
            LOG.trace(String.format("Trying to deserialize the element '%s'.", jsonElement.toString()));
        }

        // get the important classes
        final Class<?> bucketContent = resolveClass("bucketContent", jsonObject, context);

        // it may happen that we have an invalid version or type
        if (bucketContent == null) {
            return null;
        }

        // configuration
        final TimeUnit timeUnit = context.deserialize(jsonObject.get("timeUnit"), TimeUnit.class);
        final int bucketSize = context.deserialize(jsonObject.get("bucketSize"), int.class);
        final boolean fillNumberWithZero = context.deserialize(jsonObject.get("fillNumberWithZero"), boolean.class);

        // the values
        final Long now = context.deserialize(jsonObject.get("now"), Long.class);
        final Serializable[] timeSeries = timeSeriesDeserializer.apply(bucketContent, jsonObject.get("timeSeries"));

        @SuppressWarnings("unchecked")
        final BucketTimeSeriesConfig config = new BucketTimeSeriesConfig(bucketContent, timeUnit, timeSeries.length, bucketSize, fillNumberWithZero);
        return new Object[]{config, timeSeries, now};
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T> resolveClass(final String key, final JsonObject jsonObject, final JsonDeserializationContext context) {
        final String clazz = context.deserialize(jsonObject.get(key), String.class);
        if (clazz == null) {
            return null;
        } else {
            try {
                return (Class<T>) Class.forName(clazz);
            } catch (final ClassNotFoundException e) {
                throw new JsonParseException("Cannot resolve class.", e);
            }
        }
    }

    public static JsonElement serializeClass(final Class<?> clazz, final JsonSerializationContext context) {
        return context.serialize((clazz == null ? Object.class : clazz).getCanonicalName());
    }

    public static Class<?> arrayClass(final Class<?> clazz) {
        return Array.newInstance(clazz, 0).getClass();
    }
}
