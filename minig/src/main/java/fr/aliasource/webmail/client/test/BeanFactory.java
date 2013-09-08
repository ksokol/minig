package fr.aliasource.webmail.client.test;

import com.google.gwt.core.client.GWT;

public final class BeanFactory {

    public static final MyFactory instance = GWT.create(MyFactory.class);

    private BeanFactory() {

    }
}
