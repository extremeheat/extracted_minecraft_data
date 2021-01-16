package io.netty.handler.codec.http.cors;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.EmptyHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.util.internal.StringUtil;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

public final class CorsConfig {
   private final Set<String> origins;
   private final boolean anyOrigin;
   private final boolean enabled;
   private final Set<String> exposeHeaders;
   private final boolean allowCredentials;
   private final long maxAge;
   private final Set<HttpMethod> allowedRequestMethods;
   private final Set<String> allowedRequestHeaders;
   private final boolean allowNullOrigin;
   private final Map<CharSequence, Callable<?>> preflightHeaders;
   private final boolean shortCircuit;

   CorsConfig(CorsConfigBuilder var1) {
      super();
      this.origins = new LinkedHashSet(var1.origins);
      this.anyOrigin = var1.anyOrigin;
      this.enabled = var1.enabled;
      this.exposeHeaders = var1.exposeHeaders;
      this.allowCredentials = var1.allowCredentials;
      this.maxAge = var1.maxAge;
      this.allowedRequestMethods = var1.requestMethods;
      this.allowedRequestHeaders = var1.requestHeaders;
      this.allowNullOrigin = var1.allowNullOrigin;
      this.preflightHeaders = var1.preflightHeaders;
      this.shortCircuit = var1.shortCircuit;
   }

   public boolean isCorsSupportEnabled() {
      return this.enabled;
   }

   public boolean isAnyOriginSupported() {
      return this.anyOrigin;
   }

   public String origin() {
      return this.origins.isEmpty() ? "*" : (String)this.origins.iterator().next();
   }

   public Set<String> origins() {
      return this.origins;
   }

   public boolean isNullOriginAllowed() {
      return this.allowNullOrigin;
   }

   public Set<String> exposedHeaders() {
      return Collections.unmodifiableSet(this.exposeHeaders);
   }

   public boolean isCredentialsAllowed() {
      return this.allowCredentials;
   }

   public long maxAge() {
      return this.maxAge;
   }

   public Set<HttpMethod> allowedRequestMethods() {
      return Collections.unmodifiableSet(this.allowedRequestMethods);
   }

   public Set<String> allowedRequestHeaders() {
      return Collections.unmodifiableSet(this.allowedRequestHeaders);
   }

   public HttpHeaders preflightResponseHeaders() {
      if (this.preflightHeaders.isEmpty()) {
         return EmptyHttpHeaders.INSTANCE;
      } else {
         DefaultHttpHeaders var1 = new DefaultHttpHeaders();
         Iterator var2 = this.preflightHeaders.entrySet().iterator();

         while(var2.hasNext()) {
            Entry var3 = (Entry)var2.next();
            Object var4 = getValue((Callable)var3.getValue());
            if (var4 instanceof Iterable) {
               var1.add((CharSequence)var3.getKey(), (Iterable)var4);
            } else {
               var1.add((CharSequence)var3.getKey(), var4);
            }
         }

         return var1;
      }
   }

   public boolean isShortCircuit() {
      return this.shortCircuit;
   }

   /** @deprecated */
   @Deprecated
   public boolean isShortCurcuit() {
      return this.isShortCircuit();
   }

   private static <T> T getValue(Callable<T> var0) {
      try {
         return var0.call();
      } catch (Exception var2) {
         throw new IllegalStateException("Could not generate value for callable [" + var0 + ']', var2);
      }
   }

   public String toString() {
      return StringUtil.simpleClassName((Object)this) + "[enabled=" + this.enabled + ", origins=" + this.origins + ", anyOrigin=" + this.anyOrigin + ", exposedHeaders=" + this.exposeHeaders + ", isCredentialsAllowed=" + this.allowCredentials + ", maxAge=" + this.maxAge + ", allowedRequestMethods=" + this.allowedRequestMethods + ", allowedRequestHeaders=" + this.allowedRequestHeaders + ", preflightHeaders=" + this.preflightHeaders + ']';
   }

   /** @deprecated */
   @Deprecated
   public static CorsConfig.Builder withAnyOrigin() {
      return new CorsConfig.Builder();
   }

   /** @deprecated */
   @Deprecated
   public static CorsConfig.Builder withOrigin(String var0) {
      return "*".equals(var0) ? new CorsConfig.Builder() : new CorsConfig.Builder(new String[]{var0});
   }

   /** @deprecated */
   @Deprecated
   public static CorsConfig.Builder withOrigins(String... var0) {
      return new CorsConfig.Builder(var0);
   }

   /** @deprecated */
   @Deprecated
   public static final class DateValueGenerator implements Callable<Date> {
      public DateValueGenerator() {
         super();
      }

      public Date call() throws Exception {
         return new Date();
      }
   }

   /** @deprecated */
   @Deprecated
   public static class Builder {
      private final CorsConfigBuilder builder;

      /** @deprecated */
      @Deprecated
      public Builder(String... var1) {
         super();
         this.builder = new CorsConfigBuilder(var1);
      }

      /** @deprecated */
      @Deprecated
      public Builder() {
         super();
         this.builder = new CorsConfigBuilder();
      }

      /** @deprecated */
      @Deprecated
      public CorsConfig.Builder allowNullOrigin() {
         this.builder.allowNullOrigin();
         return this;
      }

      /** @deprecated */
      @Deprecated
      public CorsConfig.Builder disable() {
         this.builder.disable();
         return this;
      }

      /** @deprecated */
      @Deprecated
      public CorsConfig.Builder exposeHeaders(String... var1) {
         this.builder.exposeHeaders(var1);
         return this;
      }

      /** @deprecated */
      @Deprecated
      public CorsConfig.Builder allowCredentials() {
         this.builder.allowCredentials();
         return this;
      }

      /** @deprecated */
      @Deprecated
      public CorsConfig.Builder maxAge(long var1) {
         this.builder.maxAge(var1);
         return this;
      }

      /** @deprecated */
      @Deprecated
      public CorsConfig.Builder allowedRequestMethods(HttpMethod... var1) {
         this.builder.allowedRequestMethods(var1);
         return this;
      }

      /** @deprecated */
      @Deprecated
      public CorsConfig.Builder allowedRequestHeaders(String... var1) {
         this.builder.allowedRequestHeaders(var1);
         return this;
      }

      /** @deprecated */
      @Deprecated
      public CorsConfig.Builder preflightResponseHeader(CharSequence var1, Object... var2) {
         this.builder.preflightResponseHeader(var1, var2);
         return this;
      }

      /** @deprecated */
      @Deprecated
      public <T> CorsConfig.Builder preflightResponseHeader(CharSequence var1, Iterable<T> var2) {
         this.builder.preflightResponseHeader(var1, var2);
         return this;
      }

      /** @deprecated */
      @Deprecated
      public <T> CorsConfig.Builder preflightResponseHeader(String var1, Callable<T> var2) {
         this.builder.preflightResponseHeader(var1, (Callable)var2);
         return this;
      }

      /** @deprecated */
      @Deprecated
      public CorsConfig.Builder noPreflightResponseHeaders() {
         this.builder.noPreflightResponseHeaders();
         return this;
      }

      /** @deprecated */
      @Deprecated
      public CorsConfig build() {
         return this.builder.build();
      }

      /** @deprecated */
      @Deprecated
      public CorsConfig.Builder shortCurcuit() {
         this.builder.shortCircuit();
         return this;
      }
   }
}
