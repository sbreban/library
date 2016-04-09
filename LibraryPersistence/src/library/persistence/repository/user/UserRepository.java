package library.persistence.repository.user;

import library.model.User;

public interface UserRepository {
  User verifyUser(String userName, String password);
}
