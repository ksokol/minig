package fr.aliasource.webmail.client.test;

import com.google.gwt.http.client.Request;

public abstract class AjaxCallback<T> {

    // private final MyFactory factory = GWT.create(MyFactory.class);
    //
    // protected Class<T> persistentClass = figureOutPersistentClass();
    //
    // private Class<T> figureOutPersistentClass() {
    // ParameterizedType genericSuperclass = (ParameterizedType)
    // getClass().getGenericSuperclass();
    // Type[] actualTypeArguments = genericSuperclass.getActualTypeArguments();
    //
    // System.out.println(actualTypeArguments[0]);
    //
    // Class<T> clazz = (Class<T>) actualTypeArguments[0]; //
    // ((ParameterizedType)
    // // (getClass().getGenericSuperclass())).getActualTypeArguments()[0];
    // return clazz;
    // }
    //
    //
    // public void onResponseReceived(Request request, Response response) {
    // if (200 == response.getStatusCode()) {
    // System.out.println("-------------------");
    // System.out.println(persistentClass);
    // //
    // System.out.println(CustomRequestCallback.this.getClass().getGenericSuperclass());
    // // Type type = ((ParameterizedType)
    // // (CustomRequestCallback.this.getClass().getGenericSuperclass()))
    // // .getActualTypeArguments()[0];
    // //
    // // System.out.println(type);
    // // Class<T> clazz = (Class<T>) ((ParameterizedType)
    // // (CustomRequestCallback.this.getClass()
    // // .getGenericSuperclass())).getActualTypeArguments()[0];
    //
    // AutoBean<T> bean = AutoBeanCodex.decode(factory, persistentClass,
    // response.getText());
    //
    // onSuccess(bean.as());
    // } else {
    // onError(request, null);
    // }
    // }

    public abstract void onSuccess(T object);

    public abstract void onError(Request request, Throwable exception);

}
