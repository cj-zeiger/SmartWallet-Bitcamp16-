package me.cjzeiger.wallet;


import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
import retrofit.http.Part;
import rx.Observable;

public interface PythonInterface {
    @FormUrlEncoded
    @POST("/reg")
    Observable<RegisterResponse> register(@Field("regid") String registrationId, @Field("bankid") String capitalId);
}
