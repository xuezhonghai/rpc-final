package demo.ocean.rpc.client.cluster.impl;

import demo.ocean.rpc.client.channel.ProviderService;
import demo.ocean.rpc.client.cluster.ClusterStrategy;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PollingClusterStrategyImpl implements ClusterStrategy {


    private final AtomicInteger count = new AtomicInteger(0);


    @Override
    public ProviderService select(List<ProviderService> services) {
        int maxIndex = services.size() - 1;
        ProviderService providerService = null;
        while (true){
            int index = count.get();
            if(index > maxIndex){
                count.set(0);
            }else {
                providerService = services.get(index);
                count.incrementAndGet();
                break;
            }
        }

        return providerService;
    }

}
