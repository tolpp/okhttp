package okhttp3;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public interface AsyncInterceptor {
  void interceptRequest(Chain chain) throws IOException;

  void interceptResponse(Chain chain) throws IOException;

  interface Chain {
    Request request();

    Response response();

    void proceed(Request request) throws IOException;

    void proceed(Response response) throws IOException;

    Call call();

    int connectTimeoutMillis();

    Chain withConnectTimeout(int timeout, TimeUnit unit);

    int readTimeoutMillis();

    Chain withReadTimeout(int timeout, TimeUnit unit);

    int writeTimeoutMillis();

    Chain withWriteTimeout(int timeout, TimeUnit unit);
  }
}
