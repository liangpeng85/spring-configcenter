/**
 * 
 */
package org.springframework.configcenter.parser;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.configcenter.config.AnnotationProcessor;
import org.w3c.dom.Element;

/**
 * @author liangpeng
 * @date 2016年1月1日 上午12:17:52

 */
public class ConfigAnnotationParser extends AbstractSingleBeanDefinitionParser {
	@Override
	protected Class<?> getBeanClass(Element element) {
		return AnnotationProcessor.class;
	}

	@Override
	protected String getBeanClassName(Element element) {
		return AnnotationProcessor.class.getName();
	}

	@Override
	protected void doParse(Element element, BeanDefinitionBuilder builder) {
		super.doParse(element, builder);
	}

	@Override
	protected boolean shouldGenerateId() {
		return true;
	}

}
