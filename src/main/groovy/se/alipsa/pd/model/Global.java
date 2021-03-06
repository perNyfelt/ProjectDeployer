package se.alipsa.pd.model;

public class Global {

  private String sshUser;
  private PasswordSource sshPasswordSource;
  private String sshPassword;

  public String getSshUser() {
    return sshUser;
  }

  public void setSshUser(String sshUser) {
    this.sshUser = sshUser;
  }

  public String getSshPassword() {
    return sshPassword;
  }

  public void setSshPassword(String sshPassword) {
    this.sshPassword = sshPassword;
  }

  public PasswordSource getSshPasswordSource() {
    return sshPasswordSource;
  }

  public void setSshPasswordSource(PasswordSource sshPasswordSource) {
    this.sshPasswordSource = sshPasswordSource;
  }
}
