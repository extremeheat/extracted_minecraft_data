package io.netty.handler.codec.http2;

import io.netty.util.internal.ObjectUtil;

public abstract class AbstractInboundHttp2ToHttpAdapterBuilder<T extends InboundHttp2ToHttpAdapter, B extends AbstractInboundHttp2ToHttpAdapterBuilder<T, B>> {
   private final Http2Connection connection;
   private int maxContentLength;
   private boolean validateHttpHeaders;
   private boolean propagateSettings;

   protected AbstractInboundHttp2ToHttpAdapterBuilder(Http2Connection var1) {
      super();
      this.connection = (Http2Connection)ObjectUtil.checkNotNull(var1, "connection");
   }

   protected final B self() {
      return this;
   }

   protected Http2Connection connection() {
      return this.connection;
   }

   protected int maxContentLength() {
      return this.maxContentLength;
   }

   protected B maxContentLength(int var1) {
      this.maxContentLength = var1;
      return this.self();
   }

   protected boolean isValidateHttpHeaders() {
      return this.validateHttpHeaders;
   }

   protected B validateHttpHeaders(boolean var1) {
      this.validateHttpHeaders = var1;
      return this.self();
   }

   protected boolean isPropagateSettings() {
      return this.propagateSettings;
   }

   protected B propagateSettings(boolean var1) {
      this.propagateSettings = var1;
      return this.self();
   }

   protected T build() {
      InboundHttp2ToHttpAdapter var1;
      try {
         var1 = this.build(this.connection(), this.maxContentLength(), this.isValidateHttpHeaders(), this.isPropagateSettings());
      } catch (Throwable var3) {
         throw new IllegalStateException("failed to create a new InboundHttp2ToHttpAdapter", var3);
      }

      this.connection.addListener(var1);
      return var1;
   }

   protected abstract T build(Http2Connection var1, int var2, boolean var3, boolean var4) throws Exception;
}
