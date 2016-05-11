import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCompute;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.DeploymentMode;
import org.apache.ignite.configuration.IgniteConfiguration;

/**
 * Created by mattua on 15/04/2016.
 */
public class IgniteNodeStartup {


    public static void main(String[] args) {

        start();
    }


    public static Ignite start() {

        IgniteConfiguration config = new IgniteConfiguration();

        config.setDeploymentMode(DeploymentMode.CONTINUOUS);

        return Ignition.start(config);


    }


}
