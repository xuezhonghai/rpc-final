package demo.ocean.rpc.client.proxy;

import demo.ocean.rpc.client.config.RpcClientConfiguration;
import demo.ocean.rpc.common.annotation.RpcClient;
import demo.ocean.rpc.common.utils.SpringBeanFactory;
import lombok.AllArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.reflections.Reflections;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@AllArgsConstructor
public class ServiceProxyManager {

    private final RpcClientConfiguration configuration;

    private final ProxyHelper proxyHelper;

    public void initServiceProxyInstance(){

        Reflections reflections = new Reflections(configuration.getRpcClientApiPackage());

        Set<Class<?>> classSet = reflections.getTypesAnnotatedWith(RpcClient.class);

        if (CollectionUtils.isNotEmpty(classSet)) {

            DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory)SpringBeanFactory.context().getAutowireCapableBeanFactory();

            for (Class<?> clazz : classSet) {
                Object instance = proxyHelper.newProxyInstance(clazz);
                beanFactory.registerSingleton(clazz.getName(), instance);
            }

        }


    }


}
