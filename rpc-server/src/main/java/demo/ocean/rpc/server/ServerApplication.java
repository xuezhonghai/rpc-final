package demo.ocean.rpc.server;

import demo.ocean.rpc.server.startup.RpcServerRunner;
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
public class ServerApplication implements ApplicationRunner {


    @Autowired
    private RpcServerRunner znsServerRunner;

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
        log.info("Zns service provider application startup successfully");

    }

    @Override
    public void run(ApplicationArguments applicationArguments) {
        znsServerRunner.run();
    }
}
