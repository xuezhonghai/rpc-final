package demo.ocean.rpc.client.startup;

import demo.ocean.rpc.client.cache.ServiceRouteCache;
import demo.ocean.rpc.client.config.RpcClientConfiguration;
import demo.ocean.rpc.client.proxy.ServiceProxyManager;
import demo.ocean.rpc.client.runner.RpcRequestManager;
import demo.ocean.rpc.client.runner.RpcRequestPool;
import demo.ocean.rpc.client.zk.ServicePullManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RpcClientRunner {


    @Autowired
    private ServicePullManager servicePullManager;

    @Autowired
    private ServiceProxyManager serviceProxyManager;

    @Autowired
    private ServiceRouteCache serviceRouteCache;

    @Autowired
    private RpcClientConfiguration configuration;

    @Autowired
    private RpcRequestPool requestPool;

    public void run() {
        // 从Zookeeper拉取服务数据
        servicePullManager.pullServiceFromZk();

        // 为每个服务创建代理，并注入到SpringIOC容器中
        serviceProxyManager.initServiceProxyInstance();

        // 启动Netty客户端服务
        RpcRequestManager.startRpcRequestManager(serviceRouteCache, configuration, requestPool);
    }

}
