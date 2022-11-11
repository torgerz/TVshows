package com.example.demo;

import java.util.Random;
import java.util.logging.*;

public class ApiBackOffStrategy {
    private static Logger LOGGER = Logger.getLogger("InfoLogging");
    public static int defaultRetries=2;
    public static long defaultWaitTimeMillSec = 10000;
    private int numberOfRetries;
    private int numberOfTriesLeft;
    private long defaultTimeToWait;
    private long timeToWait;
    private final Random random = new Random();

    public ApiBackOffStrategy() {
        this(defaultRetries, defaultWaitTimeMillSec);
    }
    public ApiBackOffStrategy(int numberOfRetries, long defaultTimeToWait){
        this.numberOfRetries = numberOfRetries;
        this.numberOfTriesLeft = numberOfRetries;
        this.defaultTimeToWait = defaultTimeToWait;
        this.timeToWait = defaultTimeToWait;
    }
    public boolean shouldRetry(){

        return numberOfTriesLeft > 0;
    }
    public void errorOccurred() {

        if (!shouldRetry()) {
            LOGGER.info("*** RETRY FAILED ***");
        }
        waitUntilNextTry();
        timeToWait += random.nextInt(1000);
    }
    private void waitUntilNextTry() {

        try {
            Thread.sleep(timeToWait);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public long getTimeToWait() {
        return this.timeToWait;
    }
    public void doNotRetry() {
        numberOfTriesLeft = 0;
    }
    public void reset() {
        this.numberOfTriesLeft = numberOfRetries;
        this.timeToWait = defaultTimeToWait;
    }
}
