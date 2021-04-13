package demo.ocean.rpc.client.cluster;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
public enum ClusterStrategyEnum {

    RANDOM("random"),
    WEIGHT_RANDOM("weight_random"),
    POLLING("polling"),
    HASH("hash");


    private String code;

    public static ClusterStrategyEnum queryByCode(@NotNull String code){

        code = code.toLowerCase();

        for (ClusterStrategyEnum value : ClusterStrategyEnum.values()) {
            if(value.getCode().equals(code)){
                return value;
            }
        }

        return null;
    }


}
