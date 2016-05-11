import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteDataStreamer;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.affinity.AffinityUuid;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.stream.Stream;

/**
 * Created by mattua on 10/05/2016.
 */
public class StreamWords {

    public static void main(String[] args) throws IOException {

        Ignition.setClientMode(true);

        try (Ignite ignite = IgniteNodeStartup.start()) {

            if (!ExamplesUtils.hasServerNodes(ignite)) {

                return;

            }


            IgniteCache<AffinityUuid, String> stmCache = ignite.getOrCreateCache(CacheConfig.wordCache());

            // Create a streamer for the cache

            try (IgniteDataStreamer<AffinityUuid, String> stmr = ignite.dataStreamer(stmCache.getName())) {

                while (true) {


                    // make sure the txt file is in the resources folder
                    InputStream in = StreamWords.class.getResourceAsStream("alice-in-wonderland.txt");


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
