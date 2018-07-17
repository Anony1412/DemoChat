package demo_chat.anony1412.itptit.demochat;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class DemoChat extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
