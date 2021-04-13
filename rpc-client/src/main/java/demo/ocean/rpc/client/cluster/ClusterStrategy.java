package demo.ocean.rpc.client.cluster;

import demo.ocean.rpc.client.channel.ProviderService;

import java.util.List;

public interface ClusterStrategy {

    ProviderService select(List<ProviderService> services);
}
