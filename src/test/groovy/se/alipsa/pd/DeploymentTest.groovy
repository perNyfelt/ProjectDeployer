/*
 * This Spock specification was generated by the Gradle 'init' task.
 */
package se.alipsa.pd

import org.apache.sshd.scp.server.ScpCommandFactory
import org.apache.sshd.server.SshServer
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider
import org.apache.sshd.server.shell.InteractiveProcessShellFactory
import spock.lang.*

class DeploymentTest extends Specification {

    SshServer sshd;

    def setup() {
        sshd = SshServer.setUpDefaultServer();
        sshd.setPort(22022);
        sshd.setShellFactory(new InteractiveProcessShellFactory())
        sshd.setCommandFactory(new ScpCommandFactory())
        sshd.setPasswordAuthenticator(new MyPasswordAuthenticator())
        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider())
        sshd.start()
    }

    def cleanup() {
        sshd.stop()
    }

    def "copy file to server"() {

        println("testing!!!!!!!")
        setup: "temp file with content created"
        def pd = Deployment.create(new File(getClass().getResource("/vars.xml").toURI()))
        def tempFile = File.createTempFile("test", "txt")
        new FileWriter(tempFile).withCloseable { writer ->
            writer.write("Hello world")
        }
        when: "A file is copied to the server"
        pd.copyFile(new URL("file://" + tempFile.getAbsolutePath()), "/deploy/test.txt")

        then: "TODO: check that the file exists on the ssh server"
        true == true
    }
}
