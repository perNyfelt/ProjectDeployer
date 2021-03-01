package se.alipsa.pd

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.sshd.scp.server.ScpCommandFactory
import org.apache.sshd.server.SshServer
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider
import org.apache.sshd.server.shell.InteractiveProcessShellFactory
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

import static org.junit.jupiter.api.Assertions.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DeploymentTest {

    SshServer sshd
    def user = System.getProperty("user.name")
    static final Logger LOG = LogManager.getLogger(DeploymentTest.class)

    @BeforeAll
    void setup() {
        LOG.debug("Starting ssh server")
        sshd = SshServer.setUpDefaultServer()
        sshd.setPort(22022)
        sshd.setShellFactory(new InteractiveProcessShellFactory())
        sshd.setCommandFactory(new ScpCommandFactory())
        sshd.setPasswordAuthenticator(new MyPasswordAuthenticator())
        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider())

        sshd.start()
    }

    @AfterAll
    void tearDown() {
        LOG.debug("Stopping ssh server")
        sshd.stop()
    }

    @Test
    void testFileCopy() {
        def d = new Deployment(user, "s3crEtP255", "localhost:${sshd.getPort()}")
        def tempFile = File.createTempFile("test", "txt")
        new FileWriter(tempFile).withCloseable { writer ->
            writer.write("Hello world")
        }
        File buildDir = new File("./build")
        File targetDir = new File(buildDir, "deployTest")
        LOG.info("creating ${targetDir}: {}", d.mkdir("${targetDir.getAbsolutePath()}"))
        assertTrue(targetDir.exists(), "Failed to create ${targetDir.getAbsolutePath()}")
        LOG.info("Copying file to server")
        d.copy(tempFile.getAbsolutePath(), "${targetDir.getAbsolutePath()}/test.txt")
        def actualFile = new File(targetDir, "test.txt")
        assertTrue(actualFile.exists(), "${actualFile} does not exist, copy failed!")
    }
}
