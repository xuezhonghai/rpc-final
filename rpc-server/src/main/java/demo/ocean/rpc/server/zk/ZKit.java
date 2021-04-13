package demo.ocean.rpc.server.zk;

import demo.ocean.rpc.server.config.RpcServerConfiguration;
import lombok.AllArgsConstructor;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.stereotype.Component;

/**
 * Zookeeper连接操作接口
 */
@Component
@AllArgsConstructor
public class ZKit {

    private ZkClient zkClient;

    private RpcServerConfiguration rpcServerConfiguration;

    /***
     * 根节点创建
     */
    public void createRootNode() {
        boolean exists = zkClient.exists(rpcServerConfiguration.getZkRoot());
        if (!exists) {
            zkClient.createPersistent(rpcServerConfiguration.getZkRoot());
        }
    }

    /***
     * 创建其他节点
     * @param path
     */
    public void createPersistentNode(String path) {
        String pathName = rpcServerConfiguration.getZkRoot() + "/" + path;
        boolean exists = zkClient.exists(pathName);
        if (!exists) {
            zkClient.createPersistent(pathName);
        }
    }

    /***
     * 创建节点
     * @param path
     */
    public void createNode(String path) {
        String pathName = rpcServerConfiguration.getZkRoot() + "/" + path;
        boolean exists = zkClient.exists(pathName);
        if (!exists) {
            zkClient.createEphemeral(pathName);
        }
    }
}
