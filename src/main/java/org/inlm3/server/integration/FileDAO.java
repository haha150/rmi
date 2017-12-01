package org.inlm3.server.integration;

import org.hibernate.Session;
import org.inlm3.server.exception.FileAlreadyExistsException;
import org.inlm3.server.exception.FileDoesNotExistException;
import org.inlm3.server.model.File;
import org.inlm3.server.model.User;

import java.util.List;

public class FileDAO {

    private final SessionFactory sessionFactory;

    public FileDAO() {
        sessionFactory = SessionFactory.getInstance();
    }

    public List<File> getAllPublicFiles() {
        Session session = sessionFactory.getSession();

        String query = "from File";

        session.beginTransaction();
        List<File> files = session.createQuery(query).list();
        session.getTransaction().commit();

        session.close();

        for (File f : files) {
            if (!f.getFilePermission().equalsIgnoreCase("public")) {
                files.remove(f);
            }
        }

        return files;

    }

    public List<File> getAllPrivateFiles(String username) {
        Session session = sessionFactory.getSession();

        String query = "from File";

        session.beginTransaction();
        List<File> files = session.createQuery(query).list();
        session.getTransaction().commit();

        session.close();

        for (File f : files) {
            if (!f.getUsername().equals(username)) {
                files.remove(f);
            }
        }

        return files;
    }

    public void addFile(User user, String name, int size, String permission) throws FileAlreadyExistsException {

        File file = new File(name,size,user,permission);

        // check if user exists!
        if(doesFileExist(name)) {
            throw new FileAlreadyExistsException();
        }

        Session session = sessionFactory.getSession();
        session.beginTransaction();
        session.save(file);
        session.getTransaction().commit();
        session.close();
    }

    private boolean doesFileExist(String name) {
        Session session = sessionFactory.getSession();

        String query = "from File where filename='" + name + "'";

        session.beginTransaction();
        List<File> files = session.createQuery(query).list();
        session.getTransaction().commit();
        session.close();

        if(files.size() > 0) {
            return true;
        }

        return false;
    }

    public void deleteFile(String name) throws FileDoesNotExistException {
        File file = getFileByName(name);

        if(file == null) {
            throw new FileDoesNotExistException();
        }

        Session session = sessionFactory.getSession();
        session.beginTransaction();
        session.delete(file);
        session.getTransaction().commit();
        session.close();
    }

    private File getFileByName(String name) {
        Session session = sessionFactory.getSession();
        String query = "from File where filename='" + name + "'";
        session.beginTransaction();
        List<File> files = session.createQuery(query).list();
        session.getTransaction().commit();
        session.close();
        if(files.size() > 0) {
            File file = files.get(0);
            if(file != null && file.getUsername().equals(name)) {
                return file;
            }
        }
        return null;
    }
}
