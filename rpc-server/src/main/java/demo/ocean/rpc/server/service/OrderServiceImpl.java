package demo.ocean.rpc.server.service;

import demo.ocean.rpc.api.OrderService;
import demo.ocean.rpc.common.annotation.RpcServer;
import demo.ocean.rpc.common.utils.RpcException;
import org.springframework.beans.factory.annotation.Value;


@RpcServer(cls = OrderService.class)
public class OrderServiceImpl implements OrderService {


    @Value("${server.port}")
    private Integer serverPort;

    @Override
    public String getOrder(String userName, String orderNo) {
        if ("error".equalsIgnoreCase(userName)) {
            throw new RpcException("test exception! " + userName);
        }
        return String.format("Server(" + serverPort + "), Order Details => userName: %s, orderNo: %s", userName, orderNo);
    }
}
