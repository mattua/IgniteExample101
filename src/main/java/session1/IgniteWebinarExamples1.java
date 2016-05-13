package session1;

import model.Company;
import model.Person;
import org.apache.ignite.*;
import org.apache.ignite.cache.affinity.AffinityKey;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.cache.query.SqlQuery;
import org.apache.ignite.cluster.ClusterGroup;
import org.apache.ignite.configuration.CacheConfiguration;
import util.ExamplesUtils;
import util.IgniteNodeStartup;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by mattua on 13/04/2016.
 */


public class IgniteWebinarExamples1 {


    //from this video: part 1 https://www.youtube.com/watch?v=4CQalha3MyY


    // remember to restart all the nodes when running new functionality


    public static enum Scenario {

        BASIC,
        REMOTES,
        COUNT_WORDS_RESILIENT_COMPUTE,
        SHUTDOWN,
        DATAGRID_BASIC,
        DATAGRID_FAILOVER_AFTER_BASIC,
        DATAGRID_QUERY;

    }

    public static void main(String[] args) throws InterruptedException {


        Scenario s = Scenario.DATAGRID_QUERY;

        switch (s) {

            /**************************************************************************

             1)

             Make sure you run a few instances of IgniteNodeStartup in the background

             This will start another instance within this JVM, get an instance to the
             Ignite cluster instance (representing all running nodes)

             Then it will broadcast a simple closure to each node (each
             instance of IgniteNodeStartup application

             See this message in the console window of each

             */
            case BASIC:

            {
                Ignite ignite = IgniteNodeStartup.start();

                IgniteCompute compute = ignite.compute();

                // You will see this message in each instance window
                compute.broadcast(() -> System.out.println("hello nodes"));


            }
            break;
            /*-----------------------------------------------------------------------------
            2) REMOTES

                Again get a reference to the shared Ignite cluster
                Create a cluster group representing only the remote
                nodes (i.e. not including the node running in this
                particular JVM

                The clustergroup concept can be extended to nodes of any properties
                even determined at runtime by current server load for load balancing
             */
            case REMOTES:

            {
                Ignite ignite = IgniteNodeStartup.start();

                IgniteCluster cluster = ignite.cluster();

                IgniteCompute compute = ignite.compute();
                // create a cluster group of all nodes EXCEPT this one
                // can define group based on attributes or even resource capacity
                ClusterGroup rmts = cluster.forRemotes();

                // Metrics can be defined across cluster
                //rmts.metrics();
                compute = ignite.compute(rmts);

                compute.broadcast(() -> System.out.println("hello remotes"));


            }
            break;
           /*-----------------------------------------------------------------------------
             3) COUNT_WORDS_RESILIENT_COMPUTE

                a) START TWO instances of IgniteNodeStartUp (server nodes) (+ this one)

                Ignite will look at the number of parameters (second argument) in this case words
                and clone the closure that number of times and distribute according to its own
                internal load balancing

                b) see in the console how each of the 3 instances is executing its own word
                repeatedly

                c) KILL one of the background nodes

                shows fault tolerance
                shut down a node during this loop and observe that it fails over to the other node
                start up other nodes and watch them join the party

                See that the load has now been balanced between the remaining two nodes and
                most importantly that the letter count in the total aggregated sentence is still 17

                d) Start another background server node (IgniteNodeStartup)
                See that it rejoins and load is once again distributed across the 3 nodes

             */
            case COUNT_WORDS_RESILIENT_COMPUTE: {

                Ignite ignite = IgniteNodeStartup.start();

                for (int i = 0; i < 1000; i++) {
                    Thread.sleep(1000);

                    int k = 1;

                    // Java 8 syntax

                    // send this closure to all nodes - each instance of the
                    // closure processes one node and the closure count the
                    // number of characters and returns

                    // Then we sum the total once all the closures have returned
                    // their counts back
                    Collection<Integer> res2 = ignite.compute().apply(

                            (String w) -> {
                                System.out.println("Counting: " + w);

                                return w.length();
                            },
                            Arrays.asList("Apache Ignite Rules".split(" ")));

                    int sum2 = res2.stream().mapToInt(j -> j).sum();

                    System.out.println("Total: " + sum2);


                }


            }
            break;
            /*-----------------------------------------------------------------------------
             4) SHUTDOWN

                Demo of how to shutdown all the cluster nodes from this node

                start some nodes, then run this to check they shutdown

             */
            case SHUTDOWN: {

                Ignite ignite = IgniteNodeStartup.start();

                IgniteCluster cluster = ignite.cluster();

                cluster.stopNodes();

                ignite.close();


            }
            break;

            /*-----------------------------------------------------------------------------
             5) DATAGRID_BASIC

                Distributed key values store - can be partitioned or replicated or both
                Can run SQL on top of this - fields are columns and classes are tables

               Very simple demonstration of creating a cache instance and populating and
               retrieving some values.


               a) Run this case then SHUTDOWN the application but KEEP THE BACKGROUND NODES RUNNING,
               run the next case DATAGRID_FAILOVER_AFTER_BASIC

             */
            case DATAGRID_BASIC: {

                Ignite ignite = IgniteNodeStartup.start();

                // configuration is distributed through all nodes
                CacheConfiguration<Integer, String> cfg = new CacheConfiguration<>("webinar");

                //Configure one backup
                cfg.setBackups(1);

                // check with the cluster to see if there is already a cache with this configuration in the cluster
                IgniteCache<Integer, String> cache = ignite.getOrCreateCache(cfg);

                int cnt = 10;

                for (int k = 0; k < 1000; k++) {

                    Thread.sleep(1000);

                    // populate keys into the cache
                    for (int i = 0; i < cnt; i++) {

                        cache.put(i, Integer.toString(i));

                    }

                    for (int i = 0; i < cnt; i++) {

                        System.out.println("Got " + i + " from cache: " + cache.get(i));

                    }
                }


            }
            break;
            /*-----------------------------------------------------------------------------
             6) DATAGRID_FAILOVER_AFTER_DATAGRID_BASIC

                a) Make sure you have run (and shutdown) the previous case and kept the background nodes running

                This time we don't populate anything in the cache, but we rely on the fact that we
                know we had at least one back up configured

                b) check that values are being retrieved from the cache and printed to the screen

                c) Now shutdown one of the background nodes (IgniteNodeStartup)
                Observe that Ignite cache lives on due to the configured back up - we
                should still be retrieving all the values from the cache in the background
             */
            case DATAGRID_FAILOVER_AFTER_BASIC: {

                Ignite ignite = IgniteNodeStartup.start();


                // configuration is distributed through all nodes
                CacheConfiguration<Integer, String> cfg = new CacheConfiguration<>("webinar");


                cfg.setBackups(1);

                // check with the cluster to see if there is already a cache with this configuration
                IgniteCache<Integer, String> cache = ignite.getOrCreateCache(cfg);

                int cnt = 10;

                for (int k = 0; k < 1000; k++) {
                    Thread.sleep(1000);


                    // See that the values are still retrieved from the other nodes


                    for (int i = 0; i < cnt; i++) {

                        System.out.println("Got " + i + " from cache: " + cache.get(i));

                    }
                }


            }
            break;
            /*-----------------------------------------------------------------------------
             7) DATAGRID_QUERY

                Create some simple java objects - Person/Company which are "joined" by a
                person.companyId relationship.

                Key concept of AffinityKey meaning that Ignite will ensure that all Person objects
                will be stored on the same node as the corresponding Company object. Makes joins
                much quicker.

              a) make sure your server nodes are running
             */
            case DATAGRID_QUERY: {

                Ignition.setClientMode(true);

                Ignite ignite = IgniteNodeStartup.start();


                // default cache in ignite is partitioned cache
                // all persons who work for the same company will be stored on the same node where
                // the company is stored

                CacheConfiguration<AffinityKey<Integer>, Person> pcfg = new CacheConfiguration<>("persons");
                CacheConfiguration<Integer, Company> ccfg = new CacheConfiguration<>("companies");


                // We specifiy which types we want to index

                // use affinity key because we dont want persons to be colocated based on person id
                // rather we want to colocate via company id
                pcfg.setIndexedTypes(AffinityKey.class, Person.class);
                ccfg.setIndexedTypes(Integer.class, Company.class);

                //create the caches
                IgniteCache<AffinityKey<Integer>, Person> persons = ignite.getOrCreateCache(pcfg);
                IgniteCache<Integer, Company> companies = ignite.getOrCreateCache(ccfg);

                // Populate some objects in the distributed cache
                populate();


                // now lets run some select statements
                SqlQuery<AffinityKey<Integer>, Person> qry1 = new SqlQuery<>(Person.class, "select * from Person where salary < ?");

                int salary = 3000;
                qry1.setArgs(salary);


                // cache objects have query methods
                // results can be paginated but we can use getAll if the expected result set is not too small you can getAll
                List<javax.cache.Cache.Entry<AffinityKey<Integer>, Person>> res1 = persons.query(qry1).getAll();

                System.out.println(" there are " + res1.size() + " employees with salary less than " + salary);


                // see how we do join - the companies cache is referenced since we are executing the query against the "persons" cache
                SqlFieldsQuery qry2 = new SqlFieldsQuery("select Person.name, Company.name, salary from Person,\"companies\".Company " +
                        " where Person.companyId = Company.id " +
                        " and Company.name = ?", true);

                qry2.setArgs("GridGain");


                List<List<?>> res2 = persons.query(qry2).getAll();

                System.out.println(" there are " + res2.size() + " employees at GridGain");


                // see how we do aggregations
                SqlFieldsQuery qry3 = new SqlFieldsQuery(
                        "select avg(salary) as avgSalary, Company.name from Person,\"companies\".Company " +
                                " where Person.companyId = Company.id " +
                                " group by Company.name " +
                                "order by avgSalary asc", true);

                List<List<?>> res3 = persons.query(qry3).getAll();

                ExamplesUtils.printQueryResults(res3);

            }
            break;


        }


    }


    private static void populate() {


        Ignite ignite = Ignition.ignite();

        // fetch existing caches by name
        IgniteCache<AffinityKey<Integer>, Person> persons = ignite.cache("persons");
        IgniteCache<Integer, Company> companies = ignite.getOrCreateCache("companies");


        Company c1 = new Company(1, "GridGain");
        Company c2 = new Company(2, "Other");

        Person p1 = new Person(1, "Dmitry", 1, 1000);
        Person p2 = new Person(2, "Nikita", 1, 2000);
        Person p3 = new Person(3, "John", 2, 3000);
        Person p4 = new Person(4, "Dave", 2, 4000);

        companies.put(c1.getId(), c1);
        companies.put(c2.getId(), c2);


        // Affinity key will route the person to the same node on which the country lives
        persons.put(new AffinityKey<>(p1.getId(), p1.getCompanyId()), p1);
        persons.put(new AffinityKey<>(p2.getId(), p2.getCompanyId()), p2);
        persons.put(new AffinityKey<>(p3.getId(), p3.getCompanyId()), p3);
        persons.put(new AffinityKey<>(p4.getId(), p4.getCompanyId()), p4);


    }

}







