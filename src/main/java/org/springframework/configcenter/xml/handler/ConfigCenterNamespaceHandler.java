/**
 * 
 */
package org.springframework.configcenter.xml.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.configcenter.parser.ConfigAnnotationParser;
import org.springframework.configcenter.parser.PlaceholderParser;

/**
 * @author liangpeng
 * @date 2015年12月31日 下午6:53:46

 */
public class ConfigCenterNamespaceHandler extends NamespaceHandlerSupport {

	private static final Logger logger = LoggerFactory.getLogger(ConfigCenterNamespaceHandler.class);
	public void init() {
		logger.info("init ConfigCenterNamespaceHandler....");
		registerBeanDefinitionParser("annotation", new ConfigAnnotationParser());
		registerBeanDefinitionParser("config", new PlaceholderParser());
	}

}
