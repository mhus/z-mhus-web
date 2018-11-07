package de.mhus.cherry.web.util.filter;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.UUID;

import javax.servlet.ServletOutputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import de.mhus.cherry.web.api.CallContext;
import de.mhus.cherry.web.api.InternalCallContext;
import de.mhus.cherry.web.api.VirtualHost;
import de.mhus.cherry.web.api.WebFilter;
import de.mhus.lib.core.MFile;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MPassword;
import de.mhus.lib.core.config.IConfig;
import de.mhus.lib.core.config.MConfig;
import de.mhus.lib.core.io.http.MHttpClientBuilder;
import de.mhus.lib.core.net.Subnet;
import de.mhus.lib.core.util.Base64;
import de.mhus.lib.core.util.MUri;
import de.mhus.lib.errors.MException;

// https://en.wikipedia.org/wiki/Basic_access_authentication
public class CloudflareFilter extends MLog implements WebFilter {

	public static String NAME = "base_auth_filter";

	@Override
	public void doInitialize(UUID instance, VirtualHost vHost, IConfig config) throws MException {
		vHost.getProperties().put(NAME + instance, new Config(config));
	}

	@Override
	public boolean doFilterBegin(UUID instance, InternalCallContext call) throws MException {
		Config config = (Config)call.getVirtualHost().getProperties().get(NAME + instance);
		if (config == null) {
			send401(call, config);
			return false;
		}
		if (isCloudflare(call, config)) {
			String remoteIP = call.getHttpRequest().getHeader("CF-Connecting-IP");
			call.setRemoteIp(remoteIP);
			trace(call,config,remoteIP);
			return true;
		} else {
			String remoteIP = call.getHttpRequest().getRemoteAddr();
			call.setAttribute(CallContext.REQUEST_REMOTE_IP, remoteIP);
			trace(call,config,remoteIP);
			String auth = call.getHttpRequest().getHeader("Authorization");  
			if (auth == null) {
				send401(call, config);
				return false;
			}
	        if (!auth.toUpperCase().startsWith("BASIC ")) {   
				send401(call, config);
	            return false;  // we only do BASIC  
	        }  
	        // Get encoded user and password, comes after "BASIC "  
	        String userpassEncoded = auth.substring(6);  
	        // Decode it, using any base 64 decoder  
	        String userpassDecoded = new String( Base64.decode(userpassEncoded) );
	        // Check our user list to see if that user and password are "allowed"
	        String[] parts = userpassDecoded.split(":",2);
	        String account = null;
	        String pass = null;
	        if (parts.length > 0) account = MUri.decode(parts[0]);
	        if (parts.length > 1) pass = MUri.decode(parts[1]);
	        String accPass = config.accounts.get(account);
	        if (accPass != null) {
	        		if (MPassword.equals( accPass, pass) )
	        			return true;
	        		else
	            		log().d("password not accepted",account);
	        } else
	        		log().d("user not found",account);
	
			send401(call, config);
			return false;
		}
	}

	private void trace(InternalCallContext call, Config config, String remoteIP) {
		if (call.getVirtualHost().isTraceAccess())
			log().i("access",call.getVirtualHost().getName(),remoteIP,call.getHttpMethod(),call.getHttpPath());
	}

	private boolean isCloudflare(InternalCallContext call, Config config) {
		try {
			InetAddress remoteIP = InetAddress.getByName(call.getHttpRequest().getRemoteAddr());
			for (Subnet net : config.networks)
				if (net != null && net.isInNet(remoteIP)) return true;
		} catch (UnknownHostException e) {
			log().w(e);
		}
		return false;
	}

	private void send401(InternalCallContext call, Config config) throws MException {
		try {
			call.getHttpResponse().setStatus(401);
			call.getHttpResponse().setHeader("WWW-Authenticate", "BASIC realm=\""+config.realm+"\", charset=\"UTF-8\"");
			call.getHttpResponse().setContentType("text/html");
			ServletOutputStream os = call.getHttpResponse().getOutputStream();
			os.write(config.message.getBytes()); // utf-8?
			os.flush();
		} catch (IOException e) {
			throw new MException(e);
		}
	}

	@Override
	public void doFilterEnd(UUID instance, InternalCallContext call) throws MException {
	}

	private class Config {

		private String realm;
		private String message;
		private HashMap<String, String> accounts = new HashMap<>();
		private Subnet[] networks;

		public Config(IConfig config) {
			message = config.getString("message","Access denied");
			realm = config.getString("realm","Access");
			for (IConfig node : config.getNode("accounts").getNodes())
				try {
					accounts.put(node.getString("user"), node.getString("pass"));
				} catch (MException e) {
					log().e(e);
				}
			String url = config.getString("url","none"); // "https://www.cloudflare.com/ips-v4"
			IConfig ipNode = config.getNode("ips");
			String[] ips = null;
			if (ipNode != null) {
				ips = MConfig.toStringArray(ipNode.getNodes(), "value");
			} else 
			if (!"none".equals(url))
			{
				try {
					HttpGet get = new HttpGet(url);
					HttpResponse res = new MHttpClientBuilder().setUseSystemProperties(true).execute(get);
					if (res.getStatusLine().getStatusCode() != 200) {
						log().e("Failed to get IPs from cloudflare",res.getStatusLine().getReasonPhrase(),url);
					} else {
						InputStream is = res.getEntity().getContent();
						ips = MFile.readLines(is, true).toArray(new String[0]);
					}
				} catch (Exception e) {
					log().e(e);
				}
				if (ips == null || ips.length == 0) {
					log().i("IPs fallback");
					ips = new String[] {
							"103.21.244.0/22",
							"103.22.200.0/22",
							"103.31.4.0/22",
							"104.16.0.0/12",
							"108.162.192.0/18",
							"131.0.72.0/22",
							"141.101.64.0/18",
							"162.158.0.0/15",
							"172.64.0.0/13",
							"173.245.48.0/20",
							"188.114.96.0/20",
							"190.93.240.0/20",
							"197.234.240.0/22",
							"198.41.128.0/17",
							"2400:cb00::/32",
							"2405:b500::/32",
							"2606:4700::/32",
							"2803:f800::/32",
							"2c0f:f248::/32",
							"2a06:98c0::/29"
					};
				}
				networks = new Subnet[ips.length];
				for (int i = 0; i < ips.length; i++)
					try {
						networks[i] = Subnet.createInstance(ips[i]);
					} catch (UnknownHostException e) {
						log().e(ips[i],e);
					}
			}
		}
	}
	
}
