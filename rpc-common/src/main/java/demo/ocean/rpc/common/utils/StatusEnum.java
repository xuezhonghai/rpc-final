package demo.ocean.rpc.common.utils;

import lombok.Getter;

@Getter
public enum StatusEnum {

    /**
     * 成功
     */
    SUCCESS(200, "OK"),

    /**
     * 未找到服务提供者
     */
    NOT_FOUND_SERVICE_PROVIDER(100001, "not found service provider");


    private Integer code;
    private String description;

    StatusEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }
}
