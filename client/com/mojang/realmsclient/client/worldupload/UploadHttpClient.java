package com.mojang.realmsclient.client.worldupload;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.Proxy.Type;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Redirect;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

interface UploadHttpClient extends AutoCloseable {
   static UploadHttpClient create(final @Nullable Proxy proxy) {
      Duration connectTimeout = Duration.ofSeconds(15L);
      if (proxy != null && proxy.type() == Type.SOCKS) {
         return new SocksUploadHttpClient(proxy, connectTimeout);
      } else {
         HttpClient.Builder builder = HttpClient.newBuilder().executor(Util.nonCriticalIoPool()).connectTimeout(connectTimeout).followRedirects(Redirect.NEVER);
         if (proxy != null) {
            builder.proxy(ProxySelector.of(proxy.type() == Type.HTTP ? (InetSocketAddress)proxy.address() : null));
         }

         final HttpClient client = builder.build();
         return new UploadHttpClient() {
            public <T> CompletableFuture<HttpResponse<T>> sendAsync(final HttpRequest request, final HttpResponse.BodyHandler<T> handler) {
               return client.sendAsync(request, handler);
            }

            public void close() {
               client.close();
            }
         };
      }
   }

   <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request, HttpResponse.BodyHandler<T> handler);

   void close();
}
