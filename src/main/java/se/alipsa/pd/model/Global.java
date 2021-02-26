package se.alipsa.pd.model;


import javax.xml.bind.annotation.XmlAttribute;

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

  @XmlAttribute(name="source")
  public void setSshPasswordSource(PasswordSource sshPasswordSource) {
    this.sshPasswordSource = sshPasswordSource;
  }
}
