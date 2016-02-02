/**
 * 
 */
package org.springframework.configcenter;

import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.configcenter.annotation.Config;

import com.netflix.curator.framework.CuratorFramework;
import com.netflix.curator.framework.CuratorFrameworkFactory;
import com.netflix.curator.framework.recipes.cache.NodeCache;
import com.netflix.curator.framework.recipes.cache.NodeCacheListener;
import com.netflix.curator.retry.RetryNTimes;

/**
 * @author liangpeng
 *
 */
public class ConfigLoader {
	private static final Logger logger = LoggerFactory
			.getLogger(ConfigLoader.class);

	private static final String ZK_NAMESPACE = "configcenter";
	private static final String ZK_CONFIG_DATA_NODE = "configData";
	private static final String ZK_CONFIG_CONSUMERS_NODE = "consumers";
	private static final int ZK_RETRY_TIMES = Integer.MAX_VALUE;
	private static final int ZK_RETRY_WAIT_TIME_MS = 5000;
	private static final String ZK_PATH_SEPARATOR = "/";

	private ConcurrentHashMap<String, ConfigItem> configItemMap = new ConcurrentHashMap<String, ConfigItem>(
			100);
	private String group;
	private String zkUrl;
	private String version;
	private String charset = "UTF-8";
	private CuratorFramework curator;
	private ExecutorService pool;
	private NodeCache nodeCache;
	private String configDataPath;
	private String configConsumersPath;
	private static ConfigLoader instance;

	private ConfigLoader(String zkUrl, String group, String version,
			String charset) {
		this.zkUrl = zkUrl;
		this.version = version;
		this.group = group;
		if (charset != null)
			this.charset = charset;
	}

	public synchronized static ConfigLoader init(String zkUrl, String group,
			String version, String charset) {
		if (instance == null) {
			try {
				instance = new ConfigLoader(zkUrl, group, version, charset)
						.build();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return instance;
	}

	private ConfigLoader build() throws Exception {
		logger.info("init configcenter zk....");
		curator = CuratorFrameworkFactory
				.builder()
				.connectString(zkUrl)
				.connectionTimeoutMs(5000)
				.sessionTimeoutMs(2000)
				.canBeReadOnly(false)
				.namespace(ZK_NAMESPACE)
				.retryPolicy(
						new RetryNTimes(ZK_RETRY_TIMES, ZK_RETRY_WAIT_TIME_MS))
				.build();
		curator.start();
		logger.info("configcenter zk initilized.");
		configDataPath = new StringBuilder(ZK_PATH_SEPARATOR).append(group)
				.append(ZK_PATH_SEPARATOR).append(version)
				.append(ZK_PATH_SEPARATOR).append(ZK_CONFIG_DATA_NODE)
				.toString();
		configConsumersPath = new StringBuilder(ZK_PATH_SEPARATOR)
				.append(group).append(ZK_PATH_SEPARATOR).append(version)
				.append(ZK_PATH_SEPARATOR).append(ZK_CONFIG_CONSUMERS_NODE)
				.toString();
		logger.info("configPath[{}]", configDataPath);
		nodeCache = new NodeCache(curator,configDataPath);
		pool = Executors.newSingleThreadExecutor();
		createConsumerNode();
		loadConfig();
		addWatcher();
		
		return this;
	}

	private void createConsumerNode() {
		logger.info("create subscription path[{}].", configConsumersPath);
		try {
			curator.create()
					.creatingParentsIfNeeded()
					.withMode(CreateMode.EPHEMERAL)
					.forPath(
							new StringBuilder(configConsumersPath)
									.append(ZK_PATH_SEPARATOR)
									.append(getHostIdentit()).toString());
		} catch (Exception e) {
			throw new RuntimeException(
					"configcenter create subscription path error.");
		}
	}

	private String getHostIdentit() throws Exception{
		return new StringBuilder(InetAddress.getLocalHost().getHostAddress())
				.append("$").append(UUID.randomUUID()).toString();
	}

	private void loadConfig() throws Exception {
		check();
		synchronized (this) {
			processConfig(load());
		}
	}

	private void check() {
		try {
			Stat s = curator.checkExists().forPath(configDataPath);
			if (s == null) {
				throw new RuntimeException(configDataPath
						+ " Not Exists,Please Check!");
			}
			if (s.getNumChildren() <= 0) {
				throw new RuntimeException(configDataPath
						+ " No children,Please Check!");
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void addWatcher() throws Exception {
		
		nodeCache.getListenable().addListener(new NodeCacheListener(){

			@Override
			public void nodeChanged() throws Exception {
				logger.info("remote config reloading...");
				ConfigLoader.this.reConfig(ConfigLoader.this.load());
				logger.info("remote config reloaded.");
				for (ConfigItem ci : configItemMap.values()) {
					ci.trigger();
					logger.info("trigger listeners on property on {}={}",
							ci.getPropertyName(), ci.getValue());
				}
			}
			
		}, pool);
		nodeCache.start(true);
	}

	private Properties load() throws Exception {
		List<String> configFiles = curator.getChildren()
				.forPath(configDataPath);
		Properties p = new Properties();
		byte[] data;
		for (String f : configFiles) {
			logger.info("loading {}@{}", f, configDataPath);
			data = curator.getData().forPath(
					new StringBuilder(configDataPath).append(ZK_PATH_SEPARATOR)
							.append(f).toString());
			p.load(new StringReader(new String(data, charset)));
		}
		logger.info("config loaded:{}", p);
		return p;
	}

	private void processConfig(Properties conf) {
		configItemMap.clear();
		for (Map.Entry<Object, Object> en : conf.entrySet()) {
			configItemMap
					.put(String.valueOf(en.getKey()),
							new ConfigItem(String.valueOf(en.getKey()), String
									.valueOf(en.getValue())));
		}
	}

	private void reConfig(Properties conf) {
		for (Map.Entry<Object, Object> en : conf.entrySet()) {
			ConfigItem ci = configItemMap.get(String.valueOf(en.getKey()));
			if (ci != null) {
				ci.setValue(String.valueOf(en.getValue()));
			} else {
				logger.debug("property[{}={}] is add new.", en.getKey(),
						en.getValue());
			}
		}

	}

    public static PropertyListener addConfigItemListener(String name,
			Object targetObject, Field f) {
		Config annotation = f.getAnnotation(Config.class);

		if (!instance.configItemMap.containsKey(name) && !annotation.ignore()) {
			throw new RuntimeException(String.format(
					"property[%s] not configed", name));
		}
		return instance.configItemMap.get(name).addListener(f, targetObject);
	}
    public static PropertyListener addConfigItemListener(String name,
            Object targetObject, Method m) {
        Config annotation = m.getAnnotation(Config.class);

        if (!instance.configItemMap.containsKey(name) && !annotation.ignore()) {
            throw new RuntimeException(String.format(
                    "property[%s] not configed", name));
        }
        return instance.configItemMap.get(name).addListener(m, targetObject);
    }
	public static void close() {
		if(instance != null) {
			try {
			logger.info("closing configcenter loader...");
			if(instance.nodeCache != null)instance.nodeCache.close();
			if(instance.pool != null) instance.pool.shutdownNow();
			if(instance.curator != null)instance.curator.close();
			if(instance.configItemMap != null)instance.configItemMap.clear();
			} catch(Exception e) {
				logger.warn(String.format("ConfigLoader close error,%s",e.getMessage()),e);
			}
		}
	}
}
