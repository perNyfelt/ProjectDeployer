package se.alipsa.pd

import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.LogManager
import org.apache.sshd.client.SshClient
import org.apache.sshd.client.channel.ClientChannel
import org.apache.sshd.client.session.ClientSession
import org.apache.sshd.common.channel.Channel
import org.apache.sshd.client.channel.ClientChannelEvent
import org.apache.sshd.scp.client.ScpClient
import org.apache.sshd.scp.client.ScpClientCreator

import java.util.concurrent.TimeUnit

class Ssh {

    static final Logger LOG = LogManager.getLogger(Ssh.class)

    long defaultTimeoutSeconds = 30;
    String host;
    int port;
    String username;
    String password;
    SshClient client;

    Ssh(String hostAndPort, String username, String password) {
        def arr  = hostAndPort.split(":")
        this.host = arr[0]
        this.port = Integer.parseInt(arr[1])
        this.username = username
        this.password = password
    }

    Ssh(String host, int port, String username, String password) {
        this.host = host
        this.port = port
        this.username = username
        this.password = password
    }

    String eval(String command) {
        client = SshClient.setUpDefaultClient();
        client.start();
        try (ClientSession session = createSession()) {
            session.addPasswordIdentity(password);
            session.auth().verify(defaultTimeoutSeconds, TimeUnit.SECONDS);

            try (ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
                 ClientChannel channel = session.createChannel(Channel.CHANNEL_SHELL)) {
                channel.setOut(responseStream);
                try {
                    channel.open().verify(defaultTimeoutSeconds, TimeUnit.SECONDS);
                    try (OutputStream pipedIn = channel.getInvertedIn()) {
                        pipedIn.write(command.getBytes());
                        pipedIn.flush();
                    }

                    channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED),
                            TimeUnit.SECONDS.toMillis(defaultTimeoutSeconds));
                    return new String(responseStream.toByteArray());
                } finally {
                    channel.close(false);
                }
            }
        } finally {
            client.stop();
        }
    }

    private createSession() {
        LOG.debug("connecting to ${username}@${host}:${port}")
        return client.connect(username, host, port).verify(defaultTimeoutSeconds, TimeUnit.SECONDS).getSession()
    }

    def upload(String from, String to) throws IOException {
        client = SshClient.setUpDefaultClient();
        //println("Starting ssh client")
        client.start();
        ScpClientCreator creator = ScpClientCreator.instance();
        try (ClientSession session = createSession()) {
            session.addPasswordIdentity(password);
            session.auth().verify(defaultTimeoutSeconds, TimeUnit.SECONDS);
            //println("creating scp client")
            ScpClient scpClient = creator.createScpClient(session)
            scpClient.upload(from, to, ScpClient.Option.PreserveAttributes)
        } finally {
            client.stop();
        }
    }
}
