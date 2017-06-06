package com.brein.time.timeseries.gson;

import com.brein.time.timeseries.BucketTimeSeries;
import com.brein.time.timeseries.BucketTimeSeriesConfig;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.stream.Stream;

public class BucketTimeSeriesTypeConverter
        implements JsonSerializer<BucketTimeSeries>, JsonDeserializer<BucketTimeSeries> {

    public Class<BucketTimeSeries> getType() {
        return BucketTimeSeries.class;
    }

    @Override
    public JsonElement serialize(final BucketTimeSeries bucketTimeSeries,
                                 final Type type,
                                 final JsonSerializationContext context) {
        return TypeConverterHelper.serialize(bucketTimeSeries, context);
    }

    @SuppressWarnings("unchecked")
    @Override
    public BucketTimeSeries deserialize(final JsonElement jsonElement,
                                        final Type type,
                                        final JsonDeserializationContext context) throws JsonParseException {
        final Object[] values = TypeConverterHelper.deserialize(jsonElement, context);

        if (values == null || values.length != 3 ||
                Stream.of(values)
                        .filter(v -> v != null)
                        .findAny()
                        .orElse(null) == null) {
            return null;
        } else {
            return new BucketTimeSeries((BucketTimeSeriesConfig) values[0],
                    (Serializable[]) values[1], (long) values[2]);
        }
    }
}
