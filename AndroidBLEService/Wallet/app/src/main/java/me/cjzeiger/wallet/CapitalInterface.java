package me.cjzeiger.wallet;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public interface CapitalInterface {

    @GET("/accounts/{id}/purchases")
    Observable<Purchase> getPurchases(@Path("id") String accountid);

    @GET("/accounts/{id}")
    Observable<Account> getAccount(@Path("id") String accountid, @Query("key") String apikey);
}
