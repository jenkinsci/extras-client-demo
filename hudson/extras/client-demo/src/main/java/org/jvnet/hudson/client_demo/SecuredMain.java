package org.jvnet.hudson.client_demo;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;

/**
 * Java client program to demonstrate how to access Hudson remote API,
 * when authentication is involved.
 *
 * @author Kohsuke Kawaguchi
 * @see http://hudson.gotdns.com/wiki/display/HUDSON/Remote+access+API
 */
public class SecuredMain {
    /**
     * On most security configurations, except "delegate to servlet container"
     * authentication, simply sending in the BASIC authentication pre-emptively works.
     * See http://hc.apache.org/httpclient-3.x/authentication.html
     * for how to configure pre-emptive authentication.
     *
     * <p>
     * However, in the "delegate to servlet container" mode, BASIC auth
     * support depends on the container implementation, and hence inherently
     * unreliable. The following code uses Jakarta Commons HTTP client
     * to work around this problem by essentially emulating what the user
     * does through the browser.
     *
     * <p>
     * The code first emulates a click of the "login" link, then submit
     * the login form. Once that's done, you are authenticated, so you
     * can access the information you wanted. This is all possible
     * because {@link HttpClient} maintains a cookie jar in it.
     */
    public static void main(String[] args) throws IOException {
        HttpClient client = new HttpClient();

        String hostName = "http://localhost:8080/";
        GetMethod loginLink = new GetMethod(hostName+"loginEntry");
        client.executeMethod(loginLink);
        checkResult(loginLink.getStatusCode());

        String location = hostName+"j_security_check";
        while(true) {
            PostMethod loginMethod = new PostMethod(location);
            loginMethod.addParameter("j_username", "username"); // TODO: replace with real user name and password
            loginMethod.addParameter("j_password", "password");
            loginMethod.addParameter("action", "login");
            client.executeMethod(loginMethod);
            if(loginMethod.getStatusCode()/100==3) {
                // Commons HTTP client refuses to handle redirects for POST
                // so we have to do it manually.
                location = loginMethod.getResponseHeader("Location").getValue();
                continue;
            }
            checkResult(loginMethod.getStatusCode());
            break;
        }

        HttpMethod method = new GetMethod(hostName+"log");
        client.executeMethod(method);
        checkResult(method.getStatusCode());

        System.out.println(method.getResponseBodyAsString());
    }

    private static void checkResult(int i) throws IOException {
        if(i/100!=2)
            throw new IOException();
    }
}
