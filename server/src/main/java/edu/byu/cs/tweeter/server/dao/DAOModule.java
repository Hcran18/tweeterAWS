package edu.byu.cs.tweeter.server.dao;

import com.google.inject.AbstractModule;

public class DAOModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(FollowDAOInterface.class).to(FollowDAO.class);
    }
}
