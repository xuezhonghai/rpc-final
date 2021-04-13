package demo.ocean.rpc.api;

import demo.ocean.rpc.common.annotation.RpcClient;

@RpcClient
public interface OrderService {

    String getOrder(String userName, String orderNo);
}
