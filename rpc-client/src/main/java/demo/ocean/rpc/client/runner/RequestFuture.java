package demo.ocean.rpc.client.runner;


import demo.ocean.rpc.common.data.RpcResponse;

import java.util.concurrent.Future;

/**
 * <p>Description: </p>
 * @date 
 * @author 
 * @version 1.0
 * <p>Copyright:Copyright(c)2020</p>
 */
public interface RequestFuture<T> extends Future<T> {

    Throwable cause();

    void setCause(Throwable cause);

    boolean isWriteSuccess();

    void setWriteResult(boolean result);

    String requestId();

    T response();

    void setResponse(RpcResponse response);

    boolean isTimeout();


}