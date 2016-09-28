package cn.com.sina.ea.erwa;

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.MyDataCenterInstanceConfig;
import com.netflix.appinfo.providers.EurekaConfigBasedInstanceInfoProvider;
import com.netflix.client.ClientFactory;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.DiscoveryManager;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.EurekaClientConfig;
import com.netflix.loadbalancer.DynamicServerListLoadBalancer;
import com.netflix.loadbalancer.RandomRule;
import com.netflix.loadbalancer.Server;

public class RibbonClientExample {

	private static final Logger logger = LoggerFactory
			.getLogger(RibbonClientExample.class);

	private static ApplicationInfoManager applicationInfoManager;
	private static EurekaClient eurekaClient;

	private static synchronized ApplicationInfoManager initializeApplicationInfoManager(
			EurekaInstanceConfig instanceConfig) {
		if (applicationInfoManager == null) {
			InstanceInfo instanceInfo = new EurekaConfigBasedInstanceInfoProvider(
					instanceConfig).get();
			applicationInfoManager = new ApplicationInfoManager(instanceConfig,
					instanceInfo);
		}

		return applicationInfoManager;
	}

	private static synchronized EurekaClient initializeEurekaClient(
			ApplicationInfoManager applicationInfoManager,
			EurekaClientConfig clientConfig) {
		if (eurekaClient == null) {
			eurekaClient = new DiscoveryClient(applicationInfoManager,
					clientConfig);
		}

		return eurekaClient;
	}

	public void sendRequestToServiceUsingEureka() {

		// Register with Eureka
		// DiscoveryManager.getInstance().initComponent(
		// new MyDataCenterInstanceConfig(),
		// new DefaultEurekaClientConfig());
		// ApplicationInfoManager.getInstance().setInstanceStatus(
		// InstanceStatus.UP);
		// get LoadBalancer instance from configuration, properties file

		ApplicationInfoManager applicationInfoManager = initializeApplicationInfoManager(new MyDataCenterInstanceConfig());
		initializeEurekaClient(applicationInfoManager,
				new DefaultEurekaClientConfig());

		DynamicServerListLoadBalancer<?> lb = (DynamicServerListLoadBalancer<?>) ClientFactory
				.getNamedLoadBalancer("erwa");
		// show all servers in the list
		List<Server> list = lb.getServerList(false);
		Iterator<Server> it = list.iterator();
		while (it.hasNext()) {
			Server server = it.next();
			logger.debug("application service host:" + server.getHost()
					+ ";port=" + server.getPort());
		}
		// use RandomRule 's RandomRule algorithm to get a random server from lb
		// 's server list
		RandomRule randomRule = new RandomRule();
		Server randomAlgorithmServer = randomRule.choose(lb, null);
		logger.debug("random algorithm server host:"
				+ randomAlgorithmServer.getHost() + ";port:"
				+ randomAlgorithmServer.getPort());
		// communicate with the server

		this.unRegisterWithEureka();
	}

	public void unRegisterWithEureka() {
		// Un register from eureka.
		DiscoveryManager.getInstance().shutdownComponent();
	}

	public static void main(String[] args) {
		RibbonClientExample sampleEurekaRibbonClient = new RibbonClientExample();
		sampleEurekaRibbonClient.sendRequestToServiceUsingEureka();

	}
}
