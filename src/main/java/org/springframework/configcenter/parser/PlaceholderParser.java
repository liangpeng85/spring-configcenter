/**
 * 
 */
package org.springframework.configcenter.parser;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.configcenter.config.ConfigPlaceholderConfigator;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * @author liangpeng
 * @date 2016年1月1日 上午12:17:52

 */
public class PlaceholderParser extends AbstractSingleBeanDefinitionParser {

	private static final String ATTRIBUTENAME_ZKSERVER = "zkServer";
	public static final String ATTRIBUTENAME_VERSION = "version";
	public static final String ATTRIBUTENAME_GROUP = "group";
	public static final String ATTRIBUTENAME_CHARSET = "charset";
	@Override
	protected Class<?> getBeanClass(Element element) {
		return ConfigPlaceholderConfigator.class;
	}

	@Override
	protected String getBeanClassName(Element element) {
		return ConfigPlaceholderConfigator.class.getName();
	}

	@Override
	protected void doParse(Element element, BeanDefinitionBuilder builder) {
		String zkServer = element.getAttribute(ATTRIBUTENAME_ZKSERVER);
		String version = element.getAttribute(ATTRIBUTENAME_VERSION);
		String group = element.getAttribute(ATTRIBUTENAME_GROUP);
		String charset = element.getAttribute(ATTRIBUTENAME_CHARSET);
		if(StringUtils.isEmpty(zkServer)) {
			throw new RuntimeException(String.format("Element[%s],attribute[%s] required", element.getLocalName(),ATTRIBUTENAME_ZKSERVER));
		}
		if(StringUtils.isEmpty(version)) {
			throw new RuntimeException(String.format("Element[%s],attribute[%s] required", element.getLocalName(),ATTRIBUTENAME_VERSION));
		}
		if(StringUtils.isEmpty(group)) {
			throw new RuntimeException(String.format("Element[%s],attribute[%s] required", element.getLocalName(),ATTRIBUTENAME_GROUP));
		}
		builder.addPropertyValue(ATTRIBUTENAME_ZKSERVER, zkServer);
		builder.addPropertyValue(ATTRIBUTENAME_VERSION,version);
		builder.addPropertyValue(ATTRIBUTENAME_GROUP, group);
		builder.addPropertyValue(ATTRIBUTENAME_CHARSET, charset);
	}

	@Override
	protected boolean shouldGenerateId() {
		return true;
	}

}
