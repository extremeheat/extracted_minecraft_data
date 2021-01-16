package io.netty.handler.codec.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPromise;
import io.netty.util.AsciiString;
import io.netty.util.ReferenceCountUtil;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

public class HttpClientUpgradeHandler extends HttpObjectAggregator implements ChannelOutboundHandler {
   private final HttpClientUpgradeHandler.SourceCodec sourceCodec;
   private final HttpClientUpgradeHandler.UpgradeCodec upgradeCodec;
   private boolean upgradeRequested;

   public HttpClientUpgradeHandler(HttpClientUpgradeHandler.SourceCodec var1, HttpClientUpgradeHandler.UpgradeCodec var2, int var3) {
      super(var3);
      if (var1 == null) {
         throw new NullPointerException("sourceCodec");
      } else if (var2 == null) {
         throw new NullPointerException("upgradeCodec");
      } else {
         this.sourceCodec = var1;
         this.upgradeCodec = var2;
      }
   }

   public void bind(ChannelHandlerContext var1, SocketAddress var2, ChannelPromise var3) throws Exception {
      var1.bind(var2, var3);
   }

   public void connect(ChannelHandlerContext var1, SocketAddress var2, SocketAddress var3, ChannelPromise var4) throws Exception {
      var1.connect(var2, var3, var4);
   }

   public void disconnect(ChannelHandlerContext var1, ChannelPromise var2) throws Exception {
      var1.disconnect(var2);
   }

   public void close(ChannelHandlerContext var1, ChannelPromise var2) throws Exception {
      var1.close(var2);
   }

   public void deregister(ChannelHandlerContext var1, ChannelPromise var2) throws Exception {
      var1.deregister(var2);
   }

   public void read(ChannelHandlerContext var1) throws Exception {
      var1.read();
   }

   public void write(ChannelHandlerContext var1, Object var2, ChannelPromise var3) throws Exception {
      if (!(var2 instanceof HttpRequest)) {
         var1.write(var2, var3);
      } else if (this.upgradeRequested) {
         var3.setFailure(new IllegalStateException("Attempting to write HTTP request with upgrade in progress"));
      } else {
         this.upgradeRequested = true;
         this.setUpgradeRequestHeaders(var1, (HttpRequest)var2);
         var1.write(var2, var3);
         var1.fireUserEventTriggered(HttpClientUpgradeHandler.UpgradeEvent.UPGRADE_ISSUED);
      }
   }

   public void flush(ChannelHandlerContext var1) throws Exception {
      var1.flush();
   }

   protected void decode(ChannelHandlerContext var1, HttpObject var2, List<Object> var3) throws Exception {
      FullHttpResponse var4 = null;

      try {
         if (!this.upgradeRequested) {
            throw new IllegalStateException("Read HTTP response without requesting protocol switch");
         }

         if (var2 instanceof HttpResponse) {
            HttpResponse var5 = (HttpResponse)var2;
            if (!HttpResponseStatus.SWITCHING_PROTOCOLS.equals(var5.status())) {
               var1.fireUserEventTriggered(HttpClientUpgradeHandler.UpgradeEvent.UPGRADE_REJECTED);
               removeThisHandler(var1);
               var1.fireChannelRead(var2);
               return;
            }
         }

         if (var2 instanceof FullHttpResponse) {
            var4 = (FullHttpResponse)var2;
            var4.retain();
            var3.add(var4);
         } else {
            super.decode(var1, var2, var3);
            if (var3.isEmpty()) {
               return;
            }

            assert var3.size() == 1;

            var4 = (FullHttpResponse)var3.get(0);
         }

         String var7 = var4.headers().get((CharSequence)HttpHeaderNames.UPGRADE);
         if (var7 != null && !AsciiString.contentEqualsIgnoreCase(this.upgradeCodec.protocol(), var7)) {
            throw new IllegalStateException("Switching Protocols response with unexpected UPGRADE protocol: " + var7);
         }

         this.sourceCodec.prepareUpgradeFrom(var1);
         this.upgradeCodec.upgradeTo(var1, var4);
         var1.fireUserEventTriggered(HttpClientUpgradeHandler.UpgradeEvent.UPGRADE_SUCCESSFUL);
         this.sourceCodec.upgradeFrom(var1);
         var4.release();
         var3.clear();
         removeThisHandler(var1);
      } catch (Throwable var6) {
         ReferenceCountUtil.release(var4);
         var1.fireExceptionCaught(var6);
         removeThisHandler(var1);
      }

   }

   private static void removeThisHandler(ChannelHandlerContext var0) {
      var0.pipeline().remove(var0.name());
   }

   private void setUpgradeRequestHeaders(ChannelHandlerContext var1, HttpRequest var2) {
      var2.headers().set((CharSequence)HttpHeaderNames.UPGRADE, (Object)this.upgradeCodec.protocol());
      LinkedHashSet var3 = new LinkedHashSet(2);
      var3.addAll(this.upgradeCodec.setUpgradeHeaders(var1, var2));
      StringBuilder var4 = new StringBuilder();
      Iterator var5 = var3.iterator();

      while(var5.hasNext()) {
         CharSequence var6 = (CharSequence)var5.next();
         var4.append(var6);
         var4.append(',');
      }

      var4.append(HttpHeaderValues.UPGRADE);
      var2.headers().add((CharSequence)HttpHeaderNames.CONNECTION, (Object)var4.toString());
   }

   public interface UpgradeCodec {
      CharSequence protocol();

      Collection<CharSequence> setUpgradeHeaders(ChannelHandlerContext var1, HttpRequest var2);

      void upgradeTo(ChannelHandlerContext var1, FullHttpResponse var2) throws Exception;
   }

   public interface SourceCodec {
      void prepareUpgradeFrom(ChannelHandlerContext var1);

      void upgradeFrom(ChannelHandlerContext var1);
   }

   public static enum UpgradeEvent {
      UPGRADE_ISSUED,
      UPGRADE_SUCCESSFUL,
      UPGRADE_REJECTED;

      private UpgradeEvent() {
      }
   }
}
