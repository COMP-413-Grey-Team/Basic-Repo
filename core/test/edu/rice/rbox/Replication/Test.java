package edu.rice.rbox.Replication;

import edu.rice.rbox.Common.Change.AddReplicaChange;
import edu.rice.rbox.Common.Change.RemoteChange;
import edu.rice.rbox.Common.GameObjectUUID;
import edu.rice.rbox.Common.ServerUUID;
import edu.rice.rbox.ObjStorage.ChangeReceiver;
import junit.framework.TestCase;

import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.Mockito.mock;

import java.util.Date;
import java.util.UUID;


/**
 * Unit tests for ReplicaManager.
 */
public class Test extends TestCase {

  private ReplicaManagerGrpc replicmanager1;
  private ReplicaManagerGrpc replicmanager2;
  ServerUUID serverUUID1;
  ServerUUID serverUUID2;

//  private final ReplicaManagerGrpc.ReplicaManagerImpl replicaManagerImpl =
//          mock(ReplicaManagerGrpc.ReplicaManagerImpl.class, delegatesTo(
//                  new ReplicaManagerGrpc.ReplicaManagerImpl() {
//                    // By default the client will receive Status.UNIMPLEMENTED for all RPCs.
//                    // You might need to implement necessary behaviors for your test here, like this:
//                    //
//                    // @Override
//                    // public void sayHello(HelloRequest request, StreamObserver<HelloReply> respObserver) {
//                    //   respObserver.onNext(HelloReply.getDefaultInstance());
//                    //   respObserver.onCompleted();
//                    // }
//                    @Override
//
//                  }));
  public void setUp() throws Exception {
    ChangeReceiver changeReceiver = new ChangeReceiver() {
      @Override
      public void receiveChange(RemoteChange change) {

      }

      @Override
      public RemoteChange getReplica(GameObjectUUID id) {
        return null;
      }

      @Override
      public void deleteReplica(GameObjectUUID id, Date timestamp) {

      }

      @Override
      public void promoteSecondary(GameObjectUUID id) {

      }
    };

    serverUUID1 = new ServerUUID(UUID.fromString("7b431969-b18b-4d2a-890f-ec3a3cc12980"));
    serverUUID2 = new ServerUUID(UUID.fromString("c025863c-c1af-43b1-b968-9061e65d130c"));

    replicmanager1 = new ReplicaManagerGrpc(changeReceiver, serverUUID1);
    replicmanager2 = new ReplicaManagerGrpc(changeReceiver, serverUUID2);

    // TODO add primaries?
  }

  /**
   * Send message to fake server, and verify behaviors from the server side.
   */
  @org.junit.jupiter.api.Test
  public void testSubscribeMsg() {
    // TODO
    GameObjectUUID objectUUID = new GameObjectUUID(UUID.fromString("7954a2a8-8707-4698-b16d-e70ffe555e0d"));
    replicmanager1.subscribe(objectUUID, serverUUID2);
  }

  public void testUnsubscribeMsg() {
    // TODO
  }

  public void testUpdate() {
    // TODO
//    RemoteChange change = new AddReplicaChange();
//    replicmanager1.updatePrimary();
  }
}
