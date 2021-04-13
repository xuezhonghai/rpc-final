package demo.ocean.rpc.client.cluster.impl;

import demo.ocean.rpc.client.channel.ProviderService;
import demo.ocean.rpc.client.cluster.ClusterStrategy;
import demo.ocean.rpc.common.utils.IpUtil;

import java.util.List;

public class HashClusterStrategyImpl implements ClusterStrategy {

    @Override
    public ProviderService select(List<ProviderService> services) {

        String realIp = IpUtil.getRealIp();
        int hashCode = realIp.hashCode();

        //获取索引
        int index = hashCode % services.size();
        return services.get(index);
    }
}
