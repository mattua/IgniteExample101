import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteDataStreamer;
import org.apache.ignite.IgniteInterruptedException;
import org.apache.ignite.cache.affinity.AffinityUuid;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.ignite.services.Service;
import org.apache.ignite.services.ServiceContext;

import javax.cache.CacheException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

/**
 * Created by mattua on 11/05/2016.
 */

// Ignite Service method
public class StreamerServiceImpl implements StreamerService, Service {


    private long words;

    private long startTime;

    // injected
    @IgniteInstanceResource
    private Ignite ignite;


    private IgniteCache<AffinityUuid, String> stmCache;

    // = ignite.getOrCreateCache(CacheConfig.wordCache());

    //
    @Override
    public void cancel(ServiceContext ctx) {

        System.out.println("Service is cancelled");
    }


    // get a handle on the cache
    @Override
    public void init(ServiceContext serviceContext) throws Exception {


        stmCache = ignite.getOrCreateCache(CacheConfig.wordCache());

        System.out.println("Service is initialised");

    }

    @Override
    public void execute(ServiceContext ctx) throws Exception {


        try (IgniteDataStreamer<AffinityUuid, String> stmr = ignite.dataStreamer(stmCache.getName())) {

            startTime = System.currentTimeMillis();

            while (!ctx.isCancelled()) {


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

                                stmr.addData(new AffinityUuid(word), word);

                            }

                        }

                    }

                } catch (CacheException e) {
                    if (!(e.getCause() instanceof IgniteInterruptedException))
                        throw e;
                }


            }


        }


    }


    @Override
    public long getWordsPerSecond() {


        return 1000 * words / (System.currentTimeMillis() - startTime);
    }
}
