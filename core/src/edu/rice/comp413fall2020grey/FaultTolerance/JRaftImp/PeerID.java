package edu.rice.comp413fall2020grey.FaultTolerance.JRaftImp;

import com.alipay.sofa.jraft.entity.PeerId;
import com.alipay.sofa.jraft.util.Endpoint;

public class PeerID {
        PeerId peer = new PeerId("localhost", 8080);
        Endpoint addr = peer.getEndpoint(); // Gets the endpoint of a node
        int index = peer.getIdx(); // Gets the index of a node, which is always set to 0 currently

        String s = peer.toString(); // The result is localhost:8080
        boolean success = peer.parse(s);  // Specifies whether PeerId parsing from a string is supported. The result is true.
}
