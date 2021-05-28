package com.example.grpc.client.grpcclient;

import com.example.grpc.server.grpcserver.PingRequest;
import com.example.grpc.server.grpcserver.PongResponse;
import com.example.grpc.server.grpcserver.PingPongServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.io.IOException;
import java.util.List;
import java.lang.Math;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.*;
import com.google.common.primitives.Ints;
import java.util.stream.Collectors;

@Service
public class GRPCClientService {


        public static long deadline = 3000000000L; //nanoseconds, 1 sec = 1*10^9 nanosec
        public static int count = 0;
        public static int blockSize = 2;
        public static int[] resultArray = new int[blockSize*2];

        public int deadlineFunction(long time_fp, long deadline, int remainingCalls){
                long totalTime = time_fp * remainingCalls;
                double remainingTime = (double) (deadline - time_fp);
                int nr_servers = (int) Math.ceil(totalTime / remainingTime);
                return nr_servers;
        }


        public int footprintFunction(String fp_server, int[][] block1, int[][] block2){
                ManagedChannel channel_fp = ManagedChannelBuilder.forAddress(fp_server, 9090).usePlaintext().build();
                PingPongServiceGrpc.PingPongServiceBlockingStub stub = PingPongServiceGrpc.newBlockingStub(channel_fp);

                long start = System.nanoTime();
                PongResponse resp_fp = stub.ping(PingRequest.newBuilder()
                .setA00(block1[0][0])
                .setA01(block1[0][1])
                .setA10(block1[1][0])
                .setA11(block1[1][1])
                .setB00(block2[0][0])
                .setB01(block2[0][1])
                .setB10(block2[1][0])
                .setB11(block2[1][1])
                .build());

        long finish = System.nanoTime();
        return (int) (finish - start);
        }

        public int[][] ping(int[][] matrix1, int[][]matrix2){

                //Specifying available servers
                ArrayList<String> servers = new ArrayList<String>();
                servers.add("54.175.134.117");
                servers.add("34.236.140.137");
                servers.add("52.22.141.212");
                servers.add("35.171.14.42");
                servers.add("52.23.173.128");
                servers.add("52.87.174.253");
                servers.add("3.84.189.128");
                servers.add("54.226.12.64");
                

                //Footprinting 
                int[][] block1 = new int[2][2];
                block1[0] = Arrays.copyOfRange(matrix1[0], 0, 2);
                block1[1] = Arrays.copyOfRange(matrix1[1], 0, 2);

                int[][] block2 = new int[2][2];
                block2[0] = Arrays.copyOfRange(matrix2[0], 0, 2);
                block2[1] = Arrays.copyOfRange(matrix2[1], 0, 2);

                long time_fp = footprintFunction(servers.get(0), block1, block2);
                int remainingCalls = (int) Math.pow((matrix1.length/2),3);
                int nr_servers = deadlineFunction(time_fp, deadline, remainingCalls);



                //Code for asynchronous calls using CompletableFuture in Java8
                List<CompletableFuture<List<Integer>>> futuresList_m = new ArrayList<CompletableFuture<List<Integer>>>();

                //Sending asynchronous MULTIPLICATION calls
                for(int i=0; i<(matrix1.length); i+=2){

                        for(int j=0; j<(matrix1.length); j+=2){

                                for(int k=0; k<(matrix1.length); k+=2){
                                        if(count==nr_servers){
                                                count=0;
                                        }

                                        ManagedChannel channel = ManagedChannelBuilder.forAddress(servers.get(count), 9090).usePlaintext().build();
                                        PingPongServiceGrpc.PingPongServiceBlockingStub stub = PingPongServiceGrpc.newBlockingStub(channel);

                                        PingRequest req = PingRequest.newBuilder()
                                                .setA00(matrix1[i][k])
                                                .setA01(matrix1[i][k+1])
                                                .setA10(matrix1[i+1][k])
                                                .setA11(matrix1[i+1][k+1])
                                                .setB00(matrix2[k][j])
                                                .setB01(matrix2[k][j+1])
                                                .setB10(matrix2[k+1][j])
                                                .setB11(matrix2[k+1][j+1])
                                                .build();


                                        CompletableFuture<List<Integer>> multAsyn = CompletableFuture.supplyAsync(()->(
                                                                stub.ping(req).getEList()));
                                        futuresList_m.add(multAsyn);

                                        count++;

                                }
                        }
                }


                CompletableFuture<Void> allFutures = CompletableFuture.allOf(futuresList_m.toArray(new CompletableFuture[futuresList_m.size()]));


                CompletableFuture<List<List<Integer>>> allCompletableFuture = allFutures.thenApply(future -> {
                        return futuresList_m.stream().map(completableFuture -> completableFuture.join()).collect(Collectors.toList());
                });

                CompletableFuture<List<List<Integer>>> completableFuture = allCompletableFuture.toCompletableFuture();

                try {
                        List<List<Integer>> finalList = completableFuture.get();
                        //Sending returned matrices to the ADDITION function at one of the servers
                        int[][] finalMatrix = new int[matrix1.length][matrix1.length];


                        ManagedChannel channel = ManagedChannelBuilder.forAddress(servers.get(0), 9090).usePlaintext().build();
                        PingPongServiceGrpc.PingPongServiceBlockingStub stub = PingPongServiceGrpc.newBlockingStub(channel);


                        for(int i=0; i<matrix1.length; i+=2){

                                for(int j=0; j<matrix1.length; j+=2){

                                        int idx_J = (int)Math.pow((matrix1.length/blockSize), 2) *(i/2) +(j*matrix1.length/blockSize/2);
                                        List<Integer> firstRow = finalList.get(idx_J);
                                        resultArray = Ints.toArray(firstRow);

                                        for(int k=1; k<(matrix1.length/blockSize); k++){

                                                int idx_K = idx_J + k;
                                                List<Integer> nextRow = finalList.get(idx_K);
                                                int[] nextArray = Ints.toArray(nextRow);

                                                PingRequest req = PingRequest.newBuilder()
                                                        .setA00(resultArray[0])
                                                        .setA01(resultArray[1])
                                                        .setA10(resultArray[2])
                                                        .setA11(resultArray[3])
                                                        .setB00(nextArray[0])
                                                        .setB01(nextArray[1])
                                                        .setB10(nextArray[2])
                                                        .setB11(nextArray[3])
                                                        .build();

                                                resultArray = Ints.toArray(stub.add(req).getEList());

                                        }
                                        finalMatrix[i][j] = resultArray[0];
                                        finalMatrix[i][j+1] = resultArray[1];
                                        finalMatrix[i+1][j] = resultArray[2];
                                        finalMatrix[i+1][j+1] = resultArray[3];

                                }
                        }

                        return finalMatrix;
                } catch (InterruptedException e) {
                        e.printStackTrace();
                } catch (ExecutionException e) {
                        e.printStackTrace();
                }

                return null;



        }


}
