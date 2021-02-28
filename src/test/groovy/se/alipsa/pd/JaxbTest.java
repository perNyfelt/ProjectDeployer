package se.alipsa.pd;

import org.junit.jupiter.api.Test;
import se.alipsa.pd.model.Environment;
import se.alipsa.pd.model.Global;
import se.alipsa.pd.model.PasswordSource;
import se.alipsa.pd.model.Target;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JaxbTest {

  @Test
  public void testUnmarshalling() throws JAXBException {

    JAXBContext context = JAXBContext.newInstance(DeploymentConfig.class);
    DeploymentConfig config = (DeploymentConfig) context.createUnmarshaller()
        .unmarshal(getClass().getResource("/vars.xml"));

    assertEquals("pernyf", config.getGlobal().getSshUser(), "deploymentConfig -> global -> sshUser");
    assertEquals(PasswordSource.prompt, config.getGlobal().getSshPasswordSource() , "deploymentConfig -> global -> sshPasswordSource");

    Environment testEnv = config.environmentForName("test");
    assertNotNull(testEnv, "deploymentConfig.getEnvironment() does not work");
    Target frontEndTarget = testEnv.targetForName("frontend");
    assertNotNull(frontEndTarget, "environment.targetForName() does not work");
    assertEquals(1, frontEndTarget.getHosts().size(), "deploymentConfig -> target[name=frontend] -> hosts");
    assertEquals("fehost1.test.alipsa.se:22022", frontEndTarget.getHosts().get(0));
  }

  @Test
  public void testMarshalling() throws IOException, JAXBException {
    DeploymentConfig config = new DeploymentConfig();
    Global global = new Global();
    global.setSshUser("per");
    global.setSshPassword("secret");
    config.setGlobal(global);

    Environment test = new Environment();
    test.setName("test");
    Target target = new Target();
    target.setName("backend");
    target.addHost("behost1.test.alipsa.se:22022");
    test.setTargets(Arrays.asList(target));

    Environment prodEnv = new Environment();
    prodEnv.setName("prod");
    Target prodTarget = new Target();
    prodTarget.setName("backend");
    prodTarget.addHost("behost1.prod.alipsa.se:22022");
    prodEnv.addTarget(prodTarget);

    config.setEnvironments(Arrays.asList(test, prodEnv));

    JAXBContext context = JAXBContext.newInstance(DeploymentConfig.class);
    Marshaller mar= context.createMarshaller();
    mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    StringWriter sw = new StringWriter();
    mar.marshal(config, sw);
    assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
        "<deploymentConfig>\n" +
        "    <global>\n" +
        "        <sshPassword>secret</sshPassword>\n" +
        "        <sshUser>per</sshUser>\n" +
        "    </global>\n" +
        "    <environment name=\"test\">\n" +
        "        <target name=\"backend\">\n" +
        "            <host>behost1.test.alipsa.se:22022</host>\n" +
        "        </target>\n" +
        "    </environment>\n" +
        "    <environment name=\"prod\">\n" +
        "        <target name=\"backend\">\n" +
        "            <host>behost1.prod.alipsa.se:22022</host>\n" +
        "        </target>\n" +
        "    </environment>\n" +
        "</deploymentConfig>\n", sw.toString());
  }

}
