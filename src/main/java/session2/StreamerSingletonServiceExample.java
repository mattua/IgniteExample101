package session2;

import org.apache.ignite.*;
import session1.IgniteWebinarExamples1;
import session2.StreamerServiceImpl;
import util.IgniteNodeStartup;

import java.io.IOException;

/**
 * Created by mattua on 13/04/2016.
 */
public class StreamerSingletonServiceExample {


    public static void main(String[] args) throws InterruptedException, IOException {


        // Prevents this node from participating
        Ignition.setClientMode(true);


        //deploy service and exit

        try (Ignite ignite = IgniteNodeStartup.start()) {


            // dont want service running on client nodes, so pass in a cluster group.
            IgniteServices svcs = ignite.services(ignite.cluster().forServers());

            // node since this is a cluster singleton, only one of the server nodes will be running this service
            // if there are multiple nodes started in the cluster, if one goes down the other one will start
            // running the service automatically
            svcs.deployClusterSingleton("wordStreamerService", new StreamerServiceImpl());

        }

        // Start and stop server nodes to demo fail over
        // Run session2.QueryWords to show that the query works exactly as before


    }


}







