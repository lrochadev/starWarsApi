package br.com.challenge.b2w.starWarsApi.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.HttpRequestRetryStrategy;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.NoHttpResponseException;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.http.protocol.HttpCoreContext;
import org.apache.hc.core5.util.TimeValue;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RetryHandlerConfiguration implements HttpRequestRetryStrategy {
    private static final HashSet<Class<?>> exceptionWhitelist = new HashSet<>();
    private static final HashSet<Class<?>> exceptionBlacklist = new HashSet<>();

    static {
        exceptionWhitelist.add(NoHttpResponseException.class);
        exceptionWhitelist.add(UnknownHostException.class);
        exceptionWhitelist.add(SocketException.class);
        exceptionWhitelist.add(SocketTimeoutException.class);
        exceptionBlacklist.add(InterruptedIOException.class);
        exceptionBlacklist.add(SSLException.class);
    }

    private final int maxRetries;
    private final int retrySleepTimeMS;

    public RetryHandlerConfiguration(int maxRetries, int retrySleepTimeMS) {
        this.maxRetries = maxRetries;
        this.retrySleepTimeMS = retrySleepTimeMS;
    }

    @Override
    public boolean retryRequest(HttpRequest httpRequest, IOException exception, int executionCount, HttpContext context) {
        boolean retry = true;
        int statusCode = 0;

        try {

            statusCode = HttpCoreContext.adapt(context).getResponse().getCode();

        } catch (Exception ex) {
            log.warn("It wasnt possible to retrieve http status code");
        }

        if (executionCount > maxRetries) {
            retry = false;
        } else if (HttpStatus.SC_FORBIDDEN == statusCode || HttpStatus.SC_NOT_FOUND == statusCode) {
            retry = true;
        } else if (isInList(exceptionWhitelist, exception)) {
            retry = true;
        } else if (isInList(exceptionBlacklist, exception)) {
            retry = false;
        }

        return retry;
    }

    @Override
    public boolean retryRequest(HttpResponse httpResponse, int executionCount, HttpContext context) {
        return executionCount <= this.maxRetries;
    }

    @Override
    public TimeValue getRetryInterval(HttpResponse httpResponse, int i, HttpContext httpContext) {
        return TimeValue.of(retrySleepTimeMS, TimeUnit.MILLISECONDS);
    }

    private boolean isInList(HashSet<Class<?>> list, Throwable error) {
        return list.stream().anyMatch(aList -> aList.isInstance(error));
    }
}