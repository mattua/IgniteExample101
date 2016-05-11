import org.apache.ignite.cache.affinity.AffinityUuid;
import org.apache.ignite.configuration.CacheConfiguration;

import javax.cache.configuration.FactoryBuilder;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Created by mattua on 10/05/2016.
 */
public class CacheConfig {


    public static CacheConfiguration<AffinityUuid,String> wordCache(){

        CacheConfiguration<AffinityUuid,String> cfg = new CacheConfiguration<>("Words");


        // important for SQL queries
        cfg.setIndexedTypes(AffinityUuid.class,String.class);

        // expiration policy of one second
        cfg.setExpiryPolicyFactory(FactoryBuilder.factoryOf(new CreatedExpiryPolicy(new Duration(TimeUnit.SECONDS,1))));



        return cfg;
    }


}
