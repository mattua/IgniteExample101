

import org.apache.ignite.*;
import org.apache.ignite.cache.affinity.AffinityKey;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.cache.query.SqlQuery;
import org.apache.ignite.cluster.ClusterGroup;
import org.apache.ignite.configuration.CacheConfiguration;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by mattua on 13/04/2016.
 */
public class IgniteWebinarExamples2 {


    //from this video: part 1 https://www.youtube.com/watch?v=4CQalha3MyY


    // remember to restart all the nodes when running new functionality




    public static enum Scenario {

        STREAM_WORDS,STREAMER_SINGLETON_SERVICE

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
        Scenario s = Scenario.STREAM_WORDS;

        switch (s) {


            case STREAM_WORDS:

            {
                // in a separate class
                StreamWords.main(null);


                // then in a separate JVM, run the QueryWords class



            }
            break;


            case   STREAMER_SINGLETON_SERVICE:{
            // Prevents this node from participating
            Ignition.setClientMode(true);


            //deploy service and exit

            try (Ignite ignite = IgniteNodeStartup.start()){


                // dont want service running on client nodes, so pass in a cluster group.
                IgniteServices svcs = ignite.services(ignite.cluster().forServers());

                // node since this is a cluster singleton, only one of the server nodes will be running this service
                // if there are multiple nodes started in the cluster, if one goes down the other one will start
                // running the service automatically
                svcs.deployClusterSingleton("wordStreamerService",new StreamerServiceImpl());

            }

            // Start and stop server nodes to demo fail over
            // Run QueryWords to show that the query works exactly as before


             }break;









        }


    }




}







