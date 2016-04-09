package library.model;

import java.io.Serializable;

public class User implements  Serializable{
  private int id;
  private String userName, password, fullName;

  public User(int id, String password, String userName, String fullName) {
    this.id = id;
    this.userName = userName;
    this.password = password;
    this.fullName = fullName;
  }

  public String getPassword() {
    return password;
  }

  public int getId() {
    return id;
  }

  public String getUserName() {
    return userName;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }
}
