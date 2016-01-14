/**
 * 
 */
package org.springframework.configcenter.config;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.configcenter.ConfigLoader;

/**
 * @author liangpeng
 * @date 2015年12月31日 下午11:41:00
 */
public class ConfigPlaceholderConfigator extends PropertyPlaceholderConfigurer implements DisposableBean{
	private static final Logger logger = LoggerFactory
			.getLogger(ConfigPlaceholderConfigator.class);
	private ConfigClientInfo configClient = new ConfigClientInfo();

	@Override
	protected void loadProperties(Properties props) throws IOException {
		logger.info("loading remote config[{}]......", configClient.toString());
		ConfigLoader.init(configClient.getZkServer(), configClient.getGroup(),
				configClient.getVersion(), configClient.getCharset());
		logger.info("remote config loaded.");
	}

	public void setZkServer(String zkServer) {
		configClient.setZkServer(zkServer);
	}

	public void setGroup(String group) {
		configClient.setGroup(group);
	}

	public void setVersion(String version) {
		configClient.setVersion(version);
	}

	public void setCharset(String charset) {
		configClient.setCharset(charset);
	}

	private class ConfigClientInfo {
		private String zkServer;
		private String group;
		private String version;
		private String charset;

		public String getZkServer() {
			return zkServer;
		}

		public void setZkServer(String zkServer) {
			this.zkServer = zkServer;
		}

		public String getGroup() {
			return group;
		}

		public void setGroup(String group) {
			this.group = group;
		}

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}

		public String getCharset() {
			return charset;
		}

		public void setCharset(String charset) {
			this.charset = charset;
		}

		public String toString() {
			return new StringBuilder().append("zkServer=").append(zkServer)
					.append(",").append("group=").append(group).append(",")
					.append("version=").append(version).append(",")
					.append("charset=").append(charset).toString();
		}
	}
	
	@Override
	public void destroy() throws Exception {
		ConfigLoader.close();
	}
}
