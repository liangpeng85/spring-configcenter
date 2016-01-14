/**
 * 
 */
package org.springframework.configcenter;

import java.lang.reflect.Field;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author liangpeng
 *
 */
public class ConfigItem {
	private static final Logger logger = LoggerFactory
			.getLogger(ConfigItem.class);
	private String value;
	private String propertyName;
	private CopyOnWriteArrayList<PropertyListener> listeners = new CopyOnWriteArrayList<PropertyListener>();

	public String getValue() {
		return value;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public ConfigItem(String propertyName, String value) {
		this.value = value;
		this.propertyName = propertyName;
	}

	public void trigger() {
		for (PropertyListener l : listeners) {
			l.set();
		}
	}

	public PropertyListener addListener(Field field, Object targetObject) {
		logger.info("add property listener-{}", field);
		PropertyListener l = new PropertyListenerImpl(field, targetObject);
		listeners.add(l);
		return l;
	}

	class PropertyListenerImpl implements PropertyListener {
		private Field field;
		private Object targetObject;

		PropertyListenerImpl(Field f, Object targetObject) {
			this.field = f;
			this.targetObject = targetObject;
		}

		public void set() {
			logger.info("trigger listener,field={}", field);
			String type = field.getType().getName();
			logger.debug("field[{}]type={}", propertyName, type);

			try {
				field.setAccessible(true);
				if (type == Long.class.getName()) {
					field.set(targetObject, Long.valueOf(ConfigItem.this.value));
				} else if (type == Integer.class.getName()) {
					field.set(targetObject, Integer.valueOf(ConfigItem.this.value));
				} else if (type == String.class.getName()) {
					field.set(targetObject, String.valueOf(ConfigItem.this.value));
				} else if (type == Float.class.getName()) {
					field.set(targetObject, Float.valueOf(ConfigItem.this.value));
				} else if (type == Double.class.getName()) {
					field.set(targetObject, Double.valueOf(ConfigItem.this.value));
				} else if (type == Boolean.class.getName()) {
					field.set(targetObject, Boolean.valueOf(ConfigItem.this.value));
				} else {
					field.set(targetObject, String.valueOf(ConfigItem.this.value));
				}

			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
}
