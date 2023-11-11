package edu.byu.cs.tweeter.client.presenter;

public abstract class Presenter {

    public interface MainView {
        void displayMessage(String message);
    }
}
