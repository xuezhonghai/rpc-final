package demo.ocean.rpc.client.cluster.impl;

import demo.ocean.rpc.client.channel.ProviderService;
import demo.ocean.rpc.client.cluster.ClusterStrategy;
import demo.ocean.rpc.client.config.RpcClientConfiguration;
import org.apache.commons.lang3.RandomUtils;
import org.apache.jute.Index;

import javax.security.auth.login.Configuration;
import java.util.ArrayList;
import java.util.List;

public class WeightRandomClusterStrategyImpl implements ClusterStrategy {

    @Override
    public ProviderService select(List<ProviderService> services) {
        List<Integer> indexList = new ArrayList<>();
        for (int i = 0; i < services.size(); i++) {
            ProviderService providerService = services.get(i);
            if(0 != providerService.getWeight()){
                for (int i1 = 0; i1 < providerService.getWeight(); i1++) {
                    indexList.add(i);
                }
            }else {
                indexList.add(i);
            }
        }
        return services.get(indexList.get(RandomUtils.nextInt(0, indexList.size())));
    }


}
