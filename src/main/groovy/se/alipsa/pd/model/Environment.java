package se.alipsa.pd.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

public class Environment {

  private String name;
  private List<Target> targets = new ArrayList<>();

  public Environment() {
  }

  public Environment(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @XmlAttribute
  public void setName(String name) {
    this.name = name;
  }

  public List<Target> getTargets() {
    return targets;
  }

  public Target targetForName(String name) {
    return targets.stream().filter(t -> t.getName().equals(name)).findAny().orElse(null);
  }

  @XmlElement(name = "target")
  public void setTargets(List<Target> targets) {
    this.targets = targets;
  }

  public void addTarget(Target prodTarget) {
    targets.add(prodTarget);
  }

  public Target createAndAddTarget(String name) {
    Target target = new Target(name);
    addTarget(target);
    return target;
  }
}
