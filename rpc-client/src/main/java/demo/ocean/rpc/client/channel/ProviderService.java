package demo.ocean.rpc.client.channel;

import com.google.common.base.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderService {

    private String serverIp;
    private int serverPort;
    private int networkPort;

    private long timeout;
    // the weight of service provider
    private int weight;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProviderService that = (ProviderService) o;
        return serverPort == that.serverPort &&
                networkPort == that.networkPort &&
                Objects.equal(serverIp, that.serverIp);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(serverIp, serverPort, networkPort);
    }
}
