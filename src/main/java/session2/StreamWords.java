package session2;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteDataStreamer;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.affinity.AffinityUuid;
import util.ExamplesUtils;
import util.IgniteNodeStartup;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

/**
 * Created by mattua on 10/05/2016.
 */
public class StreamWords {

    /*

    Streaming is essentially about injection large amounts of data into Ignite caches
    Can work with finite or continuous data sources

    */

    public static void main(String[] args) throws IOException {

        Ignition.setClientMode(true);

        try (Ignite ignite = IgniteNodeStartup.start()) {

             if (!ExamplesUtils.hasServerNodes(ignite)) {

                return;

            }

            // CacheConfig defines Type and lifetime config
            IgniteCache<AffinityUuid, String> stmCache = ignite.getOrCreateCache(CacheConfig.wordCache());

            // Create a streamer for the cache

            try (IgniteDataStreamer<AffinityUuid, String> stmr = ignite.dataStreamer(stmCache.getName())) {

                while (true) {


                    // make sure the txt file is in the resources folder
                    InputStream in = StreamWords.class.getResourceAsStream("/alice-in-wonderland.txt");

                    /*
                    Read book line by line and stream each word into the cache
                     */
                    try (LineNumberReader rdr = new LineNumberReader(new InputStreamReader(in))) {

                        for (String line = rdr.readLine(); line != null; line = rdr.readLine()) {
                            for (String word : line.split(" ")) {


                                if (!word.isEmpty()) {

                                    // create a unique key for each word and put it into cache
                                    // ignite caches work with keys and value
                                    // we need to make sure identical words go to the same node

                                    System.out.println(word);

                                    /* AffinityUuid
                                    Is a globally unique key but is associated with a word
                                    such that physically all instances of the same word will
                                    reside on the the same node

                                     */
                                    stmr.addData(new AffinityUuid(word), word);

                                }

                            }

                        }

                    }


                }


            }


        }


    }


}
