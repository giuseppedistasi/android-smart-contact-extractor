package com.gds.extractor.utils;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Luca on 30/03/2017.
 */


public class SchedulerProvider  {

    public static Scheduler computation() {
        return Schedulers.computation();
    }

    public static Scheduler io() {
        return Schedulers.io();
    }

    public static Scheduler ui() {
        return AndroidSchedulers.mainThread();
    }
}
