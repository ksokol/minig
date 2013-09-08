package fr.aliasource.webmail.client.test;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class Ajax<T> extends RequestBuilder {

	private static final MyFactory factory = GWT.create(MyFactory.class);

	private Class clazz;

	public Ajax(Method httpMethod, String url, Class clazz) {
		super(httpMethod, url);
		this.clazz = clazz;
		this.setHeader("Content-Type", "application/json");
		this.setHeader("X-Requested-With", "XMLHttpRequest");
	}

	public Request send(final AjaxCallback<T> callback) throws RequestException {
		return super.sendRequest(null, new RequestCallback() {

			@Override
			public void onResponseReceived(Request request, Response response) {
				if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
					if (response.getText() != null && !response.getText().isEmpty()) {
						AutoBean<T> bean = AutoBeanCodex.decode(factory, clazz, response.getText());
						callback.onSuccess(bean.as());
					} else {
						callback.onSuccess(null);
					}
				} else if (401 == response.getStatusCode()) {
					// TODO
					Window.Location.assign("login");

					return;
				} else {
					callback.onError(request, null);
				}
			}

			@Override
			public void onError(Request request, Throwable exception) {
				callback.onError(request, exception);
			}
		});
	}

	public Request send(T payload, final AjaxCallback<T> callback) throws RequestException {
		AutoBean<T> bean = AutoBeanUtils.getAutoBean(payload);

		return super.sendRequest(AutoBeanCodex.encode(bean).getPayload(), new RequestCallback() {

			@Override
			public void onResponseReceived(Request request, Response response) {
				// TODO
				// if (response.getStatusCode() == 201) {
				// callback.onSuccess(null);
				// return;
				// } else
				//
				if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
					if (clazz.equals(Void.class)) {
						callback.onSuccess(null);
					} else {
						// TODO
						if (response.getText() != null && !response.getText().isEmpty()) {
							AutoBean<T> bean = AutoBeanCodex.decode(factory, clazz, response.getText());
							callback.onSuccess(bean.as());
						} else {
							callback.onSuccess(null);
						}
					}
				} else if (401 == response.getStatusCode()) {
					// TODO
					Window.Location.assign("login");

					return;
				} else {
					callback.onError(request, null);
				}

			}

			@Override
			public void onError(Request request, Throwable exception) {
				callback.onError(request, exception);
			}
		});
	}
}
