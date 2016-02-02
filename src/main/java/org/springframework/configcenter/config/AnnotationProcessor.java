/**
 * 
 */
package org.springframework.configcenter.config;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
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
		parseMethod(bean);
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
	private void parseMethod(Object obj) {
        Method[] methods = obj.getClass().getDeclaredMethods();
        for (Method m : methods) {
            Config an = m.getAnnotation(Config.class);
            logger.debug("parsing method[{}].",m);
            if (an != null) {
                logger.info("process method[{}].",m);
                ConfigLoader.addConfigItemListener(an.name(), obj, m).set();
            }
        }
    }
	
}
