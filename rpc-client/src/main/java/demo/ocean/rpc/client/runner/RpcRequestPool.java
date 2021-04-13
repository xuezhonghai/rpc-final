package demo.ocean.rpc.client.runner;

import demo.ocean.rpc.common.data.RpcResponse;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Promise;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class RpcRequestPool {

    private final ConcurrentHashMap<String, Promise<RpcResponse>> requestPool = new ConcurrentHashMap<>();



    /***
     * 注册指定请求的Promise
     */
    public void submitRequest(String requestId, EventExecutor executor){
        /***
         * executor:监听操作（比如：setSuccess）
         */
        requestPool.put(requestId, new DefaultPromise<>(executor));
    }

    public RpcResponse fetchResponse(String requestId) throws Exception {
        Promise<RpcResponse> promise = requestPool.get(requestId);
        if(null != promise){
            RpcResponse rpcResponse = promise.get(10, TimeUnit.SECONDS);
            requestPool.remove(requestId);
            return rpcResponse;
        }
        return null;
    }


    /***
     * 从RpcResponseHandler中把消息提取出来，存入到Future(Promise)
     */
    public void notifyRequest(String requestId,RpcResponse response){
        Promise<RpcResponse> promise = requestPool.get(requestId);
        if(promise!=null){
            promise.setSuccess(response);
        }
    }



}
