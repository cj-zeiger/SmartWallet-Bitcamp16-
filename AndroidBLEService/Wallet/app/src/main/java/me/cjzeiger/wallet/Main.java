package me.cjzeiger.wallet;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import rx_gcm.GcmReceiverUIForeground;
import rx_gcm.Message;
import rx_gcm.internal.RxGcm;

public class Main extends AppCompatActivity implements GcmReceiverUIForeground {

    private final String user_id = "570a0ec2059d0946504a8eeb";
    private final String account_id = "570a0ec2059d0946504a8eeb";

    private CompositeSubscription sub = new CompositeSubscription();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume(){
        super.onResume();
        final String id = user_id;
        sub = new CompositeSubscription();

        PythonInterface py = Model.createRetrofitService(PythonInterface.class,"http://45.55.209.228:5000");

        sub.add(RxGcm.Notifications.currentToken()
            .flatMap(token -> {
                return py.register(token, id);


            })
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(regresp -> {
                if (regresp != null)
                    System.out.println(regresp);
            }, error -> {
                if (!error.getMessage().startsWith("403"))
                    error.printStackTrace();
            }));

    }

    @Override
    public void onPause(){
        super.onPause();

        if(sub != null)
            sub.unsubscribe();
        sub = null;
    }

    @Override
    public void onTargetNotification(Observable<Message> observable) {

    }

    @Override
    public void onMismatchTargetNotification(Observable<Message> observable) {

    }

    @Override
    public String target() {
        return "background";
    }
}
