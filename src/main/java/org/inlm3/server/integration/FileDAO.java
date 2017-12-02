package org.inlm3.server.integration;

import org.hibernate.Session;
import org.inlm3.server.exception.FileAlreadyExistsException;
import org.inlm3.server.exception.FileDoesNotExistException;
import org.inlm3.server.exception.PermissionDeniedException;
import org.inlm3.server.model.File;
import org.inlm3.server.model.User;

import java.util.ArrayList;
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

        List<File> remove = new ArrayList<>();

        for (File f : files) {
            if (!f.getFilePermission().equalsIgnoreCase("public")) {
                remove.add(f);
            }
        }

        files.removeAll(remove);

        return files;
    }

    public List<File> getAllPrivateFiles(String username) {
        Session session = sessionFactory.getSession();

        String query = "from File";

        session.beginTransaction();
        List<File> files = session.createQuery(query).list();
        session.getTransaction().commit();

        session.close();

        List<File> remove = new ArrayList<>();

        for (File f : files) {
            if (!f.getUsername().equals(username)) {
                remove.add(f);
            } else if (f.getFilePermission().equalsIgnoreCase("public")) {
                remove.add(f);
            }
        }

        files.removeAll(remove);

        return files;
    }

    public void addFile(User user, String name, int size, String permission) throws FileAlreadyExistsException {

        File file = new File(name,size,user,permission);

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

    public void deleteFile(User user, String name) throws FileDoesNotExistException, PermissionDeniedException {
        File file = getFileByName(name);

        if(file == null) {
            throw new FileDoesNotExistException();
        }

        if(!(file.getFileOwner() == user)) {
            if(!file.getFilePermission().equalsIgnoreCase("public")) {
                throw new PermissionDeniedException();
            }
        }

        Session session = sessionFactory.getSession();
        session.beginTransaction();
        session.delete(file);
        session.getTransaction().commit();
        session.close();
    }

    public void editFile(String oldName, String newName, String permission) {
        File file = getFileByName(oldName);
        file.setFileName(newName);
        file.setFilePermission(permission);
        Session session = sessionFactory.getSession();
        session.beginTransaction();
        session.update(file);
        session.getTransaction().commit();
        session.close();
    }

    public File getFileByName(String name) {
        Session session = sessionFactory.getSession();
        String query = "from File where filename='" + name + "'";
        session.beginTransaction();
        List<File> files = session.createQuery(query).list();
        session.getTransaction().commit();
        session.close();
        if(files.size() > 0) {
            return files.get(0);
        }
        return null;
    }
}
