package cn.com.sina.ea.erwa;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;

import rx.Observable;

import com.google.common.collect.Lists;
import com.netflix.client.ClientFactory;
import com.netflix.client.http.HttpRequest;
import com.netflix.client.http.HttpResponse;
import com.netflix.config.ConfigurationManager;
import com.netflix.loadbalancer.LoadBalancerBuilder;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ZoneAwareLoadBalancer;
import com.netflix.loadbalancer.reactive.LoadBalancerCommand;
import com.netflix.loadbalancer.reactive.ServerOperation;

public class RibbonLBExample {

	public static void main(String[] args) throws Exception {
		Class.forName("com.netflix.niws.loadbalancer.DiscoveryEnabledNIWSServerList");
		ConfigurationManager
				.loadPropertiesFromResources("sample-client.properties"); // 1
		System.out.println(ConfigurationManager.getConfigInstance()
				.getProperty("sample-client.ribbon.listOfServers"));

		List<Server> servers = Lists.newArrayList(new Server(
				"www.sina.com.cn:80"), new Server("www.baidu.com:80"));

		LoadBalancerCommand<String> command = LoadBalancerCommand
				.<String> builder()
				.withLoadBalancer(
						LoadBalancerBuilder.newBuilder()
								.buildFixedServerListLoadBalancer(servers))
				.build();
		for (int i = 0; i < 10; i++) {
			command.submit(new ServerOperation<String>() {
				public Observable<String> call(Server server) {
					String result = "";
					// do something
					return Observable.just(result);
				}
			}).toBlocking().single();
		}

		// RestClient client = (RestClient) ClientFactory
		// .getNamedClient("sample-client"); // 2
		// HttpRequest request = HttpRequest.newBuilder().uri(new URI("/"))
		// .build(); // 3
		// for (int i = 0; i < 20; i++) {
		// HttpResponse response = client.executeWithLoadBalancer(request); // 4
		// System.out.println("Status code for " + response.getRequestedURI()
		// + "  :" + response.getStatus());
		// }
		// @SuppressWarnings("rawtypes")
		// ZoneAwareLoadBalancer lb = (ZoneAwareLoadBalancer)
		// client.getLoadBalancer();
		// System.out.println(lb.getLoadBalancerStats());
		// ConfigurationManager.getConfigInstance().setProperty(
		// "sample-client.ribbon.listOfServers",
		// "www.linkedin.com:80,www.google.com:80"); // 5
		// System.out.println("changing servers ...");
		// Thread.sleep(3000); // 6
		// for (int i = 0; i < 20; i++) {
		// HttpResponse response = null;
		// try {
		// response = client.executeWithLoadBalancer(request);
		// System.out.println("Status code for " + response.getRequestedURI() +
		// "  : " + response.getStatus());
		// } finally {
		// if (response != null) {
		// response.close();
		// }
		// }
		// }
		// System.out.println(lb.getLoadBalancerStats()); // 7
	}

}
