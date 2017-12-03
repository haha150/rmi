package org.inlm3.server.integration;

import org.hibernate.Session;
import org.inlm3.server.exception.UserAlreadyExistsException;
import org.inlm3.server.exception.UserDoesNotExistException;
import org.inlm3.server.exception.WrongCredentialsException;
import org.inlm3.server.model.User;

import java.util.List;

public class UserDAO {

    private final SessionFactory sessionFactory;

    public UserDAO() {
        sessionFactory = SessionFactory.getInstance();
    }

    public void registerUser(String username, String password) throws UserAlreadyExistsException {

        User user = new User(username, password);

        if(doesUserExist(username)) {
            throw new UserAlreadyExistsException();
        }

        Session session = sessionFactory.getSession();
        session.beginTransaction();
        session.save(user);
        session.getTransaction().commit();
        session.close();
    }

    public void unegisterUser(String username, String password) throws UserDoesNotExistException, WrongCredentialsException {
        User user = getUserByName(username);

        if(user == null) {
            throw new UserDoesNotExistException();
        }

        if(user.getPassword().equals(password)) {
            Session session = sessionFactory.getSession();
            session.beginTransaction();
            session.delete(user);
            session.getTransaction().commit();
            session.close();
        } else {
            throw new WrongCredentialsException();
        }
    }

    private boolean doesUserExist(String username) {
        Session session = sessionFactory.getSession();

        String query = "from User where username='" + username + "'";

        session.beginTransaction();
        List<User> users = session.createQuery(query).list();
        session.getTransaction().commit();
        session.close();

        if(users.size() > 0) {
            return true;
        }

        return false;
    }

    public User login(String username, String password) throws UserDoesNotExistException, WrongCredentialsException {
        Session session = sessionFactory.getSession();

        String query = "from User where username='" + username + "'";

        session.beginTransaction();
        List<User> users = session.createQuery(query).list();
        session.getTransaction().commit();

        session.close();

        if(users.size() == 0) {
            throw new UserDoesNotExistException();
        }

        User user = users.get(0);

        if (username.equalsIgnoreCase(user.getUsername()) &&
                password.compareTo(user.getPassword()) == 0) {
            return user;
        }

        throw new WrongCredentialsException();
    }

    public User getUserByName(String username) {
        Session session = sessionFactory.getSession();
        String query = "from User where username='" + username + "'";
        session.beginTransaction();
        List<User> users = session.createQuery(query).list();
        session.getTransaction().commit();
        session.close();
        if(users.size() > 0) {
            User user = users.get(0);
            if(user != null && user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public List<User> getAllUsers() {
        Session session = sessionFactory.getSession();

        String query = "from User";

        session.beginTransaction();
        List<User> users = session.createQuery(query).list();
        session.getTransaction().commit();

        session.close();

        return users;

    }

    public void destroy() {
        sessionFactory.destroy();
    }
}