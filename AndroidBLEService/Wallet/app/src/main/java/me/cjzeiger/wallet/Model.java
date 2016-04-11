package me.cjzeiger.wallet;

import retrofit.RestAdapter;

public class Model {

    static <T> T createRetrofitService(final Class<T> clazz, final String endPoint) {
        final RestAdapter retrofit = new RestAdapter.Builder()
                .setEndpoint(endPoint)
                .build();
        T service = retrofit.create(clazz);

        return service;
    }
}
