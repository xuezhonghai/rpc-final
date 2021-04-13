package demo.ocean.rpc.client.zk;

import demo.ocean.rpc.client.cache.ServiceRouteCache;
import demo.ocean.rpc.client.channel.ProviderService;
import demo.ocean.rpc.client.config.RpcClientConfiguration;
import demo.ocean.rpc.common.annotation.RpcClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class ServicePullManager {

    @Autowired
    private ZKit zKit;

    @Autowired
    private RpcClientConfiguration configuration;

    @Autowired
    private ServiceRouteCache cache;

    public void pullServiceFromZk(){
        Reflections reflections = new Reflections(configuration.getRpcClientApiPackage());
        Set<Class<?>> clazzSet = reflections.getTypesAnnotatedWith(RpcClient.class);

        //拉取不同服务的服务配置节点
        if (CollectionUtils.isNotEmpty(clazzSet)) {
            for (Class<?> cls : clazzSet) {
                String serviceName = cls.getName();
                //获取zk里注册的服务，加入到缓存
                List<ProviderService> providerServices = zKit.getServiceInfos(serviceName);
                cache.addCache(serviceName, providerServices);
                //监听注册
                zKit.subscribeZKEvent(serviceName);
            }
        }


    }
}
