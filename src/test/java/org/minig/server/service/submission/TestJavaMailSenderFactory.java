package org.minig.server.service.submission;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Kamill Sokol
 */
public class TestJavaMailSenderFactory extends JavaMailSenderFactory {

    private TestJavaMailSender testJavaMailSender;

    @Override
    public JavaMailSender newInstance(Session session) {
        testJavaMailSender = new TestJavaMailSender();
        testJavaMailSender.setSession(session);
        return testJavaMailSender;
    }

    public Map<String, String> getProperties() {
        return testJavaMailSender.properties;
    }

    private class TestJavaMailSender extends JavaMailSenderImpl {

        private Map<String, String> properties;

        @Override
        protected Transport getTransport(Session session) throws NoSuchProviderException {
            Set<Map.Entry<Object,Object>> entries = session.getProperties().entrySet();
            HashMap<String, String> tmp = new HashMap<>();

            for(Map.Entry<Object, Object> entry : entries) {
                tmp.put(entry.getKey().toString(), entry.getValue().toString());
            }

            properties = tmp;
            return super.getTransport(session);
        }
    }
}
