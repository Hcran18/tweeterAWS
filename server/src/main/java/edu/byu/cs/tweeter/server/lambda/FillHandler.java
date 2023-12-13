package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.server.service.Filler;

public class FillHandler implements RequestHandler<Void, Void> {
    @Override
    public Void handleRequest(Void input, Context context) {
        Filler filler = new Filler();

        filler.fillDatabase();

        return null;
    }
}
