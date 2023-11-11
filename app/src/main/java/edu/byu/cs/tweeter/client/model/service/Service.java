package edu.byu.cs.tweeter.client.model.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class Service {

    public interface ServiceObserver {
        void displayError(String message);
        void displayException(Exception ex);
    }

    public void executeTask(Runnable task) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(task);
    }

}
