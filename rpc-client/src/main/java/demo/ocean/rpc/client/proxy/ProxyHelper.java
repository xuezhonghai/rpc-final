package demo.ocean.rpc.client.proxy;


import demo.ocean.rpc.client.runner.RpcRequestManager;
import demo.ocean.rpc.client.runner.RpcRequestPool;
import demo.ocean.rpc.common.data.RpcRequest;
import demo.ocean.rpc.common.data.RpcResponse;
import demo.ocean.rpc.common.utils.GlobalIDGenerator;
import demo.ocean.rpc.common.utils.RequestIdUtil;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 动态代理拦截处理器
 */
@Component
public class ProxyHelper {

    @Autowired
    private RpcRequestPool requestPool;

    /**
     * 创建新的代理实例-CGLib动态代理
     * @param cls
     * @param <T>
     * @return
     */
    public <T> T newProxyInstance(Class<T> cls) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(cls);
        enhancer.setCallback(new ProxyCallBackHandler());
        return (T) enhancer.create();
    }

    /**
     * 代理拦截实现
     */
    class ProxyCallBackHandler implements MethodInterceptor {

        @Override
        public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            //return doIntercept(method, args);
            return doInterceptAsync(method, args);
        }

        /**
         * 拦截RCP接口调用
         * @param method
         * @param parameters
         * @return
         * @throws Throwable
         */
        private Object doIntercept(Method method, Object[] parameters) throws Exception {
            //1、封装RpcRequest请求参数
            RpcRequest request = getRequest(method, parameters);
            //2、发送数据
            RpcResponse response = RpcRequestManager.sendRequest(request);
            //3、获取返回结果
            if (null == response) {
                throw new NullPointerException();
            }

            return response.getResult();
        }

        private Object doInterceptAsync(Method method, Object[] parameters) throws Exception {
            //1、封装RpcRequest请求参数
            RpcRequest request = getRequest(method, parameters);
            //向服务端发消息
            //      1)建立链接
            //      2)发送消息
            RpcRequestManager.sendRequestAsync(request);
            RpcResponse response = requestPool.fetchResponse(request.getRequestId());
            //3、获取返回结果
            if (null == response) {
                throw new NullPointerException();
            }

            return response.getResult();
        }

        private RpcRequest getRequest(Method method, Object[] parameters) {
            String requestId = RequestIdUtil.requestId();
            String methodName = method.getName();
            String className = method.getDeclaringClass().getName();
            Class<?>[] parameterTypes = method.getParameterTypes();

            return RpcRequest.builder()
                    .requestId(requestId)
                    .className(className)
                    .methodName(methodName)
                    .parameterTypes(parameterTypes)
                    .parameters(parameters)
                    .build();
        }
    }
}
