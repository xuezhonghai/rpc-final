package demo.ocean.rpc.common.utils;

public class RequestIdUtil {

    public static String requestId() {
        return GlobalIDGenerator.getInstance().nextStrId();
    }
}
