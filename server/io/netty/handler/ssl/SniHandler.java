package io.netty.handler.ssl;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.util.AsyncMapping;
import io.netty.util.DomainNameMapping;
import io.netty.util.Mapping;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;

public class SniHandler extends AbstractSniHandler<SslContext> {
   private static final SniHandler.Selection EMPTY_SELECTION = new SniHandler.Selection((SslContext)null, (String)null);
   protected final AsyncMapping<String, SslContext> mapping;
   private volatile SniHandler.Selection selection;

   public SniHandler(Mapping<? super String, ? extends SslContext> var1) {
      this((AsyncMapping)(new SniHandler.AsyncMappingAdapter(var1)));
   }

   public SniHandler(DomainNameMapping<? extends SslContext> var1) {
      this((Mapping)var1);
   }

   public SniHandler(AsyncMapping<? super String, ? extends SslContext> var1) {
      super();
      this.selection = EMPTY_SELECTION;
      this.mapping = (AsyncMapping)ObjectUtil.checkNotNull(var1, "mapping");
   }

   public String hostname() {
      return this.selection.hostname;
   }

   public SslContext sslContext() {
      return this.selection.context;
   }

   protected Future<SslContext> lookup(ChannelHandlerContext var1, String var2) throws Exception {
      return this.mapping.map(var2, var1.executor().newPromise());
   }

   protected final void onLookupComplete(ChannelHandlerContext var1, String var2, Future<SslContext> var3) throws Exception {
      if (!var3.isSuccess()) {
         Throwable var7 = var3.cause();
         if (var7 instanceof Error) {
            throw (Error)var7;
         } else {
            throw new DecoderException("failed to get the SslContext for " + var2, var7);
         }
      } else {
         SslContext var4 = (SslContext)var3.getNow();
         this.selection = new SniHandler.Selection(var4, var2);

         try {
            this.replaceHandler(var1, var2, var4);
         } catch (Throwable var6) {
            this.selection = EMPTY_SELECTION;
            PlatformDependent.throwException(var6);
         }

      }
   }

   protected void replaceHandler(ChannelHandlerContext var1, String var2, SslContext var3) throws Exception {
      SslHandler var4 = null;

      try {
         var4 = var3.newHandler(var1.alloc());
         var1.pipeline().replace((ChannelHandler)this, SslHandler.class.getName(), var4);
         var4 = null;
      } finally {
         if (var4 != null) {
            ReferenceCountUtil.safeRelease(var4.engine());
         }

      }

   }

   private static final class Selection {
      final SslContext context;
      final String hostname;

      Selection(SslContext var1, String var2) {
         super();
         this.context = var1;
         this.hostname = var2;
      }
   }

   private static final class AsyncMappingAdapter implements AsyncMapping<String, SslContext> {
      private final Mapping<? super String, ? extends SslContext> mapping;

      private AsyncMappingAdapter(Mapping<? super String, ? extends SslContext> var1) {
         super();
         this.mapping = (Mapping)ObjectUtil.checkNotNull(var1, "mapping");
      }

      public Future<SslContext> map(String var1, Promise<SslContext> var2) {
         SslContext var3;
         try {
            var3 = (SslContext)this.mapping.map(var1);
         } catch (Throwable var5) {
            return var2.setFailure(var5);
         }

         return var2.setSuccess(var3);
      }

      // $FF: synthetic method
      AsyncMappingAdapter(Mapping var1, Object var2) {
         this(var1);
      }
   }
}
