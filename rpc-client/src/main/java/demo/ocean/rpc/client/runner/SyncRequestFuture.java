package demo.ocean.rpc.client.runner;

import demo.ocean.rpc.client.runner.RequestFuture;
import demo.ocean.rpc.common.data.RpcResponse;

import java.util.concurrent.*;

public class SyncRequestFuture implements RequestFuture {

    public static final ConcurrentHashMap<String, RequestFuture> syncRequest = new ConcurrentHashMap<>();

    private CountDownLatch latch = new CountDownLatch(1);

    // 标记开始时间， 判断是否超时
    private final long begin = System.currentTimeMillis();
    // 超时时间设定
    private long timeout;
    // rpc响应对象
    private RpcResponse response;
    // 请求ID
    private final String requestId;
    // 标记是否有回调结果
    private boolean writeResult;
    // 调用异常记录
    private Throwable cause;
    // 标记调用是否超时
    private boolean isTimeout = false;

    public SyncRequestFuture(String requestId) {
        this.requestId = requestId;
    }

    public SyncRequestFuture(String requestId, long timeout) {
        this.requestId = requestId;
        this.timeout = timeout;
        writeResult = true;
        isTimeout = false;
    }

    @Override
    public Throwable cause() {
        return cause;
    }

    @Override
    public void setCause(Throwable cause) {
        this.cause = cause;
    }

    @Override
    public boolean isWriteSuccess() {
        return writeResult;
    }

    @Override
    public void setWriteResult(boolean result) {
        this.writeResult = result;
    }

    @Override
    public String requestId() {
        return requestId;
    }

    @Override
    public Object response() {
        return response;
    }

    @Override
    public void setResponse(RpcResponse response) {
        this.response = response;
        latch.countDown();
    }

    @Override
    public boolean isTimeout() {
        if (isTimeout) {
            return true;
        }
        return System.currentTimeMillis() - begin > timeout;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return true;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        latch.wait();
        return response;
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (latch.await(timeout, unit)) {
            return response;
        }
        return null;
    }
}
