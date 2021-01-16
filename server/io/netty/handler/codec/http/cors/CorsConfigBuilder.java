package io.netty.handler.codec.http.cors;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

public final class CorsConfigBuilder {
   final Set<String> origins;
   final boolean anyOrigin;
   boolean allowNullOrigin;
   boolean enabled = true;
   boolean allowCredentials;
   final Set<String> exposeHeaders = new HashSet();
   long maxAge;
   final Set<HttpMethod> requestMethods = new HashSet();
   final Set<String> requestHeaders = new HashSet();
   final Map<CharSequence, Callable<?>> preflightHeaders = new HashMap();
   private boolean noPreflightHeaders;
   boolean shortCircuit;

   public static CorsConfigBuilder forAnyOrigin() {
      return new CorsConfigBuilder();
   }

   public static CorsConfigBuilder forOrigin(String var0) {
      return "*".equals(var0) ? new CorsConfigBuilder() : new CorsConfigBuilder(new String[]{var0});
   }

   public static CorsConfigBuilder forOrigins(String... var0) {
      return new CorsConfigBuilder(var0);
   }

   CorsConfigBuilder(String... var1) {
      super();
      this.origins = new LinkedHashSet(Arrays.asList(var1));
      this.anyOrigin = false;
   }

   CorsConfigBuilder() {
      super();
      this.anyOrigin = true;
      this.origins = Collections.emptySet();
   }

   public CorsConfigBuilder allowNullOrigin() {
      this.allowNullOrigin = true;
      return this;
   }

   public CorsConfigBuilder disable() {
      this.enabled = false;
      return this;
   }

   public CorsConfigBuilder exposeHeaders(String... var1) {
      this.exposeHeaders.addAll(Arrays.asList(var1));
      return this;
   }

   public CorsConfigBuilder exposeHeaders(CharSequence... var1) {
      CharSequence[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         CharSequence var5 = var2[var4];
         this.exposeHeaders.add(var5.toString());
      }

      return this;
   }

   public CorsConfigBuilder allowCredentials() {
      this.allowCredentials = true;
      return this;
   }

   public CorsConfigBuilder maxAge(long var1) {
      this.maxAge = var1;
      return this;
   }

   public CorsConfigBuilder allowedRequestMethods(HttpMethod... var1) {
      this.requestMethods.addAll(Arrays.asList(var1));
      return this;
   }

   public CorsConfigBuilder allowedRequestHeaders(String... var1) {
      this.requestHeaders.addAll(Arrays.asList(var1));
      return this;
   }

   public CorsConfigBuilder allowedRequestHeaders(CharSequence... var1) {
      CharSequence[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         CharSequence var5 = var2[var4];
         this.requestHeaders.add(var5.toString());
      }

      return this;
   }

   public CorsConfigBuilder preflightResponseHeader(CharSequence var1, Object... var2) {
      if (var2.length == 1) {
         this.preflightHeaders.put(var1, new CorsConfigBuilder.ConstantValueGenerator(var2[0]));
      } else {
         this.preflightResponseHeader(var1, (Iterable)Arrays.asList(var2));
      }

      return this;
   }

   public <T> CorsConfigBuilder preflightResponseHeader(CharSequence var1, Iterable<T> var2) {
      this.preflightHeaders.put(var1, new CorsConfigBuilder.ConstantValueGenerator(var2));
      return this;
   }

   public <T> CorsConfigBuilder preflightResponseHeader(CharSequence var1, Callable<T> var2) {
      this.preflightHeaders.put(var1, var2);
      return this;
   }

   public CorsConfigBuilder noPreflightResponseHeaders() {
      this.noPreflightHeaders = true;
      return this;
   }

   public CorsConfigBuilder shortCircuit() {
      this.shortCircuit = true;
      return this;
   }

   public CorsConfig build() {
      if (this.preflightHeaders.isEmpty() && !this.noPreflightHeaders) {
         this.preflightHeaders.put(HttpHeaderNames.DATE, CorsConfigBuilder.DateValueGenerator.INSTANCE);
         this.preflightHeaders.put(HttpHeaderNames.CONTENT_LENGTH, new CorsConfigBuilder.ConstantValueGenerator("0"));
      }

      return new CorsConfig(this);
   }

   private static final class DateValueGenerator implements Callable<Date> {
      static final CorsConfigBuilder.DateValueGenerator INSTANCE = new CorsConfigBuilder.DateValueGenerator();

      private DateValueGenerator() {
         super();
      }

      public Date call() throws Exception {
         return new Date();
      }
   }

   private static final class ConstantValueGenerator implements Callable<Object> {
      private final Object value;

      private ConstantValueGenerator(Object var1) {
         super();
         if (var1 == null) {
            throw new IllegalArgumentException("value must not be null");
         } else {
            this.value = var1;
         }
      }

      public Object call() {
         return this.value;
      }

      // $FF: synthetic method
      ConstantValueGenerator(Object var1, Object var2) {
         this(var1);
      }
   }
}
