package edu.rice.rbox.FaultTolerance.Messages;

import io.grpc.stub.StreamObserver;
import network.HealthGrpc;
import network.RBoxProto.HealthCheckRequest;
import network.RBoxProto.HealthCheckResponse;

import java.util.logging.Logger;

import javax.inject.Inject;

/**
 * Heartbeat message sent from the registrar to the superpeers.
 */

public class HeartbeatMessage extends HealthGrpc.HealthImplBase {
    private static final Logger logger = Logger.getLogger(HeartbeatMessage.class.getName());

    @Inject
    public HeartbeatMessage() {

    }

    @Override
    public void check(HealthCheckRequest request, StreamObserver<HealthCheckResponse> responseObserver) {
        try {
//            if (healthCheck.check().get().isHealthy()) {
                responseObserver.onNext(
                        HealthCheckResponse.newBuilder().setStatus(HealthCheckResponse.ServingStatus.SERVING).build()
                );
//            } else {
//                responseObserver.onNext(
//                        HealthCheckResponse.newBuilder().setStatus(HealthCheckResponse.ServingStatus.NOT_SERVING).build()
//                );
//            }
        } catch (Exception ex) {
            responseObserver.onError(ex);
        } finally {
            responseObserver.onCompleted();
        }
    }
}