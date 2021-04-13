package demo.ocean.rpc.client.config;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import demo.ocean.rpc.client.channel.ProviderService;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class BeanConfig {

    private static final int EXPIRE_SECONDS = 86400;

    @Autowired
    private RpcClientConfiguration configuration;

    @Bean
    public ZkClient zkClient() {
        return new ZkClient(configuration.getZkAddr(), configuration.getConnectTimeout());

    }

    @Bean
    public LoadingCache<String, List<ProviderService>> buildCache() {
        return CacheBuilder.newBuilder()
                .build(new CacheLoader<String, List<ProviderService>>() {
                    @Override
                    public List<ProviderService> load(String key) throws Exception {
                        return null;
                    }
                });
    }
}
