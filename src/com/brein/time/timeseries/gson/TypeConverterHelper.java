package com.brein.time.timeseries.gson;

import com.brein.time.timeseries.BucketTimeSeries;
import com.brein.time.timeseries.BucketTimeSeriesConfig;
import com.google.gson.*;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

public class TypeConverterHelper {

    public static JsonObject serialize(final BucketTimeSeries<?> o, final JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();

        // the configuration
        jsonObject.add("timeUnit", context.serialize(o.getConfig().getTimeUnit()));
        jsonObject.add("bucketSize", context.serialize(o.getConfig().getBucketSize()));
        jsonObject.add("bucketContent", TypeConverterHelper.serializeClass(o.getConfig().getBucketContent(), context));
        jsonObject.add("fillNumberWithZero", context.serialize(o.getConfig().isFillNumberWithZero()));

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

        // get the important classes
        final Class<?> bucketContent = resolveClass("bucketContent", jsonObject, context);

        // configuration
        final TimeUnit timeUnit = context.deserialize(jsonObject.get("timeUnit"), TimeUnit.class);
        final int bucketSize = context.deserialize(jsonObject.get("bucketSize"), int.class);
        final boolean fillNumberWithZero = context.deserialize(jsonObject.get("fillNumberWithZero"), boolean.class);

        // the values
        final long now = context.deserialize(jsonObject.get("now"), long.class);
        final Serializable[] timeSeries = timeSeriesDeserializer.apply(bucketContent, jsonObject.get("timeSeries"));

        @SuppressWarnings("unchecked")
        final BucketTimeSeriesConfig config = new BucketTimeSeriesConfig(bucketContent, timeUnit, timeSeries.length, bucketSize, fillNumberWithZero);
        return new Object[]{config, timeSeries, now};
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T> resolveClass(final String key, final JsonObject jsonObject, final JsonDeserializationContext context) {
        try {
            return (Class<T>) Class.forName(context.deserialize(jsonObject.get(key), String.class));
        } catch (final ClassNotFoundException e) {
            throw new JsonParseException("Cannot resolve class.", e);
        }
    }

    public static JsonElement serializeClass(final Class<?> clazz, final JsonSerializationContext context) {
        return context.serialize((clazz == null ? Object.class : clazz).getCanonicalName());
    }

    public static Class<?> arrayClass(final Class<?> clazz) {
        return Array.newInstance(clazz, 0).getClass();
    }
}
