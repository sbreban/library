package library.persistence;

import library.persistence.repository.book.BookRepository;
import library.persistence.repository.user.UserRepository;


/**
 * Created by IntelliJ IDEA.
 * User: grigo
 * Date: Apr 1, 2009
 * Time: 10:04:18 PM
 */
public abstract class Persistence{
  private static Persistence instance=null;

  protected Persistence(){}
  public static Persistence getInstance(){
    if (instance==null){
      String repositoryName=System.getProperty("persistence-class-name");
      try {
        Class repoClass=Class.forName(repositoryName);
        if (repoClass.getSuperclass().equals(Persistence.class))
          instance=(Persistence)repoClass.newInstance();
      } catch (ClassNotFoundException e) {
        System.out.println("Persistence exception: "+e);
      } catch (IllegalAccessException e) {
        System.out.println("Persistence excep: "+e);
      } catch (InstantiationException e) {
        System.out.println("Persistence excep: "+e);
      }
    }
    return instance;
  }

  public abstract UserRepository createUserRepository();

  public abstract BookRepository createBookRepository();
}
