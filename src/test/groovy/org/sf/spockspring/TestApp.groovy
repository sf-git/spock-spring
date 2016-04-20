package org.sf.spockspring

import org.springframework.aop.framework.ProxyFactoryBean
import org.springframework.aop.target.HotSwappableTargetSource
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean


class TestApp extends App {

    //override hello service bean
    @Bean(name = HelloService.HELLO_SERVICE_BEAN_NAME)
    public ProxyFactoryBean helloService(@Qualifier("swappableHelloService") HotSwappableTargetSource targetSource) {
        def proxyFactoryBean = new ProxyFactoryBean()
        proxyFactoryBean.setTargetSource(targetSource)
        proxyFactoryBean
    }

    @Bean
    public HotSwappableTargetSource swappableHelloService() {
        new HotSwappableTargetSource(new HelloService());
    }
}
