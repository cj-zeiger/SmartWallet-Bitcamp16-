package me.cjzeiger.wallet;

import android.os.Bundle;

import com.polidea.rxandroidble.RxBleConnection;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;
import rx_gcm.GcmReceiverData;
import rx_gcm.Message;

public class WalletReceiver implements GcmReceiverData {

    private final String accountid = "570a0ec2059d0946504a8eeb";
    private final UUID uuidLine1 = new UUID(0x4d8dd3ab38444e7dL, 0x86ff4a2aa8e7f132L);
    private final UUID uuidLine2 = new UUID(0x4d8dd3ab38444e7dL, 0x86ff4a2aa8e7f133L);
    private final String api_key = "326b92975d191c335b19f9c256850259";
    private String balance;

    @Override public Observable<Message> onNotification(Observable<Message> oMessage) {
        return oMessage.doOnNext(message -> {
            Bundle payload = message.payload();

            String newpur = payload.getString("newpurchase");
            updateDisplay((App)message.application());
        });
    }

    private void updateDisplay(App a){
        CapitalInterface ci = Model.createRetrofitService(CapitalInterface.class, "http://api.reimaginebanking.com");

        CompositeSubscription sub = new CompositeSubscription();

        Observable<Account> acc = ci.getAccount(accountid, api_key);

        sub.add(acc.subscribe(success -> {
            System.out.println("success");
            System.out.println(Float.toString(success.balance));
            updateBT(Float.toString(success.balance), a);
            sub.unsubscribe();
        }, error -> {
            error.printStackTrace();
            sub.unsubscribe();
        }));
    }

    void updateBT(String l, App a){
        CompositeSubscription sub = new CompositeSubscription();
        sub.add(a.getClient().scanBleDevices()
                .filter(device -> {
                    String dname = device.getBleDevice().getName();
                    return dname != null && dname.equals("LCDDISPL");
                })
                .first()
                .timeout(10, TimeUnit.SECONDS)
                .flatMap(result -> {
                    return result.getBleDevice().establishConnection(a, false);
                }).flatMap(con -> {
                    String line = "Avail:$" + l;
                    if (line.length() < 16) {
                        line = padString(line, 16 - line.length());
                    }
                    return con.writeCharacteristic(uuidLine1, line.getBytes());
                })
                .subscribe(success -> {
                    sub.unsubscribe();
                }, err -> {
                    sub.unsubscribe();
                    err.printStackTrace();
                }));
    }

    private String padString(String line, int padding){
        StringBuffer b = new StringBuffer(line);
        for(int i = 0; i < padding; i++){
            b.append(" ");
        }

        return b.toString();
    }

}