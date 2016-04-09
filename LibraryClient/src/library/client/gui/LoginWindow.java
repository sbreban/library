package library.client.gui;

import library.services.LibraryException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class LoginWindow extends JFrame{
  private JTextField userField;
  private JPasswordField passwordField;
  private JButton loginButton, clearButton;
  private LibraryClientController libraryClientController;

  public LoginWindow(String title, LibraryClientController libraryClientController) throws HeadlessException {
    super(title);
    this.libraryClientController = libraryClientController;
    getContentPane().add(createLogin());
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
  }

  private JPanel createLogin(){
    JPanel res=new JPanel(new GridLayout(3,1));
    JPanel line1=new JPanel();
    line1.add(new JLabel("User name:"));
    line1.add(userField =new JTextField(15));
    res.add(line1);

    JPanel line2=new JPanel();
    line2.add(new JLabel("Password:"));
    line2.add(passwordField =new JPasswordField(15));
    res.add(line2);

    JPanel line3=new JPanel();
    line3.add(loginButton =new JButton("Login"));
    line3.add(clearButton =new JButton("Clear"));
    ActionListener actionListener=new ButtonListener();
    loginButton.addActionListener(actionListener);
    clearButton.addActionListener(actionListener);
    res.add(line3);
    return res;
  }

  private class ButtonListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      if (e.getSource()== loginButton){
        System.out.println("Login button pressed.");
        String user= userField.getText();
        String pass=new String(passwordField.getPassword());
        try{
          libraryClientController.login(user,pass);
          LibraryWindow win = new LibraryWindow("Library window for "+user, libraryClientController);
          win.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
          win.setSize(500,400);
          win.setLocation(600,300);
          win.setVisible(true);
          LoginWindow.this.dispose();
        } catch (LibraryException e1) {
          JOptionPane.showMessageDialog(LoginWindow.this, "Login error "+e1,"Error", JOptionPane.ERROR_MESSAGE);
        }
      } else if (e.getSource()== clearButton){
        System.out.println("Clear button pressed.");
        userField.setText("");
        passwordField.setText("");
      }
    }
  }
}
