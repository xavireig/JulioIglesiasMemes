package xavireig.com.julioiglesiasmemes;

import android.os.Handler;
import android.os.Looper;

public class MyThread extends Thread {
    Handler handler;

    public MyThread() {

    }

    @Override
    public void run() {
        Looper.prepare();
        handler = new Handler();
        Looper.loop();

    }
}
