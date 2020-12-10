//package edu.rice.rbox.FaultTolerance.Messages;
//
//import com.google.protobuf.Empty;
//import com.google.protobuf.Timestamp;
//import io.grpc.stub.StreamObserver;
//import network.ElectionGrpc;
//import network.FaultToleranceGrpc;
//import java.util.HashMap;
//import java.util.Map;
//
//public class Election extends ElectionGrpc.ElectionImplBase {
//
//        String uuid = "";
//        int numLeaderMsg = 0;
//        HashMap<String, ElectionGrpc.ElectionBlockingStub> stubmap;
//        public Election(HashMap<String, ElectionGrpc.ElectionBlockingStub> stubmap) {
//                this.stubmap = stubmap;
//        }
//
//        public int getNumLeaderMsg() {
//                return numLeaderMsg;
//        }
//
//        public void setNumLeaderMsg(int numLeaderMsg) {
//                this.numLeaderMsg = numLeaderMsg;
//        }
//
//        public String getUUID() {
//                return this.uuid;
//        }
//
//        public Timestamp getTimestamp() {
//                long millis = System.currentTimeMillis();
//                Timestamp timestamp = Timestamp.newBuilder().setSeconds(millis / 1000)
//                    .setNanos((int) ((millis % 1000) * 1000000)).build();
//                return timestamp;
//        }
//
//        @Override
//        public void connection(FaultToleranceGrpc.CheckConnection request, StreamObserver<FaultToleranceGrpc.ConnectionResult> responseObserver) {
//                String target = request.getCheckUUID();
//                ElectionGrpc.ElectionBlockingStub stub = this.stubmap.get(target);
//
//                FaultToleranceGrpc.Info information = FaultToleranceGrpc.Info.newBuilder().setSenderUUID(getUUID()).setTime(getTimestamp()).build();
//                FaultToleranceGrpc.CheckIn req = FaultToleranceGrpc.CheckIn.newBuilder().setSender(information).build();
//
//                boolean success;
//                try {
//                        stub.check(req);
//                        success = true;
//                } catch (Exception ex) {
//                        success = false;
//                        responseObserver.onError(ex);
//                }
//
//                FaultToleranceGrpc.ConnectionResult res = FaultToleranceGrpc.ConnectionResult.newBuilder().setSender(information).setResult(success).build();
//                responseObserver.onNext(res);
//        }
//
//        @Override
//        public void check(FaultToleranceGrpc.CheckIn request, StreamObserver<FaultToleranceGrpc.Info> responseObserver) {
//                FaultToleranceGrpc.Info information = FaultToleranceGrpc.Info.newBuilder().setSenderUUID(getUUID()).setTime(getTimestamp()).build();
//                responseObserver.onNext(information);
//        }
//
//
//
//        @Override
//        public void downedLeader(FaultToleranceGrpc.LeaderDown request, StreamObserver<Empty> responseObserver) {
//                this.numLeaderMsg++;
//                responseObserver.onNext(Empty.newBuilder().build());
//        }
//}
