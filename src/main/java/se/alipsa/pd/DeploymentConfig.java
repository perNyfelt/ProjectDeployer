package se.alipsa.pd;

import se.alipsa.pd.model.Environment;
import se.alipsa.pd.model.Global;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlRootElement
@XmlType(propOrder = {"global", "environments"})
public class DeploymentConfig {

  private Global global;
  private List<Environment> environments;

  public Global getGlobal() {
    return global;
  }

  @XmlElement
  public void setGlobal(Global global) {
    this.global = global;
  }

  public List<Environment> getEnvironments() {
    return environments;
  }

  public Environment environmentForName(String name) {
    return environments.stream().filter(e -> e.getName().equals(name)).findAny().orElse(null);
  }

  @XmlElement(name = "environment")
  public void setEnvironments(List<Environment> environments) {
    this.environments = environments;
  }
}
