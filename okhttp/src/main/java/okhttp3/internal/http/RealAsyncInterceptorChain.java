package okhttp3.internal.http;

import okhttp3.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static okhttp3.internal.Util.checkDuration;

public final class RealAsyncInterceptorChain implements AsyncInterceptor.Chain {
  private final List<AsyncInterceptor> interceptors;
  private final int index;
  private final Request request;
  private final Response response;
  private final Call call;
  private final EventListener eventListener;
  private final int connectTimeout;
  private final int readTimeout;
  private final int writeTimeout;
  private int calls;

  public RealAsyncInterceptorChain(List<AsyncInterceptor> interceptors, int index, Request request, Response response, Call call, EventListener eventListener, int connectTimeout, int readTimeout, int writeTimeout) {
    this.interceptors = interceptors;
    this.index = index;
    this.request = request;
    this.response = response;
    this.call = call;
    this.eventListener = eventListener;
    this.connectTimeout = connectTimeout;
    this.readTimeout = readTimeout;
    this.writeTimeout = writeTimeout;
  }

  @Override
  public Request request() {
    return request;
  }

  @Override
  public Response response() {
    return response;
  }

  @Override
  public void proceed(Request request) throws IOException {
    RealAsyncInterceptorChain next = new RealAsyncInterceptorChain(interceptors, index + 1, request, response, call, eventListener, connectTimeout, readTimeout, writeTimeout);
    AsyncInterceptor interceptor = interceptors.get(index);
    interceptor.interceptRequest(next);

  }

  @Override
  public void proceed(Response response) throws IOException {
    RealAsyncInterceptorChain previous = new RealAsyncInterceptorChain(interceptors, index - 1, request, response, call, eventListener, connectTimeout, readTimeout, writeTimeout);
    AsyncInterceptor interceptor = interceptors.get(index - 2);
    interceptor.interceptResponse(previous);
  }

  @Override
  public Call call() {
    return call;
  }

  @Override
  public int connectTimeoutMillis() {
    return connectTimeout;
  }

  @Override
  public AsyncInterceptor.Chain withConnectTimeout(int timeout, TimeUnit unit) {
    int millis = checkDuration("timeout", timeout, unit);
    return new RealAsyncInterceptorChain(interceptors, index, request, response, call, eventListener, millis, readTimeout, writeTimeout);
  }

  @Override
  public int readTimeoutMillis() {
    return readTimeout;
  }

  @Override
  public AsyncInterceptor.Chain withReadTimeout(int timeout, TimeUnit unit) {
    int millis = checkDuration("timeout", timeout, unit);
    return new RealAsyncInterceptorChain(interceptors, index, request, response, call, eventListener, connectTimeout, millis, writeTimeout);
  }

  @Override
  public int writeTimeoutMillis() {
    return writeTimeout;
  }

  @Override
  public AsyncInterceptor.Chain withWriteTimeout(int timeout, TimeUnit unit) {
    int millis = checkDuration("timeout", timeout, unit);
    return new RealAsyncInterceptorChain(interceptors, index, request, response, call, eventListener, connectTimeout, readTimeout, millis);
  }
}
