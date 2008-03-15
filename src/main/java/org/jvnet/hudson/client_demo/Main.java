package org.jvnet.hudson.client_demo;

import org.dom4j.io.SAXReader;
import org.dom4j.Document;
import org.dom4j.Element;

import java.net.URL;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.List;

/**
 * Java client program to demonstrate how to access Hudson remote API.
 *
 * @author Kohsuke Kawaguchi
 * @see http://hudson.gotdns.com/wiki/display/HUDSON/Remote+access+API
 */
public class Main {
    public static void main(String[] args) throws Exception {
        // every Hudson model object exposes the .../api/xml, but in this example
        // we'll just take the root object as an example
        URL url = new URL("http://deadlock.netbeans.org/hudson/api/xml");

        // if you are calling security-enabled Hudson and
        // need to invoke operations and APIs that are protected,
        // consult the 'SecuredMain" class
        // in this package for an example using HttpClient.

        // read it into DOM.
        Document dom = new SAXReader().read(url);

        // scan through the job list and print its status
        for( Element job : (List<Element>)dom.getRootElement().elements("job")) {
            System.out.println(String.format("Name:%s\tStatus:%s",
                job.elementText("name"), job.elementText("color")));
        }
    }
}
