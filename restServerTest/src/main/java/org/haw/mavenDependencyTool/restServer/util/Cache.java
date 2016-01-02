package org.haw.mavenDependencyTool.restServer.util;

import java.util.concurrent.Callable;

import redis.clients.jedis.Jedis;

public class Cache {
	private Jedis jedis;

	public Cache() {
		jedis = new Jedis("localhost");
	}	

	public String cache(String key, Callable<String> runnable) {
		String result = jedis.get(key);
		if(result == null){
			try {
				result = runnable.call();
			} catch (Exception e) {
				return null;
			}
			jedis.set(key,result);
		}
		return result;		
	}

}
