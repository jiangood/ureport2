package com.bstek.ureport.utils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class UPropertyUtils {
    public static Object getProperty(Object bean, String name) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
            PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor pd : descriptors) {
                if (pd.getName().equals(name)) {
                    Method readMethod = pd.getReadMethod();
                    if (readMethod != null) {
                        return readMethod.invoke(bean);
                    }
                }
            }
        } catch (IntrospectionException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Unable to get property: " + name + " from " + bean.getClass(), e);
        }
        return null;
    }

    public static PropertyDescriptor[] getPropertyDescriptors(Class<?> beanClass) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
            return beanInfo.getPropertyDescriptors();
        } catch (IntrospectionException e) {
            throw new RuntimeException("Unable to get property descriptors for: " + beanClass, e);
        }
    }
}
