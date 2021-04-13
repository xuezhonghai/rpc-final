package demo.ocean.rpc.server.startup;

import demo.ocean.rpc.server.connector.RpcServerAcceptor;
import demo.ocean.rpc.server.zk.ServicePushManager;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class RpcServerRunner {


    private static ExecutorService executor = null;

    @Autowired
    private ServicePushManager servicePushManager;

    public void run() {
        executor = Executors.newFixedThreadPool(5);

        // Start Acceptor，waiting for the service caller to fire the request call
        executor.execute(new RpcServerAcceptor());

        // Register service providers into Zookeeper
        servicePushManager.registerZk();
    }


    @PreDestroy
    public void destroy() {
        if (executor != null) {
            executor.shutdown();
        }
    }

}
