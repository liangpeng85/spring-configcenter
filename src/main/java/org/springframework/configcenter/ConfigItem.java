/**
 * 
 */
package org.springframework.configcenter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
		PropertyListener l = new FieldPropertyListenerImpl(field, targetObject);
		listeners.add(l);
		return l;
	}
	public PropertyListener addListener(Method m, Object targetObject) {
        logger.info("add property listener-{}", m);
        PropertyListener l = new MethodPropertyListenerImpl(m, targetObject);
        listeners.add(l);
        return l;
    }
	class FieldPropertyListenerImpl implements PropertyListener {
		private Field field;
		private Object targetObject;

		FieldPropertyListenerImpl(Field f, Object targetObject) {
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
	class MethodPropertyListenerImpl implements PropertyListener {
        private Method method;
        private Object targetObject;

        MethodPropertyListenerImpl(Method m, Object targetObject) {
            this.method = m;
            this.targetObject = targetObject;
        }

        private String getMethodParameterType(Method method) {
            Class[] clz = method.getParameterTypes();
            if(clz == null || clz.length != 1) {
                throw new RuntimeException("annotatied method must only one argument.");
            }
            return clz[0].getClass().getName();
        }
        public void set() {
            logger.info("trigger listener,method={}", method);
            String type = getMethodParameterType(method);
            logger.debug("field[{}]type={}", propertyName, type);

            try {
                method.setAccessible(true);
                if (type == Long.class.getName()) {
                    method.invoke(targetObject, Long.valueOf(ConfigItem.this.value));
                } else if (type == Integer.class.getName()) {
                    method.invoke(targetObject, Integer.valueOf(ConfigItem.this.value));
                } else if (type == String.class.getName()) {
                    method.invoke(targetObject, String.valueOf(ConfigItem.this.value));
                } else if (type == Float.class.getName()) {
                    method.invoke(targetObject, Float.valueOf(ConfigItem.this.value));
                } else if (type == Double.class.getName()) {
                    method.invoke(targetObject, Double.valueOf(ConfigItem.this.value));
                } else if (type == Boolean.class.getName()) {
                    method.invoke(targetObject, Boolean.valueOf(ConfigItem.this.value));
                } else {
                    method.invoke(targetObject, String.valueOf(ConfigItem.this.value));
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
