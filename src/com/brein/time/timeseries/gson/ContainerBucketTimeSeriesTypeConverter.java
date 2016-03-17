package com.brein.time.timeseries.gson;

import com.brein.time.timeseries.BucketTimeSeriesConfig;
import com.brein.time.timeseries.ContainerBucketTimeSeries;
import com.google.gson.*;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;

public class ContainerBucketTimeSeriesTypeConverter implements JsonSerializer<ContainerBucketTimeSeries>, JsonDeserializer<ContainerBucketTimeSeries> {

    public Class<ContainerBucketTimeSeries> getType() {
        return ContainerBucketTimeSeries.class;
    }

    @Override
    public JsonElement serialize(final ContainerBucketTimeSeries o, final Type type, final JsonSerializationContext context) {
        final JsonObject jsonObject = TypeConverterHelper.serialize(o, context);
        jsonObject.add("supplier", TypeConverterHelper.serializeClass(o.getCollectionType(), context));
        jsonObject.add("collectionContent", TypeConverterHelper.serializeClass(o.getCollectionContent(), context));

        return jsonObject;
    }

    @Override
    public ContainerBucketTimeSeries deserialize(final JsonElement jsonElement, final Type type, final JsonDeserializationContext context) throws JsonParseException {
        System.out.println(jsonElement.toString());

        final JsonObject jsonObject = jsonElement.getAsJsonObject();

        final Class<? extends Collection> supplierClass = TypeConverterHelper.resolveClass("supplier", jsonObject, context);
        final Supplier<Collection> supplier = () -> {
            if (supplierClass == null) {
                throw new NullPointerException();
            }

            try {
                return supplierClass.newInstance();
            } catch (final Exception e) {
                throw new IllegalArgumentException("Invalid supplier", e);
            }
        };

        // create a double array and then cast
        final Class<?> collectionContent = TypeConverterHelper.resolveClass("collectionContent", jsonObject, context);
        final Class<?> collectionContentArray = TypeConverterHelper.arrayClass(collectionContent);
        final Object[] values = TypeConverterHelper.deserialize(jsonElement, context,
                (bucketContent, el) -> context.deserialize(el, TypeConverterHelper.arrayClass(collectionContentArray)));
        final Serializable[] result = fromJson(((Object[][]) values[1]), supplier);

        @SuppressWarnings("unchecked")
        final ContainerBucketTimeSeries res = new ContainerBucketTimeSeries(supplier::get,
                (BucketTimeSeriesConfig) values[0], result, (long) values[2]);

        return res;
    }

    @SuppressWarnings("unchecked")
    protected Serializable[] fromJson(final Object[][] contents, final Supplier<Collection> supplier) {
        final int contentsLength = contents.length;
        final Serializable[] result = (Serializable[]) Array.newInstance(Serializable.class, contentsLength);
        for (int i = 0; i < contentsLength; i++) {
            final Collection coll;
            if (contents[i] == null) {
                coll = null;
            } else {
                coll = supplier.get();
                Collections.addAll(coll, contents[i]);
            }

            result[i] = Serializable.class.cast(coll);
        }

        return result;
    }
}
