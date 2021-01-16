package io.netty.handler.ssl;

import io.netty.buffer.ByteBufAllocator;
import io.netty.util.internal.ObjectUtil;
import java.util.List;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSessionContext;

public abstract class DelegatingSslContext extends SslContext {
   private final SslContext ctx;

   protected DelegatingSslContext(SslContext var1) {
      super();
      this.ctx = (SslContext)ObjectUtil.checkNotNull(var1, "ctx");
   }

   public final boolean isClient() {
      return this.ctx.isClient();
   }

   public final List<String> cipherSuites() {
      return this.ctx.cipherSuites();
   }

   public final long sessionCacheSize() {
      return this.ctx.sessionCacheSize();
   }

   public final long sessionTimeout() {
      return this.ctx.sessionTimeout();
   }

   public final ApplicationProtocolNegotiator applicationProtocolNegotiator() {
      return this.ctx.applicationProtocolNegotiator();
   }

   public final SSLEngine newEngine(ByteBufAllocator var1) {
      SSLEngine var2 = this.ctx.newEngine(var1);
      this.initEngine(var2);
      return var2;
   }

   public final SSLEngine newEngine(ByteBufAllocator var1, String var2, int var3) {
      SSLEngine var4 = this.ctx.newEngine(var1, var2, var3);
      this.initEngine(var4);
      return var4;
   }

   protected final SslHandler newHandler(ByteBufAllocator var1, boolean var2) {
      SslHandler var3 = this.ctx.newHandler(var1, var2);
      this.initHandler(var3);
      return var3;
   }

   protected final SslHandler newHandler(ByteBufAllocator var1, String var2, int var3, boolean var4) {
      SslHandler var5 = this.ctx.newHandler(var1, var2, var3, var4);
      this.initHandler(var5);
      return var5;
   }

   public final SSLSessionContext sessionContext() {
      return this.ctx.sessionContext();
   }

   protected abstract void initEngine(SSLEngine var1);

   protected void initHandler(SslHandler var1) {
      this.initEngine(var1.engine());
   }
}
