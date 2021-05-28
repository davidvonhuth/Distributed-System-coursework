package com.example.grpc.server.grpcserver;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class PingPongServiceImpl extends PingPongServiceGrpc.PingPongServiceImplBase {
    @Override
    public void ping(PingRequest request, StreamObserver<PongResponse> responseObserver) {

                PongResponse.Builder p_resp = PongResponse.newBuilder();

                int C00=request.getA00()*request.getB00()+request.getA01()*request.getB10();
                int C01=request.getA00()*request.getB01()+request.getA01()*request.getB11();
                int C10=request.getA10()*request.getB00()+request.getA11()*request.getB10();
                int C11=request.getA10()*request.getB01()+request.getA11()*request.getB11();
                p_resp.addE(C00);
                p_resp.addE(C01);
                p_resp.addE(C10);
                p_resp.addE(C11);

                responseObserver.onNext(p_resp.build());
                responseObserver.onCompleted();

    }
}
