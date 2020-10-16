package edu.rice.comp413fall2020grey.Replication.Messages;

import edu.rice.comp413fall2020grey.Common.GameObject;
import edu.rice.comp413fall2020grey.Common.GameObjectUUID;
import edu.rice.comp413fall2020grey.Common.ServerUUID;
import edu.rice.comp413fall2020grey.Common.Message;

import edu.rice.comp413fall2020grey.Replication.HolderInfo;

import java.io.Serializable;

import java.net.InetAddress;
import java.util.*;

public class ReplicaManager {

    private HashMap<GameObjectUUID, List<HolderInfo>> subscribers;
    private HashMap<GameObjectUUID, HolderInfo> publishers;
    private Queue<?> updates;
    private ServerUUID serverUUID;
    private InetAddress registrarIP;

    void sendMessage(Message msg, InetAddress ip) {
        // TODO: send msg to the superpeer with give ip address
    }

    void passReplicaToStorage(GameObject replica) {
        // TODO: pass this replica to object storage
    }

    private GameObject createReplica(GameObjectUUID primaryObjectUUID) {
        // TODO: given primary object uuid, create a replica
        return null;
    }


    void subscribe(GameObjectUUID primaryObjectUUID, InetAddress targetIP) {
        SubscribeMessage msg = new SubscribeMessage(new Date(), serverUUID, primaryObjectUUID);
        sendMessage(msg, targetIP);
    }

    void handleSubscribe(SubscribeMessage msg) {
        GameObjectUUID primaryObjectUUID = msg.getTargetObject();
        GameObject replica = createReplica(primaryObjectUUID);
        // TODO: get msg sender IP address
        InetAddress subscriberIP = null;
        subscribers.get(primaryObjectUUID).add(new HolderInfo(replica.getMetadata().getUUID(), subscriberIP));
        confirmSubscription(replica,  subscriberIP, primaryObjectUUID);
    }

    void confirmSubscription(GameObject replica, InetAddress subscriberIP, GameObjectUUID primaryObjectUUID) {
        ConfirmSubscriptionMessage msg = new ConfirmSubscriptionMessage(new Date(), serverUUID, primaryObjectUUID, replica);
        sendMessage(msg, subscriberIP);
    }

    void confirmSubscriptionHandler(ConfirmSubscriptionMessage msg) {
        GameObject replica = msg.getNewReplica();
        GameObjectUUID replicaUUID = replica.getMetadata().getUUID();
        GameObjectUUID primaryUUID = msg.getTargetObject();
        passReplicaToStorage(replica);
        // TODO: get msg sender IP address
        InetAddress publisherIP = null;
        publishers.put(replicaUUID, new HolderInfo(primaryUUID, publisherIP));
    }

    /*
     * A replica holder will send an unsubscribe message to the game object's primary
     * when a game object no longer falls in its area of interest.
     */
    void unsubscribe(GameObject replicaObject) {
        Date timestamp = new Date();
        GameObjectUUID replicaUUID = replicaObject.getMetadata().getUUID();
        HolderInfo primaryHolderInfo = publishers.get(replicaUUID);
        publishers.remove(replicaUUID);
        UnsubscribeMessage msg = new UnsubscribeMessage(timestamp, serverUUID, primaryHolderInfo.getGameObjUUID());
        sendMessage(msg, primaryHolderInfo.getSuperpeerIP());
    }

    /* The handler for receiving an unsubscribe message. */
    void handleUnsubscribe(UnsubscribeMessage msg) {
        GameObjectUUID primaryUUID = msg.getTargetObject();

        // TODO: get msg sender IP address
        InetAddress subscriberIP = null;
        subscribers.get(primaryUUID).removeIf(holderInfo -> holderInfo.getSuperpeerIP().equals(subscriberIP));
    }




    /**
     * Returns every message that Replication has received since this function was last called.
     */
    Set<?> flushCache() {
        // TODO: Implement Cache
        return null;
    }


    void sendUpdateToPrimary(GameObject gameObj, String field, Serializable value) {

        GameObjectUUID gameObjUUID = gameObj.getMetadata().getUUID();
        Date timestamp = new Date();
        HolderInfo primaryHolderInfo = publishers.get(gameObjUUID);

        UpdateMessage msg = new UpdateMessage(timestamp, serverUUID, primaryHolderInfo.getGameObjUUID(), field, value);
        sendMessage(msg, primaryHolderInfo.getSuperpeerIP());
    }


    void sendUpdateToReplicas(GameObject gameObj, String field, Serializable value, boolean isInterested) {
        GameObjectUUID gameObjUUID = gameObj.getMetadata().getUUID();
        Date timestamp = new Date();

        subscribers.get(gameObjUUID).forEach(replicaHolderInfo -> {
            GameObjectUUID replicaUUID = replicaHolderInfo.getGameObjUUID();
            UpdateMessage msg = new UpdateMessage(timestamp, serverUUID, replicaUUID, field, value);
            sendMessage(msg, replicaHolderInfo.getSuperpeerIP());
        });

        if (isInterested) {
            UpdateMessage msg = new UpdateMessage(timestamp, serverUUID, gameObjUUID, field, value);
            sendMessage(msg, registrarIP);
        }
    }

    void handleUpdate(UpdateMessage msg) {
        GameObjectUUID GameObjUUID = msg.getTargetObject();
        Date timestamp = msg.getTimestamp();
        String field = msg.getField();
        Serializable value = msg.getValue();
        // TODO: in what way shall we pass an update to our cache and eventually to object storage
        // updates.offer(?);

    }
}
