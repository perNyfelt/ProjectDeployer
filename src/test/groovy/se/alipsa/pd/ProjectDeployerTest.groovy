package se.alipsa.pd

import org.apache.sshd.scp.server.ScpCommandFactory
import org.apache.sshd.server.SshServer
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider
import org.apache.sshd.server.shell.InteractiveProcessShellFactory
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProjectDeployerTest {

    SshServer sshd;

    @BeforeAll
    void setup() {
        sshd = SshServer.setUpDefaultServer();
        sshd.setPort(22022);
        sshd.setShellFactory(new InteractiveProcessShellFactory())
        sshd.setCommandFactory(new ScpCommandFactory())
        sshd.setPasswordAuthenticator(new MyPasswordAuthenticator())
        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider())
        sshd.start()
        // TODO set up a test nexus
    }

    @AfterAll
    void cleanup() {
        sshd.stop()
    }

    @Disabled
    @Test
    void "basic deployment script"() {

        //@Grab('se.alipsa:ProjectDeployer:1.0.0')
        //import se.alipsa.pd.*

        String serverUser = "glow"
        String serverUserGroup = "users"
        String alipsaNexus ="http://localhost:8080/nexus"
        String backendServiceName = "glow-backend.service"
        String javaVersion="11.0.10"
        String baseTargetDir="/usr/local/glow"

        File glowBackendJar
        File glowFrontEndZip

        def pd = ProjectDeployer.create(new File(getClass().getResource("/vars.xml").toURI()).getAbsolutePath())
        pd.createSetupActions {
            glowBackendJar = pd.fetchJarFromRepository(alipsaNexus, "se.alipsa:glow-backend:1.2")
            glowFrontEndZip = pd.fetchJarFromRepository(alipsaNexus, "se.alipsa:glow-frontend:1.2")
        }

        // d is an instance of Deployment (there is one Deployment for each host)
        pd.createActions "backend", { d ->
            {
                d.createServerUser(serverUser, serverUserGroup)
                d.stopService(backendServiceName)
                String javaHome = d.installJava(javaVersion, baseTargetDir)
                d.copy( fromFile: glowBackendJar,
                        toFile: "${baseTargetDir}/backend/${glowBackendJar.getName()}",
                        owner: serverUser,
                        permissions: "g=rwx,u=rwx,o=r")
                d.createService(
                        name: backendServiceName,
                        exec: "${javaHome}/bin/java -jar ${glowBackendJar.getName()}",
                        runas: serverUser)
                d.startService(backendServiceName)
            }
        }

        pd.deploy "test"
    }
}
