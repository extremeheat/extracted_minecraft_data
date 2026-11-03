package com.mojang.realmsclient.client.worldupload;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodySubscribers;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Flow;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import net.minecraft.TracingExecutor;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

final class SocksUploadHttpClient implements UploadHttpClient {
   private static final Authenticator NO_HTTP_AUTHENTICATION = new Authenticator() {
   };
   private final Proxy proxy;
   private final Duration connectTimeout;

   public SocksUploadHttpClient(final Proxy proxy, final Duration connectTimeout) {
      super();
      this.proxy = proxy;
      this.connectTimeout = connectTimeout;
   }

   public <T> CompletableFuture<HttpResponse<T>> sendAsync(final HttpRequest request, final HttpResponse.BodyHandler<T> handler) {
      HttpURLConnection connection;
      try {
         connection = (HttpURLConnection)request.uri().toURL().openConnection(this.proxy);
      } catch (IOException failure) {
         return CompletableFuture.failedFuture(failure);
      }

      CompletableFuture<HttpResponse<T>> response = new CompletableFuture();
      response.whenComplete((result, failurex) -> {
         if (response.isCancelled()) {
            TracingExecutor var10000 = Util.nonCriticalIoPool();
            Objects.requireNonNull(connection);
            var10000.execute(connection::disconnect);
         }

      });
      Util.nonCriticalIoPool().execute(() -> {
         try {
            response.complete(this.execute(connection, request, handler, response));
         } catch (Throwable var9) {
            response.completeExceptionally(var9 instanceof CompletionException && var9.getCause() != null ? var9.getCause() : var9);
         } finally {
            connection.disconnect();
         }

      });
      return response;
   }

   private <T> HttpResponse<T> execute(final HttpURLConnection connection, final HttpRequest request, final HttpResponse.BodyHandler<T> handler, final CompletableFuture<?> response) throws IOException {
      Duration timeout = (Duration)request.timeout().orElse(Duration.ofMinutes(15L));
      connection.setConnectTimeout(timeoutMillis(this.connectTimeout.compareTo(timeout) < 0 ? this.connectTimeout : timeout));
      connection.setReadTimeout(timeoutMillis(timeout));
      connection.setInstanceFollowRedirects(false);
      connection.setUseCaches(false);
      connection.setAuthenticator(NO_HTTP_AUTHENTICATION);
      connection.setRequestMethod(request.method());
      request.headers().map().forEach((name, values) -> values.forEach((value) -> connection.addRequestProperty(name, value)));
      HttpRequest.BodyPublisher body = (HttpRequest.BodyPublisher)request.bodyPublisher().orElse(BodyPublishers.noBody());
      boolean hasBody = body.contentLength() != 0L || "POST".equals(request.method()) || "PUT".equals(request.method());
      if (hasBody) {
         connection.setDoOutput(true);
         if (body.contentLength() >= 0L) {
            connection.setFixedLengthStreamingMode(body.contentLength());
         } else {
            connection.setChunkedStreamingMode(8192);
         }
      }

      connection.connect();
      if (response.isDone()) {
         throw new CancellationException();
      } else {
         Optional var10000;
         if (connection instanceof HttpsURLConnection) {
            HttpsURLConnection secure = (HttpsURLConnection)connection;
            var10000 = secure.getSSLSession();
         } else {
            var10000 = Optional.empty();
         }

         Optional<SSLSession> sslSession = var10000;
         if (hasBody) {
            HttpResponse.BodySubscriber<InputStream> subscriber = BodySubscribers.ofInputStream();
            body.subscribe(new ByteSubscriber(subscriber));
            InputStream input = (InputStream)subscriber.getBody().toCompletableFuture().join();

            try {
               OutputStream output = connection.getOutputStream();

               try {
                  input.transferTo(output);
               } catch (Throwable var22) {
                  if (output != null) {
                     try {
                        output.close();
                     } catch (Throwable var20) {
                        var22.addSuppressed(var20);
                     }
                  }

                  throw var22;
               }

               if (output != null) {
                  output.close();
               }
            } catch (Throwable var23) {
               if (input != null) {
                  try {
                     input.close();
                  } catch (Throwable var19) {
                     var23.addSuppressed(var19);
                  }
               }

               throw var23;
            }

            if (input != null) {
               input.close();
            }
         }

         int statusCode = connection.getResponseCode();
         Map<String, List<String>> headers = new HashMap();
         connection.getHeaderFields().forEach((name, values) -> {
            if (name != null) {
               headers.put(name, values);
            }

         });
         HttpHeaders responseHeaders = HttpHeaders.of(headers, (name, value) -> true);
         Response<Void> info = new Response<Void>(statusCode, request, responseHeaders, (Object)null, sslSession);
         HttpResponse.BodySubscriber<T> subscriber = handler.apply(info);
         InputStream responseBody = statusCode >= 400 ? connection.getErrorStream() : connection.getInputStream();
         InputStream input = responseBody == null ? InputStream.nullInputStream() : responseBody;

         Response var16;
         try {
            BodyPublishers.ofInputStream(() -> input).subscribe(new ByteSubscriber(subscriber));
            var16 = new Response(statusCode, request, responseHeaders, subscriber.getBody().toCompletableFuture().join(), sslSession);
         } catch (Throwable var21) {
            if (input != null) {
               try {
                  input.close();
               } catch (Throwable var18) {
                  var21.addSuppressed(var18);
               }
            }

            throw var21;
         }

         if (input != null) {
            input.close();
         }

         return var16;
      }
   }

   private static int timeoutMillis(final Duration timeout) {
      return (int)Math.clamp(TimeUnit.MILLISECONDS.convert(timeout), 1L, 2147483647L);
   }

   public void close() {
   }

   private static record ByteSubscriber<T>(HttpResponse.BodySubscriber<T> subscriber) implements Flow.Subscriber<ByteBuffer> {
      private ByteSubscriber {
         super();
      }

      public void onSubscribe(final Flow.Subscription subscription) {
         this.subscriber.onSubscribe(subscription);
      }

      public void onNext(final ByteBuffer bytes) {
         this.subscriber.onNext(List.of(bytes));
      }

      public void onError(final Throwable failure) {
         this.subscriber.onError(failure);
      }

      public void onComplete() {
         this.subscriber.onComplete();
      }
   }

   private static record Response<T>(int statusCode, HttpRequest request, HttpHeaders headers, @Nullable T body, Optional<SSLSession> sslSession) implements HttpResponse<T>, HttpResponse.ResponseInfo {
      private Response {
         super();
      }

      public Optional<HttpResponse<T>> previousResponse() {
         return Optional.empty();
      }

      public URI uri() {
         return this.request.uri();
      }

      public HttpClient.Version version() {
         return Version.HTTP_1_1;
      }
   }
}
