package com.example.grpc.server.grpcserver;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class PingPongServiceImpl extends PingPongServiceGrpc.PingPongServiceImplBase {
    @Override
    public void ping(PingRequest req, StreamObserver<PongResponse> responseObserver) {

                PongResponse.Builder response = PongResponse.newBuilder();
                //for(int k=0; k<4; k++){
                //      response.addE(req.getE(k)-1);
                //}

                response.setE(req.getE()*10);



                responseObserver.onNext(response.build());
                responseObserver.onCompleted();
    }
}