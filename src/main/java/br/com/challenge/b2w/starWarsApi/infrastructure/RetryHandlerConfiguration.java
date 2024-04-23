package br.com.challenge.b2w.starWarsApi.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashSet;

@Slf4j
public class RetryHandlerConfiguration implements HttpRequestRetryHandler {
    private final static HashSet<Class<?>> exceptionWhitelist = new HashSet<>();
    private final static HashSet<Class<?>> exceptionBlacklist = new HashSet<>();

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

    public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
        boolean retry = true;
        int statusCode = 0;

        try {
            statusCode = HttpCoreContext.adapt(context).getResponse().getStatusLine().getStatusCode();
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

        if (retry) {
            try {
                Thread.sleep(retrySleepTimeMS);
            } catch (Exception ex) {
                log.error("It wasnt possible to postpone the requet");
            }
        }

        return retry;
    }

    private boolean isInList(HashSet<Class<?>> list, Throwable error) {
        for (Class<?> aList : list) {
            if (aList.isInstance(error)) {
                return true;
            }
        }
        return false;
    }
}