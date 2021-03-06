package se.alipsa.pd.model;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum PasswordSource {

  sshpass, prompt, cert, keyring;

  @Override
  public String toString() {
    return name();
  }
}
