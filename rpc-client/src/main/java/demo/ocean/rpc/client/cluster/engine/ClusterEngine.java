package demo.ocean.rpc.client.cluster.engine;

import com.google.common.collect.Maps;
import demo.ocean.rpc.client.cluster.ClusterStrategy;
import demo.ocean.rpc.client.cluster.ClusterStrategyEnum;
import demo.ocean.rpc.client.cluster.impl.HashClusterStrategyImpl;
import demo.ocean.rpc.client.cluster.impl.PollingClusterStrategyImpl;
import demo.ocean.rpc.client.cluster.impl.RandomClusterStrategyImpl;
import demo.ocean.rpc.client.cluster.impl.WeightRandomClusterStrategyImpl;

import java.util.Map;

public class ClusterEngine {

    private static final Map<ClusterStrategyEnum, ClusterStrategy> CLUSTER_STRATEGY_MAP = Maps.newConcurrentMap();

    /**
     * 定义所有的负载策略
     */
    static {
        CLUSTER_STRATEGY_MAP.put(ClusterStrategyEnum.RANDOM, new
                RandomClusterStrategyImpl());
        CLUSTER_STRATEGY_MAP.put(ClusterStrategyEnum.WEIGHT_RANDOM, new
                WeightRandomClusterStrategyImpl());
        CLUSTER_STRATEGY_MAP.put(ClusterStrategyEnum.POLLING, new
                PollingClusterStrategyImpl());
        CLUSTER_STRATEGY_MAP.put(ClusterStrategyEnum.HASH, new
                HashClusterStrategyImpl());
    }

    /**
     * 根据策略选取指定的实现引擎
     * @param clusterStrategy
     * @return
     */
    public static ClusterStrategy queryClusterStrategy(String clusterStrategy) {
        ClusterStrategyEnum clusterStrategyEnum = ClusterStrategyEnum.queryByCode(clusterStrategy);
        if (clusterStrategyEnum == null) {
            // 默认采用随机的策略
            return new RandomClusterStrategyImpl();
        }
        return CLUSTER_STRATEGY_MAP.get(clusterStrategyEnum);
    }



}
