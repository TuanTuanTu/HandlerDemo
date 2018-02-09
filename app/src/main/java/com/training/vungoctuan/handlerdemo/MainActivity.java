package com.training.vungoctuan.handlerdemo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView text_hello, text_thread;
    private static Object lock1 = new Object(), lock2 = new Object();
    private StringBuilder mStringBuilder = new StringBuilder(200);
    private Thread firstThread, secondThread;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text_hello = findViewById(R.id.text_hello);
        text_thread = findViewById(R.id.text_thread);
        initThread();
        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == 0) {
                    secondThread.start();
                    text_thread.setText("Thread Success!");
                    return true;
                }
                text_thread.setText("Thread Failed!");
                return false;
            }
        });
        mHandler.post(firstThread);

        //Use handler to add thread to queue
//        mHandler.postAtFrontOfQueue(secondThread);
//        mHandler.postAtFrontOfQueue(firstThread);
    }

    private void initThread() {
        firstThread = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (lock1) {
                    mStringBuilder.append("DeadLock Started ;)\nThread 1: Holding lock 1...");
                    text_hello.setText(mStringBuilder);
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        //
                    }
                    mStringBuilder.append("\nThread 1: Waiting for lock 2...");
                    text_hello.setText(mStringBuilder);
                    synchronized (lock2) {
                        mStringBuilder.append("\nThread 1: Holding lock 1 & 2...");
                        text_hello.setText(mStringBuilder);
                        mHandler.sendEmptyMessage(1);
                    }
                }
            }
        });
        secondThread = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (lock2) {
                    mStringBuilder.append("\nThread 2: Holding lock 2...");
                    text_hello.setText(mStringBuilder);
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        //
                    }
                    mStringBuilder.append("\nThread 2: Waiting for lock 1...");
                    text_hello.setText(mStringBuilder);
                    synchronized (lock1) {
                        mStringBuilder.append("\nThread 2: Holding lock 1 & 2...");
                        text_hello.setText(mStringBuilder);
                    }
                }
            }
        });
        //If Start 2 thread will have receive DEADLOCK :)
//        firstThread.start();
//        secondThread.start();
    }
}
