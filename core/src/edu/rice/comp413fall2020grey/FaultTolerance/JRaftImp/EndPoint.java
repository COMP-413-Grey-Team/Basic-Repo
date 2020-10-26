package edu.rice.comp413fall2020grey.FaultTolerance.JRaftImp;

import com.alipay.sofa.jraft.util.Endpoint;

public class EndPoint {
        Endpoint addr = new Endpoint("localhost", 8080);
        String s = addr.toString(); // The result is localhost:8080
//        boolean success = addr.parse(s);  // Specifies whether parsing the endpoint from a string is supported. The result is true.
}
