package se.alipsa.pd

import org.apache.sshd.client.SshClient
import org.apache.sshd.client.channel.ClientChannel
import org.apache.sshd.client.session.ClientSession
import org.apache.sshd.common.channel.Channel
import org.apache.sshd.client.channel.ClientChannelEvent

import java.util.concurrent.TimeUnit

class Ssh {

    long defaultTimeoutSeconds = 30;
    String host;
    int port;
    String username;
    String password;
    SshClient client;

    Ssh(String host, int port, String username, String password) {
        this.host = host
        this.port = port
        this.username = username
        this.password = password
        client = SshClient.setUpDefaultClient();
    }

    String eval(String command) {
        client.start();
        try (ClientSession session = client.connect(username, host, port)
                .verify(defaultTimeoutSeconds, TimeUnit.SECONDS).getSession()) {
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

}
