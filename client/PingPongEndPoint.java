package com.example.grpc.client.grpcclient;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

@RestController
public class PingPongEndpoint {

        GRPCClientService grpcClientService;

        @Autowired
        public PingPongEndpoint(GRPCClientService grpcClientService) {
                this.grpcClientService = grpcClientService;
        }


        @PostMapping(value = "/ping", consumes = "application/json")    //works without the followin-->'produces = "application/json")'
        public int[][] ping(@RequestBody Matrices matrices) {
                return grpcClientService.ping(matrices.getMatrix1(), matrices.getMatrix2());
        }
}
