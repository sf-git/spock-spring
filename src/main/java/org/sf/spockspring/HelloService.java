package org.sf.spockspring;

import org.springframework.stereotype.Service;

@Service(value = HelloService.HELLO_SERVICE_BEAN_NAME)
public class HelloService {

    public static final String HELLO_SERVICE_BEAN_NAME = "helloService";

    public String hello(String postfix) {
        return "Original Bean Says: Hello, " + postfix;
    }
}
