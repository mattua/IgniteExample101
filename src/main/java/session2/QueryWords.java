package session2;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.affinity.AffinityUuid;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import util.ExamplesUtils;
import util.IgniteNodeStartup;

import java.util.List;

/**
 * Created by mattua on 10/05/2016.
 */
public class QueryWords {

    public static void main(String[] args) throws InterruptedException {


        Ignition.setClientMode(true);

        try (Ignite ignite = IgniteNodeStartup.start()) {

            if (!ExamplesUtils.hasServerNodes(ignite)) {

                return;

            }

            IgniteCache<AffinityUuid, String> stmCache = ignite.getOrCreateCache(CacheConfig.wordCache());

            SqlFieldsQuery top10Qry = new SqlFieldsQuery(

                    "select _val, count(_val) as cnt from String " +
                            "group by _val " +
                            "order by cnt desc " +
                            "limit 10",
                    true
            );


            // _val is Ignite provided name for String values
            // In Ignite SQL, types are tables, fields are columns
            SqlFieldsQuery statsQry = new SqlFieldsQuery(
                    "select avg(cnt), min(cnt), max(cnt) from " +
                            "(select count(_val) as cnt from String group by _val)"


            );


            while (true) {

                List<List<?>> top10 = stmCache.query(top10Qry).getAll();
                List<List<?>> stats = stmCache.query(statsQry).getAll();

                List<?> row = stats.get(0);

                if (row.get(0) != null) {
                    System.out.printf("Query results [avg=%.2f, min=%d]%n", row.get(0), row.get(1), row.get(2));
                }


                ExamplesUtils.printQueryResults(top10);

                Thread.sleep(5000);

            }


        }


    }


}
