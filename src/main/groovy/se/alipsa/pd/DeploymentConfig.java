package se.alipsa.pd;

import se.alipsa.pd.model.Environment;
import se.alipsa.pd.model.Global;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
@XmlType(propOrder = {"global", "environments"})
public class DeploymentConfig {

  private Global global = new Global();
  private List<Environment> environments = new ArrayList<>();

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

  public Environment createAndAddEnvironment(String name) {
    Environment env = new Environment(name);
    addEnvironment(env);
    return env;
  }

  public void addEnvironment(Environment env) {
    environments.add(env);
  }

  @XmlElement(name = "environment")
  public void setEnvironments(List<Environment> environments) {
    this.environments = environments;
  }
}
