package demo.ocean.rpc.server.zk;

import demo.ocean.rpc.common.annotation.RpcServer;
import demo.ocean.rpc.common.utils.IpUtil;
import demo.ocean.rpc.common.utils.SpringBeanFactory;
import demo.ocean.rpc.server.config.RpcServerConfiguration;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
@AllArgsConstructor
public class ServicePushManager {


    private ZKit zKit;

    private RpcServerConfiguration configuration;

    public void registerZk(){
        Map<String, Object> beanListByAnnotationClass = SpringBeanFactory.getBeanListByAnnotationClass(RpcServer.class);

        if(MapUtils.isNotEmpty(beanListByAnnotationClass)){

            zKit.createRootNode();

            for (Map.Entry<String, Object> entry : beanListByAnnotationClass.entrySet()) {
                Object bean = entry.getValue();

                RpcServer annotation = bean.getClass().getAnnotation(RpcServer.class);

                //拿到注解的cls 字段
                Class<?> clazz = annotation.cls();

                String serviceName = clazz.getName();
                //创建服务持久化节点
                zKit.createPersistentNode(serviceName);

                String serviceAddress = IpUtil.getRealIp() + ":" + configuration.getServerPort() + ":" + configuration.getNetworkPort();

                zKit.createNode(serviceName + "/" + serviceAddress);
            }
        }
    }



}
