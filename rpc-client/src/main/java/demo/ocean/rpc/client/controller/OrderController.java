package demo.ocean.rpc.client.controller;

import demo.ocean.rpc.api.OrderService;
import demo.ocean.rpc.common.utils.SpringBeanFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单调用
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    //@Autowired
    //private OrderService orderService;

    /**
     * 获取订单信息
     */
    @GetMapping("/getOrder")
    public String getOrder(String userName) {
        //获取orderService代理对象
        OrderService orderService = SpringBeanFactory.getBean(OrderService.class);
        return orderService.getOrder(userName, "order00001");
    }
}
