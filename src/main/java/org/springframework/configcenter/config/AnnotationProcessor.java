/**
 * 
 */
package org.springframework.configcenter.config;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.configcenter.ConfigLoader;
import org.springframework.configcenter.annotation.Config;

/**
 * @author liangpeng
 * @date 2015年12月31日 下午11:39:38
 */
public class AnnotationProcessor implements BeanPostProcessor{
	private static final Logger logger = LoggerFactory.getLogger(AnnotationProcessor.class);
	public Object postProcessBeforeInitialization(Object bean, String beanName)
			throws BeansException {
		logger.info("ConfigCenter annotation processor postProcessBeforeInitialization." );
		return bean;
	}

	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		logger.info("ConfigCenter annotation processor postProcessBeforeInitialization." );
		parseField(bean);
		return bean;
	}

	private void parseField(Object obj) {
		Field[] fields = obj.getClass().getDeclaredFields();
		for (Field f : fields) {
			Config an = f.getAnnotation(Config.class);
			logger.debug("parsing field[{}].",f);
			if (an != null) {
				logger.info("process field[{}].",f);
				ConfigLoader.addConfigItemListener(an.name(), obj, f).set();
			}
		}
	}
	
}
