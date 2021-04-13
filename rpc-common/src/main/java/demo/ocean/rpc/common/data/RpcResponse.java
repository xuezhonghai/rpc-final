package demo.ocean.rpc.common.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponse {

    private String requestId;

    private Object result;
    private Throwable cause;

    public boolean isError() {
        return cause != null;
    }
}
