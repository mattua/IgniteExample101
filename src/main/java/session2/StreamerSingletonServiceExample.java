package session2;

import org.apache.ignite.*;
import session2.StreamerServiceImpl;
import util.IgniteNodeStartup;

import java.io.IOException;

/**
 * Created by mattua on 13/04/2016.
 */
public class StreamerSingletonServiceExample {


    //from this video: part 1 https://www.youtube.com/watch?v=4CQalha3MyY


    // remember to restart all the nodes when running new functionality
    public static enum Scenario {

        STREAMER_SINGLETON_SERVICE

    }
    /*
    Streaming:
        -branching pipelines
        -sliding windows - governs time to live in cache - 1 hour, or last 1000000 cache keys
        -customisable event workflow
        -ignite streams data into ignite nodes can be subsequently queried by ignite client

    Spark streams one RDD at a time whereas Ignite streams each event into Ignite without any batching of that data
     */


    public static void main(String[] args) throws InterruptedException, IOException {


        // comment
        Scenario s = Scenario.STREAMER_SINGLETON_SERVICE;

        switch (s) {


            case STREAMER_SINGLETON_SERVICE: {
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
            break;


        }


    }


}







