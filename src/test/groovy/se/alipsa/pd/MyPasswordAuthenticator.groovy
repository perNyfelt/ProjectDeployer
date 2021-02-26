package se.alipsa.pd

import org.apache.sshd.server.auth.AsyncAuthException
import org.apache.sshd.server.auth.password.PasswordAuthenticator
import org.apache.sshd.server.auth.password.PasswordChangeRequiredException
import org.apache.sshd.server.session.ServerSession

class MyPasswordAuthenticator implements PasswordAuthenticator {
    @Override
    boolean authenticate(String username, String password, ServerSession session) throws PasswordChangeRequiredException, AsyncAuthException {
        return true
    }
}
