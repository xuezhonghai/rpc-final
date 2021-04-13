package demo.ocean.rpc.client.cache;

import com.google.common.cache.LoadingCache;
import demo.ocean.rpc.client.channel.ProviderService;
import demo.ocean.rpc.client.zk.ZKit;
import demo.ocean.rpc.common.annotation.RpcClient;
import demo.ocean.rpc.common.utils.RpcException;
import demo.ocean.rpc.common.utils.SpringBeanFactory;
import lombok.AllArgsConstructor;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 存服务地址
 */
@Component
@AllArgsConstructor
public class ServiceRouteCache {

    @Autowired
    private LoadingCache<String, List<ProviderService>> cache;

    @Autowired
    private ZKit zKit;


    public void addCache(String serviceName, List<ProviderService> serviceRoutes) {
        cache.put(serviceName, serviceRoutes);
    }

    public void updateCache(String serviceName, List<ProviderService> serviceRoutes) {
        cache.put(serviceName, serviceRoutes);
    }

    public void updateCache(Map<String, List<ProviderService>> newServiceRoutesMap) {
        cache.invalidateAll();
        for (Map.Entry<String, List<ProviderService>> entry : newServiceRoutesMap.entrySet()) {
            cache.put(entry.getKey(), entry.getValue());
        }
    }

    public List<ProviderService> getServiceRoutes(String serviceName) {
        if (cache.size() == 0) {
            reloadCache();

            if (cache.size() == 0) {
                throw new RpcException("Not any service which is available.");
            }
        }
        try {
            return cache.get(serviceName);
        } catch (ExecutionException e) {
            throw new RpcException(e);
        }
    }

    /**
     * 重新加载缓存
     */
    private void reloadCache() {
        Map<String, Object> beans = SpringBeanFactory.getBeanListByAnnotationClass(RpcClient.class);
        if (MapUtils.isEmpty(beans)) {
            return;
        }
        for (Object bean : beans.values()) {
            String serviceName = bean.getClass().getName();
            List<ProviderService> serviceRoutes = zKit.getServiceInfos(serviceName);
            addCache(serviceName, serviceRoutes);
        }
    }

}
