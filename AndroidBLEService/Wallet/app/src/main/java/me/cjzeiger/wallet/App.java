package me.cjzeiger.wallet;

import android.app.Application;

import com.polidea.rxandroidble.RxBleClient;
import com.polidea.rxandroidble.RxBleDevice;

import rx_gcm.internal.RxGcm;

public class App extends Application {

    private RxBleClient rxBleClient;

    @Override
    public void onCreate() {
        rxBleClient = RxBleClient.create(this);

        super.onCreate();

        RxGcm.Notifications.register(this, WalletReceiver.class, WalletBackground.class)
                .subscribe(token -> {}, error -> {});

    }

    public RxBleClient getClient(){
        return rxBleClient;
    }
}
