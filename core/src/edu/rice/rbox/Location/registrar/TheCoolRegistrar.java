package edu.rice.rbox.Location.registrar;

import com.google.protobuf.Timestamp;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import edu.rice.rbox.FaultTolerance.ConnectionManager;
import edu.rice.rbox.FaultTolerance.Messages.PromoteSecondaryMessage;
import edu.rice.rbox.Location.Mongo.MongoManager;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.net.DatagramSocket;
import java.net.InetAddress;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import network.*;
import network.InternalRegistrarFaultToleranceGrpc.*;
import network.SuperpeerFaultToleranceGrpc.*;

public class TheCoolRegistrar {

  private static TheCoolConnectionManager connManager;

  private static ClusterManager clusterManager;

  private static double getNumRegistrarNodes () {
    return (double)clusterManager.clusterMemberStubs.size();
  }

  private static boolean leader = false;

  private String ip;

  protected String getIP() {
    return ip;
  }

  private static UUID uuid;

  private static UUID leaderUUID;

  public static UUID getUUID() {
    return uuid;
  }

  private static RBoxProto.BasicInfo getInfo() {
    return RBoxProto.BasicInfo.newBuilder().setSenderUUID(getUUID().toString()).setTime(getTimestamp()).build();
  }

  protected static Timestamp getTimestamp() {
    long millis = System.currentTimeMillis();
    Timestamp timestamp = Timestamp.newBuilder().setSeconds(millis / 1000)
            .setNanos((int) ((millis % 1000) * 1000000)).build();
    return timestamp;
  }

  private static MongoManager mongoManager;

  private static MongoClient mongoClient;

  private static ClientSession clientSession;

  String REGISTRAR = "registrar_DB";

  public TheCoolRegistrar() {
    // TODO: set up the connection manager / Mongo stuff
    //this.clusterManager = new ClusterManager(getIP(),getUUID(), new Consumer<String>());
    MongoManager mongoMan = new MongoManager();
    mongoMan.connect();
    MongoClient client = mongoMan.getMongoClient();
    client.getDatabase(MongoManager.DB_NAME).drop();
    MongoDatabase db = client.getDatabase(MongoManager.DB_NAME);
    db.createCollection(MongoManager.CLIENT_COLLECTION);
    db.createCollection(MongoManager.SUPERPEER_COLLECTION);
    db.createCollection(MongoManager.COLLECTION_NAME);
    this.connManager = new TheCoolConnectionManager(db.getCollection(MongoManager.SUPERPEER_COLLECTION),
            db.getCollection(MongoManager.CLIENT_COLLECTION));
    //TODO initialize this
    this.clusterManager = new ClusterManager(null, null, null);
  }

  public static void init() throws  Exception {
    String ip = "";
    try(final DatagramSocket socket = new DatagramSocket()){
      socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
      ip = socket.getLocalAddress().getHostAddress();
    }

    // TODO: Setup Mongo
    // TODO: Create Connection Manager
    // TODO: Start grpc server with proper services

    mongoManager.connect();
    mongoClient = mongoManager.getMongoClient();
    clientSession = mongoClient.startSession();

    System.out.println("Server Running on address: " + ip);
    // Create a new server to listen on port 8080

    Server server = ServerBuilder.forPort(8080)
                        // this is for registrar/player client interactions
                        .addService(connManager.getGameServerRegistrarImpl())
                        // this is for registrar/superpeer interactions
                        .addService(clusterManager.getInternalServiceImpl())
                        .addService((BindableService) null)
                        .build();

    // Start the server
    server.start();

    // Server threads are running in the background.
    System.out.println("Server started");

    // Don't exit the main thread. Wait until server is terminated.
    server.awaitTermination();
  }

  private static Map<InternalRegistrarFaultToleranceBlockingStub, Timestamp> mostRecentClusterHeartBeats;
  private static Map<SuperpeerFaultToleranceBlockingStub, Timestamp> mostRecentSuperpeerHeartBeats;

  public static void main( String[] args ) throws Exception {

    //start server by calling init
    init();

    // create a new thread
    Runnable target = new Runnable() {
      @Override
      public void run() {
        while (!leader) {
          if (getTimestamp().getSeconds() > clusterManager.getMostRecentHeartbeat().getSeconds() ||
                  (getTimestamp().getSeconds() == clusterManager.getMostRecentHeartbeat().getSeconds() &&
                          getTimestamp().getNanos() - clusterManager.getMostRecentHeartbeat().getNanos() > 500)) {
            if (clusterManager.clusterMemberStubs.size() > 1) {
              UUID backup = (UUID) clusterManager.clusterMemberUUIDs.keySet().toArray()[0];
              if (backup == leaderUUID) {
                backup = (UUID) clusterManager.clusterMemberUUIDs.keySet().toArray()[1];
              }
              clusterManager.clusterMemberUUIDs.get(backup).downedLeader(RBoxProto.LeaderDown.newBuilder().setSender(getInfo()).build());
            }
          }
        }
        while (leader) {
          for (InternalRegistrarFaultToleranceBlockingStub stub : clusterManager.clusterMemberStubs.keySet()) {
            UUID stubUUID = clusterManager.clusterMemberStubs.get(stub);
            RBoxProto.BasicInfo info = getInfo();
            RBoxProto.HeartBeatRequest req = RBoxProto.HeartBeatRequest.newBuilder().setSender(info).build();
            boolean success = true;
            try {
              stub.heartBeatClusterMember(req);
            } catch (Exception ex) {
              success = false;
            }
            if (success) {
              mostRecentClusterHeartBeats.putIfAbsent(stub, info.getTime());
              mostRecentClusterHeartBeats.put(stub, info.getTime());
            } else {
              if(info.getTime().getNanos() - mostRecentClusterHeartBeats.get(stub).getNanos() > 500) {
                int countSuccessfulChecks = 0;
                int countUnsuccessfulChecks = 0;
                for (InternalRegistrarFaultToleranceBlockingStub checkStub : clusterManager.clusterMemberStubs.keySet()) {
                  RBoxProto.CheckConnection check = RBoxProto.CheckConnection.newBuilder().setSender(getInfo()).setCheckUUID(stubUUID.toString()).build();
                  if (checkStub.checkConnectionToClusterMember(check).getResult()) {
                    countSuccessfulChecks++;
                  } else {
                    countUnsuccessfulChecks++;
                  }
                  if (countSuccessfulChecks > (2.0 / 3.0) * getNumRegistrarNodes()) {
                    break;
                  } else if (countUnsuccessfulChecks > (2.0 / 3.0) * getNumRegistrarNodes()) {
                    clusterManager.clusterMemberStubs.remove(stub);
                    break;
                  }
                }
              }
            }
          }
          for (SuperpeerFaultToleranceBlockingStub stub : connManager.superPeers.keySet()) {
            UUID stubUUID = UUID.fromString(connManager.superPeers.get(stub));
            RBoxProto.BasicInfo info = getInfo();
            RBoxProto.HeartBeatRequest req = RBoxProto.HeartBeatRequest.newBuilder().setSender(info).build();
            boolean success = true;
            try {
              stub.heartBeatSuperpeer(req);
            } catch (Exception ex) {
              success = false;
            }
            if (success) {
              mostRecentSuperpeerHeartBeats.putIfAbsent(stub, info.getTime());
              mostRecentSuperpeerHeartBeats.put(stub, info.getTime());
            } else {
              if(info.getTime().getNanos() - mostRecentSuperpeerHeartBeats.get(stub).getNanos() > 500) {
                //TODO use mongo to get downed objects
                List<String> secondaryUUIDs = null;
                Map<String, Timestamp> secondaryBestTimestamps = null;
                Map<String, SuperpeerFaultToleranceBlockingStub> secondaryBestSuperpeers = null;
                connManager.superPeers.remove(stub);
                for (SuperpeerFaultToleranceBlockingStub superpeer : connManager.superPeers.keySet()) {
                   RBoxProto.SecondaryTimestampsMessage timestamps;
                   timestamps = superpeer.querySecondary(RBoxProto.QuerySecondaryMessage.newBuilder().addAllPrimaryUUIDs(secondaryUUIDs).build());
                   int index = 0;
                   for (String uuid : timestamps.getPrimaryUUIDsList()) {
                     secondaryBestTimestamps.putIfAbsent(uuid, timestamps.getSecondaryTimestamps(index));
                     secondaryBestSuperpeers.putIfAbsent(uuid, superpeer);
                     //TODO check this comparison
                     if (Integer.parseInt(secondaryBestTimestamps.get(uuid).toString()) > Integer.parseInt(timestamps.getSecondaryTimestamps(index).toString())) {
                       secondaryBestTimestamps.put(uuid, timestamps.getSecondaryTimestamps(index));
                       secondaryBestSuperpeers.put(uuid, superpeer);
                     }
                     index++;
                   }
                }
                //TODO double check that everything is being updated
                for (String uuid : secondaryBestSuperpeers.keySet()) {
                  RBoxProto.PromoteSecondaryMessage promote = RBoxProto.PromoteSecondaryMessage.newBuilder().setSender(getInfo()).addPromotedUUIDs(uuid).build();
                  secondaryBestSuperpeers.get(uuid).promote(promote);
                }
              }
            }
          }
        }
      }
    };
    Thread heartBeatChecker = new Thread(target);
    heartBeatChecker.start();

    // that thread will run a while loop forever

    // that while loop will send the heartbeats

//    ClusterManager thing = new ClusterManager(null, null, new Consumer<String>() {
//      @Override
//      public void accept(String s) {
//        for (Stub : connManager.superpees) {
//          send somehing
//        }
//      }
//    })
  }
}
