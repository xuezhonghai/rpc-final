package demo.ocean.rpc.client.cluster.impl;

import demo.ocean.rpc.client.channel.ProviderService;
import demo.ocean.rpc.client.cluster.ClusterStrategy;
import org.apache.commons.lang3.RandomUtils;

import java.util.List;

public class RandomClusterStrategyImpl implements ClusterStrategy {

    @Override
    public ProviderService select(List<ProviderService> services) {
        int index = RandomUtils.nextInt(0, services.size());
        return services.get(index);
    }

}
