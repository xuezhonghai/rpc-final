package demo.ocean.rpc.client;

import demo.ocean.rpc.client.startup.RpcClientRunner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"demo.ocean.rpc"})
@SpringBootApplication
@Slf4j
public class ClientApplication implements ApplicationRunner {

    @Autowired
    private RpcClientRunner rpcClientRunner;

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
        log.info("client application startup successfully");
    }

    @Override
    public void run(ApplicationArguments args) {
        rpcClientRunner.run();
    }
}
