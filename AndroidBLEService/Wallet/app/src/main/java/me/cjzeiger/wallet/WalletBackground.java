package me.cjzeiger.wallet;


import android.os.Bundle;

import rx.Observable;
import rx_gcm.GcmReceiverUIBackground;
import rx_gcm.Message;

public class WalletBackground implements GcmReceiverUIBackground {



    @Override public void onNotification(Observable<Message> oMessage) {
        App a;
        oMessage.subscribe( message -> {

            Bundle payload = message.payload();

            String newpur = payload.getString("newpurchase");

            System.out.println(payload.toString());
        });
    }

}
