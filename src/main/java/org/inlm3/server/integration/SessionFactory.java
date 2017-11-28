package org.inlm3.server.integration;

import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.inlm3.server.model.File;
import org.inlm3.server.model.User;

public class SessionFactory {

    private static SessionFactory thisSessionFactory;
    private static org.hibernate.SessionFactory sessionFactory;

    public SessionFactory() {
        sessionFactory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addPackage("org.inlm3.server.model")
                .addAnnotatedClass(User.class)
                .addAnnotatedClass(File.class)
                .buildSessionFactory();
    }

    public static SessionFactory getInstance() {
        if(thisSessionFactory == null) {
            thisSessionFactory = new SessionFactory();
        }
        return thisSessionFactory;
    }

    public Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public void destroy() {
        sessionFactory.close();
        thisSessionFactory = null;
    }

}