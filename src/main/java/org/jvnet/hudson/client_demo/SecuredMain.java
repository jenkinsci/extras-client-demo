package org.jvnet.hudson.client_demo;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;

/**
 * @author Kohsuke Kawaguchi
 */
public class SecuredMain {
    // this works with Jetty when the server is configured for hudson's own database.
    // to use container-delegated security, need to allow circular redirects.

    // but not with GF!
//    public static void main(String[] args) throws IOException {
//        HttpClient client = new HttpClient();
//
//        client.getState().setCredentials(
//                new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM),
//                new UsernamePasswordCredentials("guest", "guest"));
//        client.getParams().setAuthenticationPreemptive(true);
//        client.getParams().setParameter(HttpClientParams.ALLOW_CIRCULAR_REDIRECTS,true);
//
//        String location = "http://localhost:8080/log";
//        while(true) {
//            HttpMethod method = new GetMethod(location);
//            int sc = client.executeMethod(method);
//            if(sc/100==3) {
//                location = method.getResponseHeader("Location").getValue();
//                continue;
//            }
//
//            System.out.println(method);
//            System.out.println(method.getResponseBodyAsString());
//            break;
//        }
//
//    }

    public static void main(String[] args) throws IOException {
        HttpClient client = new HttpClient();

        String hostName = "http://localhost:8080/";
        GetMethod loginLink = new GetMethod(hostName+"loginEntry");
        client.executeMethod(loginLink);
        checkResult(loginLink.getStatusCode());

        String location = hostName+"j_security_check";
        while(true) {
            PostMethod loginMethod = new PostMethod(location);
            loginMethod.addParameter("j_username", "username");
            loginMethod.addParameter("j_password", "password");
            loginMethod.addParameter("action", "login");
            client.executeMethod(loginMethod);
            if(loginMethod.getStatusCode()/100==3) {
                location = loginMethod.getResponseHeader("Location").getValue();
                continue;
            }
            checkResult(loginMethod.getStatusCode());
            break;
        }

        HttpMethod method = new GetMethod(hostName+"log");
        client.executeMethod(method);
        checkResult(method.getStatusCode());

        System.out.println(method);
        System.out.println(method.getResponseBodyAsString());

    }

    private static void checkResult(int i) throws IOException {
        if(i/100!=2)
            throw new IOException();
    }
}
