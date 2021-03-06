package se.alipsa.pd.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

public class Target {

  private String name;
  private List<String> hosts = new ArrayList<>();

  public String getName() {
    return name;
  }

  @XmlAttribute
  public void setName(String name) {
    this.name = name;
  }

  public List<String> getHosts() {
    return hosts;
  }

  @XmlElement(name="host")
  public void setHosts(List<String> hosts) {
    this.hosts = hosts;
  }

  public void addHost(String host) {
    hosts.add(host);
  }
}
