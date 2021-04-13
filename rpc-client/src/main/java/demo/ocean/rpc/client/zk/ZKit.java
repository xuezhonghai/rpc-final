package demo.ocean.rpc.client.zk;

import com.google.common.collect.Lists;
import demo.ocean.rpc.client.cache.ServiceRouteCache;
import demo.ocean.rpc.client.channel.ProviderService;
import demo.ocean.rpc.client.config.RpcClientConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ZKit {

    @Autowired
    private ZkClient zkClient;

    @Autowired
    private RpcClientConfiguration configuration;

    @Autowired
    private ServiceRouteCache serviceRouteCache;


    public void subscribeZKEvent(String serviceName) {
        // 1. 组装服务节点信息
        String path = configuration.getZkRoot() + "/" + serviceName;
        // 2. 订阅服务节点（监听节点变化）
        zkClient.subscribeChildChanges(path, new IZkChildListener() {
            @Override
            public void handleChildChange(String parentPath, List<String> list) throws Exception {
                // 3. 判断获取的节点信息，是否为空
                if (CollectionUtils.isNotEmpty(list)) {
                    // 4. 将服务端获取的信息， 转换为服务记录对象
                    List<ProviderService> providerServices = convertToProviderService(list);
                    // 5. 更新缓存信息
                    serviceRouteCache.updateCache(serviceName, providerServices);
                }
            }
        });
    }


    public List<ProviderService> getServiceInfos(String serviceName){
        String path = configuration.getZkRoot() + "/" + serviceName;
        List<String> children = zkClient.getChildren(path);

        List<ProviderService> providerServices = convertToProviderService(children);
        return providerServices;
    }


    /**
     * 将拉取的服务节点信息转换为服务记录对象
     * @param list
     * @return
     */
    private List<ProviderService> convertToProviderService(List<String> list) {
        if (CollectionUtils.isEmpty(list)) {
            return Lists.newArrayListWithCapacity(0);
        }
        // 将服务节点信息转换为服务记录对象
        List<ProviderService> providerServices = list.stream().map(v -> {
            String[] serviceInfos = v.split(":");
            return ProviderService.builder()
                    .serverIp(serviceInfos[0])
                    .serverPort(Integer.parseInt(serviceInfos[1]))
                    .networkPort(Integer.parseInt(serviceInfos[2]))
                    .build();
        }).collect(Collectors.toList());
        return providerServices;
    }





}
