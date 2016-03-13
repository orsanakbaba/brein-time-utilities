package com.brein.time.timeseries.gson;

import com.brein.time.timeseries.BucketTimeSeries;
import com.brein.time.timeseries.BucketTimeSeriesConfig;
import com.google.gson.*;

import java.io.Serializable;
import java.lang.reflect.Type;

public class BucketTimeSeriesTypeConverter implements JsonSerializer<BucketTimeSeries>, JsonDeserializer<BucketTimeSeries> {

    public Class<BucketTimeSeries> getType() {
        return BucketTimeSeries.class;
    }

    @Override
    public JsonElement serialize(final BucketTimeSeries bucketTimeSeries, final Type type, final JsonSerializationContext context) {
        return TypeConverterHelper.serialize(bucketTimeSeries, context);
    }

    @Override
    public BucketTimeSeries deserialize(final JsonElement jsonElement, final Type type, final JsonDeserializationContext context) throws JsonParseException {
        final Object[] values = TypeConverterHelper.deserialize(jsonElement, context);

        @SuppressWarnings("unchecked")
        final BucketTimeSeries res = new BucketTimeSeries((BucketTimeSeriesConfig) values[0], (Serializable[]) values[1], (long) values[2]);

        return res;
    }
}
